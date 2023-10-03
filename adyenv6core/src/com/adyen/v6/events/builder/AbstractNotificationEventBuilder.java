package com.adyen.v6.events.builder;

import com.adyen.v6.events.AbstractNotificationEvent;
import com.adyen.v6.model.AdyenNotificationModel;

public abstract class AbstractNotificationEventBuilder {

    public abstract AbstractNotificationEvent buildEvent(AdyenNotificationModel adyenNotificationModel);
}
