/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package com.adyen.v6.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.adyen.v6.constants.Adyenv6ordermanagementConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class Adyenv6ordermanagementManager extends GeneratedAdyenv6ordermanagementManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( Adyenv6ordermanagementManager.class.getName() );
	
	public static final Adyenv6ordermanagementManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Adyenv6ordermanagementManager) em.getExtension(Adyenv6ordermanagementConstants.EXTENSIONNAME);
	}
	
}
