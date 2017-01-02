/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
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
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Listener for order submits.
 */
public class SubmitOrderEventListener extends AbstractSiteEventListener<SubmitOrderEvent>
{
	private static final Logger LOG = Logger.getLogger(SubmitOrderEventListener.class);

	private BusinessProcessService businessProcessService;
	private BaseStoreService baseStoreService;
	private ModelService modelService;

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
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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
	protected void onSiteEvent(final SubmitOrderEvent event)
	{
		final OrderModel order = event.getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);

		// Try the store set on the Order first, then fallback to the session
		BaseStoreModel store = order.getStore();
		if (store == null)
		{
			store = getBaseStoreService().getCurrentBaseStore();
		}

		if (store == null)
		{
			LOG.warn("Unable to start fulfilment process for order [" + order.getCode()
					+ "]. Store not set on Order and no current base store defined in session.");
		}
		else
		{
			final String fulfilmentProcessDefinitionName = store.getSubmitOrderProcessCode();
			if (fulfilmentProcessDefinitionName == null || fulfilmentProcessDefinitionName.isEmpty())
			{
				LOG.warn("Unable to start fulfilment process for order [" + order.getCode() + "]. Store [" + store.getUid()
						+ "] has missing SubmitOrderProcessCode");
			}
			else
			{
				final String processCode = fulfilmentProcessDefinitionName + "-" + order.getCode() + "-" + System.currentTimeMillis();
				final OrderProcessModel businessProcessModel = getBusinessProcessService().createProcess(
						processCode, fulfilmentProcessDefinitionName);
				businessProcessModel.setOrder(order);
				getModelService().save(businessProcessModel);
				getBusinessProcessService().startProcess(businessProcessModel);
				if (LOG.isInfoEnabled())
				{
					LOG.info(String.format("Started the process %s", processCode));
				}
			}
		}
	}

	@Override
	protected boolean shouldHandleEvent(final SubmitOrderEvent event)
	{
		final OrderModel order = event.getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);
		final BaseSiteModel site = order.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return SiteChannel.B2C.equals(site.getChannel());
	}
}
