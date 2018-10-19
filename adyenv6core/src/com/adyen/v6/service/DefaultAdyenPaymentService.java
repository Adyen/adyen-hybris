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

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import com.adyen.enums.Environment;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import com.adyen.Client;
import com.adyen.Config;
import com.adyen.Util.Util;
import com.adyen.enums.Environment;
import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentRequest3d;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.PaymentMethod;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.modification.CancelOrRefundRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.model.modification.RefundRequest;
import com.adyen.model.recurring.DisableRequest;
import com.adyen.model.recurring.DisableResult;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.model.recurring.RecurringDetailsRequest;
import com.adyen.model.recurring.RecurringDetailsResult;
import com.adyen.service.Checkout;
import com.adyen.service.Modification;
import com.adyen.service.Payment;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.converters.PaymentMethodConverter;
import com.adyen.v6.factory.AdyenRequestFactory;
import com.adyen.v6.model.RequestInfo;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;

public class DefaultAdyenPaymentService implements AdyenPaymentService {
    private BaseStoreModel baseStore;
    private AdyenRequestFactory adyenRequestFactory;
    private Config config;
    private Client client;
    private PaymentMethodConverter paymentMethodConverter;

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

        Assert.notNull(merchantAccount);

        config = new Config();
        config.setApiKey(apiKey);
        config.setMerchantAccount(merchantAccount);
        config.setSkinCode(skinCode);
        config.setHmacKey(hmacKey);
        config.setApplicationName("Adyen Hybris v3.4.0");
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
    public PaymentsResponse authorisePayment(final CartData cartData, final RequestInfo requestInfo, final CustomerModel customerModel) throws Exception {
        Checkout checkout = new Checkout(client);

        PaymentsRequest paymentsRequest = getAdyenRequestFactory().createPaymentsRequest(client.getConfig().getMerchantAccount(),
                                                                                              cartData,
                                                                                              requestInfo,
                                                                                              customerModel,
                                                                                              baseStore.getAdyenRecurringContractMode());


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
        Checkout checkout = new Checkout(client);

        PaymentMethodsRequest request = new PaymentMethodsRequest();
        request.merchantAccount(client.getConfig().getMerchantAccount())
               .amount(Util.createAmount(amount, currency))
               .countryCode(countryCode);

        if(!StringUtils.isEmpty(shopperLocale)) {
            request.setShopperLocale(shopperLocale);
        }

        if(!StringUtils.isEmpty(shopperReference)) {
            request.setShopperReference(shopperReference);
        }

        LOG.debug(request);
        PaymentMethodsResponse response = checkout.paymentMethods(request);
        LOG.debug(response);

        return response.getPaymentMethods();
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
    public PaymentsResponse getPaymentDetailsFromPayload(final String payload) throws Exception {
        Checkout checkout = new Checkout(client);

        PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        paymentsDetailsRequest.setDetails(new HashMap<>());
        paymentsDetailsRequest.getDetails().put("payload", payload);

        LOG.debug(paymentsDetailsRequest);
        PaymentsResponse paymentsResponse = checkout.paymentsDetails(paymentsDetailsRequest);
        LOG.debug(paymentsResponse);

        return paymentsResponse;
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

    public AdyenRequestFactory getAdyenRequestFactory() {
        if (adyenRequestFactory == null) {
            adyenRequestFactory = new AdyenRequestFactory();
        }

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
