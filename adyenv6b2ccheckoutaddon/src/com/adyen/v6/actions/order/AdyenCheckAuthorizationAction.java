package com.adyen.v6.actions.order;

import com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Check if order is authorized
 */
public class AdyenCheckAuthorizationAction extends AbstractAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(AdyenCheckAuthorizationAction.class);

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

        if (order == null) {
            return Transition.NOK.toString();
        } else if (order.getPaymentInfo() instanceof InvoicePaymentInfoModel) {
            LOG.info("Process: " + process.getCode() + " InvoicePaymentModel");
            return Transition.OK.toString();
        }

        boolean orderAuthorized = isOrderAuthorized(order);

        if (orderAuthorized) {
            LOG.info("Process: " + process.getCode() + " Order Authorized");
            order.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
            modelService.save(order);
            return Transition.OK.toString();
        }

        if (hasAdyenPendingTransactions(order)) {
            LOG.info("Process: " + process.getCode() + " Order Waiting");
            return Transition.WAIT.toString();
        }

        LOG.error("Process: " + process.getCode() + " Order Not Authorized");
        order.setStatus(OrderStatus.PAYMENT_NOT_AUTHORIZED);
        modelService.save(order);

        return Transition.NOK.toString();
    }

    private boolean hasAdyenPendingTransactions(final OrderModel order) {
        for (final PaymentTransactionModel transaction : order.getPaymentTransactions()) {
            if (transaction.getPaymentProvider().equals(Adyenv6b2ccheckoutaddonConstants.PAYMENT_PROVIDER)
                    && !isTransactionAuthorized(transaction)) {
                return true;
            }
        }

        return false;
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
        for (final PaymentTransactionModel paymentTransactionModel : order.getPaymentTransactions()) {
            if (!isTransactionAuthorized(paymentTransactionModel)) {
                return false;
            }
        }

        return true;
    }
}
