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
package com.adyen.cockpits.cmscockpit.sitewizard;

import de.hybris.platform.cmscockpit.wizard.cmssite.CmsSiteWizard;
import de.hybris.platform.cmscockpit.wizard.cmssite.pages.CmsSiteSummaryPage;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.cockpit.wizards.generic.GenericItemWizard;
import de.hybris.platform.commerceservices.enums.SiteTheme;
import de.hybris.platform.util.localization.Localization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;


/**
 * Summary page for websites templates wizard.
 */
public class SummaryPage extends CmsSiteSummaryPage
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SummaryPage.class);

	private static final String SUMMARY_ROW_SCLASS = "summaryRow";
	private static final String SUMMARY_PAGE_CNT_SCLASS = "summaryPageCnt";

	private static final String SITE_NAME_QUALIFIER = "siteName";
	private static final String SITE_ACTIVE_QUALIFIER = "active";
	private static final String SITE_STORES_QUALIFIER = "stores";
	private static final String SITE_TEMPLATES_QUALIFIER = "templates";
	private static final String SITE_CONTENTCATALOG_NAME_QUALIFIER = "contentcatalogname";
	private static final String SITE_THEME_QUALIFIER = "theme";

	protected static final String BOOLEAN_TRUE_IMG = "/cockpit/images/bool_true.gif";
	protected static final String BOOLEAN_FALSE_IMG = "/cockpit/images/bool_false.gif";
	protected static final String BOOLEAN_NULL_IMG = "/cockpit/images/bool_null.gif";

	private static final String COCKPIT_ID_CREATEWEBSITE_SUMMARY_NAME_INPUT = "CreateWebsite_Summary_Name_input";
	private static final String COCKPIT_ID_CREATEWEBSITE_SUMMARY_ACTIVE_INPUT = "CreateWebsite_Summary_Active_image";
	private static final String COCKPIT_ID_CREATEWEBSITE_SUMMARY_BASESTORES_INPUT = "CreateWebsite_Summary_Basestores_input";
	private static final String COCKPIT_ID_CREATEWEBSITE_SUMMARY_PAGETEMPLATES_INPUT = "CreateWebsite_Summary_Pagetemplates_input";
	private static final String COCKPIT_ID_CREATEWEBSITE_SUMMARY_CONTENTCATALOGS_INPUT = "CreateWebsite_Summary_Contentcatalogs_input";
	private static final String COCKPIT_ID_CREATEWEBSITE_SUMMARY_THEME_INPUT = "CreateWebsite_Summary_Theme_input";

	protected static class ListboxRenderer implements ListitemRenderer
	{
		@Override
		public void render(final Listitem item, final Object data) throws Exception// NOPMD ZK Specific
		{
			final TypedObject wrappedObject = UISessionUtils.getCurrentSession().getTypeService().wrapItem(data);
			final Listcell listboxCell = new Listcell();
			final Label entryName = new Label(UISessionUtils.getCurrentSession().getLabelService()
					.getObjectTextLabelForTypedObject(wrappedObject));
			listboxCell.appendChild(entryName);
			item.appendChild(listboxCell);
		}
	}

	public SummaryPage()
	{
		super();
	}

	public SummaryPage(final String pageTitle)
	{
		super(pageTitle);
	}

	public SummaryPage(final String pageTitle, final GenericItemWizard wizard)
	{
		super(pageTitle, wizard);
	}

	@Override
	public Component createSummaryEntryRow(final String labelValue, final Component value)
	{
		final Hbox summaryEntryRow = new Hbox();
		summaryEntryRow.setWidths("45%,55%");
		summaryEntryRow.setSclass(SUMMARY_ROW_SCLASS);
		final Label summaryInfoLabel = new Label(labelValue);
		summaryEntryRow.appendChild(summaryInfoLabel);
		summaryEntryRow.appendChild(value);
		return summaryEntryRow;
	}

	@Override
	public Component createRepresentationItself()
	{
		UITools.detachChildren(pageContent);

		final ListboxRenderer listboxRenderer = new ListboxRenderer();

		final Div labelInfoContainer = new Div();
		labelInfoContainer.setSclass(CmsSiteWizard.WIZARD_LABEL_CONTAINER);
		labelInfoContainer.setParent(pageContent);
		final Label labelInfo = new Label(Labels.getLabel("summary.page.label.info"));
		labelInfoContainer.appendChild(labelInfo);

		final Vbox contextInformation = new Vbox();
		contextInformation.setSclass(SUMMARY_PAGE_CNT_SCLASS);
		contextInformation.setParent(pageContent);
		final Map<String, Object> information = new HashMap<String, Object>(collectAllInformation());

		createNameRow(information, contextInformation);
		createSiteActiveRow(information, contextInformation);
		createStoresRow(information, contextInformation, listboxRenderer);
		createSiteTemplatesRow(information, contextInformation, listboxRenderer);
		createContentCatalogRow(information, contextInformation, listboxRenderer);
		createThemeRow(information, contextInformation);
		return pageContainer;
	}

	protected void createNameRow(final Map<String, Object> information, final Vbox contextInformation)
	{
		final Textbox nameBox = new Textbox();
		nameBox.setReadonly(true);
		nameBox.setValue((String) information.get(SITE_NAME_QUALIFIER));
		if (UISessionUtils.getCurrentSession().isUsingTestIDs())
		{
			UITools.applyTestID(nameBox, COCKPIT_ID_CREATEWEBSITE_SUMMARY_NAME_INPUT);
		}
		contextInformation.appendChild(createSummaryEntryRow(Labels.getLabel("wizard.summarypage.cmssite.name"), nameBox));
	}

	protected void createSiteActiveRow(final Map<String, Object> information, final Vbox contextInformation)
	{
		final Object object = information.get(SITE_ACTIVE_QUALIFIER);
		final Image booleanImg = new Image();
		if (Boolean.TRUE.equals(object))
		{
			booleanImg.setSrc(BOOLEAN_TRUE_IMG);
		}
		else if (Boolean.FALSE.equals(object))
		{
			booleanImg.setSrc(BOOLEAN_FALSE_IMG);
		}
		else
		{
			booleanImg.setSrc(BOOLEAN_NULL_IMG);
		}
		if (UISessionUtils.getCurrentSession().isUsingTestIDs())
		{
			UITools.applyTestID(booleanImg, COCKPIT_ID_CREATEWEBSITE_SUMMARY_ACTIVE_INPUT);
		}
		contextInformation.appendChild(createSummaryEntryRow(Labels.getLabel("wizard.summarypage.cmssite.active"), booleanImg));
	}

	protected void createStoresRow(final Map<String, Object> information, final Vbox contextInformation,
	                               final ListboxRenderer listboxRenderer)
	{
		final Listbox storesCombobox = new Listbox();
		storesCombobox.setItemRenderer(listboxRenderer);
		storesCombobox.setModel(new SimpleListModel((List) information.get(SITE_STORES_QUALIFIER)));
		if (UISessionUtils.getCurrentSession().isUsingTestIDs())
		{
			UITools.applyTestID(storesCombobox, COCKPIT_ID_CREATEWEBSITE_SUMMARY_BASESTORES_INPUT);
		}
		contextInformation.appendChild(createSummaryEntryRow(Labels.getLabel("wizard.summarypage.cmssite.stores"), storesCombobox));
	}

	protected void createSiteTemplatesRow(final Map<String, Object> information, final Vbox contextInformation,
	                                      final ListboxRenderer listboxRenderer)
	{
		final Listbox templatesCombobox = new Listbox();
		templatesCombobox.setItemRenderer(listboxRenderer);
		templatesCombobox.setModel(new SimpleListModel((List) information.get(SITE_TEMPLATES_QUALIFIER)));
		if (UISessionUtils.getCurrentSession().isUsingTestIDs())
		{
			UITools.applyTestID(templatesCombobox, COCKPIT_ID_CREATEWEBSITE_SUMMARY_PAGETEMPLATES_INPUT);
		}
		contextInformation.appendChild(createSummaryEntryRow(Labels.getLabel("wizard.summarypage.cmssite.templates"),
				templatesCombobox));
	}

	protected void createContentCatalogRow(final Map<String, Object> information, final Vbox contextInformation,
	                                       final ListboxRenderer listboxRenderer)
	{
		final String contentCatalogName = (String) information.get(SITE_CONTENTCATALOG_NAME_QUALIFIER);
		if (StringUtils.isNotBlank(contentCatalogName))
		{
			final Textbox contentCatalogNameBox = new Textbox();
			contentCatalogNameBox.setReadonly(true);
			contentCatalogNameBox.setValue(contentCatalogName);
			if (UISessionUtils.getCurrentSession().isUsingTestIDs())
			{
				UITools.applyTestID(contentCatalogNameBox, COCKPIT_ID_CREATEWEBSITE_SUMMARY_CONTENTCATALOGS_INPUT);
			}
			contextInformation.appendChild(createSummaryEntryRow(Labels.getLabel("wizard.summarypage.cmssite.contentcatalog.name"),
					contentCatalogNameBox));
		}
		else
		{
			final Listbox selectedContentCatalogs = new Listbox();
			selectedContentCatalogs.setItemRenderer(listboxRenderer);
			selectedContentCatalogs.setModel(new SimpleListModel((List) information.get("selectedcontentcatalogs")));
			if (UISessionUtils.getCurrentSession().isUsingTestIDs())
			{
				UITools.applyTestID(selectedContentCatalogs, COCKPIT_ID_CREATEWEBSITE_SUMMARY_CONTENTCATALOGS_INPUT);
			}
			contextInformation.appendChild(createSummaryEntryRow(Labels.getLabel("wizard.summarypage.cmssite.contentcatalogs"),
					selectedContentCatalogs));
		}
	}

	protected void createThemeRow(final Map<String, Object> information, final Vbox contextInformation)
	{
		final SiteTheme themeName = (SiteTheme) information.get(SITE_THEME_QUALIFIER);
		final Textbox themeNameBox = new Textbox();
		themeNameBox.setReadonly(true);
		themeNameBox.setValue(themeName == null ? "" : themeName.getCode());
		if (UISessionUtils.getCurrentSession().isUsingTestIDs())
		{
			UITools.applyTestID(themeNameBox, COCKPIT_ID_CREATEWEBSITE_SUMMARY_THEME_INPUT);
		}
		contextInformation.appendChild(createSummaryEntryRow(
				Localization.getLocalizedString("wizard.summarypage.cmssite.theme.name"), themeNameBox));
	}

	@Override
	protected Map<String, Object> collectAllInformation()
	{
		final Map<String, Object> finalContextInformation = new HashMap<String, Object>();
		PropertyDescriptor desc = UISessionUtils.getCurrentSession().getTypeService().getPropertyDescriptor("CMSSite.name");
		finalContextInformation.put("siteName",
				getWizard().getObjectValueContainer().getValue(desc, UISessionUtils.getCurrentSession().getGlobalDataLanguageIso())
						.getLocalValue());
		desc = UISessionUtils.getCurrentSession().getTypeService().getPropertyDescriptor("CMSSite.active");
		finalContextInformation.put("active", getWizard().getObjectValueContainer().getValue(desc, null).getLocalValue());
		desc = UISessionUtils.getCurrentSession().getTypeService().getPropertyDescriptor("CMSSite.stores");
		finalContextInformation.put("stores", getWizard().getObjectValueContainer().getValue(desc, null).getLocalValue());
		desc = UISessionUtils.getCurrentSession().getTypeService().getPropertyDescriptor("CMSSite.theme");
		finalContextInformation.put("theme", getWizard().getObjectValueContainer().getValue(desc, null).getLocalValue());
		finalContextInformation.putAll(getWizard().getContext());
		return finalContextInformation;
	}

}
