package com.adyen.v6.events.builder;

import com.adyen.v6.events.AbstractNotificationEvent;
import com.adyen.v6.events.OfferClosedEvent;
import com.adyen.v6.model.AdyenNotificationModel;

public class OfferClosedEventBuilder extends AbstractNotificationEventBuilder {
    @Override
    public AbstractNotificationEvent buildEvent(AdyenNotificationModel adyenNotificationModel) {
        return new OfferClosedEvent(adyenNotificationModel);
    }
}
