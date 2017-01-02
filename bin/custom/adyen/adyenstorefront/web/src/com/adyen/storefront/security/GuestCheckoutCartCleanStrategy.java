/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.adyen.storefront.security;

import javax.servlet.http.HttpServletRequest;


/**
 * A strategy for clearing unwanted saved data from the cart for guest checkout.
 *
 */
public interface GuestCheckoutCartCleanStrategy
{

	/**
	 * Checks whether the request's page is checkout URL.
	 *
	 */
	boolean checkWhetherURLContainsCheckoutPattern(final HttpServletRequest request);

	/**
	 * Removes the delivery address, delivery mode, payment info from the session cart, if the guest user moves away from
	 * checkout pages.
	 *
	 */
	void cleanGuestCart(final HttpServletRequest request);
}
