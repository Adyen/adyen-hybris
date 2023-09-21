/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adyen.v6.setup;

import static com.adyen.v6.constants.Adyenv6subscriptionConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.adyen.v6.constants.Adyenv6subscriptionConstants;
import com.adyen.v6.service.Adyenv6subscriptionService;


@SystemSetup(extension = Adyenv6subscriptionConstants.EXTENSIONNAME)
public class Adyenv6subscriptionSystemSetup
{
	private final Adyenv6subscriptionService adyenv6subscriptionService;

	public Adyenv6subscriptionSystemSetup(final Adyenv6subscriptionService adyenv6subscriptionService)
	{
		this.adyenv6subscriptionService = adyenv6subscriptionService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		adyenv6subscriptionService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return Adyenv6subscriptionSystemSetup.class.getResourceAsStream("/adyenv6subscription/sap-hybris-platform.png");
	}
}
