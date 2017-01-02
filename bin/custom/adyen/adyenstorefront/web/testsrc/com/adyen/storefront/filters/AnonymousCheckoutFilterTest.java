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
package com.adyen.storefront.filters;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit Test for {@link AnonymousCheckoutFilter}
 */
@UnitTest
public class AnonymousCheckoutFilterTest
{
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private HttpSession httpSession; //NOPMD
	@Mock
	private FilterChain filterChain; //NOPMD
	@Mock
	private UserService userService; //NOPMD
	@Mock
	private CustomerModel customer; //NOPMD
	@Mock
	private CustomerModel guestCustomer; //NOPMD
	@Mock
	private DefaultCheckoutCustomerStrategy checkoutCustomerStrategy;
	@Mock
	private CartService cartService;
	@Mock
	private SessionService sessionService;
	@InjectMocks
	protected final AnonymousCheckoutFilter filter = new AnonymousCheckoutFilter();


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testForGuestAbandonAnonymousCheckout() throws IOException, ServletException
	{
		given(Boolean.valueOf(request.isSecure())).willReturn(Boolean.FALSE);
		given(sessionService.getAttribute(WebConstants.ANONYMOUS_CHECKOUT)).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(checkoutCustomerStrategy.isAnonymousCheckout())).willReturn(Boolean.TRUE);
		final CartModel cartModel = mock(CartModel.class);
		final AddressModel addressModel = mock(AddressModel.class);
		final DeliveryModeModel deliveryModeModel = mock(DeliveryModeModel.class);
		final PaymentInfoModel paymentModeModel = mock(PaymentInfoModel.class);
		cartModel.setDeliveryAddress(addressModel);
		cartModel.setDeliveryMode(deliveryModeModel);
		cartModel.setPaymentInfo(paymentModeModel);
		given(cartService.getSessionCart()).willReturn(cartModel);
		filter.doFilterInternal(request, response, filterChain);

		verify(cartModel).setDeliveryAddress(null);
		verify(cartModel).setDeliveryMode(null);
		verify(cartModel).setPaymentInfo(null);
		verify(sessionService).removeAttribute(WebConstants.ANONYMOUS_CHECKOUT);
	}


	@Test
	public void testForGuestInAnonymousCheckout() throws IOException, ServletException
	{
		given(Boolean.valueOf(request.isSecure())).willReturn(Boolean.TRUE);
		given(sessionService.getAttribute(WebConstants.ANONYMOUS_CHECKOUT)).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(checkoutCustomerStrategy.isAnonymousCheckout())).willReturn(Boolean.TRUE);

		filter.doFilterInternal(request, response, filterChain);

		verifyNoMoreInteractions(sessionService);
	}

}