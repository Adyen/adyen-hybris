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
package com.adyen.cockpits.cmscockpit.browser.filters;

import de.hybris.platform.cockpit.model.search.Query;
import de.hybris.platform.util.localization.Localization;



public class DesktopUiExperienceBrowserFilter extends AbstractUiExperienceFilter
{
	private static final String DESKTOP_UI_EXPERIENCE_LABEL_KEY = "desktop.ui.experience.label.key";

	@Override
	public boolean exclude(final Object item)
	{

		return false;
	}

	@Override
	public void filterQuery(final Query query)
	{
		//empty because DESKTOP pages are displayed as default 
	}


	@Override
	public String getLabel()
	{
		return Localization.getLocalizedString(DESKTOP_UI_EXPERIENCE_LABEL_KEY);
	}

}
