package com.adyen.v6.events;

import com.adyen.v6.model.AdyenNotificationModel;

public class OfferClosedEvent extends AbstractNotificationEvent {
    public OfferClosedEvent(AdyenNotificationModel adyenNotificationModel) {
        super(adyenNotificationModel);
    }
}
