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

import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.search.Query;
import de.hybris.platform.cockpit.model.search.SearchParameterValue;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.cockpit.session.BrowserFilter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public abstract class AbstractUiExperienceFilter implements BrowserFilter
{
	private static final String ABSTRACT_PAGE_DEFAULT_PROPERTY_DESC = "abstractPage.defaultPage";
	public static final String UI_EXPERIENCE_PARAM = "uiExperienceParam";
	private TypeService typeService;


	public TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	public void removeDefaultPageFilter(final Query query)
	{
		//we have to remove a defaultPage = true filter if we are interested in immediate results..
		final PropertyDescriptor propertyDescriptor = typeService.getPropertyDescriptor(ABSTRACT_PAGE_DEFAULT_PROPERTY_DESC);
		final List<SearchParameterValue> finalSearchParams = new ArrayList<SearchParameterValue>();

		for (final SearchParameterValue searchParameter : query.getParameterValues())
		{
			if (!propertyDescriptor.equals(searchParameter.getParameterDescriptor()))
			{
				finalSearchParams.add(searchParameter);
			}
		}
		query.setParameterValues(finalSearchParams);

	}
}
