/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.service;

import java.util.Date;
import java.util.UUID;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import org.apache.log4j.Logger;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.notification.NotificationHandler;
import com.adyen.v6.constants.Adyenv6coreConstants;
import com.adyen.v6.model.NotificationItemModel;
import com.adyen.v6.repository.CartRepository;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.repository.PaymentTransactionRepository;
import com.google.gson.Gson;
import de.hybris.platform.commerceservices.order.CommercePlaceOrderStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

public class DefaultAdyenNotificationService implements AdyenNotificationService {
    private ModelService modelService;
    private AdyenTransactionService adyenTransactionService;
    private AdyenBusinessProcessService adyenBusinessProcessService;
    private OrderRepository orderRepository;
    private PaymentTransactionRepository paymentTransactionRepository;
    private CartRepository cartRepository;
    private CommercePlaceOrderStrategy commercePlaceOrderStrategy;
    private SessionService sessionService;

    private static final Logger LOG = Logger.getLogger(DefaultAdyenNotificationService.class);

    @Override
    public NotificationItemModel createFromNotificationRequest(NotificationRequestItem notificationRequestItem) {
        Gson gson = new Gson();
        NotificationItemModel notificationItemModel = modelService.create(NotificationItemModel.class);

        if (notificationRequestItem.getAmount() != null) {
            notificationItemModel.setAmountCurrency(notificationRequestItem.getAmount().getCurrency());
            notificationItemModel.setAmountValue(notificationRequestItem.getAmount().getDecimalValue());
        }

        notificationItemModel.setUuid(UUID.randomUUID().toString());
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

    @Override
    public void saveFromNotificationRequest(NotificationRequestItem notificationRequestItem) {
        NotificationItemModel notificationItemModel = createFromNotificationRequest(notificationRequestItem);

        modelService.save(notificationItemModel);
    }

    @Override
    public void saveNotifications(final String requestString) {
        NotificationHandler notificationHandler = new NotificationHandler();
        NotificationRequest notificationRequest = notificationHandler.handleNotificationJson(requestString);
        LOG.debug(notificationRequest);

        //Save the notification items to the database
        for (NotificationRequestItem notificationRequestItem : notificationRequest.getNotificationItems()) {
            saveFromNotificationRequest(notificationRequestItem);
        }
    }

    @Override
    public PaymentTransactionEntryModel processCapturedEvent(NotificationItemModel notificationItemModel, PaymentTransactionModel paymentTransactionModel) {
        if (paymentTransactionModel == null) {
            LOG.debug("Parent transaction is null");
            return null;
        }

        //Register Captured transaction
        PaymentTransactionEntryModel paymentTransactionEntryModel = adyenTransactionService.createCapturedTransactionFromNotification(paymentTransactionModel, notificationItemModel);

        LOG.debug("Saving Captured transaction entry");
        modelService.save(paymentTransactionEntryModel);

        //Trigger Captured event
        OrderModel orderModel = (OrderModel) paymentTransactionModel.getOrder();
        adyenBusinessProcessService.triggerOrderProcessEvent(orderModel, Adyenv6coreConstants.PROCESS_EVENT_ADYEN_CAPTURED);

        return paymentTransactionEntryModel;
    }

    @Override
    public PaymentTransactionModel processAuthorisationEvent(NotificationItemModel notificationItemModel) {
        String orderCode = notificationItemModel.getMerchantReference();
        OrderModel orderModel = orderRepository.getOrderModel(orderCode);

        if (orderModel == null) {
            LOG.warn("Order with orderCode: " + orderCode + " was not found!");
            return null;
        }

        PaymentTransactionModel paymentTransactionModel;
        if (notificationItemModel.getSuccess()) {
            paymentTransactionModel = adyenTransactionService.authorizeOrderModel(orderModel, notificationItemModel.getMerchantReference(), notificationItemModel.getPspReference(), notificationItemModel.getAmountValue());
        } else {
            paymentTransactionModel = adyenTransactionService.storeFailedAuthorizationFromNotification(notificationItemModel, orderModel);
        }

        adyenBusinessProcessService.triggerOrderProcessEvent(orderModel, Adyenv6coreConstants.PROCESS_EVENT_ADYEN_PAYMENT_RESULT);
        adyenBusinessProcessService.triggerOrderProcessEvent(orderModel, Adyenv6coreConstants.PROCESS_EVENT_ADYEN_CAPTURED);

        return paymentTransactionModel;
    }

    @Override
    public PaymentTransactionEntryModel processCancelEvent(NotificationItemModel notificationItemModel, PaymentTransactionModel paymentTransactionModel) {
        if (paymentTransactionModel == null) {
            return null;
        }

        PaymentTransactionEntryModel paymentTransactionEntryModel = adyenTransactionService.createCancellationTransaction(paymentTransactionModel,
                                                                                                                          notificationItemModel.getMerchantReference(),
                                                                                                                          notificationItemModel.getPspReference());

        if (notificationItemModel.getSuccess()) {
            paymentTransactionEntryModel.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
        } else {
            paymentTransactionEntryModel.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE.name());
        }

        LOG.debug("Saving Cancel transaction entry");
        modelService.save(paymentTransactionEntryModel);

        return paymentTransactionEntryModel;
    }

