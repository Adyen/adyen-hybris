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
package com.adyen.storefront.interceptors.beforeview;

import de.hybris.platform.acceleratorfacades.device.DeviceDetectionFacade;
import de.hybris.platform.acceleratorfacades.device.data.DeviceData;
import de.hybris.platform.acceleratorservices.addonsupport.RequiredAddOnsNameProvider;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.enums.SiteTheme;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import com.adyen.storefront.interceptors.BeforeViewHandler;
import com.adyen.storefront.util.CSRFTokenManager;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;


/**
 * Interceptor to setup the paths to the UI resource paths in the model before passing it to the view. Sets up the path
 * to the web accessible UI resources for the following: * The current site * The current theme * The common resources
 * All of these paths are qualified by the current UiExperienceLevel
 */
public class UiThemeResourceBeforeViewHandler implements BeforeViewHandler
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(UiThemeResourceBeforeViewHandler.class);

	protected static final String COMMON = "common";
	protected static final String SHARED = "shared";
	protected static final String RESOURCE_TYPE_JAVASCRIPT = "javascript";
	protected static final String RESOURCE_TYPE_CSS = "css";

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "uiExperienceService")
	private UiExperienceService uiExperienceService;

	@Resource(name = "deviceDetectionFacade")
	private DeviceDetectionFacade deviceDetectionFacade;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	@Resource(name = "requiredAddOnsNameProvider")
	private RequiredAddOnsNameProvider requiredAddOnsNameProvider;

	@Resource(name = "commerceCommonI18NService")
	private CommerceCommonI18NService commerceCommonI18NService;

	private String defaultThemeName;

	protected String getDefaultThemeName()
	{
		return defaultThemeName;
	}

	@Required
	public void setDefaultThemeName(final String defaultThemeName)
	{
		this.defaultThemeName = defaultThemeName;
	}

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
	{
		final CMSSiteModel currentSite = cmsSiteService.getCurrentSite();

		final String siteName = currentSite.getUid();
		final String themeName = getThemeNameForSite(currentSite);
		final String uiExperienceCode = uiExperienceService.getUiExperienceLevel().getCode();
		final String uiExperienceCodeLower = uiExperienceCode.toLowerCase();
		final Object urlEncodingAttributes = request.getAttribute(WebConstants.URL_ENCODING_ATTRIBUTES);
		final String contextPath = StringUtils.remove(request.getContextPath(),
				(urlEncodingAttributes != null) ? urlEncodingAttributes.toString() : "");

		final String siteRootUrl = contextPath + "/_ui/" + uiExperienceCodeLower;
		final String sharedResourcePath = contextPath + "/_ui/" + SHARED;
		final String siteResourcePath = siteRootUrl + "/site-" + siteName;
		final String themeResourcePath = siteRootUrl + "/theme-" + themeName;
		final String commonResourcePath = siteRootUrl + "/" + COMMON;
		final LanguageModel currentLanguage = commerceCommonI18NService.getCurrentLanguage();

		modelAndView.addObject("contextPath", contextPath);
		modelAndView.addObject("sharedResourcePath", sharedResourcePath);
		modelAndView.addObject("siteResourcePath", siteResourcePath);
		modelAndView.addObject("themeResourcePath", themeResourcePath);
		modelAndView.addObject("commonResourcePath", commonResourcePath);
		modelAndView.addObject("siteRootUrl", siteRootUrl);
		modelAndView.addObject("language", (currentLanguage != null ? currentLanguage.getIsocode() : "en"));
		modelAndView.addObject("CSRFToken", CSRFTokenManager.getTokenForSession(request.getSession()));

		modelAndView.addObject("uiExperienceLevel", uiExperienceCode);

		final String detectedUiExperienceCode = uiExperienceService.getDetectedUiExperienceLevel().getCode();
		modelAndView.addObject("detectedUiExperienceCode", detectedUiExperienceCode);

		final UiExperienceLevel overrideUiExperienceLevel = uiExperienceService.getOverrideUiExperienceLevel();
		if (overrideUiExperienceLevel == null)
		{
			modelAndView.addObject("uiExperienceOverride", Boolean.FALSE);
		}
		else
		{
			modelAndView.addObject("uiExperienceOverride", Boolean.TRUE);
			modelAndView.addObject("overrideUiExperienceCode", overrideUiExperienceLevel.getCode());
		}

		final DeviceData currentDetectedDevice = deviceDetectionFacade.getCurrentDetectedDevice();
		modelAndView.addObject("detectedDevice", currentDetectedDevice);

		final List<String> dependantAddOns = requiredAddOnsNameProvider.getAddOns(request.getSession().getServletContext()
				.getServletContextName());

		modelAndView.addObject("addOnCommonCssPaths", getAddOnCommonCSSPaths(contextPath, uiExperienceCodeLower, dependantAddOns));
		modelAndView.addObject("addOnThemeCssPaths",
				getAddOnThemeCSSPaths(contextPath, themeName, uiExperienceCodeLower, dependantAddOns));
		modelAndView.addObject("addOnJavaScriptPaths",
				getAddOnJSPaths(contextPath, siteName, uiExperienceCodeLower, dependantAddOns));

	}

	protected List getAddOnCommonCSSPaths(final String contextPath, final String uiExperience, final List<String> addOnNames)
	{
		final String[] propertyNames = new String[]
		{ RESOURCE_TYPE_CSS + ".paths", //
				RESOURCE_TYPE_CSS + ".paths." + uiExperience //
		};

		return getAddOnResourcePaths(contextPath, addOnNames, propertyNames);
	}

	protected List getAddOnThemeCSSPaths(final String contextPath, final String themeName, final String uiExperience,
			final List<String> addOnNames)
	{
		final String[] propertyNames = new String[]
		{ RESOURCE_TYPE_CSS + ".paths." + uiExperience + "." + themeName };

		return getAddOnResourcePaths(contextPath, addOnNames, propertyNames);
	}

	protected List getAddOnJSPaths(final String contextPath, final String siteName, final String uiExperience,
			final List<String> addOnNames)
	{
		final String[] propertyNames = new String[]
		{ RESOURCE_TYPE_JAVASCRIPT + ".paths", //
				RESOURCE_TYPE_JAVASCRIPT + ".paths." + uiExperience //
		};

		return getAddOnResourcePaths(contextPath, addOnNames, propertyNames);
	}


	protected List getAddOnResourcePaths(final String contextPath, final List<String> addOnNames, final String[] propertyNames)
	{
		final List<String> addOnResourcePaths = new ArrayList<String>();

		for (final String addon : addOnNames)
		{
			for (final String propertyName : propertyNames)
			{
				final String addOnResourcePropertyValue = siteConfigService.getProperty(addon + "." + propertyName);
				if (addOnResourcePropertyValue != null)
				{
					final String[] propertyPaths = addOnResourcePropertyValue.split(";");
					for (final String propertyPath : propertyPaths)
					{
						addOnResourcePaths.add(contextPath + "/_ui/addons/" + addon + propertyPath);
					}
				}
			}
		}
		return addOnResourcePaths;
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
}
