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
package com.adyen.storefront.security;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.util.PathMatcher;


/**
 * Custom request matcher that returns false for urls that match the urls found in the excludeUrlSet. All other urls
 * will return true.
 */
public class ExcludeUrlRequestMatcher implements RequestMatcher
{
	private static final Logger LOG = Logger.getLogger(ExcludeUrlRequestMatcher.class);

	private Set<String> excludeUrlSet;
	private PathMatcher pathMatcher;

	@Override
	public boolean matches(final HttpServletRequest request)
	{
		// Do not match patterns specified in the excludeUrlSet to the servletPath
		for (final String excludeUrl : getExcludeUrlSet())
		{
			if (getPathMatcher().match(excludeUrl, request.getServletPath()))
			{
				// Found an exclude pattern
				return false;
			}
		}

		// Not found an exclude URL that matched therefore ok to proceed
		return true;
	}

	protected Set<String> getExcludeUrlSet()
	{
		return excludeUrlSet;
	}

	@Required
	public void setExcludeUrlSet(final Set<String> excludeUrlSet)
	{
		final Set<String> validUrls = new HashSet<String>();

		// Ensure only valid urls are added to the excludeUrlSet
		for (final String url : excludeUrlSet)
		{
			if (url.charAt(0) == '/')
			{
				validUrls.add(url);
			}
			else
			{
				LOG.warn("Ignoring ExcludeUrl [" + url + "] as it is not valid");
			}
		}

		this.excludeUrlSet = validUrls;
	}

	protected PathMatcher getPathMatcher()
	{
		return pathMatcher;
	}

	@Required
	public void setPathMatcher(final PathMatcher pathMatcher)
	{
		this.pathMatcher = pathMatcher;
	}
}
