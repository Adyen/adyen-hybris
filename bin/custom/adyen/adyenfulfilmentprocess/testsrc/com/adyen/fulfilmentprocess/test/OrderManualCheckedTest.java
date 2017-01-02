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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.task.RetryLaterException;
import com.adyen.fulfilmentprocess.actions.order.OrderManualCheckedAction;

@UnitTest
public class OrderManualCheckedTest {

	private OrderManualCheckedAction action;
	@Mock
	private ModelService mockModelService;
	@Mock
	private TimeService timeService;
	
	private OrderHistoryEntryModel historyLog;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		action = new OrderManualCheckedAction();
		action.setModelService(mockModelService);
		action.setTimeService(timeService);
		
		historyLog = new OrderHistoryEntryModel();
		BDDMockito.given(mockModelService.create(OrderHistoryEntryModel.class)).willReturn(historyLog);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testOrderCheckedNullProcess() throws RetryLaterException, Exception {
		action.execute(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderCheckedNullOrder() throws RetryLaterException, Exception {
		final OrderProcessModel process = new OrderProcessModel();
		action.execute(process);
	}
	
	@Test
	public void testOrderCheckedFraud() throws RetryLaterException, Exception {

		final OrderProcessModel process = new OrderProcessModel();
		final OrderModel fraudOrder = new OrderModel();
		fraudOrder.setFraudulent(Boolean.TRUE);
		process.setOrder(fraudOrder);
		Assert.assertEquals("NOK", action.execute(process));
		BDDMockito.verify(mockModelService).save(historyLog);
		Assert.assertEquals(fraudOrder, historyLog.getOrder());
		Assert.assertEquals(OrderStatus.SUSPENDED, fraudOrder.getStatus());
	}
	
	@Test
	public void testOrderCheckedOK() throws RetryLaterException, Exception {

		final OrderProcessModel process = new OrderProcessModel();
		final OrderModel okOrder = new OrderModel();
		okOrder.setFraudulent(Boolean.FALSE);
		process.setOrder(okOrder);
		Assert.assertEquals("OK", action.execute(process));
		BDDMockito.verify(mockModelService).save(historyLog);
		Assert.assertEquals(okOrder, historyLog.getOrder());
	}
	
	@Test
	public void testOrderUndefined() throws RetryLaterException, Exception {

		final OrderProcessModel process = new OrderProcessModel();
		final OrderModel undefinedOrder = new OrderModel();
		process.setOrder(undefinedOrder);
		Assert.assertEquals("UNDEFINED", action.execute(process));
	}
}
