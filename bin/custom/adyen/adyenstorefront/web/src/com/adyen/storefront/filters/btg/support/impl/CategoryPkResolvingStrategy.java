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
package com.adyen.storefront.filters.btg.support.impl;

import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.core.model.ItemModel;


/**
 * Implementation of {@link AbstractParsingPkResolvingStrategy} that retrieves a category pk from the request
 */
public class CategoryPkResolvingStrategy extends AbstractParsingPkResolvingStrategy
{
	private CommerceCategoryService categoryService;

	/**
	 * @param categoryService
	 *           the categoryService to set
	 */
	public void setCategoryService(final CommerceCategoryService categoryService)
	{
		this.categoryService = categoryService;
	}


	@Override
	protected ItemModel retrieveModel(final String key)
	{
		return categoryService.getCategoryForCode(key);
	}
}
