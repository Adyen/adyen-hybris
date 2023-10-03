package com.adyen.v6.events.builder;

import com.adyen.v6.events.AbstractNotificationEvent;
import com.adyen.v6.events.AuthorisationEvent;
import com.adyen.v6.model.AdyenNotificationModel;

public class AuthorisationEventBuilder extends AbstractNotificationEventBuilder {
    @Override
    public AbstractNotificationEvent buildEvent(AdyenNotificationModel adyenNotificationModel) {
        return new AuthorisationEvent(adyenNotificationModel);
    }
}
