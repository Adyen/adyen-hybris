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
package com.adyen.storefront.interceptors.beforecontroller;

import de.hybris.platform.acceleratorfacades.device.DeviceDetectionFacade;
import com.adyen.storefront.interceptors.BeforeControllerHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;


/**
 * An interceptor to set up the request the detected device information.
 */
public class DeviceDetectionBeforeControllerHandler implements BeforeControllerHandler
{
	@Resource(name = "deviceDetectionFacade")
	private DeviceDetectionFacade deviceDetectionFacade;

	@Override
	public boolean beforeController(final HttpServletRequest request, final HttpServletResponse response, final HandlerMethod handler)
	{
		// Detect the device information for the current request
		deviceDetectionFacade.initializeRequest(request);
		return true;
	}
}
