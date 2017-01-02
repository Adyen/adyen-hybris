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

import de.hybris.platform.cmscockpit.session.impl.CatalogBrowserArea;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.session.BrowserModel;
import org.apache.log4j.Logger;


/**
 *
 *
 *
 */
public class DefaultCatalogBrowserArea extends CatalogBrowserArea
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultCatalogBrowserArea.class);


	@Override
	public void update()
	{
		super.update();
		if (getPerspective().getActiveItem() != null)
		{
			final BrowserModel browserModel = getFocusedBrowser();
			if (browserModel instanceof DefaultCmsPageBrowserModel)
			{
				final TypedObject associatedPageTypeObject = ((DefaultCmsPageBrowserModel) browserModel).getCurrentPageObject();
				((DefaultCmsCockpitPerspective) getPerspective()).activateItemInEditorFallback(associatedPageTypeObject);
			}
		}
	}


}
