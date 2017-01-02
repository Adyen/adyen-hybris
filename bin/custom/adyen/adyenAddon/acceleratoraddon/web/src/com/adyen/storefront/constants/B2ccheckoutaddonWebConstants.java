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
package de.hybris.platform.storefront.constants;

/**
 * Global class for all B2ccheckoutaddon web constants. You can add global constants for your extension into this class.
 */
public final class B2ccheckoutaddonWebConstants
{
	//Dummy field to avoid pmd error - delete when you add the first real constant!
	public static final String deleteThisDummyField = "DELETE ME";

	private B2ccheckoutaddonWebConstants()
	{
		//empty to avoid instantiating this constant class
	}


	public static final String MODEL_KEY_ADDITIONAL_BREADCRUMB = "additionalBreadcrumb";

	public static final String BREADCRUMBS_KEY = "breadcrumbs";

	public static final String CONTINUE_URL = "session_continue_url";

	public static final String CART_RESTORATION = "cart_restoration";

	public static final String ANONYMOUS_CHECKOUT="anonymous_checkout";

	public static final String URL_ENCODING_ATTRIBUTES="encodingAttributes";

	public static final String LANGUAGE_ENCODING="languageEncoding";

	public static final String CURRENCY_ENCODING="currencyEncoding";}
