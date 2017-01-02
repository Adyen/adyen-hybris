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
package com.adyen.core.checkout.pci.impl;

import com.adyen.core.checkout.pci.CheckoutPciStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 */
public abstract class AbstractCheckoutPciStrategy implements CheckoutPciStrategy
{
	private CheckoutPciStrategy defaultCheckoutPciStrategy;


	protected CheckoutPciStrategy getDefaultCheckoutPciStrategy()
	{
		return this.defaultCheckoutPciStrategy;
	}

	@Required
	public void setDefaultCheckoutPciStrategy(final CheckoutPciStrategy defaultCheckoutPciStrategy)
	{
		this.defaultCheckoutPciStrategy = defaultCheckoutPciStrategy;
	}

	protected abstract boolean canSupport();
}
