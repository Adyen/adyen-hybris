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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;


/**
 * Unit test for {@link ProductCarouselComponentController}
 */
@UnitTest
public class ProductCarouselComponentControllerTest
{
	private static final String CODE_CATEGORIES = "codeProdCategories";
	private static final String CODE_PRODUCT = "codeProdProduct";
	private static final String COMPONENT_TITLE = "componentTitle";
	private ProductCarouselComponentController productCarouselComponentController;

	@Mock
	private ProductFacade productFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		productCarouselComponentController = new ProductCarouselComponentController();
		ReflectionTestUtils.setField(productCarouselComponentController, "accProductFacade", productFacade);

	}

	@Test
	public void testFillModel()
	{
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final Model model = mock(Model.class);
		final ProductCarouselComponentModel component = mock(ProductCarouselComponentModel.class);
		final ProductModel productModelProducts = mock(ProductModel.class);
		final ProductModel productModelCategories = mock(ProductModel.class);
		final ProductData productDataProducts = mock(ProductData.class);
		final ProductData productDataCategories = mock(ProductData.class);
		final CategoryModel categoryModel = mock(CategoryModel.class);
		given(productModelCategories.getCode()).willReturn(CODE_CATEGORIES);
		given(productModelProducts.getCode()).willReturn(CODE_PRODUCT);
		given(component.getProducts()).willReturn(Collections.singletonList(productModelProducts));
		given(
				productFacade.getProductForOptions(Mockito.same(productModelProducts),
						(List<ProductOption>) Mockito.argThat(new OptionsMatcher()))).willReturn(productDataProducts);
		given(
				productFacade.getProductForOptions(Mockito.same(productModelCategories),
						(List<ProductOption>) Mockito.argThat(new OptionsMatcher()))).willReturn(productDataCategories);
		given(component.getCategories()).willReturn(Collections.singletonList(categoryModel));
		given(categoryModel.getProducts()).willReturn(Collections.singletonList(productModelCategories));
		given(component.getTitle()).willReturn(COMPONENT_TITLE);

		productCarouselComponentController.fillModel(request, model, component);
		verify(productFacade).getProductForOptions(Mockito.same(productModelProducts),
				(List<ProductOption>) Mockito.argThat(new OptionsMatcher()));
		verify(productFacade).getProductForOptions(Mockito.same(productModelCategories),
				(List<ProductOption>) Mockito.argThat(new OptionsMatcher()));
		verify(model).addAttribute("title", COMPONENT_TITLE);
		verify(model).addAttribute(Mockito.same("productData"), Mockito.anyListOf(ProductData.class));
	}

	class OptionsMatcher extends ArgumentMatcher
	{
		@Override
		public boolean matches(final Object object)
		{
			if (object instanceof List)
			{
				final List<ProductOption> options = (List<ProductOption>) object;
				if (options.size() != 2)
				{
					return false;
				}
				if (!options.get(0).equals(ProductOption.BASIC))
				{
					return false;
				}
				if (!options.get(1).equals(ProductOption.PRICE))
				{
					return false;
				}
				return true;
			}
			return false;
		}
	}
}
