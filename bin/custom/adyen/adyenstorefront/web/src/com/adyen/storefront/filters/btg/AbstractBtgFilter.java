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
package com.adyen.storefront.filters.btg;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.btg.events.AbstractBTGRuleDataEvent;
import de.hybris.platform.servicelayer.event.EventService;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * FilterBean to produce request scoped BTG events
 */
public abstract class AbstractBtgFilter extends OncePerRequestFilter
{
	private static final Logger LOG = Logger.getLogger(AbstractBtgFilter.class);

	private EventService eventService;
	private SiteConfigService siteConfigService;

	private static final String CONFIG_BTG_ENABLED = "storefront.btg.enabled";

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	@Override
	public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{
		if (getSiteConfigService().getBoolean(CONFIG_BTG_ENABLED, false))
		{
			final AbstractBTGRuleDataEvent<Serializable> event = getEvent(request);
			publishEvent(event);
			try
			{
				chain.doFilter(request, response);
			}
			finally
			{
				if (isRequestScoped() && event != null)
				{
					publishEvent(getCleanupEvent(event));
				}
			}
		}
		else
		// BTG is disabled, just run the filter chain
		{
			chain.doFilter(request, response);
		}

	}

	/**
	 * Factory method to retrieve the BTG event
	 * 
	 * @param request
	 * @return event or null, if no event should be sent
	 */
	protected abstract AbstractBTGRuleDataEvent getEvent(final HttpServletRequest request);

	/**
	 * Factory method to retrieve the inverse event for request scoped events.
	 * 
	 * @param event
	 * @return inverse event or null, if no event should be sent
	 */
	protected AbstractBTGRuleDataEvent getCleanupEvent(final AbstractBTGRuleDataEvent event)
	{
		AbstractBTGRuleDataEvent result = null;
		try
		{
			result = event.getClass().getConstructor(event.getClass()).newInstance(event);
		}
		catch (final Exception e)
		{
			LOG.warn("Could not create cleanup event", e);
		}
		return result;
	}

	/**
	 * Retrieves if the generated event should be request scoped
	 * 
	 * @return true, if the event is request scoped
	 */
	protected boolean isRequestScoped()
	{
		return false;
	}

	/**
	 * Publish an BTG event failure tolerant.
	 * 
	 * @param event
	 */
	protected void publishEvent(final AbstractBTGRuleDataEvent<?> event)
	{
		if (event != null)
		{
			try
			{
				eventService.publishEvent(event);
			}
			catch (final Exception e)
			{
				LOG.error("Could not publish event", e);
			}
		}
	}

	/**
	 * @param siteConfigService
	 *           the siteConfigService to set
	 */
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	/**
	 * @return the siteConfigService
	 */
	public SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}
}
