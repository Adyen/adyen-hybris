package com.adyen.v6.populator;

import javax.annotation.Nonnull;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;

/**
 * Created by rikt on 5/11/17.
 */
public class TaxValuesOrderEntryPopulator extends OrderEntryPopulator {

    @Override
    public void populate(@Nonnull final AbstractOrderEntryModel source, @Nonnull final OrderEntryData target)
    {
        target.setTaxValues(source.getTaxValues());

    }
}
