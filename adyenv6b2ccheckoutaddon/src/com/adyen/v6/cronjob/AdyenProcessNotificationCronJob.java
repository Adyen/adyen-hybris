package com.adyen.v6.cronjob;

import com.adyen.v6.model.NotificationItemModel;
import com.adyen.v6.repository.NotificationItemRepository;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.repository.PaymentTransactionRepository;
import com.adyen.v6.service.AdyenTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.adyen.model.notification.NotificationRequestItem.EVENT_CODE_AUTHORISATION;
import static com.adyen.model.notification.NotificationRequestItem.EVENT_CODE_CAPTURE;
import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.PROCESS_EVENT_ADYEN_AUTHORIZED;
import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.PROCESS_EVENT_ADYEN_CAPTURED;

/**
 * Notification handling cronjob
 */
public class AdyenProcessNotificationCronJob extends AbstractJobPerformable<CronJobModel> {
    private static final Logger LOG = Logger.getLogger(AdyenProcessNotificationCronJob.class);

    private ModelService modelService;
    private BusinessProcessService businessProcessService;
    private AdyenTransactionService adyenTransactionService;
    private NotificationItemRepository notificationItemRepository;
    private OrderRepository orderRepository;
    private PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public PerformResult perform(final CronJobModel cronJob) {
        LOG.info("Start processing..");

        final List nonProcessedNotifications = notificationItemRepository.getNonProcessedNotifications();

        for (final Iterator it = nonProcessedNotifications.iterator(); it.hasNext(); ) {
            final NotificationItemModel notificationItemModel = (NotificationItemModel) it.next();

            notificationItemModel.setProcessed(true);
            //TODO: processedAt ?
            //TODO: add check for duplicate notifications

            LOG.info("Processing order with code: " + notificationItemModel.getMerchantReference());

            processNotification(notificationItemModel);

            LOG.info("Notification with PSPReference " + notificationItemModel.getPspReference() + " was processed");
            modelService.save(notificationItemModel);
        }

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    /**
     * Handles CAPTURE eventCode
     *
     * @param notificationItemModel
     * @param paymentTransactionModel
     */
    public void processCapturedEvent(
            NotificationItemModel notificationItemModel,
            PaymentTransactionModel paymentTransactionModel) {
        //Register Captured transaction
        PaymentTransactionEntryModel paymentTransactionEntryModel = adyenTransactionService
                .createCapturedTransactionFromNotification(
                        paymentTransactionModel,
                        notificationItemModel
                );

        LOG.info("Saving Captured transaction entry");
        modelService.save(paymentTransactionEntryModel);

        //Trigger Captured event
        OrderModel orderModel = (OrderModel) paymentTransactionModel.getOrder();
        triggerEvent(orderModel, PROCESS_EVENT_ADYEN_CAPTURED);
    }

    /**
     * Handles AUTHORISATION eventCode
     */
    public PaymentTransactionModel processAuthorisationEvent(
            NotificationItemModel notificationItemModel,
            OrderModel orderModel) {
        if (orderModel == null) return null;

        PaymentTransactionModel paymentTransactionModel = null;
        if (notificationItemModel.getSuccess()) {
            paymentTransactionModel = adyenTransactionService.authorizeOrderModel(
                    orderModel,
                    notificationItemModel.getMerchantReference(),
                    notificationItemModel.getPspReference()
            );
        } else {
            paymentTransactionModel = adyenTransactionService.storeFailedAuthorizationFromNotification(
                    notificationItemModel,
                    orderModel
            );
        }

        triggerEvent(orderModel, PROCESS_EVENT_ADYEN_AUTHORIZED);

        //todo: trigger only for manual capture
        triggerEvent(orderModel, PROCESS_EVENT_ADYEN_CAPTURED);
        return paymentTransactionModel;
    }

    /**
     * Process a notification item
     *
     * @param notificationItemModel
     */
    public void processNotification(NotificationItemModel notificationItemModel) {
        PaymentTransactionModel paymentTransaction;
        switch (notificationItemModel.getEventCode()) {
            case EVENT_CODE_CAPTURE:
                paymentTransaction = paymentTransactionRepository.getTransactionModel(notificationItemModel.getOriginalReference());
                processCapturedEvent(notificationItemModel, paymentTransaction);
                break;
            case EVENT_CODE_AUTHORISATION:
                paymentTransaction = paymentTransactionRepository.getTransactionModel(notificationItemModel.getPspReference());
                if (paymentTransaction == null) {
                    OrderModel orderModel = orderRepository.getOrderModel(notificationItemModel.getMerchantReference());
                    processAuthorisationEvent(notificationItemModel, orderModel);
                } else {
                    LOG.info("Authorisation already processed " + paymentTransaction.getRequestId());
                }
                break;
        }
    }

    /**
     * Trigger AdyenCaptured event
     *
     * @param orderModel
     */
    private void triggerEvent(OrderModel orderModel, String event) {
        final Collection<OrderProcessModel> orderProcesses = orderModel.getOrderProcess();
        for (final OrderProcessModel orderProcess : orderProcesses) {
            LOG.info("Order process code: " + orderProcess.getCode());
            //TODO: send only on "order-process-*" ?
            final String eventName = orderProcess.getCode() + "_" + event;
            LOG.info("Sending event:" + eventName);
            businessProcessService.triggerEvent(eventName);
        }
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    public AdyenTransactionService getAdyenTransactionService() {
        return adyenTransactionService;
    }

    public void setAdyenTransactionService(AdyenTransactionService adyenTransactionService) {
        this.adyenTransactionService = adyenTransactionService;
    }

    public NotificationItemRepository getNotificationItemRepository() {
        return notificationItemRepository;
    }

    public void setNotificationItemRepository(NotificationItemRepository notificationItemRepository) {
        this.notificationItemRepository = notificationItemRepository;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public PaymentTransactionRepository getPaymentTransactionRepository() {
        return paymentTransactionRepository;
    }

    public void setPaymentTransactionRepository(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }
}
