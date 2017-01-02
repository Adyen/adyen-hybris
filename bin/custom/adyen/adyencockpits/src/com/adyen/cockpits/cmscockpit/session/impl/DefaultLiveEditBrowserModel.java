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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.misc.UrlUtils;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmscockpit.components.liveedit.impl.DefaultLiveEditViewModel;
import de.hybris.platform.cmscockpit.events.impl.CmsUrlChangeEvent;
import de.hybris.platform.cmscockpit.session.impl.LiveEditBrowserModel;
import de.hybris.platform.cockpit.components.contentbrowser.AbstractContentBrowser;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;


/**
 * Represents a browser area model for <b>Live Edit Perspective</p>
 */
public class DefaultLiveEditBrowserModel extends LiveEditBrowserModel
{
	private static final Logger LOG = Logger.getLogger(DefaultLiveEditBrowserModel.class);

	private DefaultLiveEditViewModel viewModel;
	private String currentUrl = StringUtils.EMPTY;
	private CMSSiteModel activeSite;
	private UserModel frontendUser;
	private String frontendSessionId;
	private String relatedPagePk;

	//flags
	private boolean previewDataActive = false;

	//services
	private CMSAdminSiteService adminSiteService;
	private I18NService i18nService;
	private ModelService modelService;
	private CatalogVersionModel actiaveCatalogVersion;
	private UserService userService;

	@Override
	public void blacklistItems(final Collection<Integer> indexes)
	{
		// do nothing
	}

