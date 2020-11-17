/*
 *                       ######
 *                       ######
 * ############    ####( ######  #####. ######  ############   ############
 * #############  #####( ######  #####. ######  #############  #############
 *        ######  #####( ######  #####. ######  #####  ######  #####  ######
 * ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 * ###### ######  #####( ######  #####. ######  #####          #####  ######
 * #############  #############  #############  #############  #####  ######
 *  ############   ############  #############   ############  #####  ######
 *                                      ######
 *                               #############
 *                               ############
 *
 * Adyen Hybris Extension
 *
 * Copyright (c) 2020 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 */

package com.adyen.v6.security;

import com.adyen.v6.utils.SameSiteCookieAttributeAppenderUtils;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * This class uses code written by Igor Zarvanskyi and published on https://clutcher.github.io/post/hybris/same_site_login_issue/
 */
public class AdyenGUIDAuthenticationSuccessHandler implements AuthenticationSuccessHandler
{
	private GUIDCookieStrategy guidCookieStrategy;
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	private SameSiteCookieAttributeAppenderUtils sameSiteCookieAttributeAppenderUtils;

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException
	{
		getGuidCookieStrategy().setCookie(request, response);

		// onAuthenticationSuccess will commit response, so we won't be able to change it, that's why we should execute filter before it.
		getSameSiteCookieAttributeAppenderUtils().addSameSiteAttribute(request, response);

		getAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authentication);
	}

	protected GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	/**
	 * @param guidCookieStrategy the guidCookieStrategy to set
	 */
	@Required
	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}

	protected AuthenticationSuccessHandler getAuthenticationSuccessHandler()
	{
		return authenticationSuccessHandler;
	}

	/**
	 * @param authenticationSuccessHandler the authenticationSuccessHandler to set
	 */
	@Required
	public void setAuthenticationSuccessHandler(final AuthenticationSuccessHandler authenticationSuccessHandler)
	{
		this.authenticationSuccessHandler = authenticationSuccessHandler;
	}

	protected SameSiteCookieAttributeAppenderUtils getSameSiteCookieAttributeAppenderUtils() {
		return sameSiteCookieAttributeAppenderUtils;
	}

	public void setSameSiteCookieAttributeAppenderUtils(SameSiteCookieAttributeAppenderUtils sameSiteCookieAttributeAppenderUtils) {
		this.sameSiteCookieAttributeAppenderUtils = sameSiteCookieAttributeAppenderUtils;
	}
}
