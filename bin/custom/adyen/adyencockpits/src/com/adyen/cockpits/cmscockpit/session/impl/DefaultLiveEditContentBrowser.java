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
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmscockpit.components.welcomepage.SiteBox;
import de.hybris.platform.cmscockpit.events.impl.CmsUrlChangeEvent;
import de.hybris.platform.cmscockpit.services.CmsCockpitService;
import de.hybris.platform.cmscockpit.session.impl.LiveEditContentBrowser;
import de.hybris.platform.cockpit.components.contentbrowser.AbstractBrowserComponent;
import de.hybris.platform.cockpit.components.contentbrowser.AbstractContentBrowser;
import de.hybris.platform.cockpit.components.contentbrowser.AbstractMainAreaBrowserComponent;
import de.hybris.platform.cockpit.components.contentbrowser.CaptionBrowserComponent;
import de.hybris.platform.cockpit.model.editor.EditorListener;
import de.hybris.platform.cockpit.model.editor.ReferenceUIEditor;
import de.hybris.platform.cockpit.model.editor.UIEditor;
import de.hybris.platform.cockpit.model.editor.impl.AbstractUIEditor;
import de.hybris.platform.cockpit.model.general.UIItemView;
import de.hybris.platform.cockpit.model.meta.BaseType;
import de.hybris.platform.cockpit.model.meta.EditorFactory;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.PropertyEditorDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.model.search.SearchType;
import de.hybris.platform.cockpit.services.config.EditorConfiguration;
import de.hybris.platform.cockpit.services.config.EditorRowConfiguration;
import de.hybris.platform.cockpit.services.config.EditorSectionConfiguration;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ObjectValueHandler;
import de.hybris.platform.cockpit.services.values.ValueHandlerException;
import de.hybris.platform.cockpit.session.AdvancedBrowserModel;
import de.hybris.platform.cockpit.session.BrowserModel;
import de.hybris.platform.cockpit.session.UIBrowserArea;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.session.impl.CreateContext;
import de.hybris.platform.cockpit.util.TypeTools;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import com.adyen.cockpits.components.liveedit.DefaultLiveEditView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;


/**
 *
 */
public class DefaultLiveEditContentBrowser extends LiveEditContentBrowser
{
	private static final Logger LOG = Logger.getLogger(DefaultLiveEditContentBrowser.class);
	private static final int EDITORS_PER_ROW = 5;
	private static final String PERSP_TAG = "persp";
	private static final String EVENTS_TAG = "events";
	private static final String PAGE_VIEW_PERSPECTIVE_ID = "cmscockpit.perspective.catalog";
	private static final String CMS_NAVIGATION_EVENT = "pageviewnavigation";
	private static final String CMS_PNAV_SITE = "pnav-site";
	private static final String CMS_PNAV_CATALOG = "pnav-catalog";
	private static final String CMS_PNAV_PAGE = "pnav-page";
	private EditorFactory editorFactory = null;

	private CMSPreviewService cmsPreviewService;
	private CMSAdminSiteService cmsAdminSiteService;
	private UserService userService;
	private SessionService sessionService;
	private CmsCockpitService cmsCockpitService;


	@Override
	public EditorFactory getEditorFactory()
	{
		if (editorFactory == null)
		{
			editorFactory = (EditorFactory) SpringUtil.getBean("EditorFactory");
		}
		return editorFactory;
	}

	@Override
	public boolean update()
	{
		updateCaption();
		updatePreviewContextSection();
		return getMainAreaComponent().update();
	}

	@Override
	protected void updatePreviewContextSection()
	{
		final BrowserModel currentBrowserModel = getModel();
		if (currentBrowserModel instanceof DefaultLiveEditBrowserModel)
		{
			final DefaultLiveEditBrowserModel liveEditBrowserModel = (DefaultLiveEditBrowserModel) currentBrowserModel;
			liveEditBrowserModel.fireTogglePreviewDataMode(DefaultLiveEditContentBrowser.this, false);
		}
	}

