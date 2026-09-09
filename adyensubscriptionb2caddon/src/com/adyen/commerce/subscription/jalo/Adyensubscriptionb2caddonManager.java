/*
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2026 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.commerce.subscription.jalo;

import de.hybris.platform.jalo.extension.Extension;

/**
 * The extension manager named by this addon's extensioninfo.xml. It does nothing; it has to exist.
 *
 * <p>Declaring a generated coremodule without this class in source makes the platform refuse to load the
 * extension at start-up with {@code ClassNotFoundException} for exactly this name. The build does not
 * catch it — the class is only looked for when the extension is loaded — and the symptom is not an error
 * page: the CMS page and the addon's JSP still render, because both are data and files rather than
 * extension code, so the page comes up empty with no controller behind it and nothing in the log tying
 * the two together.</p>
 *
 * <p>Written by hand rather than extending the usual {@code Generated…Manager}: this addon has no
 * items.xml worth the name, so the generator produces nothing to extend. Deriving straight from
 * {@link Extension} keeps the extension loadable without inventing a type system it does not have.</p>
 */
public class Adyensubscriptionb2caddonManager extends Extension
{
	@Override
	public String getName()
	{
		return "adyensubscriptionb2caddon";
	}
}
