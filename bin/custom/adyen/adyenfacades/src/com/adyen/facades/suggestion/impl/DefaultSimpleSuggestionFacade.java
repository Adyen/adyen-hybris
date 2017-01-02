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
package com.adyen.facades.suggestion.impl;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.variants.model.VariantProductModel;
import com.adyen.core.suggestion.SimpleSuggestionService;
import com.adyen.facades.suggestion.SimpleSuggestionFacade;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SimpleSuggestionFacade}.
 */
public class DefaultSimpleSuggestionFacade implements SimpleSuggestionFacade
{
	private UserService userService;
	private CategoryService categoryService;
	private ProductService productService;
	private Populator<ProductModel, ProductData> productPrimaryImagePopulator;
	private Populator<ProductModel, ProductData> productPricePopulator;
	private Converter<ProductModel, ProductData> productConverter;
	private SimpleSuggestionService simpleSuggestionService;


	@Override
	public List<ProductData> getReferencesForPurchasedInCategory(final String categoryCode,
			final List<ProductReferenceTypeEnum> referenceTypes, final boolean excludePurchased, final Integer limit)
	{
		final UserModel user = getUserService().getCurrentUser();
		final CategoryModel category = getCategoryService().getCategoryForCode(categoryCode);

		final List<ProductModel> suggestions = getSimpleSuggestionService().getReferencesForPurchasedInCategory(category,
				referenceTypes, user, excludePurchased, limit);

		return convertProducts(suggestions);
	}

	@Override
	public List<ProductData> getReferencesForProducts(final Set<String> productCodes,
			final List<ProductReferenceTypeEnum> referenceTypes, final boolean excludePurchased, final Integer limit)
	{
		final UserModel user = getUserService().getCurrentUser();

		final Set<ProductModel> products = new HashSet<ProductModel>();
		for (final String productCode : productCodes)
		{
			final ProductModel product = getProductService().getProductForCode(productCode);
			products.addAll(getAllBaseProducts(product));
		}

		final List<ProductModel> suggestions = getSimpleSuggestionService().getReferencesForProducts(
				new LinkedList<ProductModel>(products), referenceTypes, user, excludePurchased, limit);

		return convertProducts(suggestions);
	}

	protected Set<ProductModel> getAllBaseProducts(final ProductModel productModel)
	{
		final Set<ProductModel> allBaseProducts = new HashSet<ProductModel>();

		ProductModel currentProduct = productModel;
		allBaseProducts.add(currentProduct);

		while (currentProduct instanceof VariantProductModel)
		{
			currentProduct = ((VariantProductModel) currentProduct).getBaseProduct();

			if (currentProduct != null)
			{
				allBaseProducts.add(currentProduct);
			}
		}
		return allBaseProducts;
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public List<ProductData> getReferencesForPurchasedInCategory(final String categoryCode,
			final ProductReferenceTypeEnum referenceType, final boolean excludePurchased, final Integer limit)
	{
		final UserModel user = getUserService().getCurrentUser();
		final CategoryModel category = getCategoryService().getCategoryForCode(categoryCode);

		final List<ProductModel> suggestions = getSimpleSuggestionService().getReferencesForPurchasedInCategory(category, user,
				referenceType, excludePurchased, limit);

		return convertProducts(suggestions);
	}

	private List<ProductData> convertProducts(final List<ProductModel> products)
	{
		final List<ProductData> result = new LinkedList<ProductData>();
		for (final ProductModel productModel : products)
		{
			final ProductData productData = getProductConverter().convert(productModel);

			getProductPricePopulator().populate(productModel, productData);
			getProductPrimaryImagePopulator().populate(productModel, productData);

			result.add(productData);
		}
		return result;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	public ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	public Populator<ProductModel, ProductData> getProductPrimaryImagePopulator()
	{
		return productPrimaryImagePopulator;
	}

	@Required
	public void setProductPrimaryImagePopulator(final Populator<ProductModel, ProductData> productPrimaryImagePopulator)
	{
		this.productPrimaryImagePopulator = productPrimaryImagePopulator;
	}

	public Populator<ProductModel, ProductData> getProductPricePopulator()
	{
		return productPricePopulator;
	}

	@Required
	public void setProductPricePopulator(final Populator<ProductModel, ProductData> productPricePopulator)
	{
		this.productPricePopulator = productPricePopulator;
	}

	public Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	public SimpleSuggestionService getSimpleSuggestionService()
	{
		return simpleSuggestionService;
	}

	@Required
	public void setSimpleSuggestionService(final SimpleSuggestionService simpleSuggestionService)
	{
		this.simpleSuggestionService = simpleSuggestionService;
	}
}
