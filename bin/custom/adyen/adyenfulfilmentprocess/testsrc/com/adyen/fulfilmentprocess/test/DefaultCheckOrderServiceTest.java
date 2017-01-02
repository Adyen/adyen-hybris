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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import com.adyen.fulfilmentprocess.impl.DefaultCheckOrderService;

import java.util.Arrays;
import java.util.Collections;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultCheckOrderServiceTest
{
	private final DefaultCheckOrderService defaultCheckOrderService = new DefaultCheckOrderService();

	private OrderModel order;

	/**
	 * 
	 */
	@Before
	public void setUp() throws Exception
	{
		order = new OrderModel();
		order.setCalculated(Boolean.TRUE);
		order.setEntries(Arrays.<AbstractOrderEntryModel> asList(new OrderEntryModel()));
		order.setDeliveryAddress(new AddressModel());
		order.setPaymentInfo(new PaymentInfoModel());
	}

	@Test
	public void testCheck()
	{
		Assertions.assertThat(defaultCheckOrderService.check(order)).isFalse();
	}

	@Test
	public void testNotCalculated()
	{
		order.setCalculated(Boolean.FALSE);
		Assertions.assertThat(defaultCheckOrderService.check(order)).isFalse();
	}

	@Test
	public void testNoEntries()
	{
		order.setEntries(Collections.EMPTY_LIST);
		Assertions.assertThat(defaultCheckOrderService.check(order)).isFalse();
	}

	@Test
	public void testNoPaymentInfo()
	{
		order.setPaymentInfo(null);
		Assertions.assertThat(defaultCheckOrderService.check(order)).isFalse();
	}
}
