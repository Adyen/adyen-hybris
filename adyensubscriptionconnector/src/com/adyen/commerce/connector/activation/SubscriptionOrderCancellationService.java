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
package com.adyen.commerce.connector.activation;

import de.hybris.platform.core.model.order.OrderModel;

/**
 * Stops the subscription an order started, when that order is cancelled.
 *
 * <p>Without this the two halves disagree permanently: the order says cancelled, the billing platform goes
 * on charging the card every period, and the only person who finds out is the shopper reading their
 * statement. Order cancellation is also the only path in this integration that reaches
 * {@code SubscriptionBillingService.cancel} at all.</p>
 *
 * <p>The mirror image of {@link SubscriptionOrderActivator}, and it borrows that class's two rules: nothing
 * escapes, because the caller is cancelling an order and a billing platform being down must not turn that
 * into a failure; and the decision is idempotent, because an order can be announced cancelled more than
 * once.</p>
 */
public interface SubscriptionOrderCancellationService
{
	/**
	 * Cancels the subscription activated from this order, at the end of the period it has already been paid
	 * for. Does nothing, quietly, when the order started no subscription or when it has already been
	 * stopped.
	 *
	 * @param order the fully cancelled order; {@code null} is tolerated
	 */
	void cancelSubscriptionFor(OrderModel order);
}
