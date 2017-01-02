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

import de.hybris.platform.commerceservices.enums.PickupInStoreMode;
import de.hybris.platform.commerceservices.strategies.PickupStrategy;
import de.hybris.platform.acceleratorservices.checkout.flow.CheckoutFlowStrategy;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;

import org.springframework.beans.factory.annotation.Required;


/**
 * Checks if any of cart entries is created to be picked up in location. If so, the multi step checkout is always
 * chosen.
 */
public class PickUpInStoreCheckoutFlowStrategy extends AbstractCheckoutFlowStrategy
{
	private CheckoutFlowStrategy multiStepCheckoutFlowStrategy;
	private PickupStrategy pickupStrategy;


	protected boolean canSupport()
	{
		final PickupInStoreMode pickupInStoreMode = getPickupStrategy().getPickupInStoreMode();
		return PickupInStoreMode.BUY_AND_COLLECT.equals(pickupInStoreMode);// || PickupInStoreMode.RESERVE_AND_COLLECT.equals(pickupInStoreMode);
	}

	@Override
	public CheckoutFlowEnum getCheckoutFlow()
	{
		if (canSupport())
		{
			return getMultiStepCheckoutFlowStrategy().getCheckoutFlow();
		}
		else
		{
			return getDefaultStrategy().getCheckoutFlow();
		}
	}

	protected CheckoutFlowStrategy getMultiStepCheckoutFlowStrategy()
	{
		return multiStepCheckoutFlowStrategy;
	}

	@Required
	public void setMultiStepCheckoutFlowStrategy(final CheckoutFlowStrategy multiStepCheckoutFlowStrategy)
	{
		this.multiStepCheckoutFlowStrategy = multiStepCheckoutFlowStrategy;
	}

	protected PickupStrategy getPickupStrategy()
	{
		return pickupStrategy;
	}

	@Required
	public void setPickupStrategy(final PickupStrategy pickupStrategy)
	{
		this.pickupStrategy = pickupStrategy;
	}
}
