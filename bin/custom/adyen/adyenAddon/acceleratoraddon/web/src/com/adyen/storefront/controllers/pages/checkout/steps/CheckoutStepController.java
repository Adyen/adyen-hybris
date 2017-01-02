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
package com.adyen.storefront.controllers.pages.checkout.steps;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


public interface CheckoutStepController
{
	/**
	 * Swapping the parameter order might break the validation aspect from working.
	 * 
	 * @param model
	 * @param redirectAttributes
	 * @return returns the result for entering the checkout step
	 * @throws CMSItemNotFoundException
	 * @throws CommerceCartModificationException
	 */
	String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException,
			CommerceCartModificationException;

	/**
	 * 
	 * @param redirectAttributes
	 * @return checkoutstep link for navigating back
	 */
	String back(final RedirectAttributes redirectAttributes);

	/**
	 * 
	 * @param redirectAttributes
	 * @return checkoutstep link for navigating next
	 */
	String next(final RedirectAttributes redirectAttributes);
}
