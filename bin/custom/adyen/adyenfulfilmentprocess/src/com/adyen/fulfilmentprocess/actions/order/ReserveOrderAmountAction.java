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
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


public class ReserveOrderAmountAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();
		if (order != null)
		{
			for (final PaymentTransactionModel transaction : order.getPaymentTransactions())
			{
				for (final PaymentTransactionEntryModel entry : transaction.getEntries())
				{
					if (entry.getType().equals(PaymentTransactionType.AUTHORIZATION) && TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())
							&& TransactionStatusDetails.SUCCESFULL.name().equals(entry.getTransactionStatusDetails()))
					{
						//Check if authorized amount not equals with requested amount(order amount) change status of entry to REVIEW.
						if (entry.getAmount().doubleValue() != order.getTotalPrice().doubleValue()){
							entry.setTransactionStatus(TransactionStatus.REVIEW.name());
							modelService.save(entry);
							modelService.refresh(entry);
						}
					}
				}
			}
		}
		setOrderStatus(process.getOrder(), OrderStatus.PAYMENT_AMOUNT_RESERVED);
		return Transition.OK;
	}
}
