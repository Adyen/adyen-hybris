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
package com.adyen.storefront.filters.btg.support.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.product.ProductService;


/**
 * Implementation of {@link AbstractParsingPkResolvingStrategy} that retrieves a product pk from the request
 */
public class ProductPkResolvingStrategy extends AbstractParsingPkResolvingStrategy
{
	private ProductService productService;

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	@Override
	protected ItemModel retrieveModel(final String key)
	{
		return productService.getProductForCode(key);
	}
}
