package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.CardDetails;
import com.adyen.v6.constants.Adyenv6coreConstants;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * {@inheritDoc}
 */
public class CreditCardAdyenPaymentMethodDetailsBuilderStrategy implements AdyenPaymentMethodDetailsBuilderStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(final CartData cartData) {
        final String cartPaymentMethod = cartData.getAdyenPaymentMethod();
        return Adyenv6coreConstants.PAYMENT_METHOD_CC.equals(cartPaymentMethod) ||
                CardDetails.CARD.equals(cartPaymentMethod);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentMethodDetails buildPaymentMethodDetails(final CartData cartData) {
        return new CardDetails()
                .type(CardDetails.CARD)
                .brand(cartData.getAdyenCardBrand());
    }
}
