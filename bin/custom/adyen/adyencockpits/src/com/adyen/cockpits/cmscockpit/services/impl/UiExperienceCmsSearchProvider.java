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
package com.adyen.cockpits.cmscockpit.services.impl;

import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.cmscockpit.services.impl.CmsSearchProvider;
import de.hybris.platform.cockpit.model.search.Query;
import de.hybris.platform.core.GenericCondition;
import de.hybris.platform.core.GenericQuery;
import de.hybris.platform.core.GenericSearchField;
import com.adyen.cockpits.cmscockpit.browser.filters.AbstractUiExperienceFilter;

import java.util.ArrayList;
import java.util.List;


public class UiExperienceCmsSearchProvider extends CmsSearchProvider
{


	@Override
	public List<GenericCondition> createConditions(final Query query, final GenericQuery genQuery)
	{

		final List<GenericCondition> conditions = new ArrayList<GenericCondition>();
		conditions.addAll(super.createConditions(query, genQuery));
		conditions.addAll(createUiExperienceCondition(query, genQuery));
		return conditions;
	}

	protected List<GenericCondition> createUiExperienceCondition(final Query query, final GenericQuery genQuery)
	{

		final List<GenericCondition> list = new ArrayList<GenericCondition>();
		final UiExperienceLevel ret = (UiExperienceLevel) query.getContextParameter(AbstractUiExperienceFilter.UI_EXPERIENCE_PARAM);
		if (ret != null)
		{
			final GenericCondition itemJoinCondition = GenericCondition.createJoinCondition(new GenericSearchField("item", "pk"),
					new GenericSearchField("rest2page", "source"));
			final GenericCondition restJoinCondition = GenericCondition.createJoinCondition(new GenericSearchField("rest", "pk"),
					new GenericSearchField("rest2page", "target"));
			final GenericCondition levelJoinCondition = GenericCondition.createJoinCondition(new GenericSearchField("level", "pk"),
					new GenericSearchField("rest", "uiExperience"));
			final GenericCondition uiExpirenceCondition = GenericCondition.createConditionForValueComparison(new GenericSearchField(
					"level", "name"), de.hybris.platform.core.Operator.EQUAL, ret.getCode());
			genQuery.addInnerJoin("RestrictionsForPages", "rest2page", itemJoinCondition);
			genQuery.addInnerJoin("CMSUiExperienceRestriction", "rest", restJoinCondition);
			genQuery.addInnerJoin("UiExperienceLevel ", "level", levelJoinCondition);
			list.add(uiExpirenceCondition);
		}
		return list;
	}
}
