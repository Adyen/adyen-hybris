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
package com.adyen.storefront.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;


/**
 */
public interface BeforeControllerHandler
{
	/**
	 * Called before the DispatcherServlet calls the controller.
	 *
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return <code>true</code> if the execution chain should proceed with the
	 * next interceptor or the handler itself. Else, DispatcherServlet assumes
	 * that this interceptor has already dealt with the response itself.
	 * @throws Exception in case of errors
	 */
	boolean beforeController(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception;
}
