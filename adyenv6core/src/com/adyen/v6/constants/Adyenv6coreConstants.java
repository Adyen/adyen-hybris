/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Global class for all Adyenv6core constants. You can add global constants for your extension into this class.
 */
public final class Adyenv6coreConstants extends GeneratedAdyenv6coreConstants {
	public static final String EXTENSIONNAME = "adyenv6core";

	public static final String PLUGIN_NAME = "adyen-hybris";
	public static final String PLUGIN_VERSION = "10.1.0";
	public static final String PAYMENT_PROVIDER = "Adyen";
	public static final String PAYMENT_METHOD ="paymentMethod";

	final public static String PAYMENT_METHOD_CC = "adyen_cc";
	final public static String PAYMENT_METHOD_EPS = "eps";
	final public static String PAYMENT_METHOD_IDEAL = "ideal";
	final public static String PAYMENT_METHOD_ONECLICK = "adyen_oneclick_";
	final public static String PAYMENT_METHOD_BOLETO = "boleto";
	final public static String PAYMENT_METHOD_PIX = "pix";
	final public static String PAYMENT_METHOD_SEPA_DIRECTDEBIT = "sepadirectdebit";
	final public static String PAYMENT_METHOD_BOLETO_SANTANDER = "boletobancario_santander";
	final public static String PAYMENT_METHOD_MULTIBANCO = "multibanco";
	final public static String PAYMENT_METHOD_POS = "pos";
	final public static String PAYMENT_METHOD_PAYPAL = "paypal";
	final public static String PAYMENT_METHOD_SCHEME = "scheme";
	final public static String PAYMENT_METHOD_APPLEPAY = "applepay";
	final public static String PAYMENT_METHOD_AMAZONPAY = "amazonpay";
	final public static String PAYMENT_METHOD_BCMC = "bcmc";
	final public static String PAYMENT_METHOD_BCMC_MOBILE = "bcmc_mobile";


	public static final String PROCESS_EVENT_ADYEN_CAPTURED = "AdyenCaptured";
	public static final String PROCESS_EVENT_ADYEN_PAYMENT_RESULT = "AdyenPaymentResult";
	public static final String PROCESS_EVENT_ADYEN_REFUNDED = "AdyenRefunded";

	public static final String KLARNA = "klarna";
	public static final String RATEPAY = "ratepay";
	public static final String AFTERPAY = "afterpay_default";
	public static final String AFTERPAY_TOUCH = "afterpaytouch";
	public static final String PAYBRIGHT = "paybright";
	public static final String COUNTRY_CODE_SWEDEN = "SE";
	public static final String COUNTRY_CODE_DENMARK = "DK";
	public static final String COUNTRY_CODE_FINLAND = "FI";
	public static final String COUNTRY_CODE_NORWAY = "NO";
	public static final String KLARNA_SLICE = "klarna_account";
	//Prefix for Oney methods like facilpay_3x or facilpay_4x
	public static final String PAYMENT_METHOD_FACILPAY_PREFIX = "facilypay";
	public static final String CARD_TYPE_DEBIT = "debit";
	public static final String AFFIRM = "affirm";
	public static final String CLEARPAY = "clearpay";
	public static final String GIFT_CARD = "giftcard";

	private Adyenv6coreConstants() {
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

	public static final String PLATFORM_LOGO_CODE = "adyenv6corePlatformLogo";

	public static final List<String> OPENINVOICE_METHODS_API = Collections.unmodifiableList(new ArrayList<String>() {{
		add(KLARNA);
		add(RATEPAY);
		add(KLARNA_SLICE);
		add(AFFIRM);
		add(CLEARPAY);
		add(AFTERPAY);
		add(AFTERPAY_TOUCH);
		add(PAYBRIGHT);
	}});


	public static final List<String> OPENINVOICE_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER = Collections.unmodifiableList(new ArrayList<String>() {{
		add(COUNTRY_CODE_SWEDEN);
		add(COUNTRY_CODE_DENMARK);
		add(COUNTRY_CODE_FINLAND);
		add(COUNTRY_CODE_NORWAY);
	}});

	public static final List<String> PAYMENT_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER = Collections.unmodifiableList(new ArrayList<String>() {{
		add(RATEPAY);
		add(PAYMENT_METHOD_BOLETO);
		add(PAYMENT_METHOD_PIX);
	}});

	public static final List<String> ISSUER_PAYMENT_METHODS = Collections.unmodifiableList(new ArrayList<String>() {{
		add(PAYMENT_METHOD_IDEAL);
		add(PAYMENT_METHOD_EPS);
	}});
}
