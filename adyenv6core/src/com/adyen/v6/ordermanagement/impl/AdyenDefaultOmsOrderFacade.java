package com.adyen.v6.ordermanagement.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelEntryData;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelRequestData;
import de.hybris.platform.ordermanagementfacades.order.OmsOrderFacade;
import de.hybris.platform.ordermanagementfacades.order.impl.DefaultOmsOrderFacade;

import java.util.stream.Collectors;

/**
 * Extension of {@link DefaultOmsOrderFacade}. This has been extended to fix the ootb bug that set the order cancellation
 * as partial by default.
 */
public class AdyenDefaultOmsOrderFacade extends DefaultOmsOrderFacade implements OmsOrderFacade {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Boolean isPartialCancel(final OrderCancelRequestData orderCancelRequestData, final OrderModel order) {
        return !(orderCancelRequestData.getEntries().stream().map(OrderCancelEntryData::getOrderEntryNumber).collect(Collectors.toList())).
                containsAll(order.getEntries().stream().map(AbstractOrderEntryModel::getEntryNumber).collect(Collectors.toList()));
    }
}
