package com.adyen.v6.events;

import com.adyen.v6.model.AdyenNotificationModel;

public class CaptureEvent extends AbstractNotificationEvent {
    public CaptureEvent(AdyenNotificationModel adyenNotificationModel) {
        super(adyenNotificationModel);
    }
}
