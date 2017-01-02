/**
 *
 */
package com.adyen.v6.constants;

public interface AdyenControllerConstants
{
	String ADDON_PREFIX = "addon:/adyenv6b2ccheckoutaddon/";
	/**
	 * Class with view name constants
	 */
	interface Views
	{

		interface Pages
		{

			interface MultiStepCheckout
			{
				String CheckoutSummaryPage = ADDON_PREFIX + "pages/checkout/multi/checkoutSummaryPage";
				String SelectPaymentMethod = ADDON_PREFIX + "pages/checkout/multi/selectPaymentMethodPage";
				String Validate3DSecurePaymentPage = ADDON_PREFIX + "pages/checkout/multi/3d-secure-payment-validation";
			}
		}

	}
}
