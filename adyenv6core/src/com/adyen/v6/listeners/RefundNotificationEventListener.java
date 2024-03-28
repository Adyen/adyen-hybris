package com.adyen.v6.listeners;

import com.adyen.v6.events.RefundEvent;
import com.adyen.v6.model.AdyenNotificationModel;
import org.apache.log4j.Logger;

import java.util.Date;

public class RefundNotificationEventListener extends AbstractNotificationEventListener<RefundEvent> {


    private static final Logger LOG = Logger.getLogger(RefundNotificationEventListener.class);

    public RefundNotificationEventListener() {
        super();
    }

    @Override
    protected void onEvent(final RefundEvent event) {
        AdyenNotificationModel notificationInfoModel = event.getNotificationRequestItem();
        try {
            getAdyenNotificationService().processRefundEvent(notificationInfoModel);
            LOG.info("Refund notification with PSPReference " + notificationInfoModel.getPspReference() + " was processed");
            notificationInfoModel.setProcessedAt(new Date());
            getModelService().save(notificationInfoModel);
        }catch (Exception e) {
            logException(notificationInfoModel, e, LOG);
        }
    }

}
