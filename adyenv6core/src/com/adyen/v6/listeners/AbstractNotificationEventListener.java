package com.adyen.v6.listeners;

import com.adyen.v6.repository.PaymentTransactionRepository;
import com.adyen.v6.service.AdyenNotificationService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

public abstract class AbstractNotificationEventListener<T extends AbstractEvent> extends AbstractEventListener<T> {

    private ModelService modelService;
    private PaymentTransactionRepository paymentTransactionRepository;
    private AdyenNotificationService adyenNotificationService;

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public PaymentTransactionRepository getPaymentTransactionRepository() {
        return paymentTransactionRepository;
    }

    public void setPaymentTransactionRepository(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    public AdyenNotificationService getAdyenNotificationService() {
        return adyenNotificationService;
    }

    public void setAdyenNotificationService(AdyenNotificationService adyenNotificationService) {
        this.adyenNotificationService = adyenNotificationService;
    }
}
