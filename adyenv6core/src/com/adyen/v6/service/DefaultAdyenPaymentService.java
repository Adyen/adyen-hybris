/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.service;

import com.adyen.Client;
import com.adyen.Config;
import com.adyen.enums.Environment;
import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentRequest3d;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.PaymentMethod;
import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.checkoututility.OriginKeysRequest;
import com.adyen.model.checkoututility.OriginKeysResponse;
import com.adyen.model.modification.CancelOrRefundRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.model.modification.RefundRequest;
import com.adyen.model.recurring.DisableRequest;
import com.adyen.model.recurring.DisableResult;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.model.recurring.RecurringDetailsRequest;
import com.adyen.model.recurring.RecurringDetailsResult;
import com.adyen.model.terminal.ConnectedTerminalsRequest;
import com.adyen.model.terminal.ConnectedTerminalsResponse;
import com.adyen.model.terminal.TerminalAPIRequest;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.Checkout;
import com.adyen.service.CheckoutUtility;
import com.adyen.service.Modification;
import com.adyen.service.Payment;
import com.adyen.service.PosPayment;
import com.adyen.service.TerminalCloudAPI;
import com.adyen.service.exception.ApiException;
import com.adyen.terminal.serialization.TerminalAPIGsonBuilder;
import com.adyen.util.Util;
import com.adyen.v6.converters.PaymentMethodConverter;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.factory.AdyenRequestFactory;
import com.adyen.v6.model.RequestInfo;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.adyen.v6.constants.Adyenv6coreConstants.PLUGIN_NAME;
import static com.adyen.v6.constants.Adyenv6coreConstants.PLUGIN_VERSION;

public class DefaultAdyenPaymentService implements AdyenPaymentService {
    private BaseStoreModel baseStore;
    private AdyenRequestFactory adyenRequestFactory;
    private Config config;
    private Client client;
    private Config posConfig;
    private Client posClient;

    private PaymentMethodConverter paymentMethodConverter;

    private static final int POS_REQUEST_TIMEOUT = 25000;

    private static final Logger LOG = Logger.getLogger(DefaultAdyenPaymentService.class);

    /**
     * Prevent initialization without base store
     */
    private DefaultAdyenPaymentService() {
    }

    public DefaultAdyenPaymentService(final BaseStoreModel baseStore) {
        this.baseStore = baseStore;

        String apiKey = baseStore.getAdyenAPIKey();
        String merchantAccount = baseStore.getAdyenMerchantAccount();
        String skinCode = baseStore.getAdyenSkinCode();
        String hmacKey = baseStore.getAdyenSkinHMAC();
        String apiEndpointPrefix = baseStore.getAdyenAPIEndpointPrefix();
        boolean isTestMode = baseStore.getAdyenTestMode();
        boolean isPosEnabled = baseStore.getAdyenPosEnabled();
        if (isPosEnabled) {
            String posApiKey = baseStore.getAdyenPosApiKey();
            String posMerchantAccount = baseStore.getAdyenPosMerchantAccount();
            posConfig = new Config();
            posConfig.setApiKey(posApiKey);
            posConfig.setMerchantAccount(posMerchantAccount);
            posConfig.setReadTimeoutMillis(POS_REQUEST_TIMEOUT);
            posConfig.setApplicationName(PLUGIN_NAME + " v" + PLUGIN_VERSION);
            posClient = new Client(posConfig);

            if (isTestMode) {
                posClient.setEnvironment(Environment.TEST, null);
            } else {
                posClient.setEnvironment(Environment.LIVE, null);
            }
        }
        Assert.notNull(merchantAccount);

        config = new Config();
        config.setApiKey(apiKey);
        config.setMerchantAccount(merchantAccount);
        config.setSkinCode(skinCode);
        config.setHmacKey(hmacKey);
        config.setApplicationName(PLUGIN_NAME + " v" + PLUGIN_VERSION);
        client = new Client(config);

        if (isTestMode) {
            client.setEnvironment(Environment.TEST, null);
        } else {
            client.setEnvironment(Environment.LIVE, apiEndpointPrefix);
        }
    }

    @Override
    public PaymentResult authorise(final CartData cartData, final HttpServletRequest request, final CustomerModel customerModel) throws Exception {
        Payment payment = new Payment(client);

        PaymentRequest paymentRequest = getAdyenRequestFactory().createAuthorizationRequest(client.getConfig().getMerchantAccount(),
                                                                                            cartData,
                                                                                            request,
                                                                                            customerModel,
                                                                                            baseStore.getAdyenRecurringContractMode());


        LOG.debug(paymentRequest);
        PaymentResult paymentResult = payment.authorise(paymentRequest);
        LOG.debug(paymentResult);

        return paymentResult;
    }

