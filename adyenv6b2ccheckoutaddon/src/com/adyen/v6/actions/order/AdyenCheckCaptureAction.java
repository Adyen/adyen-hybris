package com.adyen.v6.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Check if order is captured
 */
public class AdyenCheckCaptureAction extends AbstractAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(AdyenCheckCaptureAction.class);

    public enum Transition {
        OK, NOK, WAIT;

        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<>();
            for (final Transition transitions : Transition.values()) {
                res.add(transitions.toString());
            }
            return res;
        }
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

    @Override
    public String execute(final OrderProcessModel process) {
        LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

        final OrderModel order = process.getOrder();

        if (order.getAdyenPaymentMethod() == null) {
            return Transition.OK.toString();
        }

        /*
        TakePaymentAction will move the order to PAYMENT_CAPTURED state
        Revert it back to PAYMENT_NOT_CAPTURED
         */
        order.setStatus(OrderStatus.PAYMENT_NOT_CAPTURED);
        modelService.save(order);

        BigDecimal remainingAmount = new BigDecimal(order.getTotalPrice());
        for (final PaymentTransactionModel paymentTransactionModel : order.getPaymentTransactions()) {
            //Fail if capture is rejected
            if (hasTypeAndStatus(paymentTransactionModel, PaymentTransactionType.CAPTURE, TransactionStatus.REJECTED)
                    || hasTypeAndStatus(paymentTransactionModel, PaymentTransactionType.CAPTURE, TransactionStatus.ERROR)) {
                LOG.info("Process: " + process.getCode() + " Order Not Captured");
                return Transition.NOK.toString();
            }

            PaymentTransactionEntryModel transactionEntry = getTransactionEntry(paymentTransactionModel,
                    PaymentTransactionType.CAPTURE,
                    TransactionStatus.ACCEPTED,
                    TransactionStatusDetails.SUCCESFULL);

            if (transactionEntry != null) {
                remainingAmount = remainingAmount.subtract(transactionEntry.getAmount());
                LOG.info("Remaining amount: " + remainingAmount);
            }
        }

        BigDecimal zero = new BigDecimal(0);
        //Return success if all transactions are captured
        if (remainingAmount.compareTo(zero) <= 0) {
            LOG.info("Process: " + process.getCode() + " Order Captured");
            order.setStatus(OrderStatus.PAYMENT_CAPTURED);
            modelService.save(order);
            return Transition.OK.toString();
        }

        //By default Wait for capture result
        LOG.info("Process: " + process.getCode() + " Order Waiting");
        return Transition.WAIT.toString();
    }

    private boolean hasTypeAndStatus(final PaymentTransactionModel paymentTransactionModel,
                                     final PaymentTransactionType paymentTransactionType,
                                     final TransactionStatus transactionStatus) {
        for (final PaymentTransactionEntryModel entry : paymentTransactionModel.getEntries()) {
            if (paymentTransactionType.equals(entry.getType())
                    && transactionStatus.name().equals(entry.getTransactionStatus())) {
                return true;
            }
        }

        return false;
    }

    private PaymentTransactionEntryModel getTransactionEntry(final PaymentTransactionModel paymentTransactionModel,
                                                             final PaymentTransactionType paymentTransactionType,
                                                             final TransactionStatus transactionStatus,
                                                             final TransactionStatusDetails transactionStatusDetails) {
        for (final PaymentTransactionEntryModel entry : paymentTransactionModel.getEntries()) {
            if (paymentTransactionType.equals(entry.getType())
                    && transactionStatus.name().equals(entry.getTransactionStatus())
                    && transactionStatusDetails.name().equals(entry.getTransactionStatusDetails())) {
                return entry;
            }
        }

        return null;
    }
}
