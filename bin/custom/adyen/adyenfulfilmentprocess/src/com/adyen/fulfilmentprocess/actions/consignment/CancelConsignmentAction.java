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

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;


/**
 * Mark consignment as cancelled.
 */
public class CancelConsignmentAction extends AbstractProceduralAction<ConsignmentProcessModel>
{
	@Override
	public void executeAction(final ConsignmentProcessModel process)
	{
		final ConsignmentModel consignment = process.getConsignment();
		if (consignment != null)
		{
			consignment.setStatus(ConsignmentStatus.CANCELLED);
			getModelService().save(consignment);
		}
	}
}
