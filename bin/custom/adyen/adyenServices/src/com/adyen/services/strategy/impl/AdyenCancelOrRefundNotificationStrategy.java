/**
 *
 */
package com.adyen.services.strategy.impl;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;

import com.adyen.services.integration.data.request.AdyenNotificationRequest;


/**
 * @author delli
 * 
 */
public class AdyenCancelOrRefundNotificationStrategy extends AbstractAdyenNotificationStrategy
{

	@Override
	public AdyenPaymentTransactionEntryModel handleNotification(final AdyenNotificationRequest request)
	{
		final AdyenPaymentTransactionEntryModel entry = super.handleNotification(request);
		//continue to cancel order ...

		if (isSeccessful(request) && entry != null && entry.getPaymentTransaction() != null
				&& entry.getPaymentTransaction().getOrder() != null
				&& !OrderStatus.CANCELLED.equals(entry.getPaymentTransaction().getOrder().getStatus()))
		{
			final AbstractOrderModel order = entry.getPaymentTransaction().getOrder();
			order.setStatus(OrderStatus.CANCELLED);
			getModelService().save(order);
		}
		else if (!isSeccessful(request) && entry != null && entry.getPaymentTransaction() != null
				&& entry.getPaymentTransaction().getOrder() != null
				&& !OrderStatus.CANCELLED.equals(entry.getPaymentTransaction().getOrder().getStatus()))
		{
			final AbstractOrderModel order = entry.getPaymentTransaction().getOrder();
			order.setStatus(OrderStatus.CANCEL_FAILED);
			getModelService().save(order);
		}
		return entry;

	}
}