	/**
	 * Called whenever user change the live edit mode.
	 */
	@Override
	public void fireModeChanged()
	{
		if (getMainAreaComponent() instanceof DefaultLiveEditMainAreaComponent)
		{
			((DefaultLiveEditMainAreaComponent) getMainAreaComponent()).fireModeChanged();
			getCaptionComponent().update();
		}
	}

	@Override
	public void firePreviewDataModeChanged()
	{
		updateCaption();
		resize();
	}

	@Override
	public void setModel(final BrowserModel model)
	{
		if (model instanceof DefaultLiveEditBrowserModel)
		{
			final CMSSiteModel currentSite = ((DefaultLiveEditBrowserArea) model.getArea()).getCurrentSite();
			if (currentSite != null)
			{
				((DefaultLiveEditBrowserModel) model).setCurrentUrl(currentSite.getPreviewURL());
			}
		}
		super.setModel(model);
	}


	@Override
	protected AbstractBrowserComponent createMainAreaComponent()
	{
		return new DefaultLiveEditMainAreaComponent(getModel(), this);
	}

	@Override
	protected AbstractBrowserComponent createCaptionComponent()
	{
		return new DefaultLiveEditCaptionComponent(getModel(), this);
	}

	protected TypeService getTypeService()
	{
		return UISessionUtils.getCurrentSession().getTypeService();
	}

	public class DefaultLiveEditMainAreaComponent extends AbstractMainAreaBrowserComponent
	{
		private DefaultLiveEditView liveEditView;

		public void fireModeChanged()
		{
			final BrowserModel model = getModel();
			if (model instanceof DefaultLiveEditBrowserModel)
			{
				this.liveEditView.getModel().setLiveEditModeEnabled(
						((DefaultLiveEditBrowserArea) model.getArea()).isLiveEditModeEnabled());
				this.liveEditView.update();
			}
		}

		public DefaultLiveEditMainAreaComponent(final AdvancedBrowserModel model, final AbstractContentBrowser contentBrowser)
		{
			super(model, contentBrowser);
		}

		@Override
		public boolean update()
		{
			if (this.liveEditView != null)
			{
				this.liveEditView.update();
			}
			return false;
		}

		@Override
		protected Div createMainArea()
		{
			final Div ret = new Div();
			UITools.maximize(ret);
			final BrowserModel model = getModel();
			if (model instanceof DefaultLiveEditBrowserModel)
			{
				final DefaultLiveEditBrowserModel liveEditBrowserModel = (DefaultLiveEditBrowserModel) model;
				this.liveEditView = newDefaultLiveEditView(liveEditBrowserModel);
				this.liveEditView.getViewComponent().setParent(ret);
			}
			else
			{
				ret.appendChild(new Label(Labels.getLabel(EMPTY_MESSAGE)));
			}
			return ret;
		}
		
		protected DefaultLiveEditView newDefaultLiveEditView(final DefaultLiveEditBrowserModel liveEditBrowserModel)
		{
			return new DefaultLiveEditView(liveEditBrowserModel.getViewModel(), createWelcomePanel());
		}

		protected Div createWelcomePanel()
		{
			final Div welcomePanel = new Div();
			welcomePanel.setStyle("text-align: left; padding: 20px 8px 20px 8px;");
			welcomePanel.setSclass("z-groupbox-hm welcome_group");

			final Div labelContainer = new Div();
			final Label label = new Label(Labels.getLabel("liveedit.choose.site"));
			label.setStyle("font-weight: bold");
			label.setParent(labelContainer);
			welcomePanel.appendChild(labelContainer);
			final Div siteContainer = new Div();
			injectSites(siteContainer);
			welcomePanel.appendChild(siteContainer);
			return welcomePanel;
		}

		protected void injectSites(final Component parent)
		{
			boolean first = true;
			for (final CMSSiteModel site : getCmsCockpitService().getSites())
			{
				if (first)
				{
					first = false;
				}
				else
				{
					final Space space = new Space();
					space.setOrient("vertical");
					space.setWidth("3px");
					space.setHeight("154px");
					space.setStyle("float: left; margin-right: 3px;");
					space.setBar(true);
					parent.appendChild(space);
				}
				parent.appendChild(new SiteBox(site));
			}
		}

