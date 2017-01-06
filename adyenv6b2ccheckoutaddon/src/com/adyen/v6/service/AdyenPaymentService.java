package com.adyen.v6.service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.AbstractPaymentRequest;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentRequest3d;
import com.adyen.model.PaymentResult;
import com.adyen.service.Payment;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;

import javax.servlet.http.HttpServletRequest;

//TODO: implement an interface
public class AdyenPaymentService extends DefaultPaymentServiceImpl {
    private String merchantAccount;
    private ConfigurationService configurationService;

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

        System.out.println(paymentRequest3d);
        return payment.authorise3D(paymentRequest3d);
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

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
