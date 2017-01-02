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
package com.adyen.storefront.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import com.adyen.storefront.controllers.ControllerConstants;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for web robots instructions
 */
@Controller
@Scope("tenant")
public class RobotsController extends AbstractController
{
	// Number of seconds in one day
	private static final String ONE_DAY = String.valueOf(60 * 60 * 24);

	@RequestMapping(value = "/robots.txt", method = RequestMethod.GET)
	public String getRobots(final HttpServletResponse response)
	{
		// Add cache control header to cache response for a day
		response.setHeader("Cache-Control", "public, max-age=" + ONE_DAY);

		return ControllerConstants.Views.Pages.Misc.MiscRobotsPage;
	}
}
