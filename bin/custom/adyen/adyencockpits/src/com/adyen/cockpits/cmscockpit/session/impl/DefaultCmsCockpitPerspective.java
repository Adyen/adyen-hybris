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

import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmscockpit.session.impl.CMSBrowserArea;
import de.hybris.platform.cmscockpit.session.impl.CmsCockpitPerspective;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.session.AdvancedBrowserModel;
import de.hybris.platform.cockpit.session.BrowserModel;
import de.hybris.platform.cockpit.session.EditorAreaController;
import de.hybris.platform.cockpit.session.UIBrowserArea;
import de.hybris.platform.cockpit.session.UISessionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
*
*/
public class DefaultCmsCockpitPerspective extends CmsCockpitPerspective
{
	private static final Logger LOG = Logger.getLogger(DefaultCmsCockpitPerspective.class);
	protected static final String CATALOGVERSION = ".catalogVersion";


	@Override
	protected void activateItemInEditorArea(final TypedObject activeItem)
	{
		if (!Boolean.TRUE.equals(DefaultCmsCockpitPerspective.activationFallBack.get()))
		{
			if (activeItem != null
					&& getTypeService().checkItemAlive(activeItem)
					&& UISessionUtils.getCurrentSession().getTypeService().getBaseType(Cms2Constants.TC.ABSTRACTPAGE)
							.isAssignableFrom(activeItem.getType()))
			{
				if (checkActiveSiteAndCatalog(activeItem))
				{
					closeOtherBrowsers(activeItem);
				}
				DefaultCmsPageBrowserModel model = getPageBrowserFor(activeItem);
				if (model == null)
				{
					model = newDefaultCmsPageBrowserModel();
					model.setCurrentPageObject(activeItem);
					model.initialize();
				}
				getBrowserArea().show(model);
				//we also open editor area
				if (getActiveItem() != null)
				{
					super.activateItemInEditorArea(activeItem);
				}
			}
			else
			{
				super.activateItemInEditorArea(activeItem);
			}
		}
		else
		{
			super.activateItemInEditorArea(activeItem);
		}
	}
	
	/**
	 * Hook for custom DefaultCmsPageBrowserModel
	 */
	protected DefaultCmsPageBrowserModel newDefaultCmsPageBrowserModel()
	{
		return new DefaultCmsPageBrowserModel();
	}

	@Override
	public void createNewItem(final ObjectTemplate template, final Map<String, Object> initValues, final boolean loadDefaultValues)
	{
		final Map<String, Object> initialValues = new HashMap<String, Object>();
		initialValues.putAll(initValues);
		if (!initialValues.containsKey("CMSItem" + CATALOGVERSION))
		{
			final TypedObject catalogVersion = getTypeService().wrapItem(getCmsAdminSiteService().getActiveCatalogVersion());
			initialValues.put("CMSItem" + CATALOGVERSION, catalogVersion);
		}

		getEditorArea().reset();
		setActiveItem(null);
		getEditorArea().setCurrentObject(getActiveItem());

		for (final BrowserModel b : getBrowserArea().getVisibleBrowsers())
		{
			getBrowserArea().updateActivation(b);
		}

		final EditorAreaController eac = getEditorArea().getEditorAreaController();
		eac.setCreateFromTemplate(template, initialValues, loadDefaultValues);
		eac.resetSectionPanelModel();
	}


	@Override
	protected void closeOtherBrowsers(final TypedObject item)
	{
		if (item != null)
		{
			final Object object = item.getObject();
			if (object instanceof AbstractPageModel)
			{
				// close all "incompatible" browsers
				final UIBrowserArea browserArea = UISessionUtils.getCurrentSession().getCurrentPerspective().getBrowserArea();
				if (browserArea instanceof CMSBrowserArea)
				{
					final CMSBrowserArea cmsBrowserArea = (CMSBrowserArea) browserArea;
					final AdvancedBrowserModel welcomeBrowserModel = cmsBrowserArea.getWelcomeBrowserModel();
					boolean containsWelcomeBrowser = false;
					final List<BrowserModel> allBrowsers = new ArrayList<BrowserModel>(cmsBrowserArea.getBrowsers());
					for (final BrowserModel browser : allBrowsers)
					{
						if (browser instanceof DefaultCmsPageBrowserModel || browser instanceof DefaultCmsPageBrowserModel)
						{
							cmsBrowserArea.close(browser);
						}
						if (browser.equals(welcomeBrowserModel))
						{
							containsWelcomeBrowser = true;
						}
					}

					if (!containsWelcomeBrowser)
					{
						if (welcomeBrowserModel == null)
						{
							LOG.error("Current browser area needs to provide a welcome browser model.");
						}
						else
						{
							cmsBrowserArea.addVisibleBrowser(0, welcomeBrowserModel);
						}
					}
				}
			}
		}
	}

	protected DefaultCmsPageBrowserModel getPageBrowserFor(final TypedObject page)
	{
		for (final BrowserModel browser : getBrowserArea().getBrowsers())
		{
			if (browser instanceof DefaultCmsPageBrowserModel)
			{
				final DefaultCmsPageBrowserModel pageBrowser = (DefaultCmsPageBrowserModel) browser;
				if (page.equals(pageBrowser.getCurrentPageObject()))
				{
					return pageBrowser;
				}
			}
		}
		return null;
	}

}
