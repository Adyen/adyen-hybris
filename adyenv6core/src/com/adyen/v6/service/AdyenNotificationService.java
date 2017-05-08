package com.adyen.v6.service;

import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.v6.model.NotificationItemModel;
import com.google.gson.Gson;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Adapter for notification items
 */
public class AdyenNotificationService {
    private ModelService modelService;
    private static final Logger LOG = Logger.getLogger(AdyenNotificationService.class);

    public NotificationItemModel createFromNotificationRequest(NotificationRequestItem notificationRequestItem) {
        Gson gson = new Gson();
        NotificationItemModel notificationItemModel = modelService.create(NotificationItemModel.class);

        if (notificationRequestItem.getAmount() != null) {
            notificationItemModel.setAmountCurrency(notificationRequestItem.getAmount().getCurrency());
            notificationItemModel.setAmountValue(notificationRequestItem.getAmount().getDecimalValue());
        }

        notificationItemModel.setEventCode(notificationRequestItem.getEventCode());
        notificationItemModel.setEventDate(notificationRequestItem.getEventDate());
        notificationItemModel.setMerchantAccountCode(notificationRequestItem.getMerchantAccountCode());
        notificationItemModel.setMerchantReference(notificationRequestItem.getMerchantReference());
        notificationItemModel.setOriginalReference(notificationRequestItem.getOriginalReference());
        notificationItemModel.setPspReference(notificationRequestItem.getPspReference());
        notificationItemModel.setReason(notificationRequestItem.getReason());
        notificationItemModel.setSuccess(notificationRequestItem.isSuccess());
        notificationItemModel.setPaymentMethod(notificationRequestItem.getPaymentMethod());

        String additionalDataJson = gson.toJson(notificationRequestItem.getAdditionalData());
        notificationItemModel.setAdditionalData(additionalDataJson);

        notificationItemModel.setCreatedAt(new Date());

        return notificationItemModel;
    }

    public void saveFromNotificationRequest(NotificationRequestItem notificationRequestItem) {
        NotificationItemModel notificationItemModel = createFromNotificationRequest(notificationRequestItem);

        modelService.save(notificationItemModel);
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
