package com.adyen.v6.listeners;

import com.adyen.v6.events.CaptureEvent;
import com.adyen.v6.model.AdyenNotificationModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.log4j.Logger;

import java.util.Date;

public class CaptureNotificationEventListener extends AbstractNotificationEventListener<CaptureEvent> {


    private static final Logger LOG = Logger.getLogger(CaptureNotificationEventListener.class);

    public CaptureNotificationEventListener() {
        super();
    }

    @Override
    protected void onEvent(final CaptureEvent event) {
        AdyenNotificationModel notificationInfoModel = event.getNotificationRequestItem();
        PaymentTransactionModel transactionModel = getPaymentTransactionRepository().getTransactionModel(notificationInfoModel.getOriginalReference());
        try {
            getAdyenNotificationService().processCapturedEvent(notificationInfoModel, transactionModel);
            LOG.info("Notification with PSPReference " + notificationInfoModel.getPspReference() + " was processed");
            notificationInfoModel.setProcessedAt(new Date());
            getModelService().save(notificationInfoModel);
        }catch (Exception e) {
            LOG.error("Notification with psp reference: " + notificationInfoModel.getPspReference() + " cause an exception. \n");
            LOG.error("Exception: ", e);
        }
    }

}
