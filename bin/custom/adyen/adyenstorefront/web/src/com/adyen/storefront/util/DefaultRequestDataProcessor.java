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
package com.adyen.storefront.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;


public class DefaultRequestDataProcessor implements RequestDataValueProcessor
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
		return new HashMap<String, String>();
	}

	@Override
	public String processUrl(final HttpServletRequest request, final String url)
	{
		return url;
	}

}
