package com.adyen.v6.populators;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.BooleanUtils;

public class AdyenSubscriptionOrderPopulator extends AbstractOrderPopulator<OrderModel, OrderData> {
    @Override
    public void populate(OrderModel orderModel, OrderData orderData) throws ConversionException {

        orderData.setSubscriptionOrder(BooleanUtils.isTrue(orderModel.getSubscriptionOrder()));
    }
}
