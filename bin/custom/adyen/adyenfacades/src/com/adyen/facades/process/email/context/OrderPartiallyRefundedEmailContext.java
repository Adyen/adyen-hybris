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
package com.adyen.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.orderprocessing.model.OrderModificationProcessModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;

import java.math.BigDecimal;
import java.util.List;



/**
 * Velocity context for email about partially order refund
 */
public class OrderPartiallyRefundedEmailContext extends OrderPartiallyModifiedEmailContext
{
	private PriceData refundAmount;

	@Override
	public void init(final OrderModificationProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		calculateRefundAmount();
	}

	protected void calculateRefundAmount()
	{
		BigDecimal refundAmountValue = BigDecimal.ZERO;
		PriceData totalPrice = null;
		for (final OrderEntryData entryData : getRefundedEntries())
		{
			totalPrice = entryData.getTotalPrice();
			refundAmountValue = refundAmountValue.add(totalPrice.getValue());
		}
		refundAmount = getPriceDataFactory().create(totalPrice.getPriceType(), refundAmountValue, totalPrice.getCurrencyIso());
	}

	public List<OrderEntryData> getRefundedEntries()
	{
		return super.getModifiedEntries();
	}

	public PriceData getRefundAmount()
	{
		return refundAmount;
	}
}
