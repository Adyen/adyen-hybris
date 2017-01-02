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
package com.adyen.cockpits.cmscockpit.components.contentbrowser;


import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmscockpit.components.contentbrowser.CmsPageContentBrowser;
import de.hybris.platform.cmscockpit.components.contentbrowser.CmsPageMainAreaPreviewComponentFactory;
import de.hybris.platform.cmscockpit.components.contentbrowser.CmsPageToolbarBrowserComponent;
import de.hybris.platform.cockpit.components.contentbrowser.AbstractBrowserComponent;
import de.hybris.platform.cockpit.components.contentbrowser.CaptionBrowserComponent;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.core.PK;
import com.adyen.cockpits.cmscockpit.session.impl.DefaultCmsPageBrowserModel;

import java.util.Set;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;


/**
 *
 */
public class DefaultCmsPageContentBrowser extends CmsPageContentBrowser
{
	private static final Logger LOG = Logger.getLogger(DefaultCmsPageContentBrowser.class);

	@Override
	protected AbstractBrowserComponent createCaptionComponent()
	{
		return new CaptionBrowserComponent(this.getModel(), this)
		{
			HtmlBasedComponent rightCaptionComponent = null;

			@Override
			protected HtmlBasedComponent createCaptionLabelComponent()
			{
				final Div div = new Div();
				div.setSclass("page_browser_label");
				div.appendChild(new Label(getModel().getExtendedLabel()));
				return div;
			}

			@Override
			protected HtmlBasedComponent createRightCaptionContent()
			{
				rightCaptionComponent = super.createRightCaptionContent();
				if (((DefaultCmsPageBrowserModel) getModel()).isBackButtonVisible())
				{
					final Button backBtn = new Button(Labels.getLabel("general.reload"));
					backBtn.setSclass("btnred btnReload");
					backBtn.addEventListener(Events.ON_CLICK, new EventListener()
					{
						@Override
						public void onEvent(final Event event) throws Exception
						{
							final Object previewFrame = DefaultCmsPageContentBrowser.this
									.getAttribute(CmsPageMainAreaPreviewComponentFactory.PREVIEW_FRAME_KEY);
							if (previewFrame instanceof Iframe)
							{
								((Iframe) previewFrame).invalidate();
							}
							else
							{
								LOG.warn("Could not reset page preview. Reason: Preview frame is not an Iframe");
							}
						}
					});

					rightCaptionComponent.appendChild(backBtn);
				}


				final Button button = new Button(Labels.getLabel("browser.openInLiveEdit"));
				rightCaptionComponent.appendChild(button);
				button.setSclass("btnNavigationWithLabel btnGotoLiveEdit");
				UITools.addBusyListener(button, Events.ON_CLICK, new EventListener()
				{
					@Override
					public void onEvent(final Event event)
					{
						final Object browserModel = DefaultCmsPageContentBrowser.this.getModel();
						if (browserModel instanceof DefaultCmsPageBrowserModel)
						{
							final DefaultCmsPageBrowserModel cmsPageBrowserModel = (DefaultCmsPageBrowserModel) browserModel;
							if (cmsPageBrowserModel.getCurrentPageObject() != null && cmsPageBrowserModel.getActiveSite() != null
									&& cmsPageBrowserModel.getActiveCatalogVersion() != null)
							{
								final Object activeItem = cmsPageBrowserModel.getCurrentPageObject().getObject();
								final PK pagePk = ((AbstractPageModel) activeItem).getPk();

								final StringBuilder urlBuilder = new StringBuilder();
								urlBuilder.append("?").append(PERSP_TAG);
								urlBuilder.append("=").append(LIVE_EDIT_PERSPECTIVE_ID);
								urlBuilder.append("&").append(EVENTS_TAG);
								urlBuilder.append("=").append(LIVE_EDIT_PAGE_NAVIGATION_EVENT);
								urlBuilder.append("&").append(LIVE_EDIT_SITE);
								urlBuilder.append("=").append(cmsPageBrowserModel.getActiveSite().getPk().toString());
								urlBuilder.append("&").append(LIVE_EDIT_CATALOG);
								urlBuilder.append("=").append(cmsPageBrowserModel.getActiveCatalogVersion().getPk().toString());
								urlBuilder.append("&").append(LIVE_EDIT_PAGE);
								urlBuilder.append("=").append(pagePk.getLongValueAsString());

								if (LOG.isDebugEnabled())
								{
									LOG.debug("URL for Open in live edit page navigation event: " + urlBuilder.toString());
								}

								Executions.getCurrent().sendRedirect(urlBuilder.toString());
							}
							else
							{
								LOG.error("Either currentPageObject or ActiveSite or ActiveCatalogVersion is null");
							}
						}
					}
				}, null, null);
				return rightCaptionComponent;
			}

			@Override
			public boolean update()
			{
				final boolean ret = super.update();

				if (rightCaptionComponent != null)
				{
					final Component parent = rightCaptionComponent.getParent();
					rightCaptionComponent.detach();
					parent.appendChild(createRightCaptionContent());
				}

				return ret;
			}
		};
	}

	@Override
	protected AbstractBrowserComponent createToolbarComponent()
	{
		AbstractBrowserComponent ret = null;
		if (this.getModel() instanceof DefaultCmsPageBrowserModel)
		{
			ret = new CmsPageToolbarBrowserComponent((DefaultCmsPageBrowserModel) this.getModel(), this);
		}
		return ret;
	}

	@Override
	public void updateItem(final TypedObject item, final Set<PropertyDescriptor> modifiedProperties, final Object reason)
	{
		super.updateItem(item, modifiedProperties, reason);
		if (this.getModel() instanceof DefaultCmsPageBrowserModel)
		{
			updateToolbar();

		}
	}

	@Override
	public void updateStatusBar()
	{
		//don't have a status bar
	}

}