    @Override
    public PaymentTransactionEntryModel processRefundEvent(NotificationItemModel notificationItem) {
        PaymentTransactionModel paymentTransaction = paymentTransactionRepository.getTransactionModel(notificationItem.getOriginalReference());
        if (paymentTransaction == null) {
            LOG.debug("Parent transaction is null");
            return null;
        }

        //Register Refund transaction
        PaymentTransactionEntryModel paymentTransactionEntryModel = adyenTransactionService.createRefundedTransactionFromNotification(paymentTransaction, notificationItem);

        LOG.debug("Saving Refunded transaction entry");
        modelService.save(paymentTransactionEntryModel);

        //Trigger Refunded event
        OrderModel orderModel = (OrderModel) paymentTransaction.getOrder();
        adyenBusinessProcessService.triggerReturnProcessEvent(orderModel, Adyenv6coreConstants.PROCESS_EVENT_ADYEN_REFUNDED);

        return paymentTransactionEntryModel;
    }

    @Override
    public PaymentTransactionModel processOfferClosedEvent(NotificationItemModel notificationItemModel) {
        String orderCode = notificationItemModel.getMerchantReference();
        if(!notificationItemModel.getSuccess()) {
            LOG.warn("Order " + orderCode + " received unexpected OFFER_CLOSED event with success=false");
            return null;
        }

        OrderModel orderModel = orderRepository.getOrderModel(orderCode);
        if (orderModel == null) {
            LOG.warn("Order " + orderCode + " was not found, skipping OFFER_CLOSED event...");
            return null;
        }
        if (isOrderAuthorized(orderModel)) {
            LOG.warn("Order " + orderCode + " already authorised, skipping OFFER_CLOSED event...");
            return null;
        }
        if (OrderStatus.CANCELLED.equals(orderModel.getStatus()) || OrderStatus.PROCESSING_ERROR.equals(orderModel.getStatus())) {
            LOG.warn("Order " + orderCode + " already cancelled, skipping OFFER_CLOSED event...");
            return null;
        }

        orderModel.setStatus(OrderStatus.PROCESSING_ERROR);
        orderModel.setStatusInfo("Adyen OFFER_CLOSED: " + notificationItemModel.getPspReference());
        getModelService().save(orderModel);

        return adyenTransactionService.storeFailedAuthorizationFromNotification(notificationItemModel, orderModel);
    }

    private boolean isTransactionAuthorized(final PaymentTransactionModel paymentTransactionModel) {
        for (final PaymentTransactionEntryModel entry : paymentTransactionModel.getEntries()) {
            if (entry.getType().equals(PaymentTransactionType.AUTHORIZATION)
                    && TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())) {
                return true;
            }
        }

        return false;
    }

    private boolean isOrderAuthorized(final OrderModel order) {
        if(order.getPaymentTransactions() == null || order.getPaymentTransactions().isEmpty()) {
            return false;
        }

        //A single not authorized transaction means not authorized
        for (final PaymentTransactionModel paymentTransactionModel : order.getPaymentTransactions()) {
            if (!isTransactionAuthorized(paymentTransactionModel)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void processNotification(NotificationItemModel notificationItemModel) {
        PaymentTransactionModel paymentTransaction;
        switch (notificationItemModel.getEventCode()) {
            case NotificationRequestItem.EVENT_CODE_CAPTURE:
                paymentTransaction = paymentTransactionRepository.getTransactionModel(notificationItemModel.getOriginalReference());
                processCapturedEvent(notificationItemModel, paymentTransaction);
                break;
            case NotificationRequestItem.EVENT_CODE_AUTHORISATION:
                paymentTransaction = paymentTransactionRepository.getTransactionModel(notificationItemModel.getPspReference());
                if (paymentTransaction == null) {
                    processAuthorisationEvent(notificationItemModel);
                } else {
                    LOG.warn("Authorisation already processed " + paymentTransaction.getRequestId());
                }
                break;
            case NotificationRequestItem.EVENT_CODE_CANCEL_OR_REFUND:
                paymentTransaction = paymentTransactionRepository.getTransactionModel(notificationItemModel.getOriginalReference());
                processCancelEvent(notificationItemModel, paymentTransaction);
                break;
            case NotificationRequestItem.EVENT_CODE_REFUND:
                processRefundEvent(notificationItemModel);
                break;
            case NotificationRequestItem.EVENT_CODE_OFFER_CLOSED:
                processOfferClosedEvent(notificationItemModel);
                break;
        }
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public AdyenTransactionService getAdyenTransactionService() {
        return adyenTransactionService;
    }

    public void setAdyenTransactionService(AdyenTransactionService adyenTransactionService) {
        this.adyenTransactionService = adyenTransactionService;
    }

    public AdyenBusinessProcessService getAdyenBusinessProcessService() {
        return adyenBusinessProcessService;
    }

    public void setAdyenBusinessProcessService(AdyenBusinessProcessService adyenBusinessProcessService) {
        this.adyenBusinessProcessService = adyenBusinessProcessService;
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

    public CartRepository getCartRepository() {
        return cartRepository;
    }

    public void setCartRepository(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public CommercePlaceOrderStrategy getCommercePlaceOrderStrategy() {
        return commercePlaceOrderStrategy;
    }

    public void setCommercePlaceOrderStrategy(CommercePlaceOrderStrategy commercePlaceOrderStrategy) {
        this.commercePlaceOrderStrategy = commercePlaceOrderStrategy;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

}
