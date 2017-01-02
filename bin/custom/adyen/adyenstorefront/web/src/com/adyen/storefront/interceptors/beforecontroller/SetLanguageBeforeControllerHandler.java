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
package com.adyen.storefront.interceptors.beforecontroller;

import de.hybris.platform.commerceservices.i18n.LanguageResolver;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import com.adyen.storefront.interceptors.BeforeControllerHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;


/**
 * Allow the language to be changed per request.
 */
public class SetLanguageBeforeControllerHandler implements BeforeControllerHandler
{
	private static final Logger LOG = Logger.getLogger(SetLanguageBeforeControllerHandler.class);

	public static final String DEFAULT_LANG_PARAM = "lang";

	@Resource(name = "languageResolver")
	private LanguageResolver languageResolver;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	private String languageParameter = DEFAULT_LANG_PARAM;

	protected String getLanguageParameter()
	{
		return languageParameter;
	}

	// Optional - defaults to DEFAULT_LANG_PARAM
	public void setLanguageParameter(final String paramKey)
	{
		this.languageParameter = paramKey;
	}

	@Override
	public boolean beforeController(final HttpServletRequest request, final HttpServletResponse response, final HandlerMethod handler)
	{
		if (isGetMethod(request))
		{
			final String languageIdentifier = request.getParameter(languageParameter);
			if (StringUtils.isNotBlank(languageIdentifier))
			{
				try
				{
					final LanguageModel languageModel = languageResolver.getLanguage(languageIdentifier);
					commonI18NService.setCurrentLanguage(languageModel);
				}
				catch (final IllegalArgumentException ile)
				{
					LOG.warn("Can not set session language to [" + languageIdentifier + "]. " + ile.getMessage());
					if (LOG.isDebugEnabled())
					{
						LOG.debug("Exception setting the language", ile);
					}
				}
			}
		}
		return true;
	}

	protected boolean isGetMethod(final HttpServletRequest request)
	{
		return RequestMethod.GET.name().equalsIgnoreCase(request.getMethod());
	}
}
