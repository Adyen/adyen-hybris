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
package com.adyen.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.orderprocessing.events.SendNotPickedUpConsignmentCanceledMessageEvent;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.springframework.beans.factory.annotation.Required;


/**
 * Listener for SendNotPickedUpConsignmentMessageEvent events.
 */
public class SendNotPickedUpConsignmentCanceledMessageEventListener extends
		AbstractSiteEventListener<SendNotPickedUpConsignmentCanceledMessageEvent>
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	/**
	 * @return the businessProcessService
	 */
	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	protected void onSiteEvent(final SendNotPickedUpConsignmentCanceledMessageEvent sendNotPickedUpConsignmentCanceledMessageEvent)
	{
		final ConsignmentModel consignmentModel = sendNotPickedUpConsignmentCanceledMessageEvent.getProcess().getConsignment();
		final ConsignmentProcessModel consignmentProcessModel = getBusinessProcessService().createProcess(
				"sendNotPickedUpConsignmentCanceledEmailProcess-" + consignmentModel.getCode() + "-" + System.currentTimeMillis(),
				"sendNotPickedUpConsignmentCanceledEmailProcess");
		consignmentProcessModel.setConsignment(consignmentModel);
		getModelService().save(consignmentProcessModel);
		getBusinessProcessService().startProcess(consignmentProcessModel);
	}

	@Override
	protected boolean shouldHandleEvent(final SendNotPickedUpConsignmentCanceledMessageEvent event)
	{
		final AbstractOrderModel order = event.getProcess().getConsignment().getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);
		final BaseSiteModel site = order.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return SiteChannel.B2C.equals(site.getChannel());
	}
}
