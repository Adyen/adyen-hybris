/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.adyen.v6.controllers;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.adyen.v6.security.AdyenNotificationAuthenticationProvider;
import com.adyen.v6.service.AdyenNotificationService;


@Controller
@RequestMapping(value = "/adyen/v6/notification/{baseSiteId}")

public class AdyenNotificationController {
	private static final Logger LOG = Logger.getLogger(AdyenNotificationController.class);

	@Resource(name = "adyenNotificationAuthenticationProvider")
	private AdyenNotificationAuthenticationProvider adyenNotificationAuthenticationProvider;

	@Resource(name = "adyenNotificationService")
	private AdyenNotificationService adyenNotificationService;

	private static final String RESPONSE_ACCEPTED = "[accepted]";
	private static final String RESPONSE_NOT_ACCEPTED = "[not-accepted]";

	@RequestMapping(value = "/json", method = RequestMethod.POST)
	@ResponseBody
	public String onReceive(@PathVariable final String baseSiteId, final HttpServletRequest request) {
		String requestString = null;
		try {
			//Parse response body by request input stream so that Spring doesn't try to deserialize
			requestString = IOUtils.toString(request.getInputStream());
		} catch (IOException e) {
			LOG.error(e);
			return RESPONSE_NOT_ACCEPTED;
		}

		LOG.debug("Received Adyen notification:" + requestString);
		if (! adyenNotificationAuthenticationProvider.authenticateBasic(request, baseSiteId)) {
			throw new AccessDeniedException("Wrong credentials. Please check your basesite, username and password.");
		}
		adyenNotificationService.saveNotifications(requestString);

        return RESPONSE_ACCEPTED;
    }
}
