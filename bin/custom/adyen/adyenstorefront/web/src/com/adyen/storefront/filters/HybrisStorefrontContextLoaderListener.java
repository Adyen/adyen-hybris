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
package com.adyen.storefront.filters;

import de.hybris.platform.spring.HybrisContextLoaderListener;

import java.util.List;

import org.apache.log4j.Logger;


/**
 * The HybrisContextLoaderListener load spring config files into the the web application context. The config files are
 * also loaded from properties which are prefixed with the display name of the web application. As the accelerator is a
 * template the final name of the web application is not know at this time, therefore this class also loads properties
 * using the 'acceleratorstorefront' prefix.
 */
public class HybrisStorefrontContextLoaderListener extends HybrisContextLoaderListener
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(HybrisStorefrontContextLoaderListener.class);
	protected static final String ACCELERATORSTOREFRONT = "acceleratorstorefront";

	@Override
	protected void fillConfigLocations(final String appName, final List<String> locations)
	{
		// Get the default config
		super.fillConfigLocations(appName, locations);

		// Load the 'name independent' storefront config
		fillConfigLocationsFromExtensions(ACCELERATORSTOREFRONT, locations);
	}
}
