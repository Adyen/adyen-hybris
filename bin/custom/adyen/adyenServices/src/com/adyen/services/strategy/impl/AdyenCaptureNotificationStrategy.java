/**
 *
 */
package com.adyen.services.strategy.impl;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.processengine.BusinessProcessService;

import com.adyen.services.integration.data.request.AdyenNotificationRequest;


/**
 * @author delli
 * 
 */
public class AdyenCaptureNotificationStrategy extends AbstractAdyenNotificationStrategy
{

	private BusinessProcessService businessProcessService;

	@Override
	public AdyenPaymentTransactionEntryModel handleNotification(final AdyenNotificationRequest request)
	{
		final AdyenPaymentTransactionEntryModel entry = super.handleNotification(request);

		if (entry != null && PaymentTransactionType.CAPTURE.equals(entry.getType())
				&& TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())
				&& TransactionStatusDetails.SUCCESFULL.name().equals(entry.getTransactionStatusDetails()))
		{
			final OrderModel order = (OrderModel) entry.getPaymentTransaction().getOrder();
			order.setPaymentStatus(PaymentStatus.PAID);
			getModelService().save(order);
			getModelService().refresh(order);
			for (final ConsignmentModel consignment : order.getConsignments())
			{
				consignment.setStatus(ConsignmentStatus.READY);
				getModelService().save(consignment);
				for (final ConsignmentProcessModel process : consignment.getConsignmentProcesses())
				{
					getBusinessProcessService().triggerEvent(process.getCode() + "_WaitForCapture");
				}
			}
			return entry;
		}

		return null;
	}

	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}




}
