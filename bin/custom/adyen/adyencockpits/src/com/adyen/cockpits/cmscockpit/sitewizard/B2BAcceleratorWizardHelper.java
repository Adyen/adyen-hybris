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
package com.adyen.cockpits.cmscockpit.sitewizard;

import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.util.Config;

/**
 * Creates new B2B site from given information. Intended for usage within accelerator cms site wizard.
 */
public class B2BAcceleratorWizardHelper extends AcceleratorWizardHelper
{

	@Override
	protected SiteChannel getSiteChannel()
	{
		return SiteChannel.B2B;
	}

	@Override
	protected String getStorefrontContextRoot()
	{
		return Config.getString("b2bStorefrontContextRoot", "/acceleratorstorefront");
	}
}
