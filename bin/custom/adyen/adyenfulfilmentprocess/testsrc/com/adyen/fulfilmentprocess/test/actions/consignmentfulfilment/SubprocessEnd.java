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
package com.adyen.fulfilmentprocess.test.actions.consignmentfulfilment;

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import com.adyen.fulfilmentprocess.constants.AdyenFulfilmentProcessConstants;

import org.apache.log4j.Logger;


/**
 *
 */
public class SubprocessEnd extends AbstractTestConsActionTemp
{
	private static final Logger LOG = Logger.getLogger(SubprocessEnd.class);

	@Override
	public String execute(final BusinessProcessModel process) throws Exception //NOPMD
	{
		super.execute(process);

		final ConsignmentProcessModel consProc = (ConsignmentProcessModel) process;
		getBusinessProcessService().triggerEvent(consProc.getParentProcess().getCode() + "_ConsignmentSubprocessEnd");
		LOG.info("Process: " + process.getCode() + " fire event "
				+ AdyenFulfilmentProcessConstants.CONSIGNMENT_SUBPROCESS_END_EVENT_NAME);
		((ConsignmentProcessModel) process).setDone(true);
		modelService.save(process);
		return getResult();

	}
}
