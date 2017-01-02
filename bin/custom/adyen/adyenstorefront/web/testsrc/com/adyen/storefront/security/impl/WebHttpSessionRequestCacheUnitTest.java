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
package com.adyen.storefront.security.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentMatcher;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;


@UnitTest
public class WebHttpSessionRequestCacheUnitTest
{
	//
	private final WebHttpSessionRequestCache cache = new WebHttpSessionRequestCache();

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void testSaveRequest()
	{

		BDDMockito.given(request.getRequestURL()).willReturn(new StringBuffer("dummy"));
		BDDMockito.given(request.getScheme()).willReturn("dummy");
		BDDMockito.given(request.getHeader("referer")).willReturn("some blah");

		cache.saveRequest(request, response);


		Mockito.verify(request.getSession()).setAttribute(Mockito.eq("SPRING_SECURITY_SAVED_REQUEST"),
				Mockito.argThat(new DefaultSavedRequestArgumentMatcher("some blah")));
	}

	class DefaultSavedRequestArgumentMatcher extends ArgumentMatcher<DefaultSavedRequest>
	{

		private final String url;

		DefaultSavedRequestArgumentMatcher(final String url)
		{
			this.url = url;
		}

		@Override
		public boolean matches(final Object argument)
		{
			if (argument instanceof DefaultSavedRequest)
			{
				final DefaultSavedRequest arg = (DefaultSavedRequest) argument;
				return url.equals(arg.getRedirectUrl());
			}
			return false;
		}

	}
}
