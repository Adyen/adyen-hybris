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
package com.adyen.fulfilmentprocess.test.beans;

import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * 
 */
public class ActionExecution
{
	private final BusinessProcessModel process;
	private final AbstractAction action;

	public ActionExecution(final BusinessProcessModel process, final AbstractAction action)
	{
		this.process = process;
		this.action = action;
	}

	public AbstractAction getAction()
	{
		return action;
	}

	public BusinessProcessModel getProcess()
	{
		return process;
	}

}
