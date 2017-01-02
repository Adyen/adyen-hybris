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
package com.adyen.storefront.web.view;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * The unit test for SwitchingViewResolver.
 */
@UnitTest
public class UiExperienceViewResolverTest
{
	/**
	 * Tests the UiExperienceViewResolver's getViewName method.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetViewName() throws Exception
	{
		final UiExperienceViewResolver viewResolver = new UiExperienceViewResolver();

		final Map<UiExperienceLevel, String> deviceViewPrefix = new HashMap<UiExperienceLevel, String>();
		deviceViewPrefix.put(UiExperienceLevel.DESKTOP, "desktop/");
		deviceViewPrefix.put(UiExperienceLevel.MOBILE, "mobile/");
		viewResolver.setUiExperienceViewPrefix(deviceViewPrefix);

		assertEquals("desktop/pages", viewResolver.getViewName(UiExperienceLevel.DESKTOP, "pages"));
		assertEquals("mobile/pages", viewResolver.getViewName(UiExperienceLevel.MOBILE, "pages"));
	}
}
