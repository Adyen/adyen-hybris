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
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.*;
import com.adyen.model.modification.CancelOrRefundRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.model.modification.RefundRequest;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.model.recurring.*;
import com.adyen.model.terminal.ConnectedTerminalsRequest;
import com.adyen.model.terminal.ConnectedTerminalsResponse;
import com.adyen.model.terminal.TerminalAPIRequest;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.*;
import com.adyen.service.exception.ApiException;
import com.adyen.terminal.serialization.TerminalAPIGsonBuilder;
import com.adyen.util.Util;
import com.adyen.v6.enums.AdyenRegions;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.factory.AdyenRequestFactory;
import com.adyen.v6.model.RequestInfo;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.adyen.v6.constants.Adyenv6coreConstants.PLUGIN_NAME;
import static com.adyen.v6.constants.Adyenv6coreConstants.PLUGIN_VERSION;

public class DefaultAdyenPaymentService implements AdyenPaymentService {

    private static final Logger LOG = Logger.getLogger(DefaultAdyenPaymentService.class);
    private static final int POS_REQUEST_TIMEOUT = 25000;
    private static final String CHECKOUT_ENDPOINT_LIVE_IN_SUFFIX = "-checkout-live-in.adyenpayments.com/checkout";

    private BaseStoreModel baseStore;
    private AdyenRequestFactory adyenRequestFactory;
    private Config config;
    private Client client;
    private Config posConfig;
    private Client posClient;

    /**
     * Prevent initialization without base store
     */
    private DefaultAdyenPaymentService() {
    }

    public DefaultAdyenPaymentService(final BaseStoreModel baseStore) {
        this.baseStore = baseStore;

        if (Boolean.TRUE.equals(baseStore.getAdyenPosEnabled())) {
            posConfig = new Config();
            posConfig.setApiKey(baseStore.getAdyenPosApiKey());
            posConfig.setMerchantAccount(baseStore.getAdyenPosMerchantAccount());
            posConfig.setReadTimeoutMillis(POS_REQUEST_TIMEOUT);
            posConfig.setApplicationName(PLUGIN_NAME + " v" + PLUGIN_VERSION);
            posClient = new Client(posConfig);

            if (Boolean.TRUE.equals(baseStore.getAdyenTestMode())) {
                posClient.setEnvironment(Environment.TEST, null);
            } else {
                posClient.setEnvironment(Environment.LIVE, null);
            }
        }

        config = new Config();
        config.setApiKey(baseStore.getAdyenAPIKey());
        config.setMerchantAccount(baseStore.getAdyenMerchantAccount());
        config.setApplicationName(PLUGIN_NAME + " v" + PLUGIN_VERSION);
        client = new Client(config);

        if (Boolean.TRUE.equals(baseStore.getAdyenTestMode())) {
            client.setEnvironment(Environment.TEST, null);
        } else {
            createLiveEnvironment(baseStore);
        }

    }

