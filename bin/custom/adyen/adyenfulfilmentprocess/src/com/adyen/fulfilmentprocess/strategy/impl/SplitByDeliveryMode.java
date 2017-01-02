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

import de.hybris.platform.commerceservices.delivery.dao.PickupDeliveryModeDao;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.AbstractSplittingStrategy;

import org.springframework.beans.factory.annotation.Required;


public class SplitByDeliveryMode extends AbstractSplittingStrategy
{
	private PickupDeliveryModeDao pickupDeliveryModeDao;

	@Override
	public Object getGroupingObject(final AbstractOrderEntryModel orderEntry)
	{
		return orderEntry.getDeliveryMode() != null ? orderEntry.getDeliveryMode()
				: orderEntry.getDeliveryPointOfService() == null ? orderEntry.getOrder().getDeliveryMode()
						: getPickupDeliveryModeDao().findPickupDeliveryModesForAbstractOrder(orderEntry.getOrder()).get(0);
	}

	@Override
	public void afterSplitting(final Object groupingObject, final ConsignmentModel createdOne)
	{
		createdOne.setDeliveryMode((DeliveryModeModel) groupingObject);
	}

	protected PickupDeliveryModeDao getPickupDeliveryModeDao()
	{
		return pickupDeliveryModeDao;
	}

	@Required
	public void setPickupDeliveryModeDao(final PickupDeliveryModeDao pickupDeliveryModeDao)
	{
		this.pickupDeliveryModeDao = pickupDeliveryModeDao;
	}
}
