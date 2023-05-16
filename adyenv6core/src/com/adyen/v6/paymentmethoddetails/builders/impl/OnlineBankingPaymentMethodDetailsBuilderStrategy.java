package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.GenericIssuerPaymentMethodDetails;
import com.adyen.v6.constants.Adyenv6coreConstants;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * {@inheritDoc}
 */
public class OnlineBankingPaymentMethodDetailsBuilderStrategy implements AdyenPaymentMethodDetailsBuilderStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(final CartData cartData) {
        return GenericIssuerPaymentMethodDetails.ONLINEBANKING_IN.equals(cartData.getAdyenPaymentMethod()) || Adyenv6coreConstants.PAYMENT_METHOD_ONLINEBANKING_PL.equals(cartData.getAdyenPaymentMethod());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentMethodDetails buildPaymentMethodDetails(final CartData cartData) {
        return new GenericIssuerPaymentMethodDetails().type(cartData.getAdyenPaymentMethod()).issuer(cartData.getAdyenIssuerId());
    }
}
