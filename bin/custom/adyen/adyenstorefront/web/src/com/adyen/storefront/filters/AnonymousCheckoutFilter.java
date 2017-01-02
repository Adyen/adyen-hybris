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
package com.adyen.storefront.filters;

import com.adyen.storefront.security.GuestCheckoutCartCleanStrategy;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;


public class AnonymousCheckoutFilter extends OncePerRequestFilter
{

	private GuestCheckoutCartCleanStrategy guestCheckoutCartCleanStrategy;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		getGuestCheckoutCartCleanStrategy().cleanGuestCart(request);
		filterChain.doFilter(request, response);
	}

	public GuestCheckoutCartCleanStrategy getGuestCheckoutCartCleanStrategy()
	{
		return guestCheckoutCartCleanStrategy;
	}

	@Required
	public void setGuestCheckoutCartCleanStrategy(final GuestCheckoutCartCleanStrategy guestCheckoutCartCleanStrategy)
	{
		this.guestCheckoutCartCleanStrategy = guestCheckoutCartCleanStrategy;
	}

}
