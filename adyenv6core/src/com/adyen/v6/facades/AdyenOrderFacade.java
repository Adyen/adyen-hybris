package com.adyen.v6.facades;

public interface AdyenOrderFacade {
    String getPaymentStatus(final String orderCode, final String sessionGuid);
    String getOrderCodeForGUID(final String orderGUID, final String sessionGuid);
}