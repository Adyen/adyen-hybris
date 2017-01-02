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
package com.adyen.storefront.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;


/**
 * A <code>RequestDataValueProcessor</code> that pushes a hidden field with a CSRF token into forms. This process
 * implements the {@link #getExtraHiddenFields(HttpServletRequest)} method to push the CSRF token obtained from
 * {@link CSRFTokenManager}. To register this processor to automatically process all Spring based forms register it as a
 * Spring bean named 'requestDataValueProcessor' as shown below:
 *
 * <pre>
 *  &lt;bean name="requestDataValueProcessor" class="com.eyallupu.blog.springmvc.controller.csrf.CSRFRequestDataValueProcessor"/&gt;
 * </pre>
 *
 */
public class CSRFRequestDataValueProcessor implements RequestDataValueProcessor
{


	@Override
	public String processAction(final HttpServletRequest request, final String action)
	{
		return action;
	}

	@Override
	public String processFormFieldValue(final HttpServletRequest request, final String name, final String value, final String type)
	{
		return value;
	}

	@Override
	public Map<String, String> getExtraHiddenFields(final HttpServletRequest request)
	{
		final Map<String, String> hiddenFields = new HashMap<String, String>();
		hiddenFields.put(CSRFTokenManager.CSRF_PARAM_NAME, CSRFTokenManager.getTokenForSession(request.getSession()));
		return hiddenFields;
	}

	@Override
	public String processUrl(final HttpServletRequest request, final String url)
	{
		return url;
	}

}