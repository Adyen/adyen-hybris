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

import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.v6.model.NotificationItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

public interface AdyenTransactionService {
    /**
     * Get TX entry by type and status
     */
    static PaymentTransactionEntryModel getTransactionEntry(PaymentTransactionModel paymentTransactionModel, PaymentTransactionType paymentTransactionType, TransactionStatus transactionStatus) {
        PaymentTransactionEntryModel result = paymentTransactionModel.getEntries().stream()
                .filter(
                        entry -> paymentTransactionType.equals(entry.getType())
                                && transactionStatus.name().equals(entry.getTransactionStatus())
                )
                .findFirst()
                .orElse(null);

        return result;
    }

    /**
     * Get TX entry by type, status and details
     */
    static PaymentTransactionEntryModel getTransactionEntry(PaymentTransactionModel paymentTransactionModel,
                                                            PaymentTransactionType paymentTransactionType,
                                                            TransactionStatus transactionStatus,
                                                            TransactionStatusDetails transactionStatusDetails) {
        PaymentTransactionEntryModel result = paymentTransactionModel.getEntries().stream()
                .filter(
                        entry -> paymentTransactionType.equals(entry.getType())
                                && transactionStatus.name().equals(entry.getTransactionStatus())
                                && transactionStatusDetails.name().equals(entry.getTransactionStatusDetails())
                )
                .findFirst()
                .orElse(null);

        return result;
    }

    /**
     * Creates a PaymentTransactionEntryModel with type=CAPTURE from NotificationItemModel
     */
    PaymentTransactionEntryModel createCapturedTransactionFromNotification(PaymentTransactionModel paymentTransaction, NotificationItemModel notificationItemModel);

    /**
     * Creates a PaymentTransactionEntryModel with type=REFUND_FOLLOW_ON from NotificationItemModel
     */
    PaymentTransactionEntryModel createRefundedTransactionFromNotification(PaymentTransactionModel paymentTransaction, NotificationItemModel notificationItemModel);

    /**
     * Stores the authorization transactions for an order
     */
    PaymentTransactionModel authorizeOrderModel(AbstractOrderModel abstractOrderModel, String merchantTransactionCode, String pspReference);

    /**
     * Store failed authorization transaction entry
     */
    PaymentTransactionModel storeFailedAuthorizationFromNotification(NotificationItemModel notificationItemModel, AbstractOrderModel abstractOrderModel);

    /**
     * Creates a PaymentTransactionEntryModel with type=CANCEL
     */
    PaymentTransactionEntryModel createCancellationTransaction(PaymentTransactionModel paymentTransaction, String merchantCode, String pspReference);

    /**
     * Creates a PaymentTransactionModel
     */
    PaymentTransactionModel createPaymentTransactionFromResultCode(AbstractOrderModel abstractOrderModel, String merchantTransactionCode, String pspReference, PaymentsResponse.ResultCodeEnum resultCodeEnum);

    /**
     * Creates a PaymentTransactionEntryModel with type=CAPTURE
     */
    PaymentTransactionEntryModel createCapturePaymentTransactionEntryModel(final PaymentTransactionModel paymentTransaction, final String merchantCode, final AbstractOrderModel abstractOrderModel);
}
