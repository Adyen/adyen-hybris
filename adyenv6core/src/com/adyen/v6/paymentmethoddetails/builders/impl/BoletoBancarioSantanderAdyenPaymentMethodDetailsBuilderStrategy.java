package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.constants.ApiConstants;
import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.CardDetails;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * {@inheritDoc}
 */
public class BoletoBancarioSantanderAdyenPaymentMethodDetailsBuilderStrategy implements AdyenPaymentMethodDetailsBuilderStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(final CartData cartData) {
        final String cartPaymentMethod = cartData.getAdyenPaymentMethod();
        return ApiConstants.SelectedBrand.BOLETO_SANTANDER.equals(cartPaymentMethod);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentMethodDetails buildPaymentMethodDetails(final CartData cartData) {
        return new CardDetails()
                .type(ApiConstants.SelectedBrand.BOLETO_SANTANDER);
    }
}