		@Override
		public void updateItem(final TypedObject item, final Set<PropertyDescriptor> modifiedProperties, final Object reason)
		{
			if (this.initialized)
			{
				this.liveEditView.updateItem(item, modifiedProperties, reason);
			}
			else
			{
				this.initialize();
			}
		}

		@Override
		public void updateItem(final TypedObject item, final Set<PropertyDescriptor> modifiedProperties)
		{
			updateItem(item, modifiedProperties, null);
		}


		@Override
		public void setActiveItem(final TypedObject activeItem)
		{
			// YTODO Auto-generated method stub

		}

		@Override
		public void updateActiveItems()
		{
			// YTODO Auto-generated method stub

		}

		@Override
		public void updateSelectedItems()
		{
			// YTODO Auto-generated method stub

		}

		@Override
		protected void cleanup()
		{
			// YTODO Auto-generated method stub

		}

		@Override
		protected UIItemView getCurrentItemView()
		{
			return null;
		}
	}

	@Override
	protected void updatePreviewData()
	{
		fireModeChanged();
	}

	public class DefaultLiveEditCaptionComponent extends CaptionBrowserComponent
	{
		public DefaultLiveEditCaptionComponent(final BrowserModel model, final AbstractContentBrowser contentBrowser)
		{
			super(model, contentBrowser);
		}

		@Override
		public boolean update()
		{
			final boolean ret = false;
			if (getModel().isAdvancedHeaderDropdownSticky())
			{
				this.initialize();
			}
			else
			{
				super.update();
				this.mainGroupbox.getChildren().clear();
				this.mainGroupbox.appendChild(this.createCaption());
				this.mainGroupbox.appendChild(this.createAdvancedArea());
				this.mainGroupbox.setOpen(getModel().isAdvancedHeaderDropdownVisible());
			}
			return ret;
		}

		protected SearchType getRootSearchType(final PropertyDescriptor propDescr)
		{
			SearchType searchType = null;

			final String valueTypeCode = UISessionUtils.getCurrentSession().getTypeService().getValueTypeCode(propDescr);
			if (valueTypeCode != null)
			{
				try
				{
					// get search type
					searchType = UISessionUtils.getCurrentSession().getSearchService().getSearchType(valueTypeCode);
				}
				catch (final Exception e)
				{
					//log.warn("Could not get search type for property descriptor (Reason: '" + e.getMessage() + "').");
				}
			}
			return searchType;
		}