    @Override
    public ConnectedTerminalsResponse getConnectedTerminals() throws IOException, ApiException  {
        PosPayment posPayment = new PosPayment(posClient);
        ConnectedTerminalsRequest connectedTerminalsRequest = new ConnectedTerminalsRequest();
        connectedTerminalsRequest.setMerchantAccount(posConfig.getMerchantAccount());
        if (baseStore.getAdyenPosStoreId() != null && StringUtils.isNotEmpty(baseStore.getAdyenPosStoreId())) {
            connectedTerminalsRequest.setStore(baseStore.getAdyenPosStoreId());
        }
        LOG.debug(connectedTerminalsRequest);
        ConnectedTerminalsResponse connectedTerminalsResponse = posPayment.connectedTerminals(connectedTerminalsRequest);
        LOG.debug(connectedTerminalsResponse);
        return connectedTerminalsResponse;

    }

    @Override
    public PaymentsResponse authorisePayment(final CartData cartData, final RequestInfo requestInfo, final CustomerModel customerModel) throws Exception {
        Checkout checkout = new Checkout(client);

        PaymentsRequest paymentsRequest = getAdyenRequestFactory().createPaymentsRequest(client.getConfig().getMerchantAccount(),
                                                                                         cartData,
                                                                                         requestInfo,
                                                                                         customerModel,
                                                                                         baseStore.getAdyenRecurringContractMode(),
                                                                                         baseStore.getAdyenGuestUserTokenization());

        LOG.debug(paymentsRequest);
        PaymentsResponse paymentsResponse = checkout.payments(paymentsRequest);
        LOG.debug(paymentsResponse);

        return paymentsResponse;
    }

    @Override
    public PaymentsResponse componentPayment(final CartData cartData, final PaymentMethodDetails paymentMethodDetails, final RequestInfo requestInfo, final CustomerModel customerModel) throws Exception {
        Checkout checkout = new Checkout(client);

        PaymentsRequest paymentsRequest = getAdyenRequestFactory().createPaymentsRequest(client.getConfig().getMerchantAccount(),
                cartData,
                paymentMethodDetails,
                requestInfo,
                customerModel);

        LOG.debug(paymentsRequest);
        PaymentsResponse paymentsResponse = checkout.payments(paymentsRequest);
        LOG.debug(paymentsResponse);

        return paymentsResponse;
    }

    @Override
    public PaymentsResponse authorise3DPayment(final String paymentData, final String paRes, final String md) throws Exception {
        Checkout checkout = new Checkout(client);

        PaymentsDetailsRequest paymentsDetailsRequest = getAdyenRequestFactory().create3DPaymentsRequest(paymentData, md, paRes);
        LOG.debug(paymentsDetailsRequest);

        PaymentsResponse paymentsResponse = checkout.paymentsDetails(paymentsDetailsRequest);
        LOG.debug(paymentsResponse);

        return paymentsResponse;
    }

    @Override
    public PaymentsResponse authorise3DS2Payment(String paymentData, String token, String type) throws Exception {
        Checkout checkout = new Checkout(client);
        PaymentsDetailsRequest paymentsDetailsRequest = getAdyenRequestFactory().create3DS2PaymentsRequest(paymentData, token, type);
        PaymentsResponse paymentsResponse = checkout.paymentsDetails(paymentsDetailsRequest);
        return paymentsResponse;
    }

    @Override
    public PaymentResult authorise3D(final HttpServletRequest request, final String paRes, final String md) throws Exception {
        Payment payment = new Payment(client);

        PaymentRequest3d paymentRequest3d = getAdyenRequestFactory().create3DAuthorizationRequest(client.getConfig().getMerchantAccount(), request, md, paRes);

        LOG.debug(paymentRequest3d);
        PaymentResult paymentResult = payment.authorise3D(paymentRequest3d);
        LOG.debug(paymentResult);

        return paymentResult;
    }

    @Override
    public ModificationResult capture(final BigDecimal amount, final Currency currency, final String authReference, final String merchantReference) throws Exception {
        Modification modification = new Modification(client);

        CaptureRequest captureRequest = getAdyenRequestFactory().createCaptureRequest(client.getConfig().getMerchantAccount(), amount, currency, authReference, merchantReference);

        LOG.debug(captureRequest);
        ModificationResult modificationResult = modification.capture(captureRequest);
        LOG.debug(modificationResult);

        return modificationResult;
    }

