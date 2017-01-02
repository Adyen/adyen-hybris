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
package com.adyen.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import com.adyen.storefront.controllers.ControllerConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * This controller handles Guest customer specific scenarios which doesn't need HTTPS requests.
 */
@Controller
@Scope("tenant")
@RequestMapping("/guest")
public class GuestOrderController extends AbstractPageController
{
	private static final String ORDER_GUID_PATH_VARIABLE_PATTERN = "{orderGUID:.*}";
	private static final String ORDER_DETAIL_CMS_PAGE = "order";
	private static final String REDIRECT_ORDER_EXPIRED = REDIRECT_PREFIX + "/orderExpired";
	private static final Logger LOG = Logger.getLogger(GuestOrderController.class);

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@RequestMapping(value = "/order/" + ORDER_GUID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String order(@PathVariable("orderGUID") final String orderGUID, final Model model, final HttpServletResponse response)
			throws CMSItemNotFoundException
	{
		try
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
			model.addAttribute("metaRobots", "no-index,no-foollow");
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
			final OrderData orderDetails = orderFacade.getOrderDetailsForGUID(orderGUID);
			model.addAttribute("orderData", orderDetails);
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Attempted to load a order that does not exist or is not visible");
			model.addAttribute("metaRobots", "no-index,no-follow");
			GlobalMessages.addErrorMessage(model, "system.error.page.not.found");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return ControllerConstants.Views.Pages.Error.ErrorNotFoundPage;
		}
		catch (final IllegalArgumentException ae)
		{
			return REDIRECT_ORDER_EXPIRED;

		}
		return ControllerConstants.Views.Pages.Guest.GuestOrderPage;
	}

}
