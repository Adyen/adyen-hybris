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
package com.adyen.core.hmc;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.Registry;
import de.hybris.platform.hmc.util.action.ActionEvent;
import de.hybris.platform.hmc.util.action.ActionResult;
import de.hybris.platform.hmc.util.action.ItemAction;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.ordersplitting.jalo.Consignment;
import de.hybris.platform.ordersplitting.jalo.ConsignmentProcess;
import de.hybris.platform.processengine.BusinessProcessService;


/**
 * 
 */
public class PickupConfirmationHMCAction extends ItemAction
{
	private static final long serialVersionUID = -5500487889164110964L;

	@Override
	public ActionResult perform(final ActionEvent event) throws JaloBusinessException
	{
		final Item item = getItem(event);
		if (item instanceof Consignment)
		{
			((Consignment) item).setStatus(EnumerationManager.getInstance().getEnumerationValue(ConsignmentStatus._TYPECODE,
					ConsignmentStatus.PICKUP_COMPLETE.getCode()));
			for (final ConsignmentProcess process : ((Consignment) item).getConsignmentProcesses())
			{
				getBusinessProcessService().triggerEvent(process.getCode() + "_ConsignmentPickup");
			}
			return new ActionResult(ActionResult.OK, true, false);
		}
		return new ActionResult(ActionResult.FAILED, false, false);
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return Registry.getApplicationContext().getBean("businessProcessService", BusinessProcessService.class);
	}
}
