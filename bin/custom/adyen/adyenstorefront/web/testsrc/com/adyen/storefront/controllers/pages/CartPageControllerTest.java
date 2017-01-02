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
package com.adyen.storefront.controllers.pages;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CartPageControllerTest
{
	private final CartPageController controller = Mockito.spy(new CartPageController());

	@Mock
	private SiteConfigService service;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		//
		BDDMockito.given(controller.getSiteConfigService()).willReturn(service);
	}

	@Test
	public void testNullProperty()
	{
		BDDMockito.given(service.getProperty(CartPageController.SHOW_CHECKOUT_STRATEGY_OPTIONS)).willReturn(null);

		Assert.assertFalse(controller.isCheckoutStrategyVisible());
	}

	@Test
	public void testSomeStringProperty()
	{
		BDDMockito.given(service.getProperty(CartPageController.SHOW_CHECKOUT_STRATEGY_OPTIONS)).willReturn("someString");

		Assert.assertFalse(controller.isCheckoutStrategyVisible());
	}

	@Test
	public void testTrueStringProperty()
	{
		BDDMockito.given(service.getProperty(CartPageController.SHOW_CHECKOUT_STRATEGY_OPTIONS)).willReturn("true");

		Assert.assertTrue(controller.isCheckoutStrategyVisible());
	}

	@Test
	public void testZeroStringProperty()
	{
		BDDMockito.given(service.getProperty(CartPageController.SHOW_CHECKOUT_STRATEGY_OPTIONS)).willReturn("0");

		Assert.assertFalse(controller.isCheckoutStrategyVisible());
	}

	@Test
	public void testEmptyStringProperty()
	{
		BDDMockito.given(service.getProperty(CartPageController.SHOW_CHECKOUT_STRATEGY_OPTIONS)).willReturn("");

		Assert.assertFalse(controller.isCheckoutStrategyVisible());
	}

	@Test
	public void testEmptyEmptyStringProperty()
	{
		BDDMockito.given(service.getProperty(CartPageController.SHOW_CHECKOUT_STRATEGY_OPTIONS)).willReturn(" ");

		Assert.assertFalse(controller.isCheckoutStrategyVisible());
	}
}