		private Component createEditor(final TypedObject object, final EditorRowConfiguration rowConfig,
				final ObjectValueContainer valueContainer)
		{
			final Div editorContainer = new Div();
			editorContainer
					.setStyle("float: left; position: relative; padding-left: 25px; padding-top: 8px; padding-bottom: 5px; padding-right: 3px;");

			final Div labelDiv = new Div();
			//labelDiv.setStyle("width: 145px; overflow: hidden; text-align: right; position: relative; top: 3px;");
			labelDiv.setStyle("overflow: hidden; text-align: left; padding: 3px;");
			final PropertyDescriptor propDesc = rowConfig.getPropertyDescriptor();

			String label = propDesc.getName();
			if (StringUtils.isBlank(label))
			{
				label = propDesc.getQualifier();
			}
			final Label labelComp = new Label(label);
			labelComp.setStyle("font-weight: bold; color: #666;");
			labelDiv.appendChild(labelComp);
			editorContainer.appendChild(labelDiv);

			final Div editorDiv = new Div();
			editorDiv.setStyle("width: 150px; position: relative;");
			editorContainer.appendChild(editorDiv);

			final Collection<PropertyEditorDescriptor> matchingEditorDescriptors = getEditorFactory().getMatchingEditorDescriptors(
					propDesc.getEditorType());
			if (!matchingEditorDescriptors.isEmpty())
			{
				final ObjectValueContainer.ObjectValueHolder valueHolder = valueContainer.getValue(propDesc, null);

				final PropertyEditorDescriptor ped = matchingEditorDescriptors.iterator().next();
				final UIEditor editor = org.apache.commons.lang.StringUtils.isNotBlank(rowConfig.getEditor()) ? ped
						.createUIEditor(rowConfig.getEditor()) : ped.createUIEditor(TypeTools.getMultiplicityString(propDesc));
				editor.setEditable(true);

				CreateContext createContext = null;
				if (editor instanceof ReferenceUIEditor)
				{
					SearchType rootType = getRootSearchType(propDesc);

					if (rootType != null
							&& rootType.isAssignableFrom(UISessionUtils.getCurrentSession().getTypeService()
									.getObjectType(UserModel._TYPECODE)))
					{
						rootType = UISessionUtils.getCurrentSession().getSearchService().getSearchType(CustomerModel._TYPECODE);
					}

					if (rootType != null)
					{
						((ReferenceUIEditor) editor).setRootType(rootType);
					}
					//final SearchType rootType = getRootSearchType(propDesc);
					createContext = new CreateContext(rootType, UISessionUtils.getCurrentSession().getTypeService()
							.wrapItem(valueContainer.getObject()), propDesc, UISessionUtils.getCurrentSession().getLanguageIso());
				}

				final Map<String, Object> customParameters = new HashMap<String, Object>(rowConfig.getParameters());
				customParameters.put(AbstractUIEditor.ATTRIBUTE_QUALIFIER_PARAM, propDesc.getQualifier());
				customParameters.put("languageIso", UISessionUtils.getCurrentSession().getLanguageIso());
				final CMSSiteModel cmsSiteModel = getCmsAdminSiteService().getActiveSite();
				final CatalogVersionModel catalogVersionModel = getCmsAdminSiteService().getActiveCatalogVersion();
				if (cmsSiteModel != null)
				{
					customParameters.put("availableItems",
							getCmsPreviewService().getEditableCatalogs(cmsSiteModel, catalogVersionModel));
				}

				if (createContext != null)
				{
					try
					{
						customParameters.put("createContext", createContext.clone());
					}
					catch (final CloneNotSupportedException e)
					{
						LOG.error("Clone not support for preview data!", e);
					}
				}


				final HtmlBasedComponent viewComponent = editor.createViewComponent(valueHolder.getCurrentValue(), customParameters,
						new EditorListener()
						{
							@Override
							public void valueChanged(final Object value)
							{
								valueHolder.setLocalValue(value);

								for (final ObjectValueHandler valueHandler : UISessionUtils.getCurrentSession().getValueHandlerRegistry()
										.getValueHandlerChain(object.getType()))
								{
									try
									{
										valueHandler.storeValues(valueContainer);
									}
									catch (final ValueHandlerException e)
									{
										if (LOG.isDebugEnabled())
										{
											LOG.error(e.getMessage(), e);
										}
									}
								}
							}

							@Override
							public void actionPerformed(final String actionCode)
							{
								// nop
							}
						});
				editorDiv.appendChild(viewComponent);
			}

			return editorContainer;
		}

		protected Collection<EditorRowConfiguration> getRowConfigs(final BaseType type)
		{
			final List<EditorRowConfiguration> ret = new ArrayList<EditorRowConfiguration>();

			final EditorConfiguration componentConfiguration = UISessionUtils
					.getCurrentSession()
					.getUiConfigurationService()
					.getComponentConfiguration(getTypeService().getObjectTemplate(type.getCode()), "liveEditPreviewArea",
							EditorConfiguration.class);

			if (componentConfiguration != null)
			{
				for (final EditorSectionConfiguration section : componentConfiguration.getSections())
				{
					ret.addAll(section.getSectionRows());
				}
			}

			return ret;
		}

