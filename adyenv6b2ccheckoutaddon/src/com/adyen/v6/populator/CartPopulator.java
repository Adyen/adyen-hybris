package com.adyen.v6.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;

public class CartPopulator implements Populator<CartModel, CartData>{

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final CartModel source, final CartData target) throws ConversionException {
        target.setAdyenCseToken(source.getAdyenCseToken());
        target.setAdyenRememberTheseDetails(source.getAdyenRememberTheseDetails());
    }
}
