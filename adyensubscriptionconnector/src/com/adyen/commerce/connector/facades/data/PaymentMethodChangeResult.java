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
 * What came of a shopper asking to change the card their subscriptions are billed to.
 *
 * <p>Four values because three of them need different words. Success is split by scope, since "this
 * subscription" and "all your subscriptions" are different promises and the page must make the one that
 * actually happened. And a platform that can never do this must not be answered with "please try again":
 * a refusal that invites a retry which cannot succeed is worse than no control at all.</p>
 *
 * <p>{@code FAILED} deliberately also covers every rejection - a code that is not this shopper's, a card
 * that is not theirs, a row too far gone to act on. Telling those apart on screen would tell somebody
 * probing which subscription codes exist, and the controller for the cancellation reaches the same
 * conclusion for the same reason.</p>
 */
public enum PaymentMethodChangeResult
{
	CHANGED_THIS_SUBSCRIPTION,
	CHANGED_ALL_SUBSCRIPTIONS,
	NOT_SUPPORTED_HERE,
	FAILED
}
