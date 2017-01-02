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
package com.adyen.cockpits.cscockpit.widgets.controllers.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cscockpit.widgets.controllers.strategies.impl.AbstractProductUrlStrategy;


/**
 * Product URL Strategy that builds URLs to accelerator products.
 */
public class AcceleratorProductUrlStrategy extends AbstractProductUrlStrategy
{
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	@Required
	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}

	@Override
	protected String getProductUrl(final BaseSiteModel site, final String productUrlPath)
	{
		return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(site, false, productUrlPath);
	}
}