    @Override
    public ModificationResult cancelOrRefund(final String authReference, final String merchantReference) throws Exception {
        Modification modification = new Modification(client);

        CancelOrRefundRequest cancelRequest = getAdyenRequestFactory().createCancelOrRefundRequest(client.getConfig().getMerchantAccount(), authReference, merchantReference);

        LOG.debug(cancelRequest);
        ModificationResult modificationResult = modification.cancelOrRefund(cancelRequest);
        LOG.debug(modificationResult);

        return modificationResult;
    }

    @Override
    public ModificationResult refund(final BigDecimal amount, final Currency currency, final String authReference, final String merchantReference) throws Exception {
        Modification modification = new Modification(client);

        RefundRequest refundRequest = getAdyenRequestFactory().createRefundRequest(client.getConfig().getMerchantAccount(), amount, currency, authReference, merchantReference);

        LOG.debug(refundRequest);
        ModificationResult modificationResult = modification.refund(refundRequest);
        LOG.debug(modificationResult);

        return modificationResult;
    }

    @Override
    public List<PaymentMethod> getPaymentMethods(final BigDecimal amount,
                                                 final String currency,
                                                 final String countryCode,
                                                 final String shopperLocale,
                                                 final String shopperReference) throws IOException, ApiException {

        PaymentMethodsResponse response =getPaymentMethodsResponse(amount,currency, countryCode, shopperLocale, shopperReference);
        return response.getPaymentMethods();
    }

    @Override
    public PaymentMethodsResponse getPaymentMethodsResponse(final BigDecimal amount,
                                                 final String currency,
                                                 final String countryCode,
                                                 final String shopperLocale,
                                                 final String shopperReference) throws IOException, ApiException {
        Checkout checkout = new Checkout(client);
        PaymentMethodsRequest request = new PaymentMethodsRequest();
        request.merchantAccount(client.getConfig().getMerchantAccount()).amount(Util.createAmount(amount, currency)).countryCode(countryCode);

        if (! StringUtils.isEmpty(shopperLocale)) {
            request.setShopperLocale(shopperLocale);
        }

        if (! StringUtils.isEmpty(shopperReference)) {
            request.setShopperReference(shopperReference);
        }

        LOG.debug(request);
        PaymentMethodsResponse response = checkout.paymentMethods(request);
        LOG.debug(response);

        return response;
    }

    @Override
    @Deprecated
    public List<com.adyen.model.hpp.PaymentMethod> getPaymentMethods(final BigDecimal amount,
                                                                     final String currency,
                                                                     final String countryCode,
                                                                     final String shopperLocale) throws HTTPClientException, SignatureException, IOException {
        try {
            List<PaymentMethod> checkoutPaymentMethods = getPaymentMethods(amount, currency, countryCode, shopperLocale, null);
            return checkoutPaymentMethods.stream().map(paymentMethodConverter::convert).collect(Collectors.toList());
        } catch (ApiException e) {
            LOG.error(e);
        }
        return null;
    }

    @Override
    @Deprecated
    public List<RecurringDetail> getStoredCards(final String customerId) throws IOException, ApiException {
        if (customerId == null) {
            return new ArrayList<>();
        }

        com.adyen.service.Recurring recurring = new com.adyen.service.Recurring(client);

        RecurringDetailsRequest request = getAdyenRequestFactory().createListRecurringDetailsRequest(client.getConfig().getMerchantAccount(), customerId);

        LOG.debug(request);
        RecurringDetailsResult result = recurring.listRecurringDetails(request);
        LOG.debug(result);

        //Return only cards
        List<RecurringDetail> storedCards = result.getRecurringDetails()
                                                  .stream()
                                                  .filter(detail -> (detail.getCard() != null && detail.getRecurringDetailReference() != null))
                                                  .collect(Collectors.toList());

        return storedCards;
    }

    @Override
    public boolean disableStoredCard(final String customerId, final String recurringReference) throws IOException, ApiException {
        com.adyen.service.Recurring recurring = new com.adyen.service.Recurring(client);

        DisableRequest request = getAdyenRequestFactory().createDisableRequest(client.getConfig().getMerchantAccount(), customerId, recurringReference);

        LOG.debug(request);
        DisableResult result = recurring.disable(request);
        LOG.debug(result);

        return ("[detail-successfully-disabled]".equals(result.getResponse()) || "[all-details-successfully-disabled]".equals(result.getResponse()));
    }

