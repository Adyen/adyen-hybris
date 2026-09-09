/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2026 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.commerce.connector.facades.data;

import java.io.Serializable;
import java.util.Date;

/**
 * One row of the shopper's subscription list.
 *
 * <p>A view of a {@code BillingSubscriptionRef}, not a copy of it. What is absent is the point: no external
 * subscription id, no external customer or payment-method id, no platform name, no plan code and no
 * idempotency key. Several of those identify the shopper or their card on another system, one of them is
 * the SAP order code — which is sequential — and none of them means anything to the person reading the
 * page. Handing the model straight to a view is how they would all have travelled.</p>
 */
public class SubscriptionEntryData implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** The opaque public identifier. The only thing that comes back from the page in a request. */
	private String code;

	/**
	 * The product's name, or its code when the product can no longer be resolved in the catalogue being
	 * served. Never the plan code: that is the billing platform's price identifier and reads like
	 * {@code test-subscription-plan-EUR-Monthly}.
	 */
	private String productName;

	private SubscriptionDisplayState state;

	/**
	 * The date the state talks about — the next renewal, the day access ends, the day it starts, or the day
	 * it ended — or {@code null} when it is not known yet. A subscription has no period until the first
	 * platform read, so this is genuinely absent for a while after purchase and the wording has to cope.
	 */
	private Date effectiveDate;

	/** Shown only when greater than one, because "quantity: 1" is noise on every other row. */
	private Integer quantity;

	/** The order this was bought on, so the shopper can find what they paid and when. May be absent. */
	private String orderCode;
	private Date orderDate;

	/**
	 * The card as it was at purchase, for recognition only — "the one ending 1881" — never as something the
	 * shopper is invited to change here. Changing it is not offered on any platform yet.
	 */
	private String paymentMethodSummary;

	public String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(final String productName)
	{
		this.productName = productName;
	}

	public SubscriptionDisplayState getState()
	{
		return state;
	}

	public void setState(final SubscriptionDisplayState state)
	{
		this.state = state;
	}

	public Date getEffectiveDate()
	{
		return effectiveDate;
	}

	public void setEffectiveDate(final Date effectiveDate)
	{
		this.effectiveDate = effectiveDate;
	}

	public Integer getQuantity()
	{
		return quantity;
	}

	public void setQuantity(final Integer quantity)
	{
		this.quantity = quantity;
	}

	public String getOrderCode()
	{
		return orderCode;
	}

	public void setOrderCode(final String orderCode)
	{
		this.orderCode = orderCode;
	}

	public Date getOrderDate()
	{
		return orderDate;
	}

	public void setOrderDate(final Date orderDate)
	{
		this.orderDate = orderDate;
	}

	public String getPaymentMethodSummary()
	{
		return paymentMethodSummary;
	}

	public void setPaymentMethodSummary(final String paymentMethodSummary)
	{
		this.paymentMethodSummary = paymentMethodSummary;
	}

	/**
	 * Whether to offer the shopper a way to stop this one. Convenience for the view, so the button condition
	 * is not a second copy of the rule.
	 *
	 * <p>The code is part of the condition, not only the state. A reference created before the public
	 * identifier existed carries none until the extension's essential data has been imported, and a button
	 * that posts an empty code cannot do anything but fail — which reads to the shopper as a subscription
	 * they are unable to cancel rather than as a deployment step somebody skipped.</p>
	 */
	public boolean isCancellable()
	{
		return state != null && state.isCancellable() && code != null && !code.isBlank();
	}
}
