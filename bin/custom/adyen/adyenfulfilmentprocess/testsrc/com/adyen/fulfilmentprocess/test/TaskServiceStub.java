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
package com.adyen.fulfilmentprocess.test;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskConditionModel;
import de.hybris.platform.task.TaskEngine;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *
 */
public class TaskServiceStub implements TaskService
{
	private List<TaskModel> tasks = new ArrayList<TaskModel>();

	public void runTasks() throws RetryLaterException
	{
		final List<TaskModel> tmpTasks = new ArrayList<TaskModel>();
		for (final TaskModel task : tasks)
		{
			if (task.getConditions() == null || task.getConditions().isEmpty())
			{
				tmpTasks.add(task);
			}
		}
		for (final TaskModel task : tmpTasks)
		{
			runTask(task);
		}

	}

	public ProcessTaskModel runProcessTask(final String beanId) throws RetryLaterException
	{
		ProcessTaskModel processTask = null;
		for (final TaskModel task : tasks)
		{
			if (task instanceof ProcessTaskModel)
			{
				if (((ProcessTaskModel) task).getAction().equals(beanId))
				{
					processTask = (ProcessTaskModel) task;
					break;
				}
			}
		}
		if (processTask != null)
		{
			runTask(processTask);

		}
		return processTask;
	}

	private void runTask(final TaskModel task) throws RetryLaterException
	{
		final TaskRunner ret = Registry.getApplicationContext().getBean(task.getRunnerBean(), TaskRunner.class);
		tasks.remove(task);
		ret.run(this, task);
	}

	public List<TaskModel> cleanup()
	{
		final List<TaskModel> res = tasks;
		tasks = new ArrayList<TaskModel>();
		return res;
	}

	@Override
	public void triggerEvent(final String event)
	{
		final List<TaskModel> tmpTasks = new ArrayList<TaskModel>();

		for (final TaskModel task : tasks)
		{
			for (final TaskConditionModel condition : task.getConditions())
			{
				if (condition.getUniqueID().equals(event))
				{
					tmpTasks.add(task);

				}
			}
		}

		for (final TaskModel task : tmpTasks)
		{
			try
			{
				runTask(task);
			}
			catch (final RetryLaterException e)
			{
				// YTODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void scheduleTask(final TaskModel task)
	{
		synchronized (tasks)
		{
			tasks.add(task);
		}

	}

	@Override
	public TaskEngine getEngine()
	{
		return null;
	}

	public List<TaskModel> getTasks()
	{
		return tasks;
	}

	@Override
	public void triggerEvent(final String event, final Date expirationDate) {
		throw new RuntimeException("Not implemented");
		
	}

}
