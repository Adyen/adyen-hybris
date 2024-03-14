package com.adyen.v6.facades;

import de.hybris.platform.commercefacades.order.data.OrderData;

public interface AdyenOrderFacade {
    String getPaymentStatus(final String orderCode, final Object sessionGuid);

}
