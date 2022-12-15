package com.adyen.v6.populator;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordermanagementfacades.order.converters.populator.OrderCancelPopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

public class AdyenOrderCancelPopulator extends OrderCancelPopulator {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final OrderModel source, final OrderData target) throws ConversionException {
        validateParameterNotNull(source, "Parameter source cannot be null.");
        validateParameterNotNull(target, "Parameter target cannot be null.");

        final UserModel userModel = getUserService().getCurrentUser();
        final boolean isFullCancellationAllowed = getOrderCancelService()
                .isCancelPossible(source, userModel, false, false).isAllowed();
        final boolean isPartialCancellationAllowed = getOrderCancelService()
                .isCancelPossible(source, userModel, false, false).isAllowed();
        target.setCancellable(isFullCancellationAllowed || isPartialCancellationAllowed);

        final Map<AbstractOrderEntryModel, Long> cancellableEntryQuantityMap = getCancelableEntriesStrategy()
                .getAllCancelableEntries(source, userModel);
        cancellableEntryQuantityMap.forEach((entry, qty) -> target.getEntries().forEach(orderEntryData -> {
            // Case of MultiD product
            if (isMultidimensionalEntry(orderEntryData)) {
                orderEntryData.getEntries().stream()
                        .filter(nestedOrderEntry -> nestedOrderEntry.getEntryNumber().equals(entry.getEntryNumber()))
                        .forEach(nestedOrderEntryData -> nestedOrderEntryData.setCancellableQty(qty));
            }
            // Case of non MultiD product
            else {
                if (orderEntryData.getEntryNumber().equals(entry.getEntryNumber())) {
                    orderEntryData.setCancellableQty(qty);
                }
            }
        }));
    }
}
