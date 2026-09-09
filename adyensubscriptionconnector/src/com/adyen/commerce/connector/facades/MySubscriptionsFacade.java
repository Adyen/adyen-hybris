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
package com.adyen.commerce.connector.facades;

import com.adyen.commerce.connector.facades.data.SubscriptionOverviewData;

/**
 * The shopper's own view of their subscriptions, and the one thing they may do to them.
 *
 * <p>Every method is scoped to the customer in session and there is no overload that takes one. That is the
 * access control: a facade that accepted a customer would put the decision in the hands of each caller, and
 * one of those callers is a web tier where the identity in the path is attacker-controlled until a filter
 * has vouched for it.</p>
 */
public interface MySubscriptionsFacade
{
	/**
	 * Everything to show on the page for the customer in session. Returns an empty overview — never null,
	 * never an exception — when there is no such customer, so an anonymous request renders an empty page
	 * rather than a stack trace.
	 */
	SubscriptionOverviewData getSubscriptionsForCurrentCustomer();

	/**
	 * Stops the named subscription at the end of the period the shopper has already paid for.
	 *
	 * <p>Never immediately. On one platform an immediate cancellation is a different API verb that ends
	 * service at once, and neither adapter sends an instruction about the money — so the shopper would lose
	 * access they had bought and get nothing back.</p>
	 *
	 * @param code the public identifier from the page
	 * @return whether it was cancelled. {@code false} covers every reason the answer is no — not this
	 *         customer's subscription, no longer in a state that can be cancelled, or the platform refused
	 *         — because telling them apart on screen would tell an unauthenticated caller which codes exist
	 */
	boolean cancelForCurrentCustomer(String code);

	/**
	 * Points the shopper's billing at a card they already have vaulted with Adyen. Proof of concept.
	 *
	 * <h3>What this is, precisely</h3>
	 * <p>Not "change the card on this subscription". On Chargebee a payment source belongs to the
	 * <em>customer</em>, and the import replaces their primary one, so every subscription of theirs that
	 * bills against the primary follows. The subscription code is taken only to establish which customer
	 * and which store are meant, and to refuse the request when the row is not theirs.</p>
	 *
	 * <h3>What it deliberately does not do</h3>
	 * <p>It adds no new card. The shopper picks one Adyen has already vaulted for them, which is what makes
	 * a proof of concept possible at all: minting a new token would need zero-auth, and zero-auth here
	 * carries no 3DS, so a card requiring authentication could not be stored. Chargebee is also the only
	 * platform it works on — Recurly requires a network transaction id, and a token vaulted earlier has
	 * none to give.</p>
	 *
	 * @param subscriptionCode      the public identifier, used to establish ownership and the store
	 * @param storedPaymentMethodId the Adyen {@code recurringDetailReference} the shopper chose
	 * @return whether the billing platform accepted it
	 */
	boolean changePaymentMethodForCurrentCustomer(String subscriptionCode, String storedPaymentMethodId);
}
