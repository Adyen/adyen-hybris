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

import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.v6.model.NotificationItemModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * Service for Adyen notifications manipulation
 */
public interface AdyenNotificationService {

    /**
     * Process NotificationItemModel
     * Handles multiple eventCodes
     */
    void processNotification(NotificationItemModel notificationItemModel);

    /**
     * Parse HTTP request body and save NotificationItemModels
     */
    void saveNotifications(String requestString);

    /**
     * Create NotificationItemModel from NotificationRequestItem
     */
    NotificationItemModel createFromNotificationRequest(NotificationRequestItem notificationRequestItem);

    /**
     * Save NotificationItemModel from NotificationRequestItem
     */
    void saveFromNotificationRequest(NotificationRequestItem notificationRequestItem);

    /**
     * Process notification with eventCode=CAPTURED
     */
    PaymentTransactionEntryModel processCapturedEvent(NotificationItemModel notificationItemModel, PaymentTransactionModel paymentTransactionModel);

    /**
     * Process notification with eventCode=AUTHORISED
     */
    PaymentTransactionModel processAuthorisationEvent(NotificationItemModel notificationItemModel);

    /**
     * Process notification with eventCode=CANCEL_OR_REFUND
     */
    PaymentTransactionEntryModel processCancelEvent(NotificationItemModel notificationItemModel, PaymentTransactionModel paymentTransactionModel);

    /**
     * Process notification with eventCode=REFUND
     */
    PaymentTransactionEntryModel processRefundEvent(NotificationItemModel notificationItem);

    /**
     * Process notification with eventCode=OFFER_CLOSED
     * @return
     */
    PaymentTransactionModel processOfferClosedEvent(NotificationItemModel notificationItem);

}