    @Override
    public PaymentsResponse getPaymentDetailsFromPayload(HashMap<String, String> details) throws Exception {
        Checkout checkout = new Checkout(client);

        PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        paymentsDetailsRequest.setDetails(details);

        LOG.debug(paymentsDetailsRequest);
        PaymentsResponse paymentsResponse = checkout.paymentsDetails(paymentsDetailsRequest);
        LOG.debug(paymentsResponse);

        return paymentsResponse;
    }

    @Override
    public PaymentsResponse getPaymentDetailsFromPayload(Map<String, String> details, String paymentData) throws Exception {
        Checkout checkout = new Checkout(client);
        PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        paymentsDetailsRequest.setDetails(details);
        paymentsDetailsRequest.setPaymentData(paymentData);

        LOG.debug(paymentsDetailsRequest);
        PaymentsResponse paymentsResponse = checkout.paymentsDetails(paymentsDetailsRequest);
        LOG.debug(paymentsResponse);

        return paymentsResponse;
    }

    @Override
    public String getOriginKey(String originDomain) throws ApiException, IOException {
        CheckoutUtility checkoutUtility = new CheckoutUtility(client);
        OriginKeysRequest originKeysRequest = new OriginKeysRequest();
        String originkey = "";
        ArrayList<String> originDomains = new ArrayList<>(Arrays.asList(originDomain));

        originKeysRequest.setOriginDomains(originDomains);
        LOG.debug(originKeysRequest);
        OriginKeysResponse originKeysResponse = checkoutUtility.originKeys(originKeysRequest);

        if (originKeysResponse != null && originKeysResponse.getOriginKeys() != null) {
            originkey = originKeysResponse.getOriginKeys().get(originDomain);
        }
        return originkey;
    }

    @Override
    public String getHppEndpoint() {
        return config.getHppEndpoint();
    }

    @Override
    public String getDeviceFingerprintUrl() {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date today = Calendar.getInstance().getTime();
        return "https://live.adyen.com/hpp/js/df.js?v=" + df.format(today);
    }

    /**
     * Send POS Payment Request using Adyen Terminal API
     */
    @Override
    public TerminalAPIResponse sendSyncPosPaymentRequest(CartData cartData, CustomerModel customer, String serviceId) throws Exception {
        TerminalCloudAPI terminalCloudAPI = new TerminalCloudAPI(posClient);

        RecurringContractMode recurringContractMode = getBaseStore().getAdyenPosRecurringContractMode();
        TerminalAPIRequest terminalApiRequest = adyenRequestFactory.createTerminalAPIRequest(cartData, customer, recurringContractMode, serviceId);

        LOG.debug(TerminalAPIGsonBuilder.create().toJson(terminalApiRequest));
        TerminalAPIResponse terminalApiResponse = terminalCloudAPI.sync(terminalApiRequest);

        LOG.debug(TerminalAPIGsonBuilder.create().toJson(terminalApiResponse));
        return terminalApiResponse;
    }

    /**
     * Send POS Status Request using Adyen Terminal API
     */
    @Override
    public TerminalAPIResponse sendSyncPosStatusRequest(CartData cartData, String originalServiceId) throws Exception {
        TerminalCloudAPI terminalCloudAPI = new TerminalCloudAPI(posClient);

        TerminalAPIRequest terminalApiRequest = adyenRequestFactory.createTerminalAPIRequestForStatus(cartData, originalServiceId);

        LOG.debug(TerminalAPIGsonBuilder.create().toJson(terminalApiRequest));
        TerminalAPIResponse terminalApiResponse = terminalCloudAPI.sync(terminalApiRequest);

        LOG.debug(TerminalAPIGsonBuilder.create().toJson(terminalApiResponse));
        return terminalApiResponse;
    }

    public AdyenRequestFactory getAdyenRequestFactory() {
        return adyenRequestFactory;
    }

    public void setAdyenRequestFactory(AdyenRequestFactory adyenRequestFactory) {
        this.adyenRequestFactory = adyenRequestFactory;
    }

    public BaseStoreModel getBaseStore() {
        return baseStore;
    }

    public void setBaseStore(BaseStoreModel baseStore) {
        this.baseStore = baseStore;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Config getConfig() {
        return config;
    }

    public PaymentMethodConverter getPaymentMethodConverter() {
        return paymentMethodConverter;
    }

    public void setPaymentMethodConverter(PaymentMethodConverter paymentMethodConverter) {
        this.paymentMethodConverter = paymentMethodConverter;
    }
}
