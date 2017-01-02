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
package com.adyen.core.checkout.pci.impl;

import com.adyen.core.checkout.pci.CheckoutPciStrategy;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;

import org.springframework.beans.factory.annotation.Required;


/**
 * Uses fixed {@link CheckoutPciOptionEnum} as result. Used most likely on the end of checkout PCI option strategy
 * chain.
 */
public class FixedCheckoutPciStrategy implements CheckoutPciStrategy
{
	private CheckoutPciOptionEnum subscriptionPciOption;

	@Override
	public CheckoutPciOptionEnum getSubscriptionPciOption()
	{
		return this.subscriptionPciOption;
	}

	@Required
	public void setSubscriptionPciOption(final CheckoutPciOptionEnum subscriptionPciOption)
	{
		this.subscriptionPciOption = subscriptionPciOption;
	}
}
