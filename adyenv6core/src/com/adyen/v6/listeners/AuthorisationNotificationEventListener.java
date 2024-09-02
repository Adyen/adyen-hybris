package com.adyen.v6.listeners;

import com.adyen.v6.events.AuthorisationEvent;
import com.adyen.v6.model.AdyenNotificationModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.log4j.Logger;

import java.util.Date;

public class AuthorisationNotificationEventListener extends AbstractNotificationEventListener<AuthorisationEvent> {


    private static final Logger LOG = Logger.getLogger(AuthorisationNotificationEventListener.class);

    public AuthorisationNotificationEventListener() {
        super();
    }

    @Override
    protected void onEvent(final AuthorisationEvent event) {
        AdyenNotificationModel notificationInfoModel = event.getNotificationRequestItem();
        PaymentTransactionModel transactionModel = getPaymentTransactionRepository().getTransactionModel(notificationInfoModel.getPspReference());
        try {
            if (transactionModel == null) {
                getAdyenNotificationService().processAuthorisationEvent(notificationInfoModel);
            } else {
                LOG.warn("Authorisation already processed " + transactionModel.getRequestId());
            }
            LOG.info("Notification with PSPReference " + notificationInfoModel.getPspReference() + " was processed");
            notificationInfoModel.setProcessedAt(new Date());
            getModelService().save(notificationInfoModel);
        }catch (Exception e) {
            logException(notificationInfoModel, e, LOG);
        }
    }

}
