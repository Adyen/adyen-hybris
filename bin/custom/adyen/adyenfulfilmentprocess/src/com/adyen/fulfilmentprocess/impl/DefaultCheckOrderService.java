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
package com.adyen.fulfilmentprocess.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import com.adyen.fulfilmentprocess.CheckOrderService;


public class DefaultCheckOrderService implements CheckOrderService
{

	@Override
	public boolean check(final OrderModel order)
	{
		if (!order.getCalculated().booleanValue())
		{
			// Order must be calculated
			return false;
		}
		if (order.getEntries().isEmpty())
		{
			// Order must have some lines
			return false;
		}
		else if (order.getPaymentInfo() == null)
		{
			// Order must have some payment info to use in the process
			return false;
		}
		else
		{
			// Order delivery options must be valid
			return checkDeliveryOptions(order);
		}
	}

	protected boolean checkDeliveryOptions(final OrderModel order)
	{
		if (order.getDeliveryMode() == null)
		{
			// Order must have an overall delivery mode 
			return false;
		}

		if (order.getDeliveryAddress() == null)
		{
			for (final AbstractOrderEntryModel entry : order.getEntries())
			{
				if (entry.getDeliveryPointOfService() == null && entry.getDeliveryAddress() == null)
				{
					// Order and Entry have no delivery address and some entries are not for pickup
					return false;
				}
			}
		}

		return true;
	}
}
