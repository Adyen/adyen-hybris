package com.adyen.v6.events;

import com.adyen.v6.model.AdyenNotificationModel;

public class RefundEvent extends AbstractNotificationEvent {
    public RefundEvent(AdyenNotificationModel adyenNotificationModel) {
        super(adyenNotificationModel);
    }
}
