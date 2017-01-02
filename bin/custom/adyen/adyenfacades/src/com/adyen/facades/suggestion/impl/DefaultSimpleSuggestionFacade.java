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
package com.adyen.facades.suggestion.impl;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.variants.model.VariantProductModel;
import com.adyen.core.suggestion.SimpleSuggestionService;
import com.adyen.facades.suggestion.SimpleSuggestionFacade;

import java.util.Collections;
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
	private Converter<ProductModel, ProductData> productConverter;
	private SimpleSuggestionService simpleSuggestionService;
	private CartService cartService;

	@Override
	public List<ProductData> getReferencesForPurchasedInCategory(final String categoryCode,
			final List<ProductReferenceTypeEnum> referenceTypes, final boolean excludePurchased, final Integer limit)
	{
		final UserModel user = getUserService().getCurrentUser();
		final CategoryModel category = getCategoryService().getCategoryForCode(categoryCode);

		final List<ProductModel> suggestions = getSimpleSuggestionService().getReferencesForPurchasedInCategory(category,
				referenceTypes, user, excludePurchased, limit);

		return Converters.convertAll(suggestions, getProductConverter());
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

		return Converters.convertAll(suggestions, getProductConverter());
	}

	@Override
	public List<ProductData> getSuggestionsForProductsInCart(final List<ProductReferenceTypeEnum> referenceTypes,
			final boolean excludePurchased, final Integer limit)
	{
		if (getCartService().hasSessionCart())
		{
			final Set<ProductModel> products = new HashSet<ProductModel>();
			for (final AbstractOrderEntryModel entry : getCartService().getSessionCart().getEntries())
			{
				products.addAll(getAllBaseProducts(entry.getProduct()));
			}
			return Converters.convertAll(
					getSimpleSuggestionService().getReferencesForProducts(new LinkedList<ProductModel>(products), referenceTypes,
							getUserService().getCurrentUser(), excludePurchased, limit), getProductConverter());
		}
		return Collections.EMPTY_LIST;
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

		return Converters.convertAll(suggestions, getProductConverter());
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	protected SimpleSuggestionService getSimpleSuggestionService()
	{
		return simpleSuggestionService;
	}

	@Required
	public void setSimpleSuggestionService(final SimpleSuggestionService simpleSuggestionService)
	{
		this.simpleSuggestionService = simpleSuggestionService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}
}
