package com.adyen.v6.ordercancel.denialstrategies.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.AbstractCancelDenialStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Implementation of a OrderCancelDenialStrategy considering the conditions of Adyen to do not allow a partial
 * order cancel
 */
public class AdyenPartialOrderCancelDenialStrategy extends AbstractCancelDenialStrategy implements OrderCancelDenialStrategy {

    protected static final Logger LOG = LogManager.getLogger(AdyenPartialOrderCancelDenialStrategy.class);

    private static final String ORDER_CANCEL_CONFIG_CANNOT_BE_NULL = "Order cancel config cannot be null";
    private static final String ORDER_MODEL_CANNOT_BE_NULL = "OrderModel cannot be null";
    private static final String THE_PARTIAL_CANCEL_IS_NOT_ALLOWED = "The partial cancel is not allowed.";

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderCancelDenialReason getCancelDenialReason(final OrderCancelConfigModel orderCancelConfigModel,
                                                         final OrderModel orderModel, final PrincipalModel principalModel,
                                                         final boolean partialCancel, final boolean partialEntryCancel) {
        validateParameterNotNull(orderCancelConfigModel, ORDER_CANCEL_CONFIG_CANNOT_BE_NULL);
        validateParameterNotNull(orderModel, ORDER_MODEL_CANNOT_BE_NULL);

        if (partialCancel || partialEntryCancel) {
            LOG.debug(THE_PARTIAL_CANCEL_IS_NOT_ALLOWED);
            return getReason();
        }

        return null;
    }
}
