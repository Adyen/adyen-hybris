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

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

import org.apache.log4j.Logger;


/**
 *
 */
public class SubprocessesCompletedAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SubprocessesCompletedAction.class);

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass());

		LOG.info("Process: " + process.getCode() + " is checking for  " + process.getConsignmentProcesses().size() + " subprocess results");

		for (final ConsignmentProcessModel subProcess : process.getConsignmentProcesses())
		{
			if (!subProcess.isDone())
			{
				LOG.info("Process: " + process.getCode() + " found subprocess " + subProcess.getCode() + " incomplete -> wait again!");
				return Transition.NOK;
			}
			LOG.info("Process: " + process.getCode() + " found subprocess " + subProcess.getCode() + " complete ...");
		}
		LOG.info("Process: " + process.getCode() + " found all subprocesses complete");
		return Transition.OK;
	}
}
