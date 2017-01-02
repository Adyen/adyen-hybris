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
package com.adyen.storefront.security.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 *
 */
public class EnhancedCookieGeneratorTest {

	private static final String JSESSIONID = "JSESSIONID";
	private static final int NEVER_EXPIRES = -1;

	private final EnhancedCookieGenerator cookieGenerator = new EnhancedCookieGenerator();

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		cookieGenerator.setCookieDomain("what a domain");
		cookieGenerator.setCookieMaxAge(Integer.valueOf(NEVER_EXPIRES));
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@Test
	public void testClientSideCookieDefaultPath()
	{
		cookieGenerator.setCookieName(JSESSIONID);
		cookieGenerator.setHttpOnly(false);//client side
		cookieGenerator.addCookie(response, "cookie_monster");
		final Cookie expectedCookie = new Cookie(JSESSIONID, "cookie_monster");
		expectedCookie.setPath("/");
		expectedCookie.setSecure(false);
		expectedCookie.setMaxAge(NEVER_EXPIRES);
		expectedCookie.setDomain("what a domain");

		Mockito.verify(response).addCookie(Mockito.argThat(new CookieArgumentMatcher(expectedCookie)));
		assertNoHeaderAdjustments();
	}


	@Test
	public void testClientSideCookieDynamicPath()
	{
		cookieGenerator.setCookieName(JSESSIONID);
		cookieGenerator.setHttpOnly(false);//client side
		cookieGenerator.setCookieSecure(true);
		cookieGenerator.setUseDefaultPath(false);

		BDDMockito.given(request.getContextPath()).willReturn("/some_path");

		cookieGenerator.addCookie(response, "cookie_monster");

		final Cookie expectedCookie = new Cookie(JSESSIONID, "cookie_monster");
		expectedCookie.setPath("/some_path");
		expectedCookie.setSecure(true);
		expectedCookie.setMaxAge(NEVER_EXPIRES);
		expectedCookie.setDomain("what a domain");

		Mockito.verify(response).addCookie(Mockito.argThat(new CookieArgumentMatcher(expectedCookie)));
		assertNoHeaderAdjustments();
	}

	@Test
	public void testServerSideCookieDefaultPath()
	{
		cookieGenerator.setCookieName("guid");
		cookieGenerator.setHttpOnly(true);//server side
		BDDMockito.given(request.getContextPath()).willReturn("/");
		cookieGenerator.addCookie(response, "cookie_monster");
		cookieGenerator.setUseDefaultPath(false);

		final Cookie expectedCookie = new Cookie("guid", "cookie_monster");
		expectedCookie.setPath("/");
		expectedCookie.setSecure(false);
		expectedCookie.setMaxAge(NEVER_EXPIRES);
		expectedCookie.setDomain("what a domain");

		Mockito.verify(response).addHeader(EnhancedCookieGenerator.HEADER_COOKIE,
				"guid=cookie_monster; Version=1; Domain=\"what a domain\"; Path=/; HttpOnly");
	}


	@Test
	public void testServerSideCookieDynamicPath()
	{
		cookieGenerator.setCookieName(JSESSIONID);
		cookieGenerator.setHttpOnly(true);//server side
		cookieGenerator.setUseDefaultPath(false);
		BDDMockito.given(request.getContextPath()).willReturn("/some_path");
		cookieGenerator.addCookie(response, "cookie_monster");

		final Cookie expectedCookie = new Cookie(JSESSIONID, "cookie_monster");
		expectedCookie.setPath("/some_path");
		expectedCookie.setSecure(false);
		expectedCookie.setMaxAge(NEVER_EXPIRES);
		expectedCookie.setDomain("what a domain");

		Mockito.verify(response).addHeader(EnhancedCookieGenerator.HEADER_COOKIE,
				"JSESSIONID=cookie_monster; Version=1; Domain=\"what a domain\"; Path=/some_path; HttpOnly");
	}


	/**
	 *
	 */
	private void assertNoHeaderAdjustments()
	{
		Mockito.verify(response, Mockito.times(0)).addHeader(Mockito.anyString(), Mockito.anyString());
	}

	private class CookieArgumentMatcher extends ArgumentMatcher<Cookie>
	{
		private final Cookie expectedCookie;

		CookieArgumentMatcher(final Cookie cookie) {
			this.expectedCookie = cookie;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.mockito.ArgumentMatcher#matches(java.lang.Object)
		 */
		@Override
		public boolean matches(final Object argument) {
			if (argument instanceof Cookie) {
				final Cookie givenCookie = (Cookie) argument;
				if (givenCookie.getSecure() == expectedCookie.getSecure()
						&& givenCookie.getMaxAge() == expectedCookie.getMaxAge()
						&& givenCookie.getName().equals(expectedCookie.getName())
						&& (givenCookie.getPath() == expectedCookie.getPath() || givenCookie.getPath().equals(expectedCookie.getPath()))
						&& givenCookie.getValue().equals(expectedCookie.getValue())
						&& (givenCookie.getDomain() == expectedCookie.getDomain() || givenCookie.getDomain().equals(expectedCookie.getDomain()))) {
					return true;
				}
				Assert.fail("Expected \n[" + ToStringBuilder.reflectionToString(expectedCookie) + "]\n but got \n["
						+ ToStringBuilder.reflectionToString(argument) + "]");
			}
			return false;
		}
	}
}
