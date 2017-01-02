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
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 * This action implements payment authorization using {@link CreditCardPaymentInfoModel}. Any other payment model could
 * be implemented here, or in a separate action, if the process flow differs.
 */
public class CheckAuthorizeOrderPaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();

		if (order != null)
		{
			if (order.getPaymentInfo() instanceof InvoicePaymentInfoModel)
			{
				return Transition.OK;
			}
			else
			{
				for (final PaymentTransactionModel transaction : order.getPaymentTransactions())
				{
					for (final PaymentTransactionEntryModel entry : transaction.getEntries())
					{
						if (entry.getType().equals(PaymentTransactionType.AUTHORIZATION) && TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())
								&& TransactionStatusDetails.SUCCESFULL.name().equals(entry.getTransactionStatusDetails()))
						{
							order.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
							modelService.save(order);
							return Transition.OK;
						} else if (entry.getType().equals(PaymentTransactionType.AUTHORIZATION) && TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())
								&& "UNSUCCESSFULL".equals(entry.getTransactionStatusDetails()))
						{
							order.setStatus(OrderStatus.PAYMENT_NOT_AUTHORIZED);
							modelService.save(order);
							return Transition.NOK;
						}
					}
				}
			}
		}
		return Transition.NOK;
	}
}
