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
package com.adyen.core.checkout.flow.impl;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.acceleratorservices.checkout.flow.CheckoutFlowStrategy;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Uid site mapping based flow strategy. If there is no mapping configured for current site uid uses
 * {@link #getDefaultStrategy()} flow.
 * 
 * @since 4.6
 * @spring.bean siteCheckoutFlowStrategy
 */
public class SiteCheckoutFlowStrategy extends AbstractCheckoutFlowStrategy
{
	private static final Logger LOG = Logger.getLogger(SiteCheckoutFlowStrategy.class);

	private CMSSiteService cmsSiteService;
	private Map<String, CheckoutFlowStrategy> siteMappings;

	@Override
	public CheckoutFlowEnum getCheckoutFlow()
	{
		final String siteUid = getCurrentSiteUid();
		final CheckoutFlowStrategy strategy = getSiteMappings().get(siteUid);

		if (strategy == null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Using default " + getDefaultStrategy() + " for given site " + siteUid);
			}
			return getDefaultStrategy().getCheckoutFlow();
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Found a " + strategy + " for given site " + siteUid);
			}
			return strategy.getCheckoutFlow();
		}
	}

	protected String getCurrentSiteUid()
	{
		final CMSSiteModel siteModel = getCmsSiteService().getCurrentSite();
		Preconditions.checkNotNull(siteModel, "Could not find current site");

		final String siteUid = siteModel.getUid();
		Preconditions.checkNotNull(siteUid, "Site uid for  current site " + siteModel + " is empty");
		return siteUid;
	}

	protected Map<String, CheckoutFlowStrategy> getSiteMappings()
	{
		return siteMappings;
	}

	@Required
	public void setSiteMappings(final Map<String, CheckoutFlowStrategy> strategiesMappings)
	{
		this.siteMappings = strategiesMappings;
	}

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}
}
