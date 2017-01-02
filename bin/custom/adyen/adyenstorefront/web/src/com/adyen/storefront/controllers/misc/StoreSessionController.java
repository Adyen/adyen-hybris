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
package com.adyen.storefront.controllers.misc;

import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import com.adyen.storefront.filters.StorefrontFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;


/**
 * Controller for store session. Used to change the session language, currency and experience level.
 */
@Controller
@Scope("tenant")
@RequestMapping("/_s")
public class StoreSessionController extends AbstractController
{
	private static final Logger LOG = Logger.getLogger(StoreSessionController.class);

	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "uiExperienceService")
	private UiExperienceService uiExperienceService;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource(name = "urlEncoderService")
	private UrlEncoderService urlEncoderService;


	@RequestMapping(value = "/language", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String selectLanguage(@RequestParam("code") final String isoCode, final HttpServletRequest request)
	{
		final String previousLanguage = storeSessionFacade.getCurrentLanguage().getIsocode();
		storeSessionFacade.setCurrentLanguage(isoCode);
		if (!userFacade.isAnonymousUser())
		{
			userFacade.syncSessionLanguage();
		}
		return (urlEncoderService.isLanguageEncodingEnabled()) ? getReturnRedirectUrlForUrlEncoding(request, previousLanguage,
				storeSessionFacade.getCurrentLanguage().getIsocode()) : getReturnRedirectUrlWithoutReferer(request);
	}

	@RequestMapping(value = "/currency", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String selectCurrency(@RequestParam("code") final String isoCode, final HttpServletRequest request)
	{
		final String previousCurrency = storeSessionFacade.getCurrentCurrency().getIsocode();
		storeSessionFacade.setCurrentCurrency(isoCode);
		userFacade.syncSessionCurrency();
		return (urlEncoderService.isCurrencyEncodingEnabled()) ? getReturnRedirectUrlForUrlEncoding(request, previousCurrency,
				storeSessionFacade.getCurrentCurrency().getIsocode()) : getReturnRedirectUrlWithoutReferer(request);
	}

	@RequestMapping(value = "/ui-experience", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String selectUiExperienceLevel(@RequestParam("level") final String uiExperienceLevelString,
			final HttpServletRequest request)
	{
		if (uiExperienceLevelString == null || uiExperienceLevelString.isEmpty())
		{
			// Empty value - clear the override
			uiExperienceService.setOverrideUiExperienceLevel(null);
		}
		else
		{
			final UiExperienceLevel uiExperienceLevel = toUiExperienceLevel(uiExperienceLevelString);
			if (uiExperienceLevel == null)
			{
				LOG.warn("Unknown UiExperience level [" + uiExperienceLevelString + "] available values are: "
						+ Arrays.toString(getAvailableUiExperienceLevelsCodes()));
			}
			else
			{
				uiExperienceService.setOverrideUiExperienceLevel(uiExperienceLevel);
			}
		}

		// Always clear the prompt hide flag
		setHideUiExperienceLevelOverridePrompt(request, false);
		return getReturnRedirectUrl(request);
	}

	protected UiExperienceLevel toUiExperienceLevel(final String code)
	{
		if (code != null && !code.isEmpty())
		{
			try
			{
				return enumerationService.getEnumerationValue(UiExperienceLevel.class, code);
			}
			catch (final UnknownIdentifierException ignore)
			{
				// Ignore, return null
			}
		}
		return null;
	}

	protected List<UiExperienceLevel> getAvailableUiExperienceLevels()
	{
		return enumerationService.getEnumerationValues(UiExperienceLevel.class);
	}

	protected String[] getAvailableUiExperienceLevelsCodes()
	{
		final List<UiExperienceLevel> availableUiExperienceLevels = getAvailableUiExperienceLevels();
		if (availableUiExperienceLevels == null || availableUiExperienceLevels.isEmpty())
		{
			return new String[0];
		}

		final String[] codes = new String[availableUiExperienceLevels.size()];
		for (int i = 0; i < codes.length; i++)
		{
			codes[i] = availableUiExperienceLevels.get(i).getCode();
		}

		return codes;
	}

	@RequestMapping(value = "/ui-experience-level-prompt", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String selectUiExperienceLevelPrompt(@RequestParam("hide") final boolean hideFlag, final HttpServletRequest request)
	{
		setHideUiExperienceLevelOverridePrompt(request, hideFlag);
		return getReturnRedirectUrl(request);
	}

	protected void setHideUiExperienceLevelOverridePrompt(final HttpServletRequest request, final boolean flag)
	{
		request.getSession().setAttribute("hideUiExperienceLevelOverridePrompt", Boolean.valueOf(flag));
	}

	protected String getReturnRedirectUrl(final HttpServletRequest request)
	{
		final String referer = request.getHeader("Referer");
		if (referer != null && !referer.isEmpty())
		{
			return REDIRECT_PREFIX + referer;
		}
		return REDIRECT_PREFIX + '/';
	}

	protected String getReturnRedirectUrlWithoutReferer(final HttpServletRequest request)
	{
		final String originalReferer = (String) request.getSession().getAttribute(StorefrontFilter.ORIGINAL_REFERER);
		if (StringUtils.isNotBlank(originalReferer))
		{
			return REDIRECT_PREFIX + originalReferer;
		}

		final String referer = StringUtils.remove(request.getRequestURL().toString(), request.getServletPath());
		if (referer != null && !referer.isEmpty())
		{
			return REDIRECT_PREFIX + referer;
		}
		return REDIRECT_PREFIX + '/';
	}

	protected String getReturnRedirectUrlForUrlEncoding(final HttpServletRequest request, final String old, final String current)
	{
		final String originalReferer = (String) request.getSession().getAttribute(StorefrontFilter.ORIGINAL_REFERER);
		if (StringUtils.isNotBlank(originalReferer))
		{
			return REDIRECT_PREFIX + StringUtils.replace(originalReferer, "/" + old + "/", "/" + current + "/");
		}

		String referer = StringUtils.remove(request.getRequestURL().toString(), request.getServletPath());
		if (!StringUtils.endsWith(referer, "/"))
		{
			referer = referer + "/";
		}
		if (referer != null && !referer.isEmpty() && StringUtils.contains(referer, "/" + old + "/"))
		{
			return REDIRECT_PREFIX + StringUtils.replace(referer, "/" + old + "/", "/" + current + "/");
		}
		return REDIRECT_PREFIX + referer;
	}

	@ExceptionHandler(UnknownIdentifierException.class)
	public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request)
	{
		final Map<String, Object> currentFlashScope = RequestContextUtils.getOutputFlashMap(request);
		currentFlashScope.put(GlobalMessages.ERROR_MESSAGES_HOLDER, exception.getMessage());
		return REDIRECT_PREFIX + "/404";
	}
}
