package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.CardDetails;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;

public class GiftcardPaymentMethodDetailsBuilderStrategy implements AdyenPaymentMethodDetailsBuilderStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(final CartData cartData) {
        final String cartPaymentMethod = cartData.getAdyenPaymentMethod();

        return CardDetails.GIFTCARD.equals(cartPaymentMethod);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentMethodDetails buildPaymentMethodDetails(final CartData cartData) {

        return new CardDetails()
                .type(CardDetails.GIFTCARD)
                .brand(cartData.getAdyenGiftCardBrand());
    }

}