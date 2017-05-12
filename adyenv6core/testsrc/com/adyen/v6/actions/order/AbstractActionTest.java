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
package com.adyen.v6.actions.order;

import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_PROVIDER;

/**
 * Abstract for order-process actions tests
 */
public class AbstractActionTest {
    protected PaymentTransactionEntryModel createAuthorizedEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.AUTHORIZATION);
        entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
        entry.setAmount(new BigDecimal("12.34"));

        return entry;
    }

    protected PaymentTransactionEntryModel createAuthorizedRejectedEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.AUTHORIZATION);
        entry.setTransactionStatus(TransactionStatus.REJECTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE.name());

        return entry;
    }

    protected PaymentTransactionEntryModel createCaptureReceivedEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.CAPTURE);
        entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.REVIEW_NEEDED.name());

        return entry;
    }

    protected PaymentTransactionEntryModel createCaptureSuccessEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.CAPTURE);
        entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
        entry.setAmount(new BigDecimal("12.34"));

        return entry;
    }

    protected PaymentTransactionEntryModel createCaptureRejectedEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.CAPTURE);
        entry.setTransactionStatus(TransactionStatus.REJECTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.GENERAL_SYSTEM_ERROR.name());

        return entry;
    }

    protected PaymentTransactionEntryModel createRefundRejectedEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.REFUND_FOLLOW_ON);
        entry.setTransactionStatus(TransactionStatus.REJECTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE.name());

        return entry;
    }

    protected PaymentTransactionEntryModel createRefundSuccessEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.REFUND_FOLLOW_ON);
        entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());

        return entry;
    }

    protected PaymentTransactionModel createAdyenTransaction() {
        PaymentTransactionModel adyenTransaction = new PaymentTransactionModel();
        adyenTransaction.setPaymentProvider(PAYMENT_PROVIDER);
        adyenTransaction.setEntries(new ArrayList<>());

        return adyenTransaction;
    }
}
