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
package com.adyen.core.suggestion;

import de.hybris.bootstrap.annotations.IntegrationTest;
import com.adyen.core.suggestion.impl.DefaultSimpleSuggestionService;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link DefaultSimpleSuggestionService}
 */
@IntegrationTest
public class DefaultSimpleSuggestionServiceIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private SimpleSuggestionService simpleSuggestionService;

	@Resource
	private UserService userService;

	@Resource
	private CategoryService categoryService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/adyencore/test/testSimpleSuggestionService.csv", "utf-8");
	}

	@Test
	public void testReferencesForPurchasedInCategory()
	{
		final UserModel user = userService.getUserForUID("deJol");
		final CategoryModel category = categoryService.getCategoryForCode("cameras");

		List<ProductModel> result = simpleSuggestionService.getReferencesForPurchasedInCategory(category, user, null, false, null);
		Assert.assertEquals(4, result.size());
		result = simpleSuggestionService.getReferencesForPurchasedInCategory(category, user, null, false, NumberUtils.INTEGER_ONE);
		Assert.assertEquals(1, result.size());
		result = simpleSuggestionService.getReferencesForPurchasedInCategory(category, user, ProductReferenceTypeEnum.SIMILAR,
				false, null);
		Assert.assertEquals(1, result.size());
		result = simpleSuggestionService.getReferencesForPurchasedInCategory(category, user, ProductReferenceTypeEnum.ACCESSORIES,
				false, null);
		Assert.assertEquals(2, result.size());
		result = simpleSuggestionService.getReferencesForPurchasedInCategory(category, user, ProductReferenceTypeEnum.ACCESSORIES,
				true, null);
		Assert.assertEquals(1, result.size());
		final ProductModel product = result.get(0);
		Assert.assertEquals("adapterDC", product.getCode());
		Assert.assertEquals("adapter", product.getName());
	}
}
