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
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.util.localization.Localization;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * This action check if authorization has review status
 */
public class CheckTransactionReviewStatusAction extends AbstractAction<OrderProcessModel>
{
	private TicketBusinessService ticketBusinessService;

	public enum Transition
	{
		OK, NOK, WAIT;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();
			for (final Transition transitions : Transition.values())
			{
				res.add(transitions.toString());
			}
			return res;
		}
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	@Override
	public final String execute(final OrderProcessModel process) throws RetryLaterException, Exception
	{
		return executeAction(process).toString();
	}

	protected Transition executeAction(final OrderProcessModel process)
	{
		Transition result;

		final OrderModel order = process.getOrder();
		if (order != null)
		{
			for (final PaymentTransactionModel transaction : order.getPaymentTransactions())
			{
				result = checkPaymentTransaction(transaction, order);
				if (!Transition.OK.equals(result))
				{
					return result;
				}
			}
		}

		return Transition.OK;
	}

	protected Transition checkPaymentTransaction(final PaymentTransactionModel transaction, final OrderModel orderModel)
	{
		final List<PaymentTransactionEntryModel> transactionEntries = transaction.getEntries();
		for (int index = transactionEntries.size() - 1; index >= 0; index--)
		{
			final PaymentTransactionEntryModel entry = transactionEntries.get(index);

			if (isReviewDecision(entry))
			{
				if (isReviewAccepted(entry))
				{
					orderModel.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
					getModelService().save(orderModel);
					return Transition.OK;
				}
				else
				{
					orderModel.setStatus(OrderStatus.PAYMENT_NOT_AUTHORIZED);
					getModelService().save(orderModel);
					return Transition.NOK;
				}
			}
			else if (isAuthorization(entry))
			{
				if (isAuthorizationInReview(entry))
				{
					final String ticketTitle = Localization.getLocalizedString("message.ticket.orderinreview.title");
					final String ticketMessage = Localization.getLocalizedString("message.ticket.orderinreview.content",
							new Object[] { orderModel.getCode() });
					createTicket(ticketTitle, ticketMessage, orderModel, CsTicketCategory.FRAUD, CsTicketPriority.HIGH);

					orderModel.setStatus(OrderStatus.SUSPENDED);
					getModelService().save(orderModel);
					return Transition.WAIT;
				}
				else
				{
					return Transition.OK;
				}
			}

			// Continue onto next entry
		}
		return Transition.OK;
	}

	protected CsTicketModel createTicket(final String subject, final String description, final OrderModel order,
			final CsTicketCategory category, final CsTicketPriority priority)
	{
		final CsTicketModel newTicket = modelService.create(CsTicketModel.class);
		newTicket.setHeadline(subject);
		newTicket.setCategory(category);
		newTicket.setPriority(priority);
		newTicket.setOrder(order);
		newTicket.setCustomer(order.getUser());

		final CsCustomerEventModel newTicketEvent = new CsCustomerEventModel();
		newTicketEvent.setText(description);

		return getTicketBusinessService().createTicket(newTicket, newTicketEvent);
	}

	protected boolean isReviewDecision(final PaymentTransactionEntryModel entry)
	{
		return PaymentTransactionType.REVIEW_DECISION.equals(entry.getType());
	}

	protected boolean isReviewAccepted(final PaymentTransactionEntryModel entry)
	{
		return TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus());
	}

	protected boolean isAuthorization(final PaymentTransactionEntryModel entry)
	{
		return PaymentTransactionType.AUTHORIZATION.equals(entry.getType());
	}

	protected boolean isAuthorizationInReview(final PaymentTransactionEntryModel entry)
	{
		return TransactionStatus.REVIEW.name().equals(entry.getTransactionStatus());
	}

	protected TicketBusinessService getTicketBusinessService()
	{
		return ticketBusinessService;
	}

	@Required
	public void setTicketBusinessService(final TicketBusinessService ticketBusinessService)
	{
		this.ticketBusinessService = ticketBusinessService;
	}
}
