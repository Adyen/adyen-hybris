/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.adyen.v6.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;
import com.adyen.v6.constants.Adyenv6notificationConstants;

@SuppressWarnings("PMD")
public class Adyenv6notificationManager extends GeneratedAdyenv6notificationManager
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( Adyenv6notificationManager.class.getName() );
	
	public static final Adyenv6notificationManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Adyenv6notificationManager) em.getExtension(Adyenv6notificationConstants.EXTENSIONNAME);
	}
	
}
