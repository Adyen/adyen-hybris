/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.adyen.storefront.security;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;


@UnitTest
public class StorefrontAuthenticationSuccessHandlerTest
{

	private final StorefrontAuthenticationSuccessHandler authenticationSuccessHandler = BDDMockito
			.spy(new StorefrontAuthenticationSuccessHandler());

	@Mock
	private SessionService sessionService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private Authentication authentication;

	@Mock
	private CartFacade cartFacade;
	@Mock
	private CustomerFacade customerFacade;
	@Mock
	private BruteForceAttackCounter bruteForceAttackCounter;
	@Mock
	private CartData savedCart1;
	@Mock
	private CartData savedCart2;
	@Mock
	private CartData sessionCart;

	List<CartData> savedCarts;

	private static String CART_MERGED = "cartMerged";

	private static String SAVED_CART_1 = "savedCart1";
	private static String SESSION_CART = "sessionCart";

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		BDDMockito.given(authenticationSuccessHandler.getCartFacade()).willReturn(cartFacade);
		BDDMockito.given(authenticationSuccessHandler.getCustomerFacade()).willReturn(customerFacade);
		BDDMockito.given(authenticationSuccessHandler.getBruteForceAttackCounter()).willReturn(bruteForceAttackCounter);
		BDDMockito.given(authenticationSuccessHandler.getSessionService()).willReturn(sessionService);
		createUserCarts();
	}

	@Test
	public void shouldContinueToDefaultUrl()
	{
		BDDMockito.given(request.getAttribute(CART_MERGED)).willReturn(Boolean.TRUE);
		BDDMockito.doReturn(Boolean.FALSE).when(authenticationSuccessHandler).isAlwaysUseDefaultTargetUrl();
		authenticationSuccessHandler.setDefaultTargetUrl("/im/a/default/");

		Assert.assertTrue(StringUtils.equals("/im/a/default/", authenticationSuccessHandler.determineTargetUrl(request, response)));
	}

	@Test
	public void shouldContinueToCheckoutNoMerge()
	{
		BDDMockito.given(request.getAttribute(CART_MERGED)).willReturn(Boolean.FALSE);
		BDDMockito.doReturn(Boolean.FALSE).when(authenticationSuccessHandler).isAlwaysUseDefaultTargetUrl();
		authenticationSuccessHandler.setDefaultTargetUrl("/checkout");

		Assert.assertTrue(StringUtils.equals("/checkout", authenticationSuccessHandler.determineTargetUrl(request, response)));
	}

	@Test
	public void shouldRedirectToCartFromCheckoutMerge()
	{
		BDDMockito.given(request.getAttribute(CART_MERGED)).willReturn(Boolean.TRUE);
		BDDMockito.doReturn(Boolean.FALSE).when(authenticationSuccessHandler).isAlwaysUseDefaultTargetUrl();
		authenticationSuccessHandler.setDefaultTargetUrl("/checkout");

		Assert.assertTrue(StringUtils.equals("/cart", authenticationSuccessHandler.determineTargetUrl(request, response)));
	}

	@Test
	public void shouldNotReturnSessionCart()
	{
		BDDMockito.given(authenticationSuccessHandler.getCartFacade().getCartsForCurrentUser()).willReturn(savedCarts);

		Assert.assertNull(authenticationSuccessHandler.getMostRecentSavedCart(sessionCart));
	}

	@Test
	public void shouldReturnSavedCart()
	{
		savedCarts.add(savedCart1);
		savedCarts.add(savedCart2);
		BDDMockito.given(authenticationSuccessHandler.getCartFacade().getCartsForCurrentUser()).willReturn(savedCarts);

		Assert.assertEquals(authenticationSuccessHandler.getMostRecentSavedCart(sessionCart), savedCart1);
	}

	@Test
	public void shouldNotMergeCartsNoneSaved() throws Exception
	{
		setupAuthenticationHandler();
		BDDMockito.given(
				authenticationSuccessHandler.getMostRecentSavedCart(authenticationSuccessHandler.getCartFacade().getSessionCart()))
				.willReturn(null);

		authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
		BDDMockito.verify(authenticationSuccessHandler.getCartFacade(), BDDMockito.times(0)).restoreCartAndMerge(
				BDDMockito.anyString(), BDDMockito.anyString());
	}

	@Test
	public void shouldNotMergeCartsCurrentCartEmpty() throws Exception
	{
		BDDMockito.given(Boolean.valueOf(cartFacade.hasEntries())).willReturn(Boolean.FALSE);
		savedCarts.add(savedCart1);

		setupAuthenticationHandler();
		authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
		BDDMockito.verify(authenticationSuccessHandler.getCartFacade(), BDDMockito.times(0)).restoreCartAndMerge(SAVED_CART_1,
				SESSION_CART);
	}

	@Test
	public void shouldMergeCarts() throws Exception
	{
		BDDMockito.given(Boolean.valueOf(cartFacade.hasEntries())).willReturn(Boolean.TRUE);
		savedCarts.add(savedCart1);

		setupAuthenticationHandler();
		authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
		BDDMockito.verify(authenticationSuccessHandler.getCartFacade()).restoreCartAndMerge(SAVED_CART_1, SESSION_CART);
	}

	protected void setupAuthenticationHandler()
	{
		BDDMockito.doReturn(Boolean.FALSE).when(authenticationSuccessHandler).isAlwaysUseDefaultTargetUrl();

		BDDMockito.doNothing().when(customerFacade).loginSuccess();
		final CustomerData customer = BDDMockito.mock(CustomerData.class);
		BDDMockito.given(customer.getUid()).willReturn("customer");
		BDDMockito.given(customerFacade.getCurrentCustomer()).willReturn(customer);

		BDDMockito.given(authenticationSuccessHandler.getCartFacade().getCartsForCurrentUser()).willReturn(savedCarts);
		BDDMockito.given(cartFacade.hasSessionCart()).willReturn(Boolean.TRUE);
		BDDMockito.given(cartFacade.getSessionCart()).willReturn(sessionCart);

		BDDMockito.doNothing().when(bruteForceAttackCounter).resetUserCounter("customer");
	}

	protected void createUserCarts()
	{
		BDDMockito.given(savedCart1.getGuid()).willReturn(SAVED_CART_1);
		BDDMockito.given(savedCart2.getGuid()).willReturn("savedCart2");
		BDDMockito.given(sessionCart.getGuid()).willReturn(SESSION_CART);
		final OrderEntryData entry = BDDMockito.mock(OrderEntryData.class);
		final List<OrderEntryData> orderEntries = new ArrayList();
		orderEntries.add(entry);

		BDDMockito.given(sessionCart.getEntries()).willReturn(orderEntries);

		savedCarts = new ArrayList();

		savedCarts.add(sessionCart);

	}
}
