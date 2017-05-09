package com.adyen.v6.service;

import de.hybris.platform.core.model.order.OrderModel;

public interface AdyenBusinessProcessService {
    /**
     * Trigger order-process event
     */
    void triggerOrderProcessEvent(OrderModel orderModel, String event);

    /**
     * Trigger return-process event
     */
    void triggerReturnProcessEvent(OrderModel orderModel, String event);
}
