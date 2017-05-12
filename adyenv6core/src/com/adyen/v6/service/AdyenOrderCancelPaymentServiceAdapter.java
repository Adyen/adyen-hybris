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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.OrderCancelPaymentServiceAdapter;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;

import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_PROVIDER;
import static de.hybris.platform.core.enums.OrderStatus.CANCELLED;

/**
 * Used for cancellations by ImmediateCancelRequestExecutor
 */
public class AdyenOrderCancelPaymentServiceAdapter implements OrderCancelPaymentServiceAdapter {
    private PaymentService paymentService;
    private ModelService modelService;
    private CalculationService calculationService;

    private static final Logger LOG = Logger.getLogger(AdyenOrderCancelPaymentServiceAdapter.class);

    /**
     * Issues a cancel request for complete cancelled orders
     *
     * @param order
     */
    @Override
    public void recalculateOrderAndModifyPayments(final OrderModel order) {
        LOG.debug("recalculateOrderAndModifyPayments received for order: " + order.getCode() + ":"
                + order.getTotalPrice() + ":" + order.getStatus().getCode());

        try {
            calculationService.recalculate(order);
        } catch (CalculationException e) {
            LOG.error(e);
        }

        //Send the cancel request only when the whole order is cancelled
        if (!CANCELLED.getCode().equals(order.getStatus().getCode())) {
            LOG.info("Partial cancellation - do nothing");
            return;
        }

        if(order.getPaymentTransactions().isEmpty()) {
            LOG.warn("No transaction found!");
            return;
        }
        final PaymentTransactionModel transaction = order.getPaymentTransactions().get(0);

        //Ignore non-Adyen payments
        if (!PAYMENT_PROVIDER.equals(transaction.getPaymentProvider())) {
            LOG.debug("Different Payment provider: " + transaction.getPaymentProvider());
            return;
        }

        if (transaction.getEntries().isEmpty()) {
            LOG.warn("Cannot find auth transaction!");
            return;
        }

        PaymentTransactionEntryModel authorizationTransaction = transaction.getEntries().get(0);

        PaymentTransactionEntryModel cancellationTransaction = paymentService.cancel(authorizationTransaction);

        LOG.info("Saving transaction " + cancellationTransaction.getRequestId()
                + ":" + cancellationTransaction.getTransactionStatus()
                + ":" + cancellationTransaction.getTransactionStatusDetails());
        modelService.save(cancellationTransaction);
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public CalculationService getCalculationService() {
        return calculationService;
    }

    public void setCalculationService(CalculationService calculationService) {
        this.calculationService = calculationService;
    }
}
