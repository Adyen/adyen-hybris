/*
 * [y] hybris Platform
 * 
 * Copyright (c) 2000-2016 SAP SE
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information of SAP 
 * Hybris ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with the 
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.adyen.v6.constants;

/**
 * Global class for all Adyenv6core constants. You can add global constants for your extension into this class.
 */
public final class Adyenv6coreConstants extends GeneratedAdyenv6coreConstants
{
	public static final String EXTENSIONNAME = "adyenv6core";
	public static final String PAYMENT_PROVIDER = "Adyen";

	final public static String PAYMENT_METHOD_CC = "adyen_cc";
	final public static String PAYMENT_METHOD_ONECLICK = "adyen_oneclick_";

	public static final String PROCESS_EVENT_ADYEN_CAPTURED = "AdyenCaptured";
	public static final String PROCESS_EVENT_ADYEN_AUTHORIZED = "AdyenAuthorized";
	public static final String PROCESS_EVENT_ADYEN_REFUNDED = "AdyenRefunded";

	private Adyenv6coreConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

    public static final String PLATFORM_LOGO_CODE = "adyenv6corePlatformLogo";
}
