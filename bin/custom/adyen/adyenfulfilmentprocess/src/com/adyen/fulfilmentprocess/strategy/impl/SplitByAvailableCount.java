/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package com.adyen.fulfilmentprocess.strategy.impl;

import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.AbstractSplittingStrategy;

import org.springframework.beans.factory.annotation.Required;


public class SplitByAvailableCount extends AbstractSplittingStrategy
{
	private CommerceStockService commerceStockService;

	@Override
	public Object getGroupingObject(final AbstractOrderEntryModel orderEntry)
	{
		if (orderEntry.getDeliveryPointOfService() != null)
		{
			final Long stock = getCommerceStockService().getStockLevelForProductAndPointOfService(orderEntry.getProduct(),
					orderEntry.getDeliveryPointOfService());
			return Boolean.valueOf(stock == null || stock.longValue() >= orderEntry.getQuantity().longValue());
		}
		else
		{
			Long stock = Long.valueOf(0);
			if(orderEntry.getOrder().getStore() != null)
			{
			stock = getCommerceStockService().getStockLevelForProductAndBaseStore(orderEntry.getProduct(),
					orderEntry.getOrder().getStore());
			}
			return Boolean.valueOf(stock == null || stock.longValue() >= orderEntry.getQuantity().longValue());
		}
	}

	@Override
	public void afterSplitting(final Object groupingObject, final ConsignmentModel createdOne)
	{
		//nothing to do		
	}

	protected CommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	@Required
	public void setCommerceStockService(final CommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}
}
