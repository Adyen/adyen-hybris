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
package com.adyen.storefront.controllers.cms;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.enums.CartTotalDisplayType;
import de.hybris.platform.acceleratorcms.model.components.MiniCartComponentModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import com.adyen.storefront.controllers.ControllerConstants;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;


@UnitTest
public class MiniCartComponentControllerTest
{

	private static final String COMPONENT_UID = "componentUid";
	private static final String TEST_COMPONENT_UID = "MiniCart";
	private static final String TEST_TYPE_CODE = "myTypeCode";
	private static final String TEST_TYPE_VIEW = ControllerConstants.Views.Cms.ComponentPrefix
			+ StringUtils.lowerCase(TEST_TYPE_CODE);
	private static final Integer TOTAL_UNIT_COUNT = Integer.valueOf(1);
	private static final BigDecimal SUB_TOTAL_VALUE = BigDecimal.valueOf(100);
	private static final BigDecimal TOTAL_VALUE = BigDecimal.valueOf(200);
	private static final BigDecimal DELIVERY_VALUE = BigDecimal.valueOf(20);

	private MiniCartComponentController miniCartComponentController;
	private MiniCartComponentModel miniCartComponentModel;

	@Mock
	private DefaultCMSComponentService cmsComponentService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private CartFacade cartFacade;

	@Before
	public void setUp() throws CMSItemNotFoundException
	{
		MockitoAnnotations.initMocks(this);
		miniCartComponentController = new MiniCartComponentController();
		miniCartComponentController.setCmsComponentService(cmsComponentService);

		miniCartComponentModel = new MiniCartComponentModel();

		final PriceData subTotal = new PriceData();
		subTotal.setValue(SUB_TOTAL_VALUE);
		final PriceData totalPrice = new PriceData();
		totalPrice.setValue(TOTAL_VALUE);
		final PriceData deliveryCost = new PriceData();
		deliveryCost.setValue(DELIVERY_VALUE);

		final CartData cartData = new CartData();
		cartData.setSubTotal(subTotal);
		cartData.setTotalPrice(totalPrice);
		cartData.setDeliveryCost(deliveryCost);
		cartData.setTotalUnitCount(TOTAL_UNIT_COUNT);

		given(cartFacade.getMiniCart()).willReturn(cartData);
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);

		ReflectionTestUtils.setField(miniCartComponentController, "cartFacade", cartFacade, CartFacade.class);
	}

	@Test
	public void testSubtotal() throws Exception
	{
		final ExtendedModelMap model = new ExtendedModelMap();
		miniCartComponentModel.setTotalDisplay(CartTotalDisplayType.SUBTOTAL);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(miniCartComponentModel);
		miniCartComponentController.handleGet(request, response, model);
		final PriceData priceData = (PriceData) model.get(MiniCartComponentController.SUB_TOTAL);
		Assert.assertEquals(SUB_TOTAL_VALUE, priceData.getValue());
	}

	@Test
	public void testTotal() throws Exception
	{
		final ExtendedModelMap model = new ExtendedModelMap();
		miniCartComponentModel.setTotalDisplay(CartTotalDisplayType.TOTAL);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(miniCartComponentModel);
		miniCartComponentController.handleGet(request, response, model);
		final PriceData priceData = (PriceData) model.get(MiniCartComponentController.TOTAL_PRICE);
		Assert.assertEquals(TOTAL_VALUE, priceData.getValue());
	}

	@Test
	public void testTotalWithoutDelivery() throws Exception
	{
		final ExtendedModelMap model = new ExtendedModelMap();
		miniCartComponentModel.setTotalDisplay(CartTotalDisplayType.TOTAL_WITHOUT_DELIVERY);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(miniCartComponentModel);
		miniCartComponentController.handleGet(request, response, model);
		final PriceData priceData = (PriceData) model.get(MiniCartComponentController.TOTAL_NO_DELIVERY);
		Assert.assertEquals(TOTAL_VALUE.subtract(DELIVERY_VALUE), priceData.getValue());
	}

	@Test
	public void testRenderComponent() throws Exception
	{
		final ExtendedModelMap model = new ExtendedModelMap();
		final String viewName = miniCartComponentController.handleComponent(request, response, model, miniCartComponentModel);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}
}
