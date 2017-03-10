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
        final PaymentTransactionModel transaction = order.getPaymentTransactions().get(0);

        LOG.info("recalculateOrderAndModifyPayments received for order: " + order.getCode() + ":"
                + order.getTotalPrice() + ":" + order.getStatus().getCode());

        try {
            calculationService.recalculate(order);
        } catch (CalculationException e) {
            LOG.error(e);
        }

        //Send the cancel request only when the whole order is cancelled
        if (!CANCELLED.getCode().equals(order.getStatus().getCode())) {
            LOG.info("Partial cancellation");
            return;
        }

        if (transaction == null) {
            LOG.error("No transaction found!");
            return;
        }

        //Ignore non-Adyen payments
        if (!PAYMENT_PROVIDER.equals(transaction.getPaymentProvider())) {
            return;
        }

        PaymentTransactionEntryModel authorizationTransaction = transaction.getEntries().get(0);

        if (authorizationTransaction == null) {
            LOG.error("Cannot find auth transaction!");
            return;
        }

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
