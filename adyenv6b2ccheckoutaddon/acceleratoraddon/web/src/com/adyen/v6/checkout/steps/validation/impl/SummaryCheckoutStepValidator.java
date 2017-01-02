/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.adyen.v6.checkout.steps.validation.impl;

import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.AbstractCheckoutStepValidator;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.yacceleratorstorefront.checkout.steps.validation.impl.DefaultSummaryCheckoutStepValidator;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


public class SummaryCheckoutStepValidator extends AbstractCheckoutStepValidator
{
	private static final Logger LOGGER = Logger.getLogger(DefaultSummaryCheckoutStepValidator.class);

	@Override
	public ValidationResults validateOnEnter(final RedirectAttributes redirectAttributes)
	{
		final ValidationResults cartResult = checkCartAndDelivery(redirectAttributes);
		if (cartResult != null) {
			return cartResult;
		}

		final ValidationResults paymentResult = checkPaymentMethodAndPickup(redirectAttributes);
		if (paymentResult != null) {
			return paymentResult;
		}

		return ValidationResults.SUCCESS;
	}

	protected ValidationResults checkPaymentMethodAndPickup(final RedirectAttributes redirectAttributes) {
		//TODO: implement checkoutFlowFacade for hasNoPaymentInfo?
//		if (getCheckoutFlowFacade().hasNoPaymentInfo())
//		{
//			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
//					"checkout.multi.paymentDetails.notprovided");
//			return ValidationResults.REDIRECT_TO_PAYMENT_METHOD;
//		}

		final CartData cartData = getCheckoutFacade().getCheckoutCart();

		if (!getCheckoutFacade().hasShippingItems())
		{
			cartData.setDeliveryAddress(null);
		}

		if (!getCheckoutFacade().hasPickUpItems() && "pickup".equals(cartData.getDeliveryMode().getCode()))
		{
			return ValidationResults.REDIRECT_TO_PICKUP_LOCATION;
		}
		return null;
	}

	protected ValidationResults checkCartAndDelivery(final RedirectAttributes redirectAttributes) {
		if (!getCheckoutFlowFacade().hasValidCart())
		{
			LOGGER.info("Missing, empty or unsupported cart");
			return ValidationResults.REDIRECT_TO_CART;
		}

		if (getCheckoutFlowFacade().hasNoDeliveryAddress())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryAddress.notprovided");
			return ValidationResults.REDIRECT_TO_DELIVERY_ADDRESS;
		}

		if (getCheckoutFlowFacade().hasNoDeliveryMode())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryMethod.notprovided");
			return ValidationResults.REDIRECT_TO_DELIVERY_METHOD;
		}
		return null;
	}
}