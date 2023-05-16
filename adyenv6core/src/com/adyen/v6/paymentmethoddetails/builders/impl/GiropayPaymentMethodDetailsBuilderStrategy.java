package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.GiropayDetails;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * {@inheritDoc}
 */
public class GiropayPaymentMethodDetailsBuilderStrategy implements AdyenPaymentMethodDetailsBuilderStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(final CartData cartData) {
        return GiropayDetails.GIROPAY.equals(cartData.getAdyenPaymentMethod());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentMethodDetails buildPaymentMethodDetails(final CartData cartData) {
        return new GiropayDetails();
    }
}
