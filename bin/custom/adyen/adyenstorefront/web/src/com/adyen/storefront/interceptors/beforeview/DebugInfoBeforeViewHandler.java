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

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.util.Config;
import com.adyen.storefront.interceptors.BeforeViewHandler;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/**
 * BeforeViewHandler that adds additional debug information to the request attributes so that these can be output into the
 * page to help with development or production issues.
 *
 * Simply serializes the jalo session attributes to a string stored in the request attributes.
 *
 * Debug output is typically rendered into the view by the debugFooter.tag.
 */
public class DebugInfoBeforeViewHandler implements BeforeViewHandler
{
	private static final String SHOW_STOREFRONT_DEBUG_INFO_PROPERTY_KEY = "storefront.show.debug.info";

	private static final String SHOW_STOREFRONT_DEBUG_INFO = "showStorefrontDebugInfo";
	private static final String JALO_SESSION_ATTRIBUTES = "storefrontDebugJaloSessionAttributes";

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
	{
		final boolean showDebug = Config.getBoolean(SHOW_STOREFRONT_DEBUG_INFO_PROPERTY_KEY, false);

		// Store the show debug flag in a request attribute
		request.setAttribute(SHOW_STOREFRONT_DEBUG_INFO, Boolean.valueOf(showDebug));

		if (showDebug)
		{
			final JaloSession currentSession = JaloSession.getCurrentSession();

			final TreeMap<String, Object> attributeMap = new TreeMap<String, Object>();
			// Build up the session attributes as a request attribute
			attributeMap.putAll(currentSession.getAttributes());
			// Add the session id as an attribute
			attributeMap.put("JaloSession ID", currentSession.getSessionID());

			request.setAttribute(JALO_SESSION_ATTRIBUTES, mapToString(attributeMap));
		}
	}

	protected String mapToString(final Map<String, Object> map)
	{
		final StringBuilder buf = new StringBuilder();

		for (final Map.Entry<String, Object> entry : map.entrySet())
		{
			buf.append('[').append(entry.getKey()).append("] = [").append(entry.getValue()).append("]\n");
		}

		return buf.toString();
	}
}
