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
package com.adyen.fulfilmentprocess.actions.consignment;

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import com.adyen.fulfilmentprocess.constants.AdyenFulfilmentProcessConstants;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class SubprocessEndAction extends AbstractProceduralAction<ConsignmentProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SubprocessEndAction.class);

	private BusinessProcessService businessProcessService;

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	@Override
	public void executeAction(final ConsignmentProcessModel process)
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass());

		try
		{
			// simulate different ending times
			Thread.sleep((long) (Math.random() * 2000));
		}
		catch (final InterruptedException e)
		{
			// can't help it
		}

		process.setDone(true);

		save(process);
		LOG.info("Process: " + process.getCode() + " wrote DONE marker");

		getBusinessProcessService().triggerEvent(
				process.getParentProcess().getCode() + "_"
						+ AdyenFulfilmentProcessConstants.CONSIGNMENT_SUBPROCESS_END_EVENT_NAME);
		LOG.info("Process: " + process.getCode() + " fired event "
				+ AdyenFulfilmentProcessConstants.CONSIGNMENT_SUBPROCESS_END_EVENT_NAME);
	}
}
