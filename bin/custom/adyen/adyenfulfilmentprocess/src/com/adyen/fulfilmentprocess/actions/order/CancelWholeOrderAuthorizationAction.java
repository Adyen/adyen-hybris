/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package com.adyen.fulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;

import java.util.List;

import org.apache.log4j.Logger;


/**
 * Cancels the authorized payment. The code expects previously authorized payment transaction, otherwise the order is
 * set to PROCESSING_ERROR status.
 */
public class CancelWholeOrderAuthorizationAction extends AbstractProceduralAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(CancelWholeOrderAuthorizationAction.class);

	private PaymentService paymentService;

	@Override
	public void executeAction(final OrderProcessModel process)
	{
		LOG.debug("The transaction is being cancelled.");

		final OrderModel order = process.getOrder();

		final List<PaymentTransactionModel> txns = order.getPaymentTransactions();

		// sanity check. There could be a problem with this order.
		if (txns.size() != 1)
		{
			LOG.error("Processing error - missing or ambiguous transaction.");
			setOrderStatus(order, OrderStatus.PROCESSING_ERROR);
			return;
		}

		final PaymentTransactionModel txn = txns.iterator().next();
		final List<PaymentTransactionEntryModel> txnEntries = txn.getEntries();

		// another sanity check. Also here could be a problem with this order.
		if (txnEntries.size() != 1)
		{
			LOG.error("Processing error - missing or ambiguous transaction entries.");
			setOrderStatus(order, OrderStatus.PROCESSING_ERROR);
			return;
		}

		final PaymentTransactionEntryModel txnEntry = txnEntries.iterator().next();

		final PaymentTransactionEntryModel txnResultEntry = getPaymentService().cancel(txnEntry);

		if (TransactionStatus.ACCEPTED.name().equals(txnResultEntry.getTransactionStatus()))
		{
			LOG.debug("Cancel successful.");
			setOrderStatus(order, OrderStatus.CANCELLED);
		}
		else
		{
			LOG.error("Processing error - Cancel command failed.");
			setOrderStatus(order, OrderStatus.PROCESSING_ERROR);
		}
	}

	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}
}
