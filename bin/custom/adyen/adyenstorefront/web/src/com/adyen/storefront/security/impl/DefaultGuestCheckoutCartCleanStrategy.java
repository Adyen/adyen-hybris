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
package com.adyen.storefront.security.impl;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import com.adyen.storefront.security.GuestCheckoutCartCleanStrategy;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultGuestCheckoutCartCleanStrategy implements GuestCheckoutCartCleanStrategy
{
	public static final String AJAX_REQUEST_HEADER_NAME = "X-Requested-With";

	private String checkoutURLPattern;
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	private CartService cartService;
	private SessionService sessionService;
	private UserService userService;

	@Override
	public void cleanGuestCart(final HttpServletRequest request)
	{

		if (Boolean.TRUE.equals(getSessionService().getAttribute(WebConstants.ANONYMOUS_CHECKOUT))
				&& getCheckoutCustomerStrategy().isAnonymousCheckout()
				&& StringUtils.isBlank(request.getHeader(AJAX_REQUEST_HEADER_NAME)) && isGetMethod(request)
				&& !checkWhetherURLContainsCheckoutPattern(request))
		{
			final CartModel cartModel = getCartService().getSessionCart();
			cartModel.setDeliveryAddress(null);
			cartModel.setDeliveryMode(null);
			cartModel.setPaymentInfo(null);
			cartModel.setUser(getUserService().getAnonymousUser());
			getCartService().saveOrder(cartModel);
			getSessionService().removeAttribute(WebConstants.ANONYMOUS_CHECKOUT);
			getSessionService().removeAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID);
		}

	}

	@Override
	public boolean checkWhetherURLContainsCheckoutPattern(final HttpServletRequest request)
	{
		return request.getRequestURL().toString().matches(getCheckoutURLPattern());
	}

	protected boolean isGetMethod(final HttpServletRequest httpRequest)
	{
		return "GET".equalsIgnoreCase(httpRequest.getMethod());
	}

	protected CheckoutCustomerStrategy getCheckoutCustomerStrategy()
	{
		return checkoutCustomerStrategy;
	}

	@Required
	public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy)
	{
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public String getCheckoutURLPattern()
	{
		return checkoutURLPattern;
	}

	@Required
	public void setCheckoutURLPattern(final String checkoutURLPattern)
	{
		this.checkoutURLPattern = checkoutURLPattern;
	}

}
