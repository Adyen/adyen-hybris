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
package com.adyen.cockpits.components.liveedit;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;

import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cmscockpit.components.liveedit.LiveEditView;
import de.hybris.platform.cmscockpit.components.liveedit.impl.DefaultLiveEditViewModel;
import de.hybris.platform.cmscockpit.events.impl.CmsUrlChangeEvent;
import de.hybris.platform.cmscockpit.session.impl.LiveEditBrowserArea;
import de.hybris.platform.cockpit.components.notifier.Notification;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.session.UIBrowserArea;
import de.hybris.platform.cockpit.session.UICockpitPerspective;
import de.hybris.platform.cockpit.session.UISessionUtils;
import com.adyen.cockpits.cmscockpit.session.impl.DefaultLiveEditBrowserArea;

/**
 * Controller of (live edit) preview of a site.
 * <p/>
 * <b>Note:</b><br/>
 * We using this component for displaying a preview of particular web application within <b>cmscockpit</b> in following
 * modes:
 * <ul>
 * <li>displaying preview of particular page in <b>WCMS Page perspective</b></li>
 * <li>displaying live edit session in <b>Live Edit perspective</b>
 * </ul>
 */
public class DefaultLiveEditView extends LiveEditView<DefaultLiveEditPopupEditDialog>
{

	private static final Logger LOG = Logger.getLogger(DefaultLiveEditView.class);

	public DefaultLiveEditView(final DefaultLiveEditViewModel model) {
		super(model);
		initialize();
	}
	
	public DefaultLiveEditView(final DefaultLiveEditViewModel model, final Div welcomePanel)
	{
		super(model, welcomePanel);
		initialize();
	}
	
	@Override
	protected DefaultLiveEditPopupEditDialog createLiveEditPopupDialog(
			final String[] passedAttributes) throws InterruptedException {
		return new DefaultLiveEditPopupEditDialog(passedAttributes, getModel().getCurrentPreviewData().getCatalogVersions(), DefaultLiveEditView.this);
	}
	
	protected void onUrlChangeEvent(final String[] attributes)
	{
		getContentFrame().setVisible(true);
		//final String currentUrl = extractRequestPath(passedAttributes[1]);
		//getModel().setCurrentUrl(currentUrl);
		final UICockpitPerspective currentPerspective = UISessionUtils.getCurrentSession().getCurrentPerspective();
		if (!getModel().isPreviewDataValid())
		{
			final Notification notification = new Notification(Labels.getLabel("cmscockpit.liveditsession.expired"),
					Labels.getLabel("cmscockpit.liveditsession.expired.description"));
			currentPerspective.getNotifier().setNotification(notification);

			final UIBrowserArea currentBrowserArea = currentPerspective.getBrowserArea();
			if (currentBrowserArea instanceof DefaultLiveEditBrowserArea)
			{
				final LiveEditBrowserArea liveEditBrowserArea = ((LiveEditBrowserArea) currentBrowserArea);
				liveEditBrowserArea.fireModeChange(false);
			}
		}
		final CmsUrlChangeEvent cmsUrlChangeEvent = new CmsUrlChangeEvent(currentPerspective, extractRequestPath(attributes[1]),
				attributes[2], attributes[3], attributes[4]);
		UISessionUtils.getCurrentSession().sendGlobalEvent(cmsUrlChangeEvent);
	}


	protected void refreshContentFrame()
	{
		getContentFrame().setVisible(getModel().isContentVisible());
		if (getModel().isContentVisible())
		{
			final String generatedUrl = getModel().computeFinalUrl();
			if (getModel().getSite() != null && StringUtils.isBlank(getModel().getSite().getPreviewURL())
					|| StringUtils.isBlank(generatedUrl))
			{
				try
				{
					Messagebox.show(Labels.getLabel("site_url_empty"), Labels.getLabel("general.warning"), Messagebox.OK,
							Messagebox.EXCLAMATION);
				}
				catch (final InterruptedException e)
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("Errors occured while showing message box!", e);
					}
				}
			}
			else
			{
				getContentFrame().setSrc(generatedUrl);
				if (getModel() != null)
				{
					final PreviewDataModel previewDataModel = getModel().getCurrentPreviewData();
					if (previewDataModel != null && previewDataModel.getUiExperience() != null)
					{
						if (UiExperienceLevel.MOBILE.getCode().equalsIgnoreCase(previewDataModel.getUiExperience().getCode()))
						{
							getContentFrame().setWidth("320px");
						}
						else
						{
							getContentFrame().setWidth("100%");
						}
					}
	
				}
	
				Events.echoEvent(ON_INVALIDATE_LATER_EVENT, getContentFrame(), null);
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Current url : " + getModel().getCurrentUrl());
				}
			}
		}
	}


	public void updateItem(final TypedObject item, final Set<PropertyDescriptor> modifiedProperties, final Object reason)
	{
	
		if (!(reason instanceof DefaultLiveEditPopupEditDialog))
		{
			if (this.getPopupEditorDialog() != null && this.getPopupEditorDialog().isVisible() && !this.getPopupEditorDialog().equals(reason))
			{
				getPopupEditorDialog().update();
			}
		}
	}

}




