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
package com.adyen.core.suggestion.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import com.adyen.core.suggestion.dao.impl.DefaultSimpleSuggestionDao;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultSimpleSuggestionServiceTest}
 */
@UnitTest
public class DefaultSimpleSuggestionServiceTest
{
	@Mock
	private DefaultSimpleSuggestionDao simpleSuggestionDao;
	private DefaultSimpleSuggestionService defaultSimpleSuggestionService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultSimpleSuggestionService = new DefaultSimpleSuggestionService();
		defaultSimpleSuggestionService.setSimpleSuggestionDao(simpleSuggestionDao);
	}

	@Test
	public void testGetReferencedProductsForBoughtCategory()
	{
		final UserModel user = mock(UserModel.class);
		final CategoryModel category = mock(CategoryModel.class);

		final Integer limit = NumberUtils.INTEGER_ONE;
		final boolean excludePurchased = true;
		final List<ProductModel> result = Collections.emptyList();
		final ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		given(simpleSuggestionDao.findProductsRelatedToPurchasedProductsByCategory(category, user, type, excludePurchased, limit))
				.willReturn(result);

		final List<ProductModel> actual = defaultSimpleSuggestionService.getReferencesForPurchasedInCategory(category, user, type,
				excludePurchased, limit);
		Assert.assertEquals(result, actual);
	}
}
