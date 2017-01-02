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
package com.adyen.storefront.filters.btg.support;

import javax.servlet.http.HttpServletRequest;


/**
 * Retrieve a key by parsing the URL
 */
public interface UrlParsingStrategy
{
	/**
	 * Parse the request url to retrieve a key
	 *
	 * @param request the request
	 * @return key or null, if no key could be parsed
	 */
	String parse(HttpServletRequest request);
}
