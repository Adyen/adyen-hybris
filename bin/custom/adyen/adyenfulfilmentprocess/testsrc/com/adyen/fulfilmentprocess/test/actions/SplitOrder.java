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
package com.adyen.fulfilmentprocess.test.actions;

import de.hybris.platform.core.Registry;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import com.adyen.fulfilmentprocess.constants.AdyenFulfilmentProcessConstants;

import java.util.Arrays;

import org.apache.log4j.Logger;


/**
 *
 */
public class SplitOrder extends TestActionTemp
{
	private static final Logger LOG = Logger.getLogger(SplitOrder.class);

	private int subprocessCount = 1;

	public void setSubprocessCount(final int subprocessCount)
	{
		this.subprocessCount = subprocessCount;
	}

	@Override
	public String execute(final BusinessProcessModel process) throws Exception //NOPMD
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass());

		final BusinessProcessParameterModel warehouseCounter = new BusinessProcessParameterModel();
		warehouseCounter.setName(AdyenFulfilmentProcessConstants.CONSIGNMENT_COUNTER);
		warehouseCounter.setProcess(process);
		warehouseCounter.setValue(Integer.valueOf(subprocessCount));
		save(warehouseCounter);

		final BusinessProcessParameterModel params = new BusinessProcessParameterModel();
		params.setName(AdyenFulfilmentProcessConstants.PARENT_PROCESS);
		params.setValue(process.getCode());


		for (int i = 0; i < subprocessCount; i++)
		{
			final ConsignmentProcessModel consProcess = modelService.create(ConsignmentProcessModel.class);
			consProcess.setParentProcess((OrderProcessModel) process);
			consProcess.setCode(process.getCode() + "_" + i);
			consProcess.setProcessDefinitionName("consignment-process-test");
			params.setProcess(consProcess);
			consProcess.setContextParameters(Arrays.asList(params));
			consProcess.setState(ProcessState.CREATED);
			modelService.save(consProcess);
			getBusinessProcessService().startProcess(consProcess);
			LOG.info("Subprocess: " + process.getCode() + "_" + i + " started");
		}

		//getQueueService().actionExecuted(process, this);
		return "OK";
	}



	/**
	 * @return the businessProcessService
	 */
	@Override
	public BusinessProcessService getBusinessProcessService()
	{
		return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
	}

}
