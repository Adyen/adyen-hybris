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
package com.adyen.storefront.filters.btg;

import de.hybris.platform.btg.events.AbstractBTGRuleDataEvent;
import com.adyen.storefront.filters.btg.support.PkResolvingStrategy;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * FilterBean to produce a single BTG event {@link AbstractBTGRuleDataEvent}
 */
public abstract class AbstractPkResolvingBtgFilter extends AbstractBtgFilter
{
	private PkResolvingStrategy pkResolvingStrategy;

	/**
	 * @param pkResolvingStrategy
	 *           the pkResolvingStrategy to set
	 */
	@Required
	public void setPkResolvingStrategy(final PkResolvingStrategy pkResolvingStrategy)
	{
		this.pkResolvingStrategy = pkResolvingStrategy;
	}

	@Override
	protected AbstractBTGRuleDataEvent getEvent(final HttpServletRequest request)
	{
		AbstractBTGRuleDataEvent result = null;
		final String pk = pkResolvingStrategy.resolvePrimaryKey(request);
		if (!StringUtils.isEmpty(pk))
		{
			result = internalGetEvent(pk);
		}
		return result;
	}

	/**
	 * Creates an event for the given pk
	 * 
	 * @param pk
	 * @return event
	 */
	protected abstract AbstractBTGRuleDataEvent internalGetEvent(String pk);
}