	@Override
	public void clearPreviewPageIfAny()
	{
		final PreviewDataModel previewData = this.getViewModel().getCurrentPreviewData();
		if (previewData != null)
		{
			previewData.setPage(null);
			getModelService().save(previewData);
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Cannot retrieve current preview mode!");
			}
		}
	}

	/**
	 * Sets all frontend attributes transferred to WCMSCockpit. </p>
	 */
	@Override
	public void setFrontendAttributes(final CmsUrlChangeEvent cmsUrlChangeEvent)
	{
		setCurrentUrl(cmsUrlChangeEvent.getUrl());
		setRelatedPagePk(cmsUrlChangeEvent.getRelatedPagePk());
		setFrontendUser(retriveCurrentFrontendUser(cmsUrlChangeEvent.getFrontendUserUid()));
		setFrontentSessionId(cmsUrlChangeEvent.getJaloSessionUid());
	}


	@Override
	public Object clone() throws CloneNotSupportedException
	{
		// Isn't intended to be use here.
		return null;
	}

	@Override
	public void collapse()
	{
		// Isn't intended to be use here.
	}


	@Override
	public AbstractContentBrowser createViewComponent()
	{
		return new DefaultLiveEditContentBrowser();
	}

	@Override
	public void fireModeChange(final AbstractContentBrowser content)
	{
		DefaultLiveEditContentBrowser currentContentBrowser = null;
		if (content instanceof DefaultLiveEditContentBrowser)
		{
			currentContentBrowser = (DefaultLiveEditContentBrowser) content;
			currentContentBrowser.fireModeChanged();
		}
	}

	public void fireTogglePreviewDataMode(final DefaultLiveEditContentBrowser contentBrowser)
	{
		previewDataActive = !previewDataActive;

		contentBrowser.firePreviewDataModeChanged();

	}

	public void fireTogglePreviewDataMode(final DefaultLiveEditContentBrowser contentBrowser, final boolean previewSectionActive)
	{
		previewDataActive = previewSectionActive;
		contentBrowser.firePreviewDataModeChanged();
	}

	@Override
	public CatalogVersionModel getActiaveCatalogVersion()
	{
		return actiaveCatalogVersion;
	}

	@Override
	public CMSSiteModel getActiveSite()
	{
		return activeSite;
	}

	@Override
	protected CMSAdminSiteService getCMSAdminSiteService()
	{
		if (this.adminSiteService == null)
		{
			this.adminSiteService = (CMSAdminSiteService) SpringUtil.getBean("cmsAdminSiteService");
		}
		return this.adminSiteService;
	}

	@Override
	public String getCurrentUrl()
	{
		return currentUrl;
	}

	@Override
	public UserModel getFrontendUser()
	{
		return frontendUser;
	}

	/**
	 * Retrieves internationalization service
	 * 
	 * @return internationalization service
	 */
	protected I18NService getI18NService()
	{
		if (this.i18nService == null)
		{
			this.i18nService = (I18NService) SpringUtil.getBean("i18nService");
		}
		return this.i18nService;
	}

	@Override
	public TypedObject getItem(final int index)
	{
		// Isn't intended to be use here.
		return null;
	}

	@Override
	public List<TypedObject> getItems()
	{
		// Isn't intended to be use here.
		return null;
	}

	@Override
	public String getLabel()
	{
		return "Live Edit Browser";
	}

	/**
	 * Retrieves model service
	 * 
	 * @return model service
	 */
	protected ModelService getModelService()
	{
		if (this.modelService == null)
		{
			this.modelService = (ModelService) SpringUtil.getBean("modelService");
		}
		return this.modelService;
	}

	@Override
	public PreviewDataModel getPreviewData()
	{
		PreviewDataModel previewData = this.getViewModel().getCurrentPreviewData();

		// create a new one if null or invalid
		if (previewData == null || UISessionUtils.getCurrentSession().getModelService().isRemoved(previewData))
		{
			previewData = UISessionUtils.getCurrentSession().getModelService().create(PreviewDataModel.class);
			previewData.setLanguage(getI18NService().getLanguage(UISessionUtils.getCurrentSession().getGlobalDataLanguageIso()));
			previewData.setTime(null);
			previewData.setUser(frontendUser);
			this.getViewModel().setCurrentPreviewData(previewData);
		}
		return previewData;
	}

	@Override
	public DefaultLiveEditViewModel getViewModel()
	{
		if (this.viewModel == null)
		{
			this.viewModel = newDefaultLiveEditViewModel();
		}
		return this.viewModel;
	}
	
	protected DefaultLiveEditViewModel newDefaultLiveEditViewModel()
	{
		return new DefaultLiveEditViewModel();
	}

	@Override
	public boolean isAdvancedHeaderDropdownSticky()
	{
		return true;
	}

	@Override
	public boolean isAdvancedHeaderDropdownVisible()
	{
		return isPreviewDataVisible();
	}

	@Override
	public boolean isCollapsed()
	{
		// Isn't intended to be use here.
		return false;
	}

	@Override
	public boolean isDuplicatable()
	{
		return false;
	}

	@Override
	public boolean isPreviewDataVisible()
	{
		return previewDataActive;
	}

	@Override
	public void onCmsPerpsectiveInitEvent()
	{
		getCMSAdminSiteService().setActiveSite(getActiveSite());
		getCMSAdminSiteService().setActiveCatalogVersion(getActiaveCatalogVersion());

	}

	@Override
	public void refresh()
	{
		getViewModel().clearPreviewInformation();
		updateItems();
	}

	@Override
	public void removeItems(final Collection<Integer> indexes)
	{
		// do nothing

	}

	@Override
	public void setActiaveCatalogVersion(final CatalogVersionModel actiaveCatalogVersion)
	{
		if (this.actiaveCatalogVersion == null || this.actiaveCatalogVersion != null
				&& !this.actiaveCatalogVersion.equals(actiaveCatalogVersion))
		{
			this.actiaveCatalogVersion = actiaveCatalogVersion;
			this.viewModel.setWelcomePanelVisible(actiaveCatalogVersion == null);
		}
	}

	@Override
	public void setActiveSite(final CMSSiteModel activeSite)
	{
		this.activeSite = activeSite;
	}

	@Override
	public void setCurrentSite(final CMSSiteModel site)
	{
		getViewModel().setSite(site);
	}

	@Override
	public void setCurrentUrl(final String currentUrl)
	{
		final HttpServletRequest httpServletRequest = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
		this.currentUrl = UrlUtils.extractHostInformationFromRequest(httpServletRequest, currentUrl);
		getViewModel().setCurrentUrl(this.currentUrl);
	}

	@Override
	public void setFrontendUser(final UserModel frontendUser)
	{
		this.frontendUser = frontendUser;

	}

	@Override
	protected UserModel retriveCurrentFrontendUser(final String frontendUserUid)
	{
		UserModel ret = null;
		if (StringUtils.isNotEmpty(frontendUserUid))
		{
			SessionContext ctx = null;
			try
			{
				ctx = JaloSession.getCurrentSession().createLocalSessionContext();
				ctx.setAttribute("disableRestrictions", Boolean.TRUE);
				ret = getUserService().getUser(frontendUserUid);
			}
			finally
			{
				if (ctx != null)
				{
					JaloSession.getCurrentSession().removeLocalSessionContext();
				}
			}
		}
		return ret;
	}

	@Override
	protected UserService getUserService()
	{
		if (this.userService == null)
		{
			this.userService = (UserService) SpringUtil.getBean("userService");
		}
		return this.userService;
	}

	@Override
	public void setPreviewData(final PreviewDataModel previewData)
	{
		this.getViewModel().setCurrentPreviewData(previewData);
	}

	@Override
	public void updateItems()
	{
		this.fireItemsChanged();
	}

	@Override
	public String getFrontentSessionId()
	{
		return frontendSessionId;
	}

	@Override
	public void setFrontentSessionId(final String frontentSessionId)
	{
		this.frontendSessionId = frontentSessionId;
	}

	@Override
	public String getRelatedPagePk()
	{
		return relatedPagePk;
	}

	@Override
	public void setRelatedPagePk(final String relatedPagePk)
	{
		this.relatedPagePk = relatedPagePk;
	}
}
