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
package com.adyen.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Velocity context for a forgotten password email.
 */
public class ForgottenPasswordEmailContext extends CustomerEmailContext
{
	private int expiresInMinutes = 30;
	private String token;

	public int getExpiresInMinutes()
	{
		return expiresInMinutes;
	}

	public void setExpiresInMinutes(final int expiresInMinutes)
	{
		this.expiresInMinutes = expiresInMinutes;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(final String token)
	{
		this.token = token;
	}

	public String getURLEncodedToken() throws UnsupportedEncodingException
	{
		return URLEncoder.encode(token, "UTF-8");
	}

	public String getRequestResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(),getUrlEncodingAttributes(), false, "/login/pw/request/external");
	}

	public String getSecureRequestResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(),getUrlEncodingAttributes(), true, "/login/pw/request/external");
	}

	public String getResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), getUrlEncodingAttributes(), false, "/login/pw/change",
				"token=" + getURLEncodedToken());
	}

	public String getSecureResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), getUrlEncodingAttributes(),true, "/login/pw/change",
				"token=" + getURLEncodedToken());
	}

	public String getDisplayResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(),getUrlEncodingAttributes(), false, "/my-account/update-password");
	}

	public String getDisplaySecureResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(),getUrlEncodingAttributes(), true, "/my-account/update-password");
	}

	@Override
	public void init(final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(storeFrontCustomerProcessModel, emailPageModel);
		if (storeFrontCustomerProcessModel instanceof ForgottenPasswordProcessModel)
		{
			setToken(((ForgottenPasswordProcessModel) storeFrontCustomerProcessModel).getToken());
		}
	}
}
