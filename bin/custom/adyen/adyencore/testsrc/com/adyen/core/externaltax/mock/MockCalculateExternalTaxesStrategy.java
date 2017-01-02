/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.adyen.core.externaltax.mock;

import de.hybris.platform.commerceservices.externaltax.CalculateExternalTaxesStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.util.TaxValue;


/**
 * Base {@link CalculateExternalTaxesStrategy} implementation of external tax call to return an empty
 * ExternalTaxDocument
 * 
 */
public class MockCalculateExternalTaxesStrategy implements CalculateExternalTaxesStrategy
{
	/**
	 * Default implementation to return an empty tax document.
	 */
	@Override
	public ExternalTaxDocument calculateExternalTaxes(final AbstractOrderModel abstractOrder)
	{
		final ExternalTaxDocument externalDocument = new ExternalTaxDocument();


		if (abstractOrder == null)
		{
			throw new IllegalStateException("Order is null. Cannot apply external tax to it.");
		}

		for (int i = 0; i < abstractOrder.getEntries().size(); i++)
		{
			final TaxValue taxValue = new TaxValue("taxCode1", 2.0D, true, 2.0D, abstractOrder.getCurrency() == null ? "USD"
					: abstractOrder.getCurrency().getIsocode());
			externalDocument.setTaxesForOrderEntry(i + 1, taxValue);
		}

		final TaxValue taxValue = new TaxValue("taxCode1", 3.0D, true, 3.0D, abstractOrder.getCurrency() == null ? "USD"
				: abstractOrder.getCurrency().getIsocode());
		externalDocument.setShippingCostTaxes(taxValue);

		return externalDocument;
	}
}
