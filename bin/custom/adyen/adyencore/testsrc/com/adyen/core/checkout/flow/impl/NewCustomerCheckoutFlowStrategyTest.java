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
package com.adyen.core.checkout.flow.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.acceleratorservices.checkout.flow.CheckoutFlowStrategy;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class NewCustomerCheckoutFlowStrategyTest
{
	private CheckoutFlowStrategy strategy;


	@Mock
	private UserService userService;

	@Mock
	private CustomerAccountService customerAccountService;

	@Mock
	private CustomerModel customerModel;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		strategy = initStrategy();
	}

	/**
	 * 
	 */
	private CheckoutFlowStrategy initStrategy()
	{
		final NewCustomerCheckoutFlowStrategy strategy = new NewCustomerCheckoutFlowStrategy();

		final FixedCheckoutFlowStrategy multiStepStrategy = new FixedCheckoutFlowStrategy();
		multiStepStrategy.setCheckoutFlow(CheckoutFlowEnum.MULTISTEP);

		strategy.setDefaultStrategy(multiStepStrategy);
		strategy.setNewCustomerStrategy(multiStepStrategy);

		strategy.setCustomerAccountService(customerAccountService);
		strategy.setUserService(userService);
		return strategy;
	}

	@Test
	public void testHasDefaultDeliveryAddressWithNullCurrentUser()
	{
		Mockito.doReturn(null).when(userService).getCurrentUser();

		Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, strategy.getCheckoutFlow());
	}

	@Test
	public void testHasDefaultDeliveryAddressWithNoCustomer()
	{
		Mockito.doReturn(new UserModel()).when(userService).getCurrentUser();

		Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, strategy.getCheckoutFlow());
	}

	@Test
	public void testHasDefaultDeliveryAddressWithACustomerHaveNoPaymentInfo()
	{
		final CreditCardPaymentInfoModel paymentInfoMock = Mockito.mock(CreditCardPaymentInfoModel.class);

		Mockito.doReturn(customerModel).when(userService).getCurrentUser();
		Mockito.doReturn(new AddressModel()).when(customerAccountService).getDefaultAddress(customerModel);
		Mockito.doReturn(Collections.singletonList(paymentInfoMock)).when(customerAccountService)
				.getCreditCardPaymentInfos(customerModel, true);


		Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, strategy.getCheckoutFlow());
	}


	@Test
	public void testHasDefaultDeliveryAddressWithACustomerHaveAnyPaymentInfo()
	{
		Mockito.doReturn(customerModel).when(userService).getCurrentUser();
		Mockito.doReturn(new AddressModel()).when(customerAccountService).getDefaultAddress(customerModel);
		Mockito.doReturn(Collections.EMPTY_LIST).when(customerAccountService).getCreditCardPaymentInfos(customerModel, true);


		Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, strategy.getCheckoutFlow());
	}


	@Test
	public void testHasDefaultDeliveryAddressWithACustomerHaveNoDefaultAddress()
	{
		Mockito.doReturn(customerModel).when(userService).getCurrentUser();
		Mockito.doReturn(new AddressModel()).when(customerAccountService).getDefaultAddress(customerModel);

		Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, strategy.getCheckoutFlow());
	}


	//todo test mobile logic
}
