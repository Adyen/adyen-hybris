/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.adyen.v6.constants;

/**
 * Global class for all Adyenv6b2ccheckoutaddon constants. You can add global constants for your extension into this class.
 */
public final class Adyenv6b2ccheckoutaddonConstants extends GeneratedAdyenv6b2ccheckoutaddonConstants
{
	public static final String EXTENSIONNAME = "adyenv6b2ccheckoutaddon";
	public static final String PAYMENT_PROVIDER = "Adyen";

	public static final String WS_USERNAME = "adyen.ws.username";
	public static final String WS_PASSWORD = "adyen.ws.password";

	public static final String NOTIFICATION_USERNAME = "adyen.notification.username";
	public static final String NOTIFICATION_PASSWORD = "adyen.notification.password";
	public static final String CONFIG_MERCHANT_ACCOUNT = "adyen.merchantaccount";
	public static final String CONFIG_CSE_ID = "adyen.cse.id";
	public static final String CONFIG_SKIN_CODE = "adyen.skin.code";
	public static final String CONFIG_SKIN_HMAC = "adyen.skin.hmac";

	public static final String CONFIG_IMMEDIATE_CAPTURE = "adyen.capture.immediate";

	final public static String PAYMENT_METHOD_CC = "adyen_cc";

	private Adyenv6b2ccheckoutaddonConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
