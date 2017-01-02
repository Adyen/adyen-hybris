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
package com.adyen.cockpits.cmscockpit.wizard.strategies;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cockpit.session.impl.CreateContext;
import de.hybris.platform.core.model.media.MediaModel;
import com.adyen.cockpits.cockpit.wizard.strategies.DefaultImageMediaPredefinedValuesStrategy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

public class CmsImageMediaPredefinedValuesStrategy extends DefaultImageMediaPredefinedValuesStrategy
{
	private CMSAdminSiteService cmsAdminSiteService;

	@Override
	public Map<String, Object> getPredefinedValues(final CreateContext paramCreateContext)
	{
		final Map<String, Object> ret = super.getPredefinedValues(paramCreateContext);
		final CatalogVersionModel activeCatalogVersion = getCmsAdminSiteService().getActiveCatalogVersion();
		if (activeCatalogVersion != null)
		{
			ret.put(MediaModel._TYPECODE + "." + MediaModel.CATALOGVERSION, activeCatalogVersion);
		}

		return ret;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}
}
