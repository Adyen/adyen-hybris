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
package com.adyen.storefront.filters;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 */
public class AnonymousCheckoutFilter extends OncePerRequestFilter
{

	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	private CartService cartService;
	private SessionService sessionService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
													final FilterChain filterChain) throws ServletException, IOException
	{
		if (!request.isSecure() && Boolean.TRUE.equals(getSessionService().getAttribute(WebConstants.ANONYMOUS_CHECKOUT)) &&
				getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			final CartModel cartModel = getCartService().getSessionCart();
			cartModel.setDeliveryAddress(null);
			cartModel.setDeliveryMode(null);
			cartModel.setPaymentInfo(null);
			getCartService().saveOrder(cartModel);
			getSessionService().removeAttribute(WebConstants.ANONYMOUS_CHECKOUT);
			getSessionService().removeAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID);
		}

		filterChain.doFilter(request, response);
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

}
