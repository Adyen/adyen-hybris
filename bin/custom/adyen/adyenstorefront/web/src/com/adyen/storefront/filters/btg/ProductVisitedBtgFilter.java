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
import de.hybris.platform.btg.events.ProductVisitedBTGRuleDataEvent;


/**
 * FilterBean to produce the BTG event {@link ProductVisitedBTGRuleDataEvent}
 * This is a spring configured filter that is executed by the PlatformFilterChain.
 */
public class ProductVisitedBtgFilter extends AbstractPkResolvingBtgFilter
{
	@Override
	protected AbstractBTGRuleDataEvent internalGetEvent(final String pk)
	{
		return new ProductVisitedBTGRuleDataEvent(pk);
	}
}
