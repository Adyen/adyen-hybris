package com.adyen.v6.events;

import com.adyen.v6.model.AdyenNotificationModel;

public class AuthorisationEvent extends AbstractNotificationEvent {
    public AuthorisationEvent(AdyenNotificationModel adyenNotificationModel) {
        super(adyenNotificationModel);
    }
}
