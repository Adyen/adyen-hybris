package com.adyen.v6.service;

import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.UUID;

import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.v6.model.AdyenNotificationModel;
import com.google.gson.Gson;


public class DefaultAdyenNotificationV2Service implements AdyenNotificationV2Service {
    private ModelService modelService;

    @Override
    public void onRequest(final NotificationRequest notificationRequest) {
        for (NotificationRequestItem item : notificationRequest.getNotificationItems()) {
            save(item);
        }

    }

    private void save(NotificationRequestItem notificationRequestItem) {
        getModelService().save(populate(notificationRequestItem));

    }

    private AdyenNotificationModel populate(NotificationRequestItem source) {

        AdyenNotificationModel target = new AdyenNotificationModel();
        Gson gson = new Gson();
        if (source.getAmount() != null) {
            target.setAmountCurrency(source.getAmount().getCurrency());
            target.setAmountValue(source.getAmount().getDecimalValue());
        }

        target.setUuid(UUID.randomUUID().toString());
        target.setEventCode(source.getEventCode());
        target.setEventDate(source.getEventDate());
        target.setMerchantAccountCode(source.getMerchantAccountCode());
        target.setMerchantReference(source.getMerchantReference());
        target.setOriginalReference(source.getOriginalReference());
        target.setPspReference(source.getPspReference());
        target.setReason(source.getReason());
        target.setSuccess(source.isSuccess());
        target.setPaymentMethod(source.getPaymentMethod());

        String additionalDataJson = gson.toJson(source.getAdditionalData());
        target.setAdditionalData(additionalDataJson);

        target.setCreatedAt(new Date());

        return target;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}
