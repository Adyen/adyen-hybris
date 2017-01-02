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
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 * Extension of HttpSessionRequestCache that allows pass through of cookies from the current request. This is required
 * to allow the GUIDInterceptor to see the secure cookie written during authentication.
 * 
 * The <tt>RequestCache</tt> stores the <tt>SavedRequest</tt> in the HttpSession, this is then restored perfectly.
 * Unfortunately the saved request also hides new cookies that have been written since the saved request was created.
 * This implementation allows the current request's cookie values to override the cookies within the saved request.
 */
public class WebHttpSessionRequestCache extends HttpSessionRequestCache implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final String REFERER = "referer";

	static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

	private transient PortResolver portResolver = new PortResolverImpl();
	private transient RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;
	private boolean createSessionAllowed = true;

	private SessionService sessionService;

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}



	@Override
	public void setRequestMatcher(final RequestMatcher requestMatcher)
	{
		this.requestMatcher = requestMatcher;
		super.setRequestMatcher(requestMatcher);
	}


	@Override
	public void setPortResolver(final PortResolver portResolver)
	{
		this.portResolver = portResolver;
		super.setPortResolver(portResolver);
	}


	@Override
	public void setCreateSessionAllowed(final boolean createSessionAllowed)
	{
		this.createSessionAllowed = createSessionAllowed;
	}

	@Override
	public void saveRequest(final HttpServletRequest request, final HttpServletResponse response)
	{
		//this might be called while in ExceptionTranslationFilter#handleSpringSecurityException in this case base implementation
		if (SecurityContextHolder.getContext().getAuthentication() == null)
		{
			super.saveRequest(request, response);
		}
		else
		{
			final SavedRequest savedBefore = getRequest(request, response);
			if (savedBefore != null)//to not override request saved by ExceptionTranslationFilter#handleSpringSecurityException
			{
				return;
			}

			if (getRequestMatcher().matches(request))
			{
				final DefaultSavedRequest savedRequest = new DefaultSavedRequest(request, getPortResolver())
				{
					private final String referer = request.getHeader(REFERER);
					private final String contextPath = request.getContextPath();

					@Override
					public String getRedirectUrl()
					{
						return calculateRelativeRedirectUrl(contextPath, referer);
					}
				};



				if (isCreateSessionAllowed() || request.getSession(false) != null)
				{
					request.getSession().setAttribute(SAVED_REQUEST, savedRequest);
					logger.debug("DefaultSavedRequest added to Session: " + savedRequest);
				}
			}
			else
			{
				logger.debug("Request not saved as configured RequestMatcher did not match");
			}
		}
	}


	protected boolean isCreateSessionAllowed()
	{
		return createSessionAllowed;
	}

	protected PortResolver getPortResolver()
	{
		return portResolver;
	}

	protected RequestMatcher getRequestMatcher()
	{
		return requestMatcher;
	}

	@Override
	public HttpServletRequest getMatchingRequest(final HttpServletRequest request, final HttpServletResponse response)
	{
		HttpServletRequest result = super.getMatchingRequest(request, response);
		if (result != null)
		{
			result = new CookieMergingHttpServletRequestWrapper(result, request);
		}
		return result;
	}

	protected String calculateRelativeRedirectUrl(final String contextPath, final String url)
	{
		if (UrlUtils.isAbsoluteUrl(url))
		{
			String relUrl = url.substring(url.indexOf("://") + 3);
			String modifiedContextPath = contextPath;
			final String urlEncodingAttributes = getSessionService().getAttribute(WebConstants.URL_ENCODING_ATTRIBUTES);
			if (urlEncodingAttributes != null && !url.contains(urlEncodingAttributes)
					&& modifiedContextPath.contains(urlEncodingAttributes))
			{
				modifiedContextPath = StringUtils.remove(modifiedContextPath, urlEncodingAttributes);
			}
			if (StringUtils.isEmpty(relUrl))
			{
				relUrl = "/";
			}
			else
			{
				relUrl = relUrl.substring(relUrl.indexOf(modifiedContextPath) + modifiedContextPath.length());
			}
			return relUrl;
		}
		else
		{
			return url;
		}
	}




}
