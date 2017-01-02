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
package com.adyen.storefront.util;

import de.hybris.platform.acceleratorcms.data.RequestContextData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.core.model.product.ProductModel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;


/**
 */
public class RequestContextRestrictionConverter implements Converter<RequestContextData, RestrictionData>
{
	private static final Logger LOG = Logger.getLogger(RequestContextRestrictionConverter.class);//NOPMD

	private CMSDataFactory cmsDataFactory;

	@Override
	public RestrictionData convert(final RequestContextData source)
	{
		// Basic conversion
		final RestrictionData restrictionData = createRestrictionData(source.getCategory(), source.getProduct());

		// Here you can add any custom data that you have added to the RequestContextData into the RestrictionData

		return restrictionData;
	}

	protected RestrictionData createRestrictionData(final CategoryModel category, final ProductModel product)
	{
		return getCmsDataFactory().createRestrictionData(category, product);
	}

	private CMSDataFactory getCmsDataFactory()
	{
		return cmsDataFactory;
	}

	@Required
	public void setCmsDataFactory(final CMSDataFactory cmsDataFactory)
	{
		this.cmsDataFactory = cmsDataFactory;
	}
}
