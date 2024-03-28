package com.adyen.v6.events;

import com.adyen.v6.model.AdyenNotificationModel;

public class CancelOrRefundEvent extends AbstractNotificationEvent {
    public CancelOrRefundEvent(AdyenNotificationModel adyenNotificationModel) {
        super(adyenNotificationModel);
    }
}
