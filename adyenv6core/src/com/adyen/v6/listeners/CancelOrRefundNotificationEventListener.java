package com.adyen.v6.listeners;

import com.adyen.v6.events.CancelOrRefundEvent;
import com.adyen.v6.model.AdyenNotificationModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.log4j.Logger;

import java.util.Date;

public class CancelOrRefundNotificationEventListener extends AbstractNotificationEventListener<CancelOrRefundEvent> {


    private static final Logger LOG = Logger.getLogger(CancelOrRefundNotificationEventListener.class);

    public CancelOrRefundNotificationEventListener() {
        super();
    }

    @Override
    protected void onEvent(final CancelOrRefundEvent event) {
        AdyenNotificationModel notificationInfoModel = event.getNotificationRequestItem();
        PaymentTransactionModel transactionModel = getPaymentTransactionRepository().getTransactionModel(notificationInfoModel.getOriginalReference());
        try {
            getAdyenNotificationService().processCancelEvent(notificationInfoModel, transactionModel);
            LOG.info("Cancel or refund notification with PSPReference " + notificationInfoModel.getPspReference() + " was processed");
            notificationInfoModel.setProcessedAt(new Date());
            getModelService().save(notificationInfoModel);
        }catch (Exception e) {
            LOG.error("Notification with psp reference: " + notificationInfoModel.getPspReference() + " cause an exception. \n");
            LOG.error("Exception: ", e);
        }
    }

}
