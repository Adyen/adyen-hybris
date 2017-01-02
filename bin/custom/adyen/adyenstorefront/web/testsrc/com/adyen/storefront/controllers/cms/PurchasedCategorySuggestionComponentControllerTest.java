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
package com.adyen.storefront.controllers.cms;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.model.components.PurchasedCategorySuggestionComponentModel;
import de.hybris.platform.acceleratorcms.model.components.SimpleSuggestionComponentModel;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import com.adyen.facades.suggestion.SimpleSuggestionFacade;
import com.adyen.storefront.controllers.ControllerConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import junit.framework.Assert;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


/**
 * Unit test for {@link PurchasedCategorySuggestionComponentController}
 */
@UnitTest
public class PurchasedCategorySuggestionComponentControllerTest
{
	private static final String COMPONENT_UID = "componentUid";
	private static final String TEST_COMPONENT_UID = "componentUID";
	private static final String TEST_TYPE_CODE = SimpleSuggestionComponentModel._TYPECODE;
	private static final String TEST_TYPE_VIEW = ControllerConstants.Views.Cms.ComponentPrefix
			+ StringUtils.lowerCase(TEST_TYPE_CODE);
	private static final String TITLE = "title";
	private static final String TITLE_VALUE = "Accessories";
	private static final String SUGGESTIONS = "suggestions";
	private static final String COMPONENT = "component";
	private static final String CATEGORY_CODE = "CategoryCode";

	private PurchasedCategorySuggestionComponentController purchasedCategorySuggestionComponentController;

	@Mock
	private PurchasedCategorySuggestionComponentModel purchasedCategorySuggestionComponentModel;
	@Mock
	private Model model;
	@Mock
	private DefaultCMSComponentService cmsComponentService;
	@Mock
	private SimpleSuggestionFacade simpleSuggestionFacade;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private ProductData productData;
	@Mock
	private CategoryModel categoryModel;

	private final List<ProductData> productDataList = Collections.singletonList(productData);

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		purchasedCategorySuggestionComponentController = new PurchasedCategorySuggestionComponentController();
		purchasedCategorySuggestionComponentController.setCmsComponentService(cmsComponentService);
		ReflectionTestUtils
				.setField(purchasedCategorySuggestionComponentController, "simpleSuggestionFacade", simpleSuggestionFacade);
	}

	@Test
	public void testRenderComponent() throws Exception
	{
		given(purchasedCategorySuggestionComponentModel.getMaximumNumberProducts()).willReturn(Integer.valueOf(1));
		given(purchasedCategorySuggestionComponentModel.getTitle()).willReturn(TITLE_VALUE);
		given(purchasedCategorySuggestionComponentModel.getProductReferenceTypes()).willReturn(
				Arrays.asList(ProductReferenceTypeEnum.ACCESSORIES));
		given(purchasedCategorySuggestionComponentModel.getCategory()).willReturn(categoryModel);
		given(categoryModel.getCode()).willReturn(CATEGORY_CODE);
		given(Boolean.valueOf(purchasedCategorySuggestionComponentModel.isFilterPurchased())).willReturn(Boolean.TRUE);

		given(simpleSuggestionFacade.getReferencesForPurchasedInCategory(Mockito.anyString(), Mockito.anyList(),
						Mockito.anyBoolean(), Mockito.<Integer> any())).willReturn(productDataList);

		final String viewName = purchasedCategorySuggestionComponentController.handleComponent(request, response, model,
				purchasedCategorySuggestionComponentModel);
		verify(model, Mockito.times(1)).addAttribute(TITLE, TITLE_VALUE);
		verify(model, Mockito.times(1)).addAttribute(SUGGESTIONS, productDataList);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentUid() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getAbstractCMSComponent(TEST_COMPONENT_UID)).willReturn(purchasedCategorySuggestionComponentModel);
		given(purchasedCategorySuggestionComponentModel.getMaximumNumberProducts()).willReturn(Integer.valueOf(1));
		given(purchasedCategorySuggestionComponentModel.getTitle()).willReturn(TITLE_VALUE);
		given(purchasedCategorySuggestionComponentModel.getProductReferenceTypes()).willReturn(
				Arrays.asList(ProductReferenceTypeEnum.ACCESSORIES));
		given(purchasedCategorySuggestionComponentModel.getCategory()).willReturn(categoryModel);
		given(categoryModel.getCode()).willReturn(CATEGORY_CODE);
		given(Boolean.valueOf(purchasedCategorySuggestionComponentModel.isFilterPurchased())).willReturn(Boolean.TRUE);

		given(simpleSuggestionFacade.getReferencesForPurchasedInCategory(Mockito.anyString(), Mockito.anyList(),
						Mockito.anyBoolean(), Mockito.<Integer> any())).willReturn(productDataList);

		final String viewName = purchasedCategorySuggestionComponentController.handleGet(request, response, model);
		verify(model, Mockito.times(1)).addAttribute(COMPONENT, purchasedCategorySuggestionComponentModel);
		verify(model, Mockito.times(1)).addAttribute(TITLE, TITLE_VALUE);
		verify(model, Mockito.times(1)).addAttribute(SUGGESTIONS, productDataList);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(null);
		purchasedCategorySuggestionComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound2() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		purchasedCategorySuggestionComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound3() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willThrow(new CMSItemNotFoundException(""));
		purchasedCategorySuggestionComponentController.handleGet(request, response, model);
	}
}
