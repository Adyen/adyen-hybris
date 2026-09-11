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
package com.adyen.commerce.connector.dto;

/**
 * Whose future billing a payment-method change moves, as the connector declares it.
 *
 * <p>One value, not a pair of booleans: "supported" is derivable from the scope, and a connector that could
 * say it supports the change without saying what the change touches is a connector that can produce a page
 * telling the shopper the wrong thing. The core branches on this and never on the platform's identity.</p>
 *
 * <p>A connector declares exactly one scope and applies it consistently. A platform capable of both picks
 * the one it will actually do — the core must never predict, because predicting is what puts a sentence on
 * the screen that the platform then contradicts.</p>
 */
public enum PaymentMethodChangeScope
{
	/**
	 * The shopper cannot change it here. Not a placeholder for "later": it is the honest answer for a
	 * platform whose API cannot do it, or whose account is not on a plan that includes it, and the page
	 * says so in a sentence rather than offering a control that cannot work.
	 */
	NOT_SUPPORTED,

	/**
	 * The change moves every subscription this customer has on this platform. True of Chargebee as this
	 * integration drives it: the payment source belongs to the customer and the import replaces the
	 * primary one, so the page has to say "all your subscriptions" and mean it.
	 */
	CUSTOMER,

	/**
	 * The change moves only the subscription it was asked about. What Recurly offers on sites that carry
	 * the Subscriber Wallet feature, where a subscription can be pinned to one billing info.
	 */
	SUBSCRIPTION;

	/** Whether the shopper is offered the change at all. */
	public boolean isSupported()
	{
		return this != NOT_SUPPORTED;
	}
}
