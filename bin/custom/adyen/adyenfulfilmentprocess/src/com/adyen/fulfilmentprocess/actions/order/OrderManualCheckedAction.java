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
package com.adyen.fulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.task.RetryLaterException;

import java.util.HashSet;
import java.util.Set;


public class OrderManualCheckedAction extends AbstractOrderAction<OrderProcessModel>
{
	public enum Transition
	{
		OK, NOK, UNDEFINED;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();
			for (final Transition transitions : Transition.values())
			{
				res.add(transitions.toString());
			}
			return res;
		}
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	@Override
	public final String execute(final OrderProcessModel process) throws RetryLaterException, Exception
	{
		return executeAction(process).toString();
	}

	protected Transition executeAction(final OrderProcessModel process)
	{
		ServicesUtil.validateParameterNotNull(process, "Process cannot be null");

		final OrderModel order = process.getOrder();
		ServicesUtil.validateParameterNotNull(order, "Order in process cannot be null");
		if (order.getFraudulent() != null)
		{
			final OrderHistoryEntryModel historyLog = createHistoryLog(
					"Order Manually checked by CSA - Fraud = " + order.getFraudulent(), order);
			modelService.save(historyLog);
			if (order.getFraudulent().booleanValue())
			{
				order.setStatus(OrderStatus.SUSPENDED);
				getModelService().save(order);
				return Transition.NOK;
			}
			return Transition.OK;
		}
		return Transition.UNDEFINED;
	}
}
