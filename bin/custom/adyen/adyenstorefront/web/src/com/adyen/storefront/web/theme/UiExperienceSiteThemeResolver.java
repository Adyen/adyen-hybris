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
package com.adyen.storefront.web.theme;

import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.enums.SiteTheme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.theme.AbstractThemeResolver;


/**
 * Resolve the spring theme name from the CMSSite.
 * The spring theme name is built from the CMSSite UID and the CMSSite Theme.
 */
public class UiExperienceSiteThemeResolver extends AbstractThemeResolver
{
	public static final String THEME_REQUEST_ATTRIBUTE_NAME = UiExperienceSiteThemeResolver.class.getName() + ".THEME";

	private CMSSiteService cmsSiteService;
	private UiExperienceService uiExperienceService;

	@Override
	public String resolveThemeName(final HttpServletRequest request)
	{
		// Lookup the cached theme name in the request
		String themeName = (String) request.getAttribute(THEME_REQUEST_ATTRIBUTE_NAME);

		if (themeName == null)
		{
			// Resolve Theme from CMSSiteService
			themeName = determineDefaultThemeName();

			// Cache the theme in the request attributes
			if (themeName != null)
			{
				request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, themeName);
			}
		}

		return themeName;
	}

	protected String determineDefaultThemeName()
	{
		final UiExperienceLevel uiExperienceLevel = getUiExperienceService().getUiExperienceLevel();

		// Resolve Theme from CMSSiteService
		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
		if (currentSite != null)
		{
			return combineSiteAndTheme(uiExperienceLevel.getCode(), currentSite.getUid(), getThemeNameForSite(currentSite));
		}
		return null;
	}

	protected String getThemeNameForSite(final CMSSiteModel site)
	{
		final SiteTheme theme = site.getTheme();
		if (theme != null)
		{
			final String themeCode = theme.getCode();
			if (themeCode != null && !themeCode.isEmpty())
			{
				return themeCode;
			}
		}
		return getDefaultThemeName();
	}

	protected String combineSiteAndTheme(final String uiExperienceLevel, final String siteUid, final String themeName)
	{
		return uiExperienceLevel + "," + siteUid + "," + themeName;
	}

	@Override
	public void setThemeName(final HttpServletRequest arg0, final HttpServletResponse arg1, final String arg2)
	{
		throw new UnsupportedOperationException("Cannot change theme - use a different theme resolution strategy");
	}

	/**
	 * @return the cmsSiteService
	 */
	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	/**
	 * @param cmsSiteService the CMSSiteService to set
	 */
	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	protected UiExperienceService getUiExperienceService()
	{
		return uiExperienceService;
	}

	@Required
	public void setUiExperienceService(final UiExperienceService uiExperienceService)
	{
		this.uiExperienceService = uiExperienceService;
	}
}
