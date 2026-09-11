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
 * What a completed payment-method change actually did.
 *
 * <p>{@code appliedScope} is the point of this type. The caller has to choose what the shopper is told, and
 * the only honest basis for that is what happened rather than what was advertised: a connector that declares
 * {@link PaymentMethodChangeScope#SUBSCRIPTION} and quietly replaces the customer's primary payment source
 * would otherwise produce a page saying "only this subscription" while every other subscription moved too.
 * Returning it makes the declaration falsifiable — a test can assert the two agree, and a mistake stops at
 * the connector instead of reaching a person.</p>
 *
 * <p>{@code NOT_SUPPORTED} is not a legal value here: a connector that cannot do it raises
 * {@link com.adyen.commerce.connector.exception.CapabilityUnsupportedException} instead of returning a
 * success that did nothing.</p>
 */
public record PaymentMethodChangeOutcome(BillingPaymentMethodRef paymentMethod,
                                         PaymentMethodChangeScope appliedScope)
{
	public PaymentMethodChangeOutcome
	{
		Dtos.requireValue(paymentMethod, "paymentMethod");
		Dtos.requireValue(appliedScope, "appliedScope");
		if (!appliedScope.isSupported())
		{
			throw new IllegalArgumentException("A completed payment-method change cannot report a scope of "
					+ "NOT_SUPPORTED; a connector that cannot perform it must refuse rather than succeed");
		}
	}
}
