package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.AfterpayDetails;
import com.adyen.model.checkout.details.KlarnaDetails;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * {@inheritDoc}
 */
public class AfterpayAdyenPaymentMethodDetailsBuilderStrategy implements AdyenPaymentMethodDetailsBuilderStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(final CartData cartData) {
        final String cartPaymentMethod = cartData.getAdyenPaymentMethod();

        return AfterpayDetails.AFTERPAY_DEFAULT.equals(cartPaymentMethod) ||
                AfterpayDetails.AFTERPAYTOUCH.equals(cartPaymentMethod);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentMethodDetails buildPaymentMethodDetails(final CartData cartData) {

        return new AfterpayDetails()
                .type(cartData.getAdyenPaymentMethod())
                .personalDetails(getPersonalDetails(cartData));
    }

}
