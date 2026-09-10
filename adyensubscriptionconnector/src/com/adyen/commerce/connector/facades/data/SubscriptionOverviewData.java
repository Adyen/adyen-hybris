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
import java.util.ArrayList;
import java.util.List;

/**
 * Everything the subscriptions page shows.
 *
 * <p>Two collections rather than one, because "you have no subscriptions" and "you paid for one and it
 * never started" are opposite things to be told and the second has no row to hang off. An activation that
 * failed leaves a journal entry and no reference at all, so a page built only from references shows that
 * shopper an empty list — which is the worst answer available, and the one the first draft of this page
 * gave.</p>
 */
public class SubscriptionOverviewData implements Serializable
{
	private static final long serialVersionUID = 1L;

	private List<SubscriptionEntryData> subscriptions = new ArrayList<>();

	/**
	 * Codes of orders that were paid for but whose subscription was never created and is no longer being
	 * retried. Shown as a banner naming the order, so the shopper has something to quote when they get in
	 * touch — and so they are not told they have nothing.
	 */
	private List<String> ordersAwaitingSetup = new ArrayList<>();

	public List<SubscriptionEntryData> getSubscriptions()
	{
		return subscriptions;
	}

	public void setSubscriptions(final List<SubscriptionEntryData> subscriptions)
	{
		this.subscriptions = subscriptions;
	}

	/**
	 * The subscription whose code the payment-method form should carry, or {@code null} when there is none
	 * it can use.
	 *
	 * <p>The change is per customer on Chargebee, so any one of their subscriptions identifies the customer
	 * and the store. It still cannot be "whichever is first": a reference created before the public
	 * identifier existed has none, and a form built on it posts an empty code that the facade can only
	 * refuse. Choosing here, where the rows are known, is what keeps that out of the view.</p>
	 */
	private String paymentMethodSubscriptionCode;

	public String getPaymentMethodSubscriptionCode()
	{
		return paymentMethodSubscriptionCode;
	}

	public void setPaymentMethodSubscriptionCode(final String paymentMethodSubscriptionCode)
	{
		this.paymentMethodSubscriptionCode = paymentMethodSubscriptionCode;
	}

	public List<String> getOrdersAwaitingSetup()
	{
		return ordersAwaitingSetup;
	}

	public void setOrdersAwaitingSetup(final List<String> ordersAwaitingSetup)
	{
		this.ordersAwaitingSetup = ordersAwaitingSetup;
	}

	/** True when there is genuinely nothing to say — no rows and no unfinished orders. */
	public boolean isEmpty()
	{
		return subscriptions.isEmpty() && ordersAwaitingSetup.isEmpty();
	}
}
