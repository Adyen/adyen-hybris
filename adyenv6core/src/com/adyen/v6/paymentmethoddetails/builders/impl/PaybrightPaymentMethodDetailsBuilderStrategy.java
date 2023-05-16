package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.CardDetails;
import com.adyen.v6.constants.Adyenv6coreConstants;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * {@inheritDoc}
 */
public class PaybrightPaymentMethodDetailsBuilderStrategy implements AdyenPaymentMethodDetailsBuilderStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(final CartData cartData) {
        //TODO Paybright has a missing PaymentMethodDetails
        return Adyenv6coreConstants.PAYBRIGHT.equals(cartData.getAdyenPaymentMethod());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentMethodDetails buildPaymentMethodDetails(final CartData cartData) {
        //TODO Paybright has a missing PaymentMethodDetails
        return new CardDetails().type(cartData.getAdyenPaymentMethod());
    }
}
