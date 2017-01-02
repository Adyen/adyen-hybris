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
package com.adyen.storefront.interceptors.beforeview;

import de.hybris.platform.acceleratorservices.config.HostConfigService;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.config.ConfigIntf;
import com.adyen.storefront.interceptors.BeforeViewHandler;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;


public class AnalyticsPropertiesBeforeViewHandler implements BeforeViewHandler
{
	@Resource(name = "hostConfigService")
	private HostConfigService hostConfigService;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;
	// Listener - listens to changes on the frontend and update the MapCache.
	private ConfigIntf.ConfigChangeListener cfgChangeListener;
	// HashMap used to cache jirafe properties from config.properties. Not Using Cache implementations as it is 
	// a simple,non growable cache s
	private static Map jirafeMapCache;

	private static final String JIRAFE_API_URL = "jirafeApiUrl";
	private static final String JIRAFE_API_TOKEN = "jirafeApiToken";
	private static final String JIRAFE_APPLICATION_ID = "jirafeApplicationId";
	private static final String JIRAFE_VERSION = "jirafeVersion";
	private static final String ANALYTICS_TRACKING_ID = "googleAnalyticsTrackingId";
	private static final String JIRAFE_DATA_URL = "jirafeDataUrl";
	private static final String JIRAFE_SITE_ID = "jirafeSiteId";
	private static final String JIRAFE_PREFIX = "jirafe";
	private static final String GOOGLE_PREFIX = "googleAnalyticsTrackingId";

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
	{
		// Create the change listener and register it to listen when the config properties are changed in the platform
		if (cfgChangeListener == null)
		{
			registerConfigChangeLister();
		}
		final String serverName = request.getServerName();
		// Add config properties for google analytics
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Google.ANALYTICS_TRACKING_ID, ANALYTICS_TRACKING_ID);
		// Add config properties for jirafe analytics
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Jirafe.API_URL, JIRAFE_API_URL);
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Jirafe.API_TOKEN, JIRAFE_API_TOKEN);
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Jirafe.APPLICATION_ID, JIRAFE_APPLICATION_ID);
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Jirafe.VERSION, JIRAFE_VERSION);
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Jirafe.DATA_URL, JIRAFE_DATA_URL);

		// Lookup a currency specific jirafe site id first, and only if it is missing fallback to the default site id
		final String currencyIso = commonI18NService.getCurrentCurrency().getIsocode().toLowerCase();
		final String currSpecKey = ThirdPartyConstants.Jirafe.SITE_ID + "." + currencyIso;
		final String nonSpecKey = ThirdPartyConstants.Jirafe.SITE_ID;
		if (jirafeMapCache.get(currSpecKey) == null)
		{
			final String currencySpecificJirafeSiteId = hostConfigService.getProperty(currSpecKey, serverName);
			jirafeMapCache.put(currSpecKey, currencySpecificJirafeSiteId);
		}
		if (jirafeMapCache.get(currSpecKey) != null
				&& org.apache.commons.lang.StringUtils.isNotBlank(jirafeMapCache.get(currSpecKey).toString()))
		{
			modelAndView.addObject(JIRAFE_SITE_ID, jirafeMapCache.get(currSpecKey));
		}
		else
		{
			// Fallback to the non-currency specific value
			if (jirafeMapCache.get(nonSpecKey) == null)
			{
				final String jirafeSiteId = hostConfigService.getProperty(ThirdPartyConstants.Jirafe.SITE_ID, serverName);
				jirafeMapCache.put(nonSpecKey, jirafeSiteId);
			}
			modelAndView.addObject(JIRAFE_SITE_ID, jirafeMapCache.get(nonSpecKey));
		}
	}

	protected class ConfigChangeListener implements ConfigIntf.ConfigChangeListener
	{
		@Override
		public void configChanged(final String key, final String newValue)
		{
			// Config Listener listen to changes on the platform config and updates the cache.
			if (key.startsWith(JIRAFE_PREFIX) || key.startsWith(GOOGLE_PREFIX))
			{
				jirafeMapCache.remove(key);
				jirafeMapCache.put(key, newValue);
			}
		}
	}

	protected void registerConfigChangeLister()
	{
		final ConfigIntf config = Registry.getMasterTenant().getConfig();
		cfgChangeListener = new ConfigChangeListener();
		config.registerConfigChangeListener(cfgChangeListener);
	}

	protected void addHostProperty(final String serverName, final ModelAndView modelAndView, final String configKey,
			final String modelKey)
	{
		/*
		 * Changes made to cache the jirafe properties files in a HashMap. The first time the pages are accessed the
		 * values are read from the properties file & written on to a cache and the next time onwards it is accessed from
		 * the cache.
		 */
		if (jirafeMapCache == null)
		{
			jirafeMapCache = new HashMap();
		}
		if (jirafeMapCache.get(configKey) == null)
		{
			jirafeMapCache.put(configKey, hostConfigService.getProperty(configKey, serverName));
		}
		modelAndView.addObject(modelKey, jirafeMapCache.get(configKey));
	}
}
