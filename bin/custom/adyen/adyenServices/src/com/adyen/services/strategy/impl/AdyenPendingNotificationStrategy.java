/**
 *
 */
package com.adyen.services.strategy.impl;

import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;

import com.adyen.services.integration.data.request.AdyenNotificationRequest;


/**
 * @author delli
 *
 */
public class AdyenPendingNotificationStrategy extends AbstractAdyenNotificationStrategy
{

	@Override
	public AdyenPaymentTransactionEntryModel handleNotification(final AdyenNotificationRequest request)
	{
		final AdyenPaymentTransactionEntryModel entry = super.handleNotification(request);
		entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		getModelService().save(entry);

		return entry;
	}

}
