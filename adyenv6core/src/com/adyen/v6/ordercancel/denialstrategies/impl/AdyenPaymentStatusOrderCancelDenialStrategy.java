package com.adyen.v6.ordercancel.denialstrategies.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.AbstractCancelDenialStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Stream;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Implementation of a OrderCancelDenialStrategy considering the conditions of Adyen to do not allow a partial
 * order cancel
 */
public class AdyenPaymentStatusOrderCancelDenialStrategy extends AbstractCancelDenialStrategy implements OrderCancelDenialStrategy {

    protected static final Logger LOG = LogManager.getLogger(AdyenPaymentStatusOrderCancelDenialStrategy.class);

    private static final String ORDER_CANCEL_CONFIG_CANNOT_BE_NULL = "Order cancel config cannot be null";
    private static final String ORDER_MODEL_CANNOT_BE_NULL = "OrderModel cannot be null";

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderCancelDenialReason getCancelDenialReason(final OrderCancelConfigModel orderCancelConfigModel,
                                                         final OrderModel orderModel, final PrincipalModel principalModel,
                                                         final boolean partialCancel, final boolean partialEntryCancel) {
        validateParameterNotNull(orderCancelConfigModel, ORDER_CANCEL_CONFIG_CANNOT_BE_NULL);
        validateParameterNotNull(orderModel, ORDER_MODEL_CANNOT_BE_NULL);

        if (!hasAuthorizedTransactionType(orderModel) || !hasNoCaptureTransactionType(orderModel)) {
            return getReason();
        }

        return null;
    }

    /**
     * Check is order has any {@link PaymentTransactionEntryModel} with status {@code AUTHORIZATION}
     *
     * @param orderModel
     * @return true if there is any {@link PaymentTransactionEntryModel} with status {@code AUTHORIZATION}
     */
    protected boolean hasAuthorizedTransactionType(final OrderModel orderModel) {
        return orderModel.getPaymentTransactions().stream()
                .flatMap(Stream::ofNullable)
                .map(PaymentTransactionModel::getEntries)
                .flatMap(Stream::ofNullable)
                .flatMap(List::stream)
                .anyMatch(entry -> PaymentTransactionType.AUTHORIZATION.equals(entry.getType()));
    }

    /**
     * Check is order has no {@link PaymentTransactionEntryModel} with status {@code AUTHORIZATION}
     *
     * @param orderModel
     * @return true if there is not {@link PaymentTransactionEntryModel} with status {@code AUTHORIZATION}
     */
    protected boolean hasNoCaptureTransactionType(final OrderModel orderModel) {
        return orderModel.getPaymentTransactions().stream()
                .flatMap(Stream::ofNullable)
                .map(PaymentTransactionModel::getEntries)
                .flatMap(Stream::ofNullable)
                .flatMap(List::stream)
                .noneMatch(entry -> PaymentTransactionType.CAPTURE.equals(entry.getType()));
    }
}
