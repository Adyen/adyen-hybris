package com.adyen.v6.service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.service.Payment;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
    public Client createClient() {
        final Configuration configuration = getConfigurationService().getConfiguration();

        String username = configuration.getString(WS_USERNAME);
        String password = configuration.getString(WS_PASSWORD);
        merchantAccount = configuration.getString(MERCHANT_ACCOUNT);

        Client client = new Client(
                username,
                password,
                Environment.TEST,
                "Hybris v6.0"
        );

        return client;
    }

    public Map<String, Object> authorise3D(final CartData cartData,
                                           final HttpServletRequest request,
                                           final String paRes,
                                           final String md) throws Exception {
        Client client = createClient();
        Payment paymentRequest = new Payment(client);

        Map<String, Object> params = getAuthorisationParams(cartData, request);
        params.put("paResponse", paRes);
        params.put("md", md);

        return paymentRequest.authorise3D(params);
    }

    public Map<String, Object> authorise(final CartData cartData, final HttpServletRequest request) throws Exception {
        Client client = createClient();
        Payment paymentRequest = new Payment(client);

        Map<String, Object> params = getAuthorisationParams(cartData, request);

        return paymentRequest.authorise(params);
    }

    private Map<String, Object> getAuthorisationParams(final CartData cartData, final HttpServletRequest request)
    {
        String amountValue = new Integer(
                new BigDecimal(100)
                        .multiply(cartData.getTotalPrice().getValue())
                        .intValue())
                .toString();
        String amountCurrency = cartData.getTotalPrice().getCurrencyIso();
        String reference = cartData.getCode();
        String cseToken = cartData.getAdyenCseToken();
        String shopperIP = request.getRemoteAddr();

        //TODO: refactor client library
        Map<String, Object> params = new HashMap<String, Object>();

        Map<String, String> amountMap = new HashMap<String, String>();
        amountMap.put("currency", amountCurrency);
        amountMap.put("value", amountValue); // minor units!

        params.put("amount", amountMap);

        Map<String, String> additionalData = new HashMap<String, String>();
        additionalData.put("card.encrypted.json", cseToken);

        params.put("additionalData", additionalData);

        params.put("merchantAccount", merchantAccount);
        params.put("reference", reference);
        params.put("shopperIP", shopperIP);

        //3DS parameters
        String userAgent = request.getHeader("User-Agent");
        String acceptHeader = request.getHeader("Accept");

        Map<String, String> browserInfoMap = new HashMap<String, String>();
        browserInfoMap.put("userAgent", userAgent);
        browserInfoMap.put("acceptHeader", acceptHeader);

        params.put("browserInfo", browserInfoMap);

        return params;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
