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

import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.checkout.flow.CheckoutFlowStrategy;
import com.adyen.core.checkout.pci.CheckoutPciStrategy;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link CheckoutFlowFacade}. Delegates resolving the checkout flow to an injected
 * {@link CheckoutFlowStrategy}.
 * 
 * @since 4.6
 * @spring.bean checkoutFlowFacade
 */
public class DefaultCheckoutFlowFacade extends DefaultAcceleratorCheckoutFacade implements CheckoutFlowFacade
{
	private CheckoutFlowStrategy checkoutFlowStrategy;
	private CheckoutPciStrategy checkoutPciStrategy;

	@Override
	public CheckoutFlowEnum getCheckoutFlow()
	{
		return getCheckoutFlowStrategy().getCheckoutFlow();
	}

	@Override
	public CheckoutPciOptionEnum getSubscriptionPciOption()
	{
		return getCheckoutPciStrategy().getSubscriptionPciOption();
	}

	protected CheckoutFlowStrategy getCheckoutFlowStrategy()
	{
		return checkoutFlowStrategy;
	}

	@Required
	public void setCheckoutFlowStrategy(final CheckoutFlowStrategy strategy)
	{
		this.checkoutFlowStrategy = strategy;
	}

	protected CheckoutPciStrategy getCheckoutPciStrategy()
	{
		return this.checkoutPciStrategy;
	}

	@Required
	public void setCheckoutPciStrategy(final CheckoutPciStrategy strategy)
	{
		this.checkoutPciStrategy = strategy;
	}
}
