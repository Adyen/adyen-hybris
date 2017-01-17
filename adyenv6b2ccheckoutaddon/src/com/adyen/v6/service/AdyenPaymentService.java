package com.adyen.v6.service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.*;
import com.adyen.service.Modification;
import com.adyen.service.Payment;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Currency;

//TODO: implement an interface
public class AdyenPaymentService extends DefaultPaymentServiceImpl {
    private String merchantAccount;
    private ConfigurationService configurationService;
    private static final Logger LOG = Logger.getLogger(AdyenPaymentService.class);

    //TODO: move to constants class?
    private static final String WS_USERNAME = "adyen.ws.username";
    private static final String WS_PASSWORD = "adyen.ws.password";
    private static final String MERCHANT_ACCOUNT = "adyen.merchantaccount";

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
        merchantAccount = configuration.getString(MERCHANT_ACCOUNT);

        return new Client(
                username,
                password,
                Environment.TEST,
                "Hybris v6.0"
        );
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
                .setModificationAmountData(amount.toString(), currency.getCurrencyCode())
                .merchantAccount(merchantAccount)
                .originalReference(authReference)
                .reference(merchantReference);

        LOG.info(captureRequest);
        ModificationResult modificationResult = modification.capture(captureRequest);
        LOG.info(modificationResult);
        return modificationResult;
    }

    private ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