		protected void renderEditors(final Component parent)
		{
			final DefaultLiveEditBrowserModel model = (DefaultLiveEditBrowserModel) getModel();
			final PreviewDataModel previewData = model.getPreviewData();

			if (previewData != null)
			{
				final TypedObject previewDataTO = getTypeService().wrapItem(previewData);

				final Collection<EditorRowConfiguration> rowConfigs = getRowConfigs(previewDataTO.getType());
				final Collection<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
				for (final EditorRowConfiguration row : rowConfigs)
				{
					props.add(row.getPropertyDescriptor());
				}
				final ObjectValueContainer valueContainer = TypeTools.createValueContainer(previewDataTO,
						new HashSet<PropertyDescriptor>(props), UISessionUtils.getCurrentSession().getSystemService()
								.getAvailableLanguageIsos());

				int index = 0;
				Hbox singleRow = null;
				for (final EditorRowConfiguration row : rowConfigs)
				{
					if (index % EDITORS_PER_ROW == 0)
					{
						singleRow = new Hbox();
						parent.appendChild(singleRow);
					}
					singleRow.appendChild(createEditor(previewDataTO, row, valueContainer));
					index++;
				}
			}
		}

		@Override
		protected HtmlBasedComponent createAdvancedArea()
		{
			final Div previewDataAreaDiv = new Div();

			previewDataAreaDiv.setStyle("min-height: 40px; width: 100%; background: white; border-bottom: 1px solid #ccc");

			final Div attributesDiv = new Div();
			attributesDiv.setSclass("clearfix");
			attributesDiv.setStyle("text-align: left; width: 100%; ");
			renderEditors(attributesDiv);

			final Div attrContainerDiv = new Div();
			attrContainerDiv.setStyle("max-height: 265px; overflow-y: auto;");
			attrContainerDiv.appendChild(attributesDiv);
			previewDataAreaDiv.appendChild(attrContainerDiv);

			final Div buttonDiv = new Div();
			buttonDiv.setStyle("padding: 3px 20px 3px 24px; text-align:left; height: 26px; position:relative");

			final Button applyButton = new Button(Labels.getLabel("general.apply"));
			applyButton.setStyle("position:absolute; right:20px; top: 4px");
			applyButton.addEventListener(Events.ON_CLICK, new org.zkoss.zk.ui.event.EventListener()
			{
				@Override
				public void onEvent(final Event event) throws Exception //NOPMD: ZK Specific
				{

					updatePreviewData();
				}
			});
			buttonDiv.appendChild(applyButton);
			previewDataAreaDiv.appendChild(buttonDiv);

			return previewDataAreaDiv;
		}

