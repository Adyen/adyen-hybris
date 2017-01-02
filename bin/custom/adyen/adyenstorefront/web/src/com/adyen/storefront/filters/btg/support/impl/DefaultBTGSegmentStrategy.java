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
package com.adyen.storefront.filters.btg.support.impl;

import de.hybris.platform.acceleratorcms.services.CMSPageContextService;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGEvaluationMethod;
import de.hybris.platform.btg.enums.BTGResultScope;
import de.hybris.platform.btg.segment.SegmentEvaluationException;
import de.hybris.platform.btg.servicelayer.services.evaluator.impl.SessionBTGEvaluationContextProvider;
import de.hybris.platform.btg.services.BTGEvaluationService;
import de.hybris.platform.btg.services.BTGResultService;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import com.adyen.storefront.filters.btg.support.BTGSegmentStrategy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Evaluate the BTG segments. Only evaluated once per request.
 */
public class DefaultBTGSegmentStrategy implements BTGSegmentStrategy
{
	private static final String BTG_SEGMENT_EVALUATED = "btgSegmentEvaluated";
	private static final String CONFIG_BTG_ENABLED = "storefront.btg.enabled";

	private static final Logger LOG = Logger.getLogger(DefaultBTGSegmentStrategy.class);

	private CMSSiteService cmsSiteService;
	private SessionService sessionService;
	private UserService userService;
	private BTGEvaluationService btgEvaluationService;
	private BTGResultService btgResultService;
	private boolean evaluateAnonymousSessions;
	private SiteConfigService siteConfigService;
	private CMSPageContextService cmsPageContextService;

	@Override
	public void evaluateSegment(final HttpServletRequest httpRequest) throws ServletException, IOException
	{
		// Check if we have already evaluated the BTG segments for this request
		if (!Boolean.TRUE.equals(httpRequest.getAttribute(BTG_SEGMENT_EVALUATED)))
		{
			// Flag the request as evaluated
			httpRequest.setAttribute(BTG_SEGMENT_EVALUATED, Boolean.TRUE);

			if (shouldEvaluateCurrentSession(httpRequest))
			{
				final BTGEvaluationService btgEvaluationService = getBtgEvaluationService();
				final BTGResultService btgResultService = getBtgResultService();
				final UserService userService = getUserService();
				final UserModel currentUser = userService.getCurrentUser();
				final CMSSiteService cmsSiteService = getCmsSiteService();
				final CMSSiteModel currentSite = cmsSiteService.getCurrentSite();

				try
				{
					final BTGEvaluationContext context;
					if (isPreviewDataModelValid(httpRequest))
					{
						//preview for BTGCockpit
						//always invoke FULL evaluation method and store results per session
						context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.FULL,
								BTGResultScope.SESSION);
					}
					else
					{
						//process normal request (i.e. normal browser non-btgcockpit request)
						//the evaluation method will be taken from segment!
						context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, null);
					}
					//right now we basically invalidate all results, because we don't specify  BTGRuleType
					//i.e. when user would like to invalidate only some type of rules he should specify this parameter
					btgResultService.invalidateEvaluationResults(currentSite, currentUser, context, null);
					btgEvaluationService.evaluateAllSegments(currentUser, currentSite, context);

					getSessionService().setAttribute(SessionBTGEvaluationContextProvider.BTG_CURRENT_EVALUATION_CONTEXT, context);
				}
				catch (final SegmentEvaluationException e)
				{
					// Log the exception but do not 'fail' the request
					LOG.error("Failed to evaluate BTG Segments", e);
				}
			}
		}
	}

	protected boolean shouldEvaluateCurrentSession(final HttpServletRequest httpRequest)
	{
		// Check if BTG enabled via configuration
		if (!getSiteConfigService().getBoolean(CONFIG_BTG_ENABLED, false))
		{
			return false;
		}

		// We cannot do any evaluation during logout, which we detect if there is no http session
		if (!hasHttpSession(httpRequest))
		{
			return false;
		}

		// If we are evaluating anonymous sessions then we are evaluating all sessions
		if (isEvaluateAnonymousSessions())
		{
			return true;
		}

		// Check if the current use is not anonymous
		return !getUserService().isAnonymousUser(getUserService().getCurrentUser());
	}

	protected boolean hasHttpSession(final HttpServletRequest httpRequest)
	{
		return httpRequest.getSession(false) != null;
	}

	/**
	 * Checks whether current Preview Data is valid (not removed)
	 * 
	 * @param httpRequest
	 *           current request
	 * @return true whether is valid otherwise false
	 */
	protected boolean isPreviewDataModelValid(final HttpServletRequest httpRequest)
	{
		return getCmsPageContextService().getCmsPageRequestContextData(httpRequest).getPreviewData() != null;
	}

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
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

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected BTGEvaluationService getBtgEvaluationService()
	{
		return btgEvaluationService;
	}

	@Required
	public void setBtgEvaluationService(final BTGEvaluationService btgEvaluationService)
	{
		this.btgEvaluationService = btgEvaluationService;
	}

	protected BTGResultService getBtgResultService()
	{
		return btgResultService;
	}

	@Required
	public void setBtgResultService(final BTGResultService btgResultService)
	{
		this.btgResultService = btgResultService;
	}

	/**
	 * Set to true if anonymous sessions as well as logged in sessions should be evaluated.
	 * If set to false only logged in sessions will be evaluated.
	 */
	protected boolean isEvaluateAnonymousSessions()
	{
		return evaluateAnonymousSessions;
	}

	@Required
	public void setEvaluateAnonymousSessions(final boolean evaluateAnonymousSessions)
	{
		this.evaluateAnonymousSessions = evaluateAnonymousSessions;
	}

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Required
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	protected CMSPageContextService getCmsPageContextService()
	{
		return cmsPageContextService;
	}

	@Required
	public void setCmsPageContextService(final CMSPageContextService cmsPageContextService)
	{
		this.cmsPageContextService = cmsPageContextService;
	}
}
