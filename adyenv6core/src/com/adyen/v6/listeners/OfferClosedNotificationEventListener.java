package com.adyen.v6.listeners;

import com.adyen.v6.events.OfferClosedEvent;
import com.adyen.v6.model.AdyenNotificationModel;
import org.apache.log4j.Logger;

import java.util.Date;

public class OfferClosedNotificationEventListener extends AbstractNotificationEventListener<OfferClosedEvent> {


    private static final Logger LOG = Logger.getLogger(OfferClosedNotificationEventListener.class);

    public OfferClosedNotificationEventListener() {
        super();
    }

    @Override
    protected void onEvent(final OfferClosedEvent event) {
        AdyenNotificationModel notificationInfoModel = event.getNotificationRequestItem();
        try {
            getAdyenNotificationService().processOfferClosedEvent(notificationInfoModel);
            LOG.info("Offer closed notification with PSPReference " + notificationInfoModel.getPspReference() + " was processed");
            notificationInfoModel.setProcessedAt(new Date());
            getModelService().save(notificationInfoModel);
        }catch (Exception e) {
            LOG.error("Notification with psp reference: " + notificationInfoModel.getPspReference() + " cause an exception. \n");
            LOG.error("Exception: ", e);
        }
    }

}
