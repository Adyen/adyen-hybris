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
package com.adyen.cockpits.cmscockpit.browser.filters;

import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;
import de.hybris.platform.cockpit.model.search.Query;
import de.hybris.platform.util.localization.Localization;


public class MobileUiExperienceBrowserFilter extends AbstractUiExperienceFilter
{
	private static final String MOBILE_UI_EXPERIENCE_LABEL_KEY = "mobile.ui.experience.label.key";

	@Override
	public boolean exclude(final Object item)
	{

		return false;
	}

	@Override
	public void filterQuery(final Query query)
	{
		//we have to remove a defaultPage = true filter if we are interested in immediate results..
		removeDefaultPageFilter(query);

		//we have to passed filter specific value
		query.setContextParameter(UI_EXPERIENCE_PARAM, UiExperienceLevel.MOBILE);
	}


	@Override
	public String getLabel()
	{
		return Localization.getLocalizedString(MOBILE_UI_EXPERIENCE_LABEL_KEY);

	}
}
