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

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import com.adyen.core.suggestion.SimpleSuggestionService;
import com.adyen.core.suggestion.dao.SimpleSuggestionDao;

import java.util.List;


/**
 * Default implementation of {@link SimpleSuggestionService}.
 */
public class DefaultSimpleSuggestionService implements SimpleSuggestionService
{
	private SimpleSuggestionDao simpleSuggestionDao;

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public List<ProductModel> getReferencesForPurchasedInCategory(final CategoryModel category, final UserModel user,
			final ProductReferenceTypeEnum referenceType, final boolean excludePurchased, final Integer limit)
	{
		return getSimpleSuggestionDao().findProductsRelatedToPurchasedProductsByCategory(category, user, referenceType,
				excludePurchased, limit);
	}

	@Override
	public List<ProductModel> getReferencesForPurchasedInCategory(final CategoryModel category,
			final List<ProductReferenceTypeEnum> referenceTypes, final UserModel user, final boolean excludePurchased,
			final Integer limit)
	{
		return getSimpleSuggestionDao().findProductsRelatedToPurchasedProductsByCategory(category, referenceTypes, user,
				excludePurchased, limit);
	}

	@Override
	public List<ProductModel> getReferencesForProducts(final List<ProductModel> products, final List<ProductReferenceTypeEnum> referenceTypes, final UserModel user, final boolean excludePurchased, final Integer limit)
	{
		return getSimpleSuggestionDao().findProductsRelatedToProducts(products, referenceTypes, user,
				excludePurchased, limit);
	}

	protected SimpleSuggestionDao getSimpleSuggestionDao()
	{
		return simpleSuggestionDao;
	}

	public void setSimpleSuggestionDao(final SimpleSuggestionDao simpleSuggestionDao)
	{
		this.simpleSuggestionDao = simpleSuggestionDao;
	}
}
