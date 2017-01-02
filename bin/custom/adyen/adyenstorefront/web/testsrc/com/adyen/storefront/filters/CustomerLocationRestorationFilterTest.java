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

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.customerlocation.CustomerLocationFacade;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;
import com.adyen.storefront.security.cookie.CustomerLocationCookieGenerator;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CustomerLocationRestorationFilterTest
{

	private final static String COOKIE_NAME = "customerLocationCookie";

	@InjectMocks
	private final CustomerLocationRestorationFilter filter = new CustomerLocationRestorationFilter();

	@Mock
	private CustomerLocationFacade customerLocationFacade;

	@Mock
	private CustomerLocationCookieGenerator cookieGenerator;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		final Cookie cookie = mock(Cookie.class);
		given(cookie.getName()).willReturn(COOKIE_NAME);
		final Cookie[] cookies = { cookie };

		given(request.getCookies()).willReturn(cookies);
	}


	@Test
	public void testBrandNewUser() throws ServletException, IOException
	{
		// Scenario:
		// User visits the website for the first time or has cleared all cookies
		given(cookieGenerator.getCookieName()).willReturn("");

		filter.doFilterInternal(request, response, filterChain);

		// Expect to check cookies
		verify(request, times(1)).getCookies();

		// Expect no userLocationData to be set
		verify(customerLocationFacade, never()).setUserLocationData(any(UserLocationData.class));
	}

	@Test
	public void testRegularUser() throws ServletException, IOException
	{
		// Scenario:
		// User selects a store location, then decides to pickup an item
		final UserLocationData userLocationData = mock(UserLocationData.class);
		given(customerLocationFacade.getUserLocationData()).willReturn(userLocationData);

		filter.doFilterInternal(request, response, filterChain);

		// Expect not to check cookies
		verify(request, never()).getCookies();

		// Expect no userLocationData to be set
		verify(customerLocationFacade, never()).setUserLocationData(any(UserLocationData.class));
	}

	@Test
	public void testLoggedInUserWhoLogsOut() throws ServletException, IOException
	{
		// Scenario:
		// User selects a store location, logs in, then logs out to end the session
		// The user then decides to pickup another item
		given(cookieGenerator.getCookieName()).willReturn(COOKIE_NAME);

		filter.doFilterInternal(request, response, filterChain);

		// Expect to check cookies
		verify(request, times(1)).getCookies();

		// Expect a userLocationData to be set
		verify(customerLocationFacade, times(1)).setUserLocationData(any(UserLocationData.class));
	}

}
