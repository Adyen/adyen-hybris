package com.adyen.v6.cronjob;

import com.adyen.v6.model.NotificationItemModel;
import com.adyen.v6.repository.NotificationItemRepository;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.repository.PaymentTransactionRepository;
import com.adyen.v6.service.AdyenBusinessProcessService;
import com.adyen.v6.service.AdyenTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

import static com.adyen.model.notification.NotificationRequestItem.*;
import static com.adyen.v6.constants.Adyenv6coreConstants.*;

/**
 * Notification handling cronjob
 */
public class AdyenProcessNotificationCronJob extends AbstractJobPerformable<CronJobModel> {
    private static final Logger LOG = Logger.getLogger(AdyenProcessNotificationCronJob.class);

    private ModelService modelService;
    private AdyenBusinessProcessService adyenBusinessProcessService;
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
        if (paymentTransactionModel == null) {
            LOG.info("Parent transaction is null");
            return;
        }

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
        adyenBusinessProcessService.triggerOrderProcessEvent(orderModel, PROCESS_EVENT_ADYEN_CAPTURED);
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

        adyenBusinessProcessService.triggerOrderProcessEvent(orderModel, PROCESS_EVENT_ADYEN_AUTHORIZED);

        //todo: trigger only for manual capture
        adyenBusinessProcessService.triggerOrderProcessEvent(orderModel, PROCESS_EVENT_ADYEN_CAPTURED);
        return paymentTransactionModel;
    }

    public void processCancelEvent(
            NotificationItemModel notificationItemModel,
            PaymentTransactionModel paymentTransactionModel) {
        if (paymentTransactionModel == null) {
            return;
        }

        PaymentTransactionEntryModel paymentTransactionEntryModel = adyenTransactionService
                .createCancellationTransaction(
                        paymentTransactionModel,
                        notificationItemModel.getMerchantReference(),
                        notificationItemModel.getPspReference()
                );

        if (notificationItemModel.getSuccess()) {
            paymentTransactionEntryModel.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
        } else {
            //TODO: propagate fail reasons
            paymentTransactionEntryModel.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE.name());
        }

        LOG.info("Saving Cancel transaction entry");
        modelService.save(paymentTransactionEntryModel);
    }

    /**
     * Process refund event
     *
     * @param notificationItem
     */
    private void processRefundEvent(NotificationItemModel notificationItem) {
        PaymentTransactionModel paymentTransaction = paymentTransactionRepository
                .getTransactionModel(notificationItem.getOriginalReference());
        if (paymentTransaction == null) {
            LOG.info("Parent transaction is null");
            return;
        }

        //Register Refund transaction
        PaymentTransactionEntryModel paymentTransactionEntryModel = adyenTransactionService
                .createRefundedTransactionFromNotification(
                        paymentTransaction,
                        notificationItem
                );

        LOG.info("Saving Refunded transaction entry");
        modelService.save(paymentTransactionEntryModel);

        //Trigger Refunded event
        OrderModel orderModel = (OrderModel) paymentTransaction.getOrder();
        adyenBusinessProcessService.triggerReturnProcessEvent(orderModel, PROCESS_EVENT_ADYEN_REFUNDED);
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
            case EVENT_CODE_CANCEL_OR_REFUND:
                paymentTransaction = paymentTransactionRepository.getTransactionModel(notificationItemModel.getOriginalReference());
                processCancelEvent(notificationItemModel, paymentTransaction);
                break;
            case EVENT_CODE_REFUND:
                processRefundEvent(notificationItemModel);
                break;
        }
    }


    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
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

    public AdyenBusinessProcessService getAdyenBusinessProcessService() {
        return adyenBusinessProcessService;
    }

    public void setAdyenBusinessProcessService(AdyenBusinessProcessService adyenBusinessProcessService) {
        this.adyenBusinessProcessService = adyenBusinessProcessService;
    }
}
