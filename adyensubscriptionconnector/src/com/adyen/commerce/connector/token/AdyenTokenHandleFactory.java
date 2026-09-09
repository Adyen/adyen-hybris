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
package com.adyen.commerce.connector.token;

import com.adyen.commerce.connector.dto.AdyenTokenHandle;
import com.adyen.commerce.connector.dto.CardMetadata;
import com.adyen.commerce.connector.exception.TokenContractException;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Builds the uniform {@link AdyenTokenHandle} from the artifacts the existing Adyen plugin produces
 * The plugin captures the token during checkout; this factory only reads it &mdash; no
 * PAN is ever touched.
 */
public interface AdyenTokenHandleFactory
{
	/**
	 * Assemble the token contract from a tokenized order.
	 *
	 * @throws TokenContractException if the order is missing the data required to charge the token
	 *                                (no payment info, no {@code adyenSelectedReference}, no shopper)
	 */
	AdyenTokenHandle create(AbstractOrderModel order) throws TokenContractException;

	/**
	 * Assemble the token contract for a token the shopper already has vaulted, outside any order.
	 *
	 * <p>Exists because changing the card on a running subscription has no order behind it: the shopper is
	 * not buying anything, they are pointing an existing subscription at a different card they have already
	 * stored. Everything the contract needs is still available — the merchant account from the store, the
	 * shopperReference from the customer — except the pieces an order carries.</p>
	 *
	 * <p>Two of those are absent by nature and the caller has to be able to live without them. There is no
	 * {@code networkTransactionId}, because no authorisation happened here; a connector that requires one
	 * ({@code capabilities().requiresNetworkTransactionId()}) must therefore refuse a handle built this way,
	 * which is exactly what its own validation already does. And the card metadata is whatever the caller
	 * can supply from the vault listing rather than from a PaymentInfo, so it may be partial or
	 * {@code null}.</p>
	 *
	 * @param customer               the shopper who owns the token
	 * @param store                  the base store whose Adyen merchant account minted it
	 * @param storedPaymentMethodId  the Adyen {@code recurringDetailReference}
	 * @param cardMetadata           display metadata, or {@code null} when none is available
	 * @throws TokenContractException if the customer has no shopperReference or the store no merchant account
	 */
	AdyenTokenHandle createForStoredToken(CustomerModel customer, BaseStoreModel store,
			String storedPaymentMethodId, CardMetadata cardMetadata) throws TokenContractException;
}
