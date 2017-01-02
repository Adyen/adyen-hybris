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
package com.adyen.storefront.web.wrappers;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;


/**
 * HttpServletRequestWrapper class to override the value of the context path that contains the encoding attributes. This
 * makes sure that we don't have encoded attributes for ServletPath.
 */
public class UrlEncodeHttpRequestWrapper extends HttpServletRequestWrapper
{
	private final String pattern;

	public UrlEncodeHttpRequestWrapper(final HttpServletRequest request, final String pattern)
	{
		super(request);
		this.pattern = pattern;
	}

	@Override
	public String getContextPath()
	{
		return super.getContextPath() + "/" + pattern;
	}

	@Override
	public String getRequestURI()
	{
		final String originalRequestURI = super.getRequestURI();
		final String tempRequestURI = StringUtils.remove(originalRequestURI, super.getContextPath());

		if (StringUtils.isEmpty(tempRequestURI) || StringUtils.containsOnly(tempRequestURI, "/"))
		{
			return "/";
		}
		return originalRequestURI;
	}


	@Override
	public String getServletPath()
	{
		final String originalServletPath = super.getServletPath();
		if (("/").equals(originalServletPath) || ("/" + pattern).equals(originalServletPath)
				|| ("/" + pattern + "/").equals(originalServletPath))
		{
			return "";
		}
		else if (urlPatternChecker(originalServletPath, pattern))
		{
			return StringUtils.replace(originalServletPath, "/" + pattern + "/", "/");
		}
		return originalServletPath;
	}

	protected boolean urlPatternChecker(final String urlToBeChecked, final String pattern)
	{
		boolean containsPattern = StringUtils.contains(urlToBeChecked, "/" + pattern + "/");
		if (!containsPattern)
		{
			final String[] splitUrl = urlToBeChecked.split("/");
			final String last = splitUrl[splitUrl.length - 1];
			if (last.equalsIgnoreCase(pattern))
			{
				containsPattern = true;
			}
		}
		return containsPattern;
	}
}
