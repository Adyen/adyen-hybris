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
import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentRequest3d;
import com.adyen.model.PaymentResult;
import com.adyen.model.hpp.DirectoryLookupRequest;
import com.adyen.model.hpp.PaymentMethod;
import com.adyen.model.modification.CancelOrRefundRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.model.modification.RefundRequest;
import com.adyen.model.recurring.*;
import com.adyen.service.HostedPaymentPages;
import com.adyen.service.Modification;
import com.adyen.service.Payment;
import com.adyen.service.exception.ApiException;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.adyen.Client.*;

public class DefaultAdyenPaymentService implements AdyenPaymentService {
    private BaseStoreModel baseStore;
    private AdyenRequestFactory adyenRequestFactory;
    private Config config;
    private Client client;

    private static final Logger LOG = Logger.getLogger(DefaultAdyenPaymentService.class);

    /**
     * Prevent initialization without base store
     */
    private DefaultAdyenPaymentService() {
    }

    public DefaultAdyenPaymentService(final BaseStoreModel baseStore) {
        this.baseStore = baseStore;

        String username = baseStore.getAdyenUsername();
        String password = baseStore.getAdyenPassword();
        String merchantAccount = baseStore.getAdyenMerchantAccount();
        String skinCode = baseStore.getAdyenSkinCode();
        String hmacKey = baseStore.getAdyenSkinHMAC();
        String apiEndpoint = baseStore.getAdyenAPIEndpoint();
        boolean isTestMode = baseStore.getAdyenTestMode();

        Assert.notNull(merchantAccount);

        config = new Config();
        config.setUsername(username);
        config.setPassword(password);
        config.setMerchantAccount(merchantAccount);
        config.setSkinCode(skinCode);
        config.setHmacKey(hmacKey);
        config.setApplicationName("Adyen Hybris v3.4.0");

        if (isTestMode) {
            config.setEndpoint(ENDPOINT_TEST);
            config.setHppEndpoint(HPP_TEST);
        } else {
            config.setEndpoint(ENDPOINT_LIVE);
            config.setHppEndpoint(HPP_LIVE);
        }

        //Use custom endpoint if set
        if (! StringUtils.isEmpty(apiEndpoint)) {
            config.setEndpoint(apiEndpoint);
        }

        client = new Client(config);
    }

    @Override
    public PaymentResult authorise(final CartData cartData, final RequestInfo requestInfo, final CustomerModel customerModel) throws Exception {
        Payment payment = new Payment(client);

        PaymentRequest paymentRequest = getAdyenRequestFactory().createAuthorizationRequest(client.getConfig().getMerchantAccount(),
                                                                                            cartData,
                                                                                            requestInfo,
                                                                                            customerModel,
                                                                                            baseStore.getAdyenRecurringContractMode());


        LOG.debug(paymentRequest);
        PaymentResult paymentResult = payment.authorise(paymentRequest);
        LOG.debug(paymentResult);

        return paymentResult;
    }

    @Override
    public PaymentResult authorise3D(final RequestInfo requestInfo, final String paRes, final String md) throws Exception {
        Payment payment = new Payment(client);

        PaymentRequest3d paymentRequest3d = getAdyenRequestFactory().create3DAuthorizationRequest(client.getConfig().getMerchantAccount(), requestInfo, md, paRes);

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
                                                 final String shopperLocale) throws HTTPClientException, SignatureException, IOException {
        if (client.getConfig().getSkinCode() == null || client.getConfig().getSkinCode().isEmpty()) {
            return new ArrayList<>();
        }

        HostedPaymentPages hostedPaymentPages = new HostedPaymentPages(client);

        DirectoryLookupRequest directoryLookupRequest = getAdyenRequestFactory().createListPaymentMethodsRequest(amount, currency, countryCode, shopperLocale);

        LOG.debug(directoryLookupRequest);
        List<PaymentMethod> paymentMethods = hostedPaymentPages.getPaymentMethods(directoryLookupRequest);
        LOG.debug(paymentMethods);

        return paymentMethods;
    }

    @Override
    public List<PaymentMethod> getPaymentMethods(final BigDecimal amount, final String currency, final String countryCode) throws HTTPClientException, SignatureException, IOException {
        return getPaymentMethods(amount, currency, countryCode, null);
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
}
