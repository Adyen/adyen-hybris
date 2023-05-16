package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.MolPayDetails;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * {@inheritDoc}
 */
public class MolpayPaymentMethodDetailsBuilderStrategy implements AdyenPaymentMethodDetailsBuilderStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(final CartData cartData) {
        return MolPayDetails.EBANKING_TH.equals(cartData.getAdyenPaymentMethod()) ||
                MolPayDetails.EBANKING_MY.equals(cartData.getAdyenPaymentMethod()) ||
                MolPayDetails.EBANKING_VN.equals(cartData.getAdyenPaymentMethod()) ||
                MolPayDetails.EBANKING_FPX_MY.equals(cartData.getAdyenPaymentMethod()) ||
                MolPayDetails.EBANKING_DIRECT_MY.equals(cartData.getAdyenPaymentMethod());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentMethodDetails buildPaymentMethodDetails(final CartData cartData) {
        return new MolPayDetails().type(cartData.getAdyenPaymentMethod());
    }
}
