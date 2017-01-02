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
package com.adyen.facades.flow.impl;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Specialised version of the DefaultCheckoutFlowFacade that allows the checkout flow and pci strategy to be overridden
 * in the session. This is primarily used for demonstration purposes and you may not need to use this sub-class in your
 * environment.
 */
public class SessionOverrideCheckoutFlowFacade extends DefaultCheckoutFlowFacade
{
	private static final Logger LOG = Logger.getLogger(SessionOverrideCheckoutFlowFacade.class);

	public static final String SESSION_KEY_CHECKOUT_FLOW = "SessionOverrideCheckoutFlow-CheckoutFlow";
	public static final String SESSION_KEY_SUBSCRIPTION_PCI_OPTION = "SessionOverrideCheckoutFlow-SubscriptionPciOption";


	private SessionService sessionService;

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Override
	public CheckoutFlowEnum getCheckoutFlow()
	{
		CheckoutFlowEnum sessionOverride = getSessionService().getAttribute(SESSION_KEY_CHECKOUT_FLOW);
		if (getCurrentUserForCheckout().getType() != null)
		{
			if (getCurrentUserForCheckout().getType().equals(CustomerType.valueOf(CustomerType.GUEST.getCode())))
			{
				LOG.info("Guest user, resetting CheckoutFlow to [" + CheckoutFlowEnum.MULTISTEP + "]");
				sessionOverride = CheckoutFlowEnum.MULTISTEP;
			}
		}
		if (sessionOverride != null)
		{
			LOG.info("Session Override CheckoutFlow [" + sessionOverride + "]");
			return sessionOverride;
		}

		// Fallback to default
		return super.getCheckoutFlow();
	}

	@Override
	public CheckoutPciOptionEnum getSubscriptionPciOption()
	{
		final CheckoutPciOptionEnum sessionOverride = getSessionService().getAttribute(SESSION_KEY_SUBSCRIPTION_PCI_OPTION);
		if (sessionOverride != null)
		{
			LOG.info("Session Override SubscriptionPciOption [" + sessionOverride + "]");
			return sessionOverride;
		}

		// Fallback to default
		return super.getSubscriptionPciOption();
	}

	public static void resetSessionOverrides()
	{
		final SessionService sessionService = getStaticSessionService();
		sessionService.removeAttribute(SESSION_KEY_CHECKOUT_FLOW);
		sessionService.removeAttribute(SESSION_KEY_SUBSCRIPTION_PCI_OPTION);
	}

	public static void setSessionOverrideCheckoutFlow(final CheckoutFlowEnum checkoutFlow)
	{
		getStaticSessionService().setAttribute(SESSION_KEY_CHECKOUT_FLOW, checkoutFlow);
	}

	public static void setSessionOverrideSubscriptionPciOption(final CheckoutPciOptionEnum checkoutPciOption)
	{
		getStaticSessionService().setAttribute(SESSION_KEY_SUBSCRIPTION_PCI_OPTION, checkoutPciOption);
	}

	protected static SessionService getStaticSessionService()
	{
		return Registry.getApplicationContext().getBean("sessionService", SessionService.class);
	}
}
