package com.adyen.v6.acceleratorfacades.flow.impl;

import de.hybris.platform.acceleratorfacades.flow.impl.DefaultCheckoutFlowFacade;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * Created by georgios on 30/12/16.
 */
public class AdyenCheckoutFlowFacade extends DefaultCheckoutFlowFacade
{
    @Override
    public CartData getCheckoutCart()
    {
        final CartData cartData = getCartFacade().getSessionCart();
        if (cartData != null)
        {
            cartData.setDeliveryAddress(getDeliveryAddress());
            cartData.setDeliveryMode(getDeliveryMode());
        }

        return cartData;
    }

    @Override
    public boolean hasNoPaymentInfo()
    {
        final CartData cartData = getCheckoutCart();
        return cartData == null || cartData.getPaymentInfo() == null;
    }
}
