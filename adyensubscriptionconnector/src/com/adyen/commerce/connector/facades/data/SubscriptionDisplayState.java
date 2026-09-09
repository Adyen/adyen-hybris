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

/**
 * What a shopper is told about one subscription, and whether they are offered a way to stop it.
 *
 * <p>Deliberately not the normalized platform status. That vocabulary answers "what does the platform say",
 * which is a different question from "what should this person read": a subscription the shopper has already
 * stopped is still {@code ACTIVE} on both platforms and stays that way until the term ends, and one that has
 * never been confirmed by any platform read is {@code PENDING} whether it is about to start or was created
 * a second ago. Collapsing those into the platform's words would produce a page that is accurate and
 * useless.</p>
 *
 * <p>Whether a date is shown is not part of the state. Every state that can carry one may also be missing
 * it — the period end is only known after the first reconciliation — so the date travels separately on
 * {@link SubscriptionEntryData} and the wording is chosen from its presence. Encoding "with date" and
 * "without date" as separate states would double this enum to say the same thing twice.</p>
 */
public enum SubscriptionDisplayState
{
	/**
	 * No platform has confirmed this subscription yet. Never carries a date and never offers a button:
	 * there is nothing to tell the shopper and nothing dependable to cancel.
	 */
	SETTING_UP(false),

	/** Running and will renew. */
	ACTIVE(true),

	/** Will start on a future date and has not begun billing. Cancellable, and cheaply so. */
	STARTING_SOON(true),

	/** Still serving, but the platform has been told not to renew it. */
	ENDING(false),

	/** Not being served and not being billed; only an operator can resume it on either platform. */
	PAUSED(false),

	/** A payment did not go through and the platform is retrying. Still stoppable — and this is the state shoppers most often want to stop from. */
	PAST_DUE(true),

	/** Already set to end, and carrying an unpaid invoice as well. */
	PAST_DUE_ENDING(false),

	/** Over. */
	ENDED(false),

	/**
	 * The status could not be interpreted, the platform's adapter is not installed or configured, or the
	 * store this subscription belongs to could not be determined. Shown rather than hidden — a subscription
	 * that is billing somebody must appear on their page even when we cannot describe it — but never with a
	 * button, because acting on it would be acting on a guess.
	 */
	UNAVAILABLE(false);

	private final boolean cancellable;

	SubscriptionDisplayState(final boolean cancellable)
	{
		this.cancellable = cancellable;
	}

	/**
	 * Whether the shopper is offered a way to stop this subscription themselves.
	 *
	 * <p>Read by the page to decide whether to render the button and by the facade to decide whether to
	 * honour a request that arrives anyway — a form can be replayed after the state has moved on.</p>
	 */
	public boolean isCancellable()
	{
		return cancellable;
	}
}
