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
package com.adyen.storefront.controllers.integration;


import de.hybris.platform.acceleratorservices.payment.PaymentService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Controller to handle merchant callbacks from a subscription provider
 */
@Controller
public class MerchantCallbackController extends BaseIntegrationController
{
	protected static final Logger LOG = Logger.getLogger(MerchantCallbackController.class);

	@Resource(name = "acceleratorPaymentService")
	private PaymentService acceleratorPaymentService;


	@RequestMapping(value = "/integration/merchant_callback", method = RequestMethod.POST)
	public void process(final HttpServletRequest request, final HttpServletResponse response)
	{
		initializeSiteFromRequest(request);

		try
		{
			acceleratorPaymentService.handleCreateSubscriptionCallback(getParameterMap(request));
		}
		finally
		{
			//Kill this session at the end of the request processing in order to reduce the server overhead, otherwise
			//this session will hang around until it's timed out.
			final HttpSession session = request.getSession(false);
			if (session != null)
			{
				session.invalidate();
			}
		}

		response.setStatus(HttpServletResponse.SC_OK);
	}
}
