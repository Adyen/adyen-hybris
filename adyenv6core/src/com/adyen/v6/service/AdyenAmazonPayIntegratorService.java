package com.adyen.v6.service;

/**
 * Service which takes care of the interaction with amazonpay API
 */
public interface AdyenAmazonPayIntegratorService {

    /**
     * Get the amazonpayToken from the amazon API reaching the checkoutSession endpoint
     *
     * @param checkoutSessionId The checkoutSessionId provided from Adyen
     * @return the amazonPayToken associated to the given checkoutSessionId
     */
    String getAmazonPayTokenByCheckoutSessionId(final String checkoutSessionId);
}
