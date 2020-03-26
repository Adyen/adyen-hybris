/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.acceleratorfacades.flow.impl;

import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.util.TerminalAPIUtil;
import de.hybris.platform.acceleratorfacades.flow.impl.DefaultCheckoutFlowFacade;
import de.hybris.platform.cmsfacades.common.service.SearchResultConverter;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

public class AdyenCheckoutFlowFacade extends DefaultCheckoutFlowFacade
{
    private static final Logger LOGGER = Logger.getLogger(AdyenCheckoutFlowFacade.class);
    private AdyenCheckoutFacade adyenCheckoutFacade;
    private Converter<CartModel, CartData> cartConverter;


    @Override
    public CartData getCheckoutCart()
    {
        try {
           CartModel cartModel =  getAdyenCheckoutFacade().restoreSessionCart();
           return getCartConverter().convert(cartModel);
        } catch (InvalidCartException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

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

    public Converter<CartModel, CartData> getCartConverter() {
        return cartConverter;
    }

    public void setCartConverter(Converter<CartModel, CartData> cartConverter) {
        this.cartConverter = cartConverter;
    }

    public AdyenCheckoutFacade getAdyenCheckoutFacade() {
        return adyenCheckoutFacade;
    }

    public void setAdyenCheckoutFacade(AdyenCheckoutFacade adyenCheckoutFacade) {
        this.adyenCheckoutFacade = adyenCheckoutFacade;
    }
}
