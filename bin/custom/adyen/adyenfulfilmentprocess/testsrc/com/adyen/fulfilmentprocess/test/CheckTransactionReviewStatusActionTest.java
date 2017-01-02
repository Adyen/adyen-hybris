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
package com.adyen.fulfilmentprocess.test;

import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import com.adyen.fulfilmentprocess.actions.order.CheckTransactionReviewStatusAction;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CheckTransactionReviewStatusActionTest
{
	protected static final String OK = "OK"; //NOPMD
	protected static final String NOK = "NOK";
	protected static final String WAIT = "WAIT";

	protected CheckTransactionReviewStatusAction action = new CheckTransactionReviewStatusAction();
	protected PaymentTransactionEntryModel authorizationAccepted;
	protected PaymentTransactionEntryModel authorizationReview;
	protected PaymentTransactionEntryModel reviewAccepted;
	protected PaymentTransactionEntryModel reviewRejected;
	protected OrderProcessModel process = new OrderProcessModel();
	protected List<PaymentTransactionEntryModel> paymentTransactionEntriesList = new ArrayList<PaymentTransactionEntryModel>();

	@Mock
	private ModelService modelService;
	@Mock
	private TicketBusinessService ticketBusinessService;
	

	protected PaymentTransactionEntryModel createPaymentTransactionEntry(final PaymentTransactionType type,
			final TransactionStatus status)
	{
		final PaymentTransactionEntryModel paymentTransactionEntry = new PaymentTransactionEntryModel();
		paymentTransactionEntry.setType(type);
		paymentTransactionEntry.setTransactionStatus(status.toString());
		return paymentTransactionEntry;
	}

	@Before
	public void setUp() throws Exception
	{
		// Used for MockitoAnnotations annotations
		MockitoAnnotations.initMocks(this);
		action.setModelService(modelService);
		action.setTicketBusinessService( ticketBusinessService );
		BDDMockito.given( modelService.create( CsTicketModel.class ) ).willReturn( new CsTicketModel() );

		authorizationAccepted = createPaymentTransactionEntry(PaymentTransactionType.AUTHORIZATION, TransactionStatus.ACCEPTED);
		authorizationReview = createPaymentTransactionEntry(PaymentTransactionType.AUTHORIZATION, TransactionStatus.REVIEW);
		reviewAccepted = createPaymentTransactionEntry(PaymentTransactionType.REVIEW_DECISION, TransactionStatus.ACCEPTED);
		reviewRejected = createPaymentTransactionEntry(PaymentTransactionType.REVIEW_DECISION, TransactionStatus.REJECTED);

		process = new OrderProcessModel();
		final OrderModel order = new OrderModel();
		process.setOrder(order);
		final List<PaymentTransactionModel> paymentTransactionList = new ArrayList<PaymentTransactionModel>();
		order.setPaymentTransactions(paymentTransactionList);
		final PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
		paymentTransactionList.add(paymentTransactionModel);
		paymentTransactionEntriesList = new ArrayList<PaymentTransactionEntryModel>();
		paymentTransactionModel.setEntries(paymentTransactionEntriesList);
	}

	@Test
	public void testAcceptedAuthorization()
	{
		paymentTransactionEntriesList.add(authorizationAccepted);
		try
		{
			final String result = action.execute(process);
			Assert.assertEquals(OK, result);
		}
		catch (final RetryLaterException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}


	@Test
	public void testReviewAuthorization()
	{
		paymentTransactionEntriesList.add(authorizationReview);

		try
		{
			final String result = action.execute(process);
			Assert.assertEquals(WAIT, result);
		}
		catch (final RetryLaterException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testAcceptedReviewAuthorization()
	{
		paymentTransactionEntriesList.add(authorizationReview);
		paymentTransactionEntriesList.add(reviewAccepted);

		try
		{
			final String result = action.execute(process);
			Assert.assertEquals(OK, result);
		}
		catch (final RetryLaterException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testRejectedReviewAuthorization()
	{
		paymentTransactionEntriesList.add(authorizationReview);
		paymentTransactionEntriesList.add(reviewRejected);

		try
		{
			final String result = action.execute(process);
			Assert.assertEquals(NOK, result);
		}
		catch (final RetryLaterException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testMultipleReviewAuthorization()
	{
		paymentTransactionEntriesList.add(authorizationReview);
		paymentTransactionEntriesList.add(reviewRejected);
		paymentTransactionEntriesList.add(authorizationReview);

		try
		{
			final String result = action.execute(process);
			Assert.assertEquals(WAIT, result);
		}
		catch (final RetryLaterException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testMultipleReview()
	{
		paymentTransactionEntriesList.add(authorizationReview);
		paymentTransactionEntriesList.add(reviewRejected);
		paymentTransactionEntriesList.add(authorizationReview);
		paymentTransactionEntriesList.add(reviewAccepted);

		try
		{
			final String result = action.execute(process);
			Assert.assertEquals(OK, result);
		}
		catch (final RetryLaterException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

}
