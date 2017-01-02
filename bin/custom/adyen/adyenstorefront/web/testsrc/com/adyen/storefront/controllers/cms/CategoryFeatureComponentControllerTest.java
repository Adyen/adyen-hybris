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
import de.hybris.platform.acceleratorcms.model.components.CategoryFeatureComponentModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import com.adyen.storefront.controllers.ControllerConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

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
 * Unit test for {@link CategoryFeatureComponentController}
 */
@UnitTest
public class CategoryFeatureComponentControllerTest
{
	private static final String COMPONENT_UID = "componentUid";
	private static final String TEST_COMPONENT_UID = "componentUID";
	private static final String TEST_TYPE_CODE = "myTypeCode";
	private static final String TEST_TYPE_VIEW = ControllerConstants.Views.Cms.ComponentPrefix
			+ StringUtils.lowerCase(TEST_TYPE_CODE);
	private static final String COMPONENT = "component";
	private static final String TEST_CATEGORY_URL = "TestCategoryUrl";
	private static final String URL = "url";

	private CategoryFeatureComponentController categoryFeatureComponentController;

	@Mock
	private Model model;
	@Mock
	private DefaultCMSComponentService cmsComponentService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private CategoryFeatureComponentModel categoryFeatureComponentModel;
	@Mock
	private Converter<CategoryModel, CategoryData> categoryUrlConverter;
	@Mock
	private CategoryModel categoryModel;
	@Mock
	private CategoryData categoryData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		categoryFeatureComponentController = new CategoryFeatureComponentController();
		categoryFeatureComponentController.setCmsComponentService(cmsComponentService);
		ReflectionTestUtils.setField(categoryFeatureComponentController, "categoryUrlConverter", categoryUrlConverter);
	}

	@Test
	public void testRenderComponent() throws Exception
	{
		given(categoryFeatureComponentModel.getCategory()).willReturn(categoryModel);
		given(categoryFeatureComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);
		given(categoryUrlConverter.convert(categoryModel)).willReturn(categoryData);
		given(categoryData.getUrl()).willReturn(TEST_CATEGORY_URL);

		final String viewName = categoryFeatureComponentController.handleComponent(request, response, model,
				categoryFeatureComponentModel);
		verify(model, Mockito.times(1)).addAttribute(URL, TEST_CATEGORY_URL);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentNoCategory() throws Exception
	{
		given(categoryFeatureComponentModel.getCategory()).willReturn(null);
		given(categoryFeatureComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);

		final String viewName = categoryFeatureComponentController.handleComponent(request, response, model,
				categoryFeatureComponentModel);
		verify(model, Mockito.times(0)).addAttribute(URL, TEST_CATEGORY_URL);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentUid() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(categoryFeatureComponentModel);
		given(categoryFeatureComponentModel.getCategory()).willReturn(categoryModel);
		given(categoryFeatureComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);
		given(categoryUrlConverter.convert(categoryModel)).willReturn(categoryData);
		given(categoryData.getUrl()).willReturn(TEST_CATEGORY_URL);

		final String viewName = categoryFeatureComponentController.handleGet(request, response, model);
		verify(model, Mockito.times(1)).addAttribute(COMPONENT, categoryFeatureComponentModel);
		verify(model, Mockito.times(1)).addAttribute(URL, TEST_CATEGORY_URL);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(null);
		categoryFeatureComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound2() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		categoryFeatureComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound3() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willThrow(new CMSItemNotFoundException(""));
		categoryFeatureComponentController.handleGet(request, response, model);
	}

}
