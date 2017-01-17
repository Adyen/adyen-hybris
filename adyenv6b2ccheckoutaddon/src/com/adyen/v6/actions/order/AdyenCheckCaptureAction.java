package com.adyen.v6.actions.order;

import com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants;
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

        boolean allCaptured = true;
        for (final PaymentTransactionModel paymentTransactionModel : order.getPaymentTransactions()) {
            //Skip if not Adyen transaction
            if (!Adyenv6b2ccheckoutaddonConstants.PAYMENT_PROVIDER.equals(paymentTransactionModel.getPaymentProvider())) {
                continue;
            }

            //Process only transactions that have capture request (capture-received)
            if (hasTransactionEntry(
                    paymentTransactionModel,
                    PaymentTransactionType.CAPTURE,
                    TransactionStatus.ACCEPTED,
                    TransactionStatusDetails.REVIEW_NEEDED)) {

                //Fail if capture is rejected
                if (hasTypeAndStatus(paymentTransactionModel, PaymentTransactionType.CAPTURE, TransactionStatus.REJECTED)
                        || hasTypeAndStatus(paymentTransactionModel, PaymentTransactionType.CAPTURE, TransactionStatus.ERROR)) {
                    LOG.info("Process: " + process.getCode() + " Order Not Captured");
                    order.setStatus(OrderStatus.PAYMENT_NOT_CAPTURED);
                    return Transition.NOK.toString();
                }

                //Check if transaction is not captured yet
                if (!hasTransactionEntry(
                        paymentTransactionModel,
                        PaymentTransactionType.CAPTURE,
                        TransactionStatus.ACCEPTED,
                        TransactionStatusDetails.SUCCESFULL)) {
                    allCaptured = false;
                }
            }
        }

        //Return success if all transactions are captured
        if (allCaptured) {
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

    private boolean hasTransactionEntry(final PaymentTransactionModel paymentTransactionModel,
                                        final PaymentTransactionType paymentTransactionType,
                                        final TransactionStatus transactionStatus,
                                        final TransactionStatusDetails transactionStatusDetails) {
        for (final PaymentTransactionEntryModel entry : paymentTransactionModel.getEntries()) {
            if (paymentTransactionType.equals(entry.getType())
                    && transactionStatus.name().equals(entry.getTransactionStatus())
                    && transactionStatusDetails.name().equals(entry.getTransactionStatusDetails())) {
                return true;
            }
        }

        return false;
    }
}
