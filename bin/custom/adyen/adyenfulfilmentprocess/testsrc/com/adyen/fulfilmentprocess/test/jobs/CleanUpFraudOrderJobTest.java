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
package com.adyen.fulfilmentprocess.test.jobs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.JobDao;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import com.adyen.fulfilmentprocess.constants.AdyenFulfilmentProcessConstants;
import com.adyen.fulfilmentprocess.jobs.CleanUpFraudOrderJob;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CleanUpFraudOrderJobTest extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;
	@Resource
	private JobDao jobDao;
	@Resource
	private CronJobService cronJobService;
	@Resource
	private BusinessProcessService businessProcessService;

	@Resource(name = "cleanUpFraudOrderJob")
	private CleanUpFraudOrderJob job;


	private final BusinessProcessService mockedService = mock(BusinessProcessService.class);

	private final CronJobModel cronJob = new CronJobModel();
	private String processCode = null;
	private ServicelayerJobModel jobModel = null;
	private static final String JOB_NAME = "cleanUpFraudOrderJob";
	private static final String PROCESS_DEFINITION_NAME = AdyenFulfilmentProcessConstants.ORDER_PROCESS_NAME;
	private static final String EVENT_NAME_SUFFIX = "_CleanUpEvent";

	@Before
	public void setup() throws Exception
	{
		findJobModel();
		job.setBusinessProcessService(mockedService);
	}

	@After
	public void after()
	{
		job.setBusinessProcessService(businessProcessService);
	}

	private void findJobModel()
	{
		final List<JobModel> jobModels = jobDao.findJobs(JOB_NAME);
		if (jobModels.size() > 1)
		{
			throw new IllegalStateException("Expected only one job model, but get: " + jobModels.size());
		}
		if (jobModels.isEmpty())
		{
			jobModel = new ServicelayerJobModel();
			jobModel.setCode("cleanUpFraudOrderJob");
			jobModel.setSpringId("cleanUpFraudOrderJob");
			modelService.save(jobModel);
		}
		else
		{
			jobModel = (ServicelayerJobModel) jobModels.get(0);
		}
	}

	private void addCronJob()
	{
		cronJob.setCode("cleanUpFraudOrderCronJob");
		cronJob.setJob(jobModel);
		modelService.save(cronJob);
	}

	private void setupProcess()
	{
		processCode = PROCESS_DEFINITION_NAME + UUID.randomUUID();
		final String action = "waitForCleanUp";
		final BusinessProcessModel businessProcess = businessProcessService.createProcess(processCode, PROCESS_DEFINITION_NAME);
		final ProcessTaskModel processTask = new ProcessTaskModel();
		processTask.setProcess(businessProcess);
		processTask.setAction(action);
		processTask.setRunnerBean("anything");
		modelService.save(processTask);
		businessProcess.setCurrentTasks(Collections.singletonList(processTask));
		modelService.save(businessProcess);
	}

	@Test
	public void testCheckIfCleanUpFraudEventWasSent()
	{
		//given
		setupProcess();
		addCronJob();
		//when
		final boolean performJobSynchronously = true;
		cronJobService.performCronJob(cronJob, performJobSynchronously);
		//then
		final int expectedNumberOfTriggeredEvents = 1;
		final String expectedEventName = processCode + EVENT_NAME_SUFFIX;
		verify(mockedService, times(expectedNumberOfTriggeredEvents)).triggerEvent(expectedEventName);
	}
}
