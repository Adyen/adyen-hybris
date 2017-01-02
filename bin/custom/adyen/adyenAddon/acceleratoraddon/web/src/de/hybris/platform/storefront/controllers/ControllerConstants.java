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
package de.hybris.platform.storefront.controllers;

public interface ControllerConstants
{
	String ADDON_PREFIX = "addon:/adyenAddon/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{

		interface Pages
		{

			interface MultiStepCheckout
			{
				String AddEditDeliveryAddressPage = ADDON_PREFIX + "pages/checkout/multi/addEditDeliveryAddressPage";
				String ChooseDeliveryMethodPage = ADDON_PREFIX + "pages/checkout/multi/chooseDeliveryMethodPage";
				String ChoosePickupLocationPage = ADDON_PREFIX + "pages/checkout/multi/choosePickupLocationPage";
				String AddPaymentMethodPage = ADDON_PREFIX + "pages/checkout/multi/addPaymentMethodPage";
				String CheckoutSummaryPage = ADDON_PREFIX + "pages/checkout/multi/checkoutSummaryPage";
				String HostedOrderPageErrorPage = ADDON_PREFIX + "pages/checkout/multi/hostedOrderPageErrorPage";
				String HostedOrderPostPage = ADDON_PREFIX + "pages/checkout/multi/hostedOrderPostPage";
				String SilentOrderPostPage = ADDON_PREFIX + "pages/checkout/multi/silentOrderPostPage";
				String GiftWrapPage = ADDON_PREFIX + "pages/checkout/multi/giftWrapPage";
				String Validate3DSecurePaymentPage = ADDON_PREFIX + "pages/checkout/multi/adyen-3d-secure-payment-validation";
			}
		}

		interface Fragments
		{

			interface Checkout
			{
				String TermsAndConditionsPopup = ADDON_PREFIX + "fragments/checkout/termsAndConditionsPopup";
				String BillingAddressForm = ADDON_PREFIX + "fragments/checkout/billingAddressForm";
			}

		}
	}
}
