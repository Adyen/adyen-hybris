package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.GenericIssuerPaymentMethodDetails;
import com.adyen.model.checkout.details.PayUUpiDetails;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * {@inheritDoc}
 */
public class WalletsIndiaPaymentMethodDetailsBuilderStrategy implements AdyenPaymentMethodDetailsBuilderStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(final CartData cartData) {
        return GenericIssuerPaymentMethodDetails.WALLET_IN.equals(cartData.getAdyenPaymentMethod());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentMethodDetails buildPaymentMethodDetails(final CartData cartData) {
        return new GenericIssuerPaymentMethodDetails().type(cartData.getAdyenPaymentMethod()).issuer(cartData.getAdyenIssuerId());
    }
}
