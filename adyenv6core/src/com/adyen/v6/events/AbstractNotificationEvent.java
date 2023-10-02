package com.adyen.v6.events;


import com.adyen.v6.model.AdyenNotificationModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public abstract class AbstractNotificationEvent extends AbstractEvent {

    private final AdyenNotificationModel adyenNotificationModel;

    protected AbstractNotificationEvent(AdyenNotificationModel adyenNotificationModel) {
        this.adyenNotificationModel = adyenNotificationModel;
    }

    public AdyenNotificationModel getNotificationRequestItem() {
        return adyenNotificationModel;
    }
}
