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
package com.adyen.core.externaltax.impl;

import de.hybris.platform.commerceservices.externaltax.DecideExternalTaxesStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Accelerator
 * 
 */
public class AcceleratorDetermineExternalTaxStrategy implements DecideExternalTaxesStrategy
{
	/**
	 * Initially just to test if the delivery mode and address are set, than calculate the external taxes. TODO: Hash of
	 * products in cart, delivery mode, delivery address and payment information to determine whether or not to calculate
	 * taxes.
	 */
	@Override
	public boolean shouldCalculateExternalTaxes(final AbstractOrderModel abstractOrder)
	{
		if (abstractOrder == null)
		{
			throw new IllegalStateException("Order is null. Cannot apply external tax to it.");
		}

		return (Boolean.TRUE.equals(abstractOrder.getNet()) && abstractOrder.getDeliveryMode() != null && abstractOrder
				.getDeliveryAddress() != null);
	}
}
