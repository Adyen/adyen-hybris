package com.adyen.v6.cronjob;

import com.adyen.v6.model.NotificationItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.apache.log4j.Logger;

import java.util.*;

import static com.adyen.model.notification.NotificationRequestItem.EVENT_CODE_CAPTURE;
import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.PAYMENT_PROVIDER;
import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.GENERAL_SYSTEM_ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;

/**
 * Notification handling cronjob
 */
public class AdyenProcessNotificationCronJob extends AbstractJobPerformable<CronJobModel> {
    private static final Logger LOG = Logger.getLogger(AdyenProcessNotificationCronJob.class);

    private ModelService modelService;
    private BusinessProcessService businessProcessService;
    private CommonI18NService commonI18NService;

    public static final String EVENT_ADYEN_CAPTURED = "AdyenCaptured";

    @Override
    public PerformResult perform(final CronJobModel cronJob) {
        LOG.info("Start processing..");

        //Select the non-processed notifications
        final FlexibleSearchQuery selectNonProcessedNotificationsQuery = new FlexibleSearchQuery(
                "SELECT {pk} FROM {" + NotificationItemModel._TYPECODE + "}"
                        + " WHERE {" + NotificationItemModel.PROCESSED + "} = false ORDER BY {pk} ASC LIMIT 1000"
        );
        final List nonProcessedNotifications = flexibleSearchService
                .search(selectNonProcessedNotificationsQuery)
                .getResult();

        for (final Iterator it = nonProcessedNotifications.iterator(); it.hasNext(); ) {
            final NotificationItemModel notificationItemModel = (NotificationItemModel) it.next();

            System.out.println(notificationItemModel);

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
        PaymentTransactionEntryModel paymentTransactionEntryModel = createCapturedPaymentTransactionEntryModel(
                paymentTransactionModel,
                notificationItemModel
        );

        LOG.info("Saving Captured transaction entry");
        modelService.save(paymentTransactionEntryModel);

        //Trigger Captured event
        OrderModel orderModel = (OrderModel) paymentTransactionModel.getOrder();
        final Collection<OrderProcessModel> orderProcesses = orderModel.getOrderProcess();
        for (final OrderProcessModel orderProcess : orderProcesses) {
            LOG.info("Order process code: " + orderProcess.getCode());
            //TODO: send only on "order-process-*" ?
            final String eventName = orderProcess.getCode() + "_" + EVENT_ADYEN_CAPTURED;
            LOG.info("Sending event:" + eventName);
            businessProcessService.triggerEvent(eventName);
        }
    }

    /**
     * Process a notification item
     *
     * @param notificationItemModel
     */
    public void processNotification(NotificationItemModel notificationItemModel) {
        switch (notificationItemModel.getEventCode()) {
            case EVENT_CODE_CAPTURE:
                final Map queryParams = new HashMap();
                queryParams.put("paymentProvider", PAYMENT_PROVIDER);
                queryParams.put("requestId", notificationItemModel.getOriginalReference());
                final FlexibleSearchQuery selectOrderQuery = new FlexibleSearchQuery(
                        "SELECT {pk} FROM {" + PaymentTransactionModel._TYPECODE + "}"
                                + " WHERE {" + PaymentTransactionModel.PAYMENTPROVIDER + "} = ?paymentProvider"
                                + " AND {" + PaymentTransactionEntryModel.REQUESTID + "} = ?requestId",
                        queryParams
                );

                LOG.info("Finding transaction with PSP reference: " + notificationItemModel.getOriginalReference());
                final PaymentTransactionModel paymentTransaction = flexibleSearchService.searchUnique(selectOrderQuery);

                processCapturedEvent(notificationItemModel, paymentTransaction);
                break;
        }
    }

    /**
     * Create a capture transaction entry
     * TODO: move to TX service
     *
     * @param paymentTransaction
     * @param notificationItemModel
     * @return
     */
    public PaymentTransactionEntryModel createCapturedPaymentTransactionEntryModel(
            final PaymentTransactionModel paymentTransaction,
            final NotificationItemModel notificationItemModel) {
        final PaymentTransactionEntryModel transactionEntryModel = modelService.create(PaymentTransactionEntryModel.class);

        String code = paymentTransaction.getRequestId() + "_" + paymentTransaction.getEntries().size();

        transactionEntryModel.setType(PaymentTransactionType.CAPTURE);
        transactionEntryModel.setPaymentTransaction(paymentTransaction);
        transactionEntryModel.setRequestId(notificationItemModel.getPspReference());
        transactionEntryModel.setRequestToken(notificationItemModel.getMerchantReference());
        transactionEntryModel.setCode(code);
        transactionEntryModel.setTime(notificationItemModel.getEventDate());
        transactionEntryModel.setAmount(notificationItemModel.getAmountValue());

        String currencyCode = notificationItemModel.getAmountCurrency();
        final CurrencyModel currency = getCommonI18NService().getCurrency(currencyCode);
        transactionEntryModel.setCurrency(currency);

        if (notificationItemModel.getSuccess()) {
            transactionEntryModel.setTransactionStatus(ACCEPTED.name());
            transactionEntryModel.setTransactionStatusDetails(SUCCESFULL.name());
        } else {
            transactionEntryModel.setTransactionStatus(REJECTED.name());
            transactionEntryModel.setTransactionStatusDetails(GENERAL_SYSTEM_ERROR.name());
            //TODO: store reasoning
        }

        return transactionEntryModel;
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

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
