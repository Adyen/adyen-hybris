package com.adyen.v6.events.builder;

import com.adyen.v6.events.AbstractNotificationEvent;
import com.adyen.v6.events.CancelOrRefundEvent;
import com.adyen.v6.model.AdyenNotificationModel;

public class CancelOrRefundEventBuilder extends AbstractNotificationEventBuilder{
    @Override
    public AbstractNotificationEvent buildEvent(AdyenNotificationModel adyenNotificationModel) {
        return new CancelOrRefundEvent(adyenNotificationModel);
    }
}
