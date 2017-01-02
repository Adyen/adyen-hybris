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
package com.adyen.core.checkout.flow.impl;

import de.hybris.platform.acceleratorservices.checkout.flow.CheckoutFlowStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Base {@link CheckoutFlowStrategy} implementation, gives {@link #defaultStrategy} fallback functionality.
 */
public abstract class AbstractCheckoutFlowStrategy implements CheckoutFlowStrategy
{
	private CheckoutFlowStrategy defaultStrategy;

	protected CheckoutFlowStrategy getDefaultStrategy()
	{
		return defaultStrategy;
	}

	@Required
	public void setDefaultStrategy(final CheckoutFlowStrategy defaultStrategy)
	{
		this.defaultStrategy = defaultStrategy;
	}
}
