package com.adyen.v6.facades;

import de.hybris.platform.commercefacades.order.data.OrderData;

public interface AdyenOrderFacade {
    public String getPaymentStatus(final String orderCode, final Object sessionGuid);

    public OrderData getOrderDetailsForCode(final String code, final Object sessionGuid);
}
