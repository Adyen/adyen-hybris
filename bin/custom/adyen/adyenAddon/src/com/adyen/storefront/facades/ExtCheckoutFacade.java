/**
 *
 */
package com.adyen.storefront.facades;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

import com.adyen.services.integration.data.response.AdyenListRecurringDetailsResponse;


/**
 * @author Kenneth Zhou
 * 
 */
public interface ExtCheckoutFacade
{
	public AdyenListRecurringDetailsResponse retrieveSavedPaymentMethod();

	public void storeCVC(String CVC);

	public void clearCVC();

	public String getSessionCVC();

	public CartModel getCartModel();

	public boolean isPaymentUthorized();

	public PaymentTransactionEntryModel authorizeAdyenPayment(final String securityCode);
}
