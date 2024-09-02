package com.adyen.v6.events.builder;

import com.adyen.v6.events.AbstractNotificationEvent;
import com.adyen.v6.events.CaptureEvent;
import com.adyen.v6.model.AdyenNotificationModel;

public class CaptureEventBuilder extends AbstractNotificationEventBuilder{
    @Override
    public AbstractNotificationEvent buildEvent(AdyenNotificationModel adyenNotificationModel) {
        return new CaptureEvent(adyenNotificationModel);
    }
}
