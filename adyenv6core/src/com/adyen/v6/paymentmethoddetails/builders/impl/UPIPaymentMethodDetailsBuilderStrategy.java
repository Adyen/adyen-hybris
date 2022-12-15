package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.UpiCollectDetails;
import com.adyen.model.checkout.details.UpiDetails;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * {@inheritDoc}
 */
public class UPIPaymentMethodDetailsBuilderStrategy implements AdyenPaymentMethodDetailsBuilderStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(final CartData cartData) {
        return cartData.getAdyenPaymentMethod().contains(UpiDetails.UPI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentMethodDetails buildPaymentMethodDetails(final CartData cartData) {
        return new UpiCollectDetails().virtualPaymentAddress(cartData.getAdyenUPIVirtualAddress());
    }
}
