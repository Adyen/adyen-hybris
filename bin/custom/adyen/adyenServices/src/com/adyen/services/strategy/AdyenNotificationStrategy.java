/**
 *
 */
package com.adyen.services.strategy;

import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import com.adyen.services.integration.data.request.AdyenNotificationRequest;


/**
 * @author delli
 * 
 */
public interface AdyenNotificationStrategy
{
	public AdyenPaymentTransactionEntryModel handleNotification(final AdyenNotificationRequest request);

	String getEventCode(final AdyenNotificationRequest request);

	AdyenPaymentTransactionEntryModel createTransactionEntry(final AdyenNotificationRequest request);

	PaymentTransactionModel fetchTransaction(final AdyenNotificationRequest request);
}