    private void createLiveEnvironment(final BaseStoreModel baseStore) {

        this.config.setEnvironment(Environment.LIVE);
        this.config.setMarketPayEndpoint(Client.MARKETPAY_ENDPOINT_LIVE);
        this.config.setHppEndpoint(Client.HPP_LIVE);
        this.config.setCheckoutEndpoint(Client.ENDPOINT_PROTOCOL + baseStore.getAdyenAPIEndpointPrefix() + Client.CHECKOUT_ENDPOINT_LIVE_SUFFIX);
        this.config.setEndpoint(Client.ENDPOINT_PROTOCOL + baseStore.getAdyenAPIEndpointPrefix() + Client.ENDPOINT_LIVE_SUFFIX);
        this.config.setTerminalApiCloudEndpoint(Client.TERMINAL_API_ENDPOINT_LIVE);
        this.config.setPosTerminalManagementApiEndpoint(Client.POS_TERMINAL_MANAGEMENT_ENDPOINT_LIVE);
        this.config.setDataProtectionEndpoint(Client.DATA_PROTECTION_ENDPOINT_LIVE);
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
    public ConnectedTerminalsResponse getConnectedTerminals() throws IOException, ApiException {
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
    public PaymentsDetailsResponse authorise3DSPayment(Map<String, String> details) throws Exception {
        Checkout checkout = new Checkout(client);
        PaymentsDetailsRequest paymentsDetailsRequest = getAdyenRequestFactory().create3DSPaymentsRequest(details);

        LOG.debug(paymentsDetailsRequest);
        PaymentsDetailsResponse paymentsDetailsResponse = checkout.paymentsDetails(paymentsDetailsRequest);
        LOG.debug(paymentsDetailsResponse);

        return paymentsDetailsResponse;
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
    public PaymentCaptureResource captures(final BigDecimal amount, final Currency currency, final String authReference, final String merchantReference) throws Exception {
        final Checkout checkout = new Checkout(client);

        final CreatePaymentCaptureRequest captureRequest = new CreatePaymentCaptureRequest();
        captureRequest.setAmount(Util.createAmount(amount, currency.getCurrencyCode()));
        captureRequest.setReference(merchantReference);
        captureRequest.setMerchantAccount(client.getConfig().getMerchantAccount());

        LOG.debug(captureRequest);
        final PaymentCaptureResource paymentCaptureResource = checkout.paymentsCaptures(authReference, captureRequest);
        LOG.debug(paymentCaptureResource);

        return paymentCaptureResource;
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
    public PaymentReversalResource cancelOrRefunds(final String authReference, final String merchantReference) throws Exception {
        final Checkout checkout = new Checkout(client);

        final CreatePaymentReversalRequest reversalRequest = new CreatePaymentReversalRequest();
        reversalRequest.setReference(merchantReference);
        reversalRequest.setMerchantAccount(client.getConfig().getMerchantAccount());

        LOG.debug(reversalRequest);
        final PaymentReversalResource paymentReversalResource = checkout.paymentsReversals(authReference, reversalRequest);
        LOG.debug(paymentReversalResource);

        return paymentReversalResource;
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
    public PaymentRefundResource refunds(final BigDecimal amount, final Currency currency, final String pspReference, final String reference) throws Exception {
        final Checkout checkout = new Checkout(client);

        final CreatePaymentRefundRequest refundRequest = new CreatePaymentRefundRequest();
        refundRequest.setAmount(Util.createAmount(amount, currency.getCurrencyCode()));
        refundRequest.setMerchantAccount(client.getConfig().getMerchantAccount());
        refundRequest.setReference(reference);

        LOG.debug(refundRequest);
        final PaymentRefundResource paymentRefundResource = checkout.paymentsRefunds(pspReference, refundRequest);
        LOG.debug(paymentRefundResource);

        return paymentRefundResource;
    }


    @Override
    public List<PaymentMethod> getPaymentMethods(final BigDecimal amount,
                                                 final String currency,
                                                 final String countryCode,
                                                 final String shopperLocale,
                                                 final String shopperReference) throws IOException, ApiException {

        final PaymentMethodsResponse response = getPaymentMethodsResponse(amount, currency, countryCode, shopperLocale, shopperReference);
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

        if (!StringUtils.isEmpty(shopperLocale)) {
            request.setShopperLocale(shopperLocale);
        }

        if (!StringUtils.isEmpty(shopperReference)) {
            request.setShopperReference(shopperReference);
        }

        LOG.debug(request);
        final PaymentMethodsResponse response = checkout.paymentMethods(request);
        LOG.debug(response);

        return response;
    }

    @Override
    @Deprecated
    public List<PaymentMethod> getPaymentMethods(final BigDecimal amount,
                                                 final String currency,
                                                 final String countryCode,
                                                 final String shopperLocale) throws IOException {
        try {
            return getPaymentMethods(amount, currency, countryCode, shopperLocale, null);
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
    public PaymentsDetailsResponse getPaymentDetailsFromPayload(HashMap<String, String> details) throws Exception {
        Checkout checkout = new Checkout(client);

        PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        paymentsDetailsRequest.setDetails(details);

        LOG.debug(paymentsDetailsRequest);
        PaymentsDetailsResponse paymentsResponse = checkout.paymentsDetails(paymentsDetailsRequest);
        LOG.debug(paymentsResponse);

        return paymentsResponse;
    }

    @Override
    public PaymentsDetailsResponse getPaymentDetailsFromPayload(Map<String, String> details, String paymentData) throws Exception {
        Checkout checkout = new Checkout(client);
        PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        paymentsDetailsRequest.setDetails(details);
        paymentsDetailsRequest.setPaymentData(paymentData);

        LOG.debug(paymentsDetailsRequest);
        PaymentsDetailsResponse paymentsResponse = checkout.paymentsDetails(paymentsDetailsRequest);
        LOG.debug(paymentsResponse);

        return paymentsResponse;
    }


    @Override
    public CreateCheckoutSessionResponse getPaymentSessionData(final CartData cartData) throws IOException, ApiException {
        final Checkout checkout = new Checkout(client);
        final PriceData totalPriceWithTax = cartData.getTotalPriceWithTax();

        final CreateCheckoutSessionRequest createCheckoutSessionRequest = new CreateCheckoutSessionRequest();
        createCheckoutSessionRequest.amount(Util.createAmount(totalPriceWithTax.getValue(), totalPriceWithTax.getCurrencyIso()));
        createCheckoutSessionRequest.merchantAccount(getBaseStore().getAdyenMerchantAccount());
        createCheckoutSessionRequest.countryCode(cartData.getDeliveryAddress().getCountry().getIsocode());
        createCheckoutSessionRequest.returnUrl(Optional.ofNullable(cartData.getAdyenReturnUrl()).orElse("returnUrl"));
        createCheckoutSessionRequest.reference(cartData.getCode());

        return  checkout.sessions(createCheckoutSessionRequest);
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

    @Override
    public BigDecimal calculateAmountWithTaxes(final AbstractOrderModel abstractOrderModel) {
        final Double totalPrice = abstractOrderModel.getTotalPrice();
        final Double totalTax = Boolean.TRUE.equals(abstractOrderModel.getNet()) ? abstractOrderModel.getTotalTax() : Double.valueOf(0d);
        final BigDecimal totalPriceWithoutTaxBD = BigDecimal.valueOf(totalPrice == null ? 0d : totalPrice).setScale(2,
                RoundingMode.HALF_EVEN);
        return BigDecimal.valueOf(totalTax == null ? 0d : totalTax)
                .setScale(2, RoundingMode.HALF_EVEN).add(totalPriceWithoutTaxBD);
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

}
