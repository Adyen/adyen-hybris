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
package com.adyen.storefront.security.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.user.UserService;
import com.adyen.storefront.security.AcceleratorAuthenticationProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@UnitTest
public class CartUsurpProtectionUnitTest
{


	@Mock private CartService cartService;
	@Mock private UserService userService;
	@Mock private CartModel cartModel;
	@Mock private UserModel userModel;
	@Mock private CustomerModel customerModel;
	@InjectMocks private AcceleratorAuthenticationProvider aaprovider = new AcceleratorAuthenticationProvider();


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldSetCartNull()
	{


		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartService.getSessionCart().getUser()).willReturn(userModel);
		given(cartService.getSessionCart().getUser().getUid()).willReturn("001");

		given(userService.getAnonymousUser()).willReturn(customerModel);
		given(userService.getAnonymousUser().getUid()).willReturn("999");


		aaprovider.checkCartForUser("007");
		verify(cartService, times(1)).setSessionCart(null);
	}


	@Test
	public void shouldKeepCartAsSameUser()
	{

		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartService.getSessionCart().getUser()).willReturn(userModel);
		given(cartService.getSessionCart().getUser().getUid()).willReturn("001");

		given(userService.getAnonymousUser()).willReturn(customerModel);
		given(userService.getAnonymousUser().getUid()).willReturn("999");


		aaprovider.checkCartForUser("001");
		verify(cartService, never()).setSessionCart(null);
	}

	@Test
	public void shouldKeepCartAsAnonymous()
	{

		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartService.getSessionCart().getUser()).willReturn(userModel);
		given(cartService.getSessionCart().getUser().getUid()).willReturn("999");

		given(userService.getAnonymousUser()).willReturn(customerModel);
		given(userService.getAnonymousUser().getUid()).willReturn("999");


		aaprovider.checkCartForUser("007");
		verify(cartService, never()).setSessionCart(null);
	}
}
