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
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.model.components.ProductFeatureComponentModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import com.adyen.storefront.controllers.ControllerConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;


/**
 * Unit test for {@link ProductFeatureComponentController}
 */
@UnitTest
public class ProductFeatureComponentControllerTest
{
	private static final String COMPONENT_UID = "componentUid";
	private static final String TEST_COMPONENT_UID = "componentUID";
	private static final String TEST_TYPE_CODE = "myTypeCode";
	private static final String TEST_TYPE_VIEW = ControllerConstants.Views.Cms.ComponentPrefix
			+ StringUtils.lowerCase(TEST_TYPE_CODE);
	private static final String COMPONENT = "component";
	private static final String TEST_PRODUCT_URL = "TestProductUrl";
	private static final String URL = "url";

	private ProductFeatureComponentController productFeatureComponentController;

	@Mock
	private Model model;
	@Mock
	private DefaultCMSComponentService cmsComponentService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private ProductFeatureComponentModel productFeatureComponentModel;
	@Mock
	private ProductModel productModel;
	@Mock
	private ProductData productData;
	@Mock
	private ProductFacade productFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productFeatureComponentController = new ProductFeatureComponentController();
		productFeatureComponentController.setCmsComponentService(cmsComponentService);
		ReflectionTestUtils.setField(productFeatureComponentController, "accProductFacade", productFacade);
	}

	@Test
	public void testRenderComponent() throws Exception
	{
		given(productFeatureComponentModel.getProduct()).willReturn(productModel);
		given(productFeatureComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);
		given(
				productFacade.getProductForOptions(productModel,
						Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY))).willReturn(productData);
		given(productData.getUrl()).willReturn(TEST_PRODUCT_URL);

		final String viewName = productFeatureComponentController.handleComponent(request, response, model,
				productFeatureComponentModel);
		verify(model, Mockito.times(1)).addAttribute("product", productData);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentNoProduct() throws Exception
	{
		given(productFeatureComponentModel.getProduct()).willReturn(null);
		given(productFeatureComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);

		final String viewName = productFeatureComponentController.handleComponent(request, response, model,
				productFeatureComponentModel);
		verify(model, Mockito.times(0)).addAttribute(URL, TEST_PRODUCT_URL);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentUid() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(productFeatureComponentModel);
		given(productFeatureComponentModel.getProduct()).willReturn(productModel);
		given(productFeatureComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);
		given(
				productFacade.getProductForOptions(productModel,
						Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY))).willReturn(productData);
		given(productData.getUrl()).willReturn(TEST_PRODUCT_URL);

		final String viewName = productFeatureComponentController.handleGet(request, response, model);
		verify(model, Mockito.times(1)).addAttribute(COMPONENT, productFeatureComponentModel);
		verify(model, Mockito.times(1)).addAttribute("product", productData);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(null);
		productFeatureComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound2() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		productFeatureComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound3() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willThrow(new CMSItemNotFoundException(""));
		productFeatureComponentController.handleGet(request, response, model);
	}

}