		@Override
		protected void createAdditionalRightCaptionComponents(final Hbox hbox)
		{
			UITools.detachChildren(hbox);
			final BrowserModel browserModel = getModel();
			if (browserModel instanceof DefaultLiveEditBrowserModel)
			{
				final DefaultLiveEditBrowserModel liveEditBrowserModel = (DefaultLiveEditBrowserModel) getModel();
				if (liveEditBrowserModel.getActiaveCatalogVersion() != null)
				{
					final UIBrowserArea area = getModel().getArea();
					if (area instanceof DefaultLiveEditBrowserArea)
					{
						final DefaultLiveEditBrowserArea liveEditArea = (DefaultLiveEditBrowserArea) area;
						final boolean liveEditModeEnabled = liveEditArea.isLiveEditModeEnabled();
						// Live edit button
						createRightCaptionButton(Labels.getLabel(liveEditModeEnabled ? "browser.liveEditOn" : "browser.liveEditOff"),
								(liveEditModeEnabled ? "btnliveeditcontent_quickedit_active" : "btnliveeditcontent_quickedit"), hbox, new org.zkoss.zk.ui.event.EventListener()
								{
									@Override
									public void onEvent(final Event event) throws Exception //NOPMD: ZK Specific
									{
										liveEditArea.fireModeChange();
									}
								});

						final boolean isPreviewDataActive = liveEditBrowserModel.isPreviewDataVisible();
						// Preview context button
						createRightCaptionButton(Labels.getLabel("browser.previewData"), 
								(isPreviewDataActive ? "btnliveeditcontent_previewcontext_active" : "btnliveeditcontent_previewcontext"),
								hbox, new org.zkoss.zk.ui.event.EventListener()
								{
									@Override
									public void onEvent(final Event event) throws Exception //NOPMD: ZK Specific
									{
										liveEditBrowserModel.fireTogglePreviewDataMode(DefaultLiveEditContentBrowser.this);
									}
								});

						final String sitePk = getCmsAdminSiteService().getActiveSite().getPk().toString();
						final String catalogPk = getCmsAdminSiteService().getActiveCatalogVersion().getPk().toString();

						// Open in page edit button
						if (StringUtils.isNotBlank(liveEditBrowserModel.getRelatedPagePk()))
						{
							createRightCaptionButton(Labels.getLabel("browser.openInPageEdit"), "btnliveeditcontent_pageedit", hbox,
									new org.zkoss.zk.ui.event.EventListener()
									{
										@Override
										public void onEvent(final Event event) throws Exception //NOPMD: ZK Specific
										{
											final StringBuilder urlBuilder = new StringBuilder();
											urlBuilder.append("?").append(PERSP_TAG);
											urlBuilder.append("=").append(PAGE_VIEW_PERSPECTIVE_ID);
											urlBuilder.append("&").append(EVENTS_TAG);
											urlBuilder.append("=").append(CMS_NAVIGATION_EVENT);
											urlBuilder.append("&").append(CMS_PNAV_SITE);
											urlBuilder.append("=").append(sitePk);
											urlBuilder.append("&").append(CMS_PNAV_CATALOG);
											urlBuilder.append("=").append(catalogPk);
											urlBuilder.append("&").append(CMS_PNAV_PAGE);
											urlBuilder.append("=").append(liveEditBrowserModel.getRelatedPagePk());

											if (LOG.isDebugEnabled())
											{
												LOG.debug("URL for Open in Page edit event: " + urlBuilder.toString());
											}

											Executions.getCurrent().sendRedirect(urlBuilder.toString());
										}
									});
						}
					}
				}
			}
		}
	}

	@Override
	protected Button createRightCaptionButton(final String label, final String sClass, final HtmlBasedComponent parent,
			final org.zkoss.zk.ui.event.EventListener listener)
	{
		final Button button = new Button(label);
		button.setSclass(sClass);
		button.addEventListener(Events.ON_CLICK, listener);
		parent.appendChild(button);
		return button;
	}

	@Override
	protected void updateAfterChangedUrl(final CmsUrlChangeEvent cmsUrlChangeEvent)
	{
		final BrowserModel browserModel = getModel();
		if (browserModel instanceof DefaultLiveEditBrowserModel)
		{
			final DefaultLiveEditBrowserModel liveEditBrowserModel = (DefaultLiveEditBrowserModel) browserModel;
			liveEditBrowserModel.clearPreviewPageIfAny();
			liveEditBrowserModel.setFrontendAttributes(cmsUrlChangeEvent);
			updateCaption();
		}
		else
		{
			LOG.error("Cannot retrieve current browser model  - wrong type!");
		}
	}

	@Override
	protected CMSPreviewService getCmsPreviewService()
	{
		if (this.cmsPreviewService == null)
		{
			this.cmsPreviewService = (CMSPreviewService) SpringUtil.getBean("cmsPreviewService");
		}
		return this.cmsPreviewService;
	}

	@Override
	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		if (this.cmsAdminSiteService == null)
		{
			this.cmsAdminSiteService = (CMSAdminSiteService) SpringUtil.getBean("cmsAdminSiteService");
		}
		return this.cmsAdminSiteService;
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
	protected SessionService getSessionService()
	{
		if (this.sessionService == null)
		{
			this.sessionService = (SessionService) SpringUtil.getBean("sessionService");
		}
		return this.sessionService;
	}

	@Override
	protected CmsCockpitService getCmsCockpitService()
	{
		if (cmsCockpitService == null)
		{
			this.cmsCockpitService = (CmsCockpitService) SpringUtil.getBean("cmsCockpitService");
		}
		return cmsCockpitService;
	}

	@Override
	public void setCmsCockpitService(final CmsCockpitService cmsCockpitService)
	{
		this.cmsCockpitService = cmsCockpitService;
	}
}
