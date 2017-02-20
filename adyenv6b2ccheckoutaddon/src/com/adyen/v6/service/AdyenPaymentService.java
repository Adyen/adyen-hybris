package com.adyen.v6.service;

import com.adyen.Client;
import com.adyen.Config;
import com.adyen.Util.Util;
import com.adyen.enums.Environment;
import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.*;
import com.adyen.model.hpp.DirectoryLookupRequest;
import com.adyen.model.hpp.PaymentMethod;
import com.adyen.model.modification.CancelRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.model.modification.RefundRequest;
import com.adyen.service.HostedPaymentPages;
import com.adyen.service.Modification;
import com.adyen.service.Payment;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Currency;
import java.util.List;

import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.*;

//TODO: implement an interface
public class AdyenPaymentService extends DefaultPaymentServiceImpl {
    private String merchantAccount;
    private ConfigurationService configurationService;
    private static final Logger LOG = Logger.getLogger(AdyenPaymentService.class);

    /**
     * Returns a Client
     * It is not a singleton, so that it can support dynamic credential changes
     *
     * @return
     */
    private Client createClient() {
        final Configuration configuration = getConfigurationService().getConfiguration();

        String username = configuration.getString(WS_USERNAME);
        String password = configuration.getString(WS_PASSWORD);
        merchantAccount = configuration.getString(CONFIG_MERCHANT_ACCOUNT);
        String skinCode = configuration.getString(CONFIG_SKIN_CODE);
        String hmacKey = configuration.getString(CONFIG_SKIN_HMAC);

        Config config = new Config();
        config.setUsername(username);
        config.setPassword(password);
        config.setMerchantAccount(merchantAccount);
        config.setSkinCode(skinCode);
        config.setHmacKey(hmacKey);
        config.setApplicationName("Hybris v6.0");

        Client client = new Client(config);
        client.setEnvironment(Environment.TEST);

        return client;
    }

    public PaymentResult authorise(final CartData cartData, final HttpServletRequest request) throws Exception {
        Client client = createClient();
        Payment payment = new Payment(client);

        String amount = cartData.getTotalPrice().getValue().toString();
        String currency = cartData.getTotalPrice().getCurrencyIso();
        String reference = cartData.getCode();
        String cseToken = cartData.getAdyenCseToken();

        PaymentRequest paymentRequest = createBasePaymentRequest(new PaymentRequest(), request)
                .reference(reference)
                .setAmountData(
                        amount,
                        currency
                )
                .setCSEToken(cseToken);

        return payment.authorise(paymentRequest);
    }

    public PaymentResult authorise3D(final HttpServletRequest request,
                                     final String paRes,
                                     final String md) throws Exception {
        Client client = createClient();
        Payment payment = new Payment(client);

        PaymentRequest3d paymentRequest3d = createBasePaymentRequest(new PaymentRequest3d(), request)
                .set3DRequestData(md, paRes);

        LOG.info(paymentRequest3d); //TODO: anonymize
        PaymentResult paymentResult = payment.authorise3D(paymentRequest3d);
        LOG.info(paymentResult);

        return paymentResult;
    }

    private <T extends AbstractPaymentRequest> T createBasePaymentRequest(
            T abstractPaymentRequest,
            final HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String acceptHeader = request.getHeader("Accept");
        String shopperIP = request.getRemoteAddr();

        abstractPaymentRequest
                .merchantAccount(merchantAccount)
                .setBrowserInfoData(
                        userAgent,
                        acceptHeader
                )
                .shopperIP(shopperIP);

        return abstractPaymentRequest;
    }

    /**
     * Performs Capture request
     *
     * @param amount
     * @param currency
     * @param authReference
     * @param merchantReference
     * @return
     * @throws Exception
     */
    public ModificationResult capture(final BigDecimal amount,
                                      final Currency currency,
                                      final String authReference,
                                      final String merchantReference) throws Exception {
        Client client = createClient();
        Modification modification = new Modification(client);

        final CaptureRequest captureRequest = new CaptureRequest()
                .fillAmount(amount.toString(), currency.getCurrencyCode())
                .merchantAccount(merchantAccount)
                .originalReference(authReference)
                .reference(merchantReference);

        LOG.info(captureRequest);
        ModificationResult modificationResult = modification.capture(captureRequest);
        LOG.info(modificationResult);

        return modificationResult;
    }

    /**
     * Performs cancelOrRefund request
     *
     * @param authReference
     * @param merchantReference
     * @return
     * @throws Exception
     */
    public ModificationResult cancelOrRefund(final String authReference, final String merchantReference) throws Exception {
        Client client = createClient();
        Modification modification = new Modification(client);

        final CancelRequest cancelRequest = new CancelRequest()
                .merchantAccount(merchantAccount)
                .originalReference(authReference)
                .reference(merchantReference);

        LOG.info(cancelRequest);
        ModificationResult modificationResult = modification.cancelOrRefund(cancelRequest);
        LOG.info(modificationResult);

        return modificationResult;
    }

    /**
     * Performs refund request
     *
     * @param amount
     * @param currency
     * @param authReference
     * @param merchantReference
     * @return
     * @throws Exception
     */
    public ModificationResult refund(final BigDecimal amount,
                                     final Currency currency,
                                     final String authReference,
                                     final String merchantReference) throws Exception {
        Client client = createClient();
        Modification modification = new Modification(client);

        final RefundRequest refundRequest = new RefundRequest()
                .fillAmount(amount.toString(), currency.getCurrencyCode())
                .merchantAccount(merchantAccount)
                .originalReference(authReference)
                .reference(merchantReference);

        LOG.info(refundRequest);
        ModificationResult modificationResult = modification.refund(refundRequest);
        LOG.info(modificationResult);

        return modificationResult;
    }

    /**
     * Get Payment methods using HPP Directory Lookup
     *
     * @param amount
     * @param currency
     * @param countryCode
     * @return
     * @throws HTTPClientException
     * @throws SignatureException
     * @throws IOException
     */
    public List<PaymentMethod> getPaymentMethods(final BigDecimal amount,
                                                 final String currency,
                                                 final String countryCode) throws HTTPClientException, SignatureException, IOException {
        Amount amountData = Util.createAmount(amount.toString(), currency);
        DirectoryLookupRequest directoryLookupRequest = new DirectoryLookupRequest()
                .setCountryCode(countryCode)
                .setMerchantReference("GetPaymentMethods")
                .setPaymentAmount(amountData.getValue().toString())
                .setCurrencyCode(amountData.getCurrency());

        LOG.info(directoryLookupRequest);

        Client client = createClient();
        HostedPaymentPages hostedPaymentPages = new HostedPaymentPages(client);

        List<PaymentMethod> paymentMethods = hostedPaymentPages.getPaymentMethods(directoryLookupRequest);
        LOG.info(paymentMethods);

        return paymentMethods;
    }

    private ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
