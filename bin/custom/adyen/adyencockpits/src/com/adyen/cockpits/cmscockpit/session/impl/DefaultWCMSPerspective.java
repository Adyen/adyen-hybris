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
package com.adyen.cockpits.cmscockpit.session.impl;

import de.hybris.platform.cmscockpit.events.impl.CmsPerspectiveInitEvent;
import de.hybris.platform.cockpit.session.UISessionUtils;

import java.util.Map;


/**
 * 
 * Default implementation of WCMSPerspective.
 * 
 */
public class DefaultWCMSPerspective extends DefaultCmsCockpitPerspective
{
	@Override
	public void initialize(final Map<String, Object> params)
	{
		UISessionUtils.getCurrentSession().sendGlobalEvent(new CmsPerspectiveInitEvent(this));
		super.initialize(params);
	}
}
