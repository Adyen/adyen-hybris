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
package com.adyen.core.strategies.impl;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.strategies.ProductReferenceTargetStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import com.adyen.core.model.ApparelSizeVariantProductModel;
import com.adyen.core.model.ApparelStyleVariantProductModel;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This strategy attempts to default x-sells to those matching the current products size.
 */
public class ApparelSizeProductReferenceTargetStrategy implements ProductReferenceTargetStrategy
{
	private CommerceCategoryService commerceCategoryService;
	private String rootCategoryCode;

	protected CommerceCategoryService getCommerceCategoryService()
	{
		return commerceCategoryService;
	}

	@Required
	public void setCommerceCategoryService(final CommerceCategoryService commerceCategoryService)
	{
		this.commerceCategoryService = commerceCategoryService;
	}

	protected String getRootCategoryCode()
	{
		return rootCategoryCode;
	}

	@Required
	public void setRootCategoryCode(final String rootCategoryCode)
	{
		this.rootCategoryCode = rootCategoryCode;
	}


	@Override
	public ProductModel getTarget(final ProductModel sourceProduct, final ProductReferenceModel reference)
	{
		if (sourceProduct instanceof ApparelSizeVariantProductModel
				&& reference.getTarget() instanceof ApparelStyleVariantProductModel)
		{
			final List<CategoryModel> sourceSuperCategories = getSuperCategoriesOfType(sourceProduct);
			final List<CategoryModel> targetSuperCategories = getSuperCategoriesOfType(reference.getTarget());
			if (CollectionUtils.containsAny(sourceSuperCategories, targetSuperCategories))
			{
				// matching taxonomy categories so try a size map
				final String size = ((ApparelSizeVariantProductModel) sourceProduct).getSize();
				for (final VariantProductModel variant : reference.getTarget().getVariants())
				{
					if (variant instanceof ApparelSizeVariantProductModel
							&& size.equals(((ApparelSizeVariantProductModel) variant).getSize()))
					{
						return variant;
					}
				}
			}
		}
		return null;
	}

	protected List<CategoryModel> getSuperCategoriesOfType(final ProductModel productModel)
	{
		final List<CategoryModel> superCategories = new LinkedList<CategoryModel>();
		for (final CategoryModel superCategory : productModel.getSupercategories())
		{
			if (isCategoryType(superCategory))
			{
				superCategories.add(superCategory);
			}
		}
		return superCategories;
	}

	protected boolean isCategoryType(final CategoryModel category)
	{
		for (final CategoryModel cm : getRootCategories(category))
		{
			if (getRootCategoryCode().equals(cm.getCode()))
			{
				return true;
			}
		}
		return false;
	}

	protected Set<CategoryModel> getRootCategories(final CategoryModel category)
	{
		final Set<CategoryModel> result = new LinkedHashSet<CategoryModel>();
		for (final List<CategoryModel> path : getCommerceCategoryService().getPathsForCategory(category))
		{
			result.add(path.get(0));
		}
		return result;
	}
}
