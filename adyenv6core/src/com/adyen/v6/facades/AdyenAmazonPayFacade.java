package com.adyen.v6.facades;

/**
 * Facade responsible for any direct amazonpay interaction logic
 */
public interface AdyenAmazonPayFacade {

    /**
     * It gets the amazonpay Token given an already created checkout session id
     * @param amazonpayCheckoutSessionId the previously created checkout session
     * @return the amazonPayToken related with the amazonPay session
     */
    String getAmazonPayToken(final String amazonpayCheckoutSessionId);

    /**
     * Resolves the url for amazon pay controller by site
     * @param url the url
     * @return the complete url
     */
    String getReturnUrl(final String url);
}
