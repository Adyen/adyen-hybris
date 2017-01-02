/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.adyen.cockpits.productcockpit.editor;

import de.hybris.platform.basecommerce.model.externaltax.ProductTaxCodeModel;
import de.hybris.platform.cockpit.components.dialog.DescriptionTooltip;
import de.hybris.platform.cockpit.components.sectionpanel.Section;
import de.hybris.platform.cockpit.components.sectionpanel.SectionPanel;
import de.hybris.platform.cockpit.components.sectionpanel.SectionRenderer;
import de.hybris.platform.cockpit.model.editor.EditorListener;
import de.hybris.platform.cockpit.model.meta.ObjectType;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.model.referenceeditor.collection.CollectionUIEditor;
import de.hybris.platform.cockpit.services.SystemService;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.session.impl.CustomEditorSection;
import de.hybris.platform.cockpit.session.impl.DefaultEditorSectionPanelModel;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.externaltax.ProductTaxCodeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;


/**
 * Render for custom section with external tax product is configured by {@link ExternalTaxesSectionConfiguration}.
 * <p/>
 * @spring.bean externalTaxSectionRenderer
 */
public class ExternalTaxesSectionRenderer implements SectionRenderer
{
	private static final String EXTERNAL_TAXES_CNT_SCLASS = "externalTaxesEditor";
	private static final String EXTERNAL_TAXES_SECTION = "externalTaxesSection";
	private static final String EDITOR_SECTION_ROW_CNT_SCLASS = "externalTaxesRowComponent";
	private static final String EDITOR_LABEL_CNT_SCLASS = "editorrowlabelcontainer";


	private TypeService typeService;
	private SystemService systemService;
	private ProductTaxCodeService productTaxCodeService;

	public ProductTaxCodeService getProductTaxCodeService() {
		return productTaxCodeService;
	}

	public void setProductTaxCodeService(final ProductTaxCodeService productTaxCodeService) {
		this.productTaxCodeService = productTaxCodeService;
	}

	@Override
	public void render(final SectionPanel panel, final Component parent, final Component captionComponent, final Section section)
	{
		UITools.detachChildren(parent);

		if (panel.getModel() instanceof DefaultEditorSectionPanelModel)
		{
			final Div mainCnt = new Div();
			mainCnt.setSclass(EXTERNAL_TAXES_SECTION);
			mainCnt.setParent(parent);

			if (section instanceof CustomEditorSection)
			{
				final CustomEditorSection customSection = (CustomEditorSection) section;
				if (customSection.getSectionConfiguration() instanceof ExternalTaxesSectionConfiguration)
				{
					final ExternalTaxesSectionConfiguration secConfig = (ExternalTaxesSectionConfiguration) customSection
							.getSectionConfiguration();
					if (secConfig != null)
					{
						final HtmlBasedComponent rowDiv = new Div();
						rowDiv.setSclass(EDITOR_SECTION_ROW_CNT_SCLASS);
						rowDiv.setParent(mainCnt);

						final DescriptionTooltip descTooltip = new DescriptionTooltip("External taxes");

						final Hbox rowCnt = new Hbox();
						rowCnt.setParent(rowDiv);

						final String rWidths = descTooltip.isRender().booleanValue() ? "11em" + ",none,18px" : "11em" + ",none";
						rowCnt.setWidth("100%");
						rowCnt.setStyle("table-layout:fixed;");
						rowCnt.setWidths(rWidths);

						final Label rowLabel = new Label(Labels.getLabel("adyencockpits.product.externalTaxes"));
						rowLabel.setTooltiptext(Labels.getLabel("adyencockpits.product.externalTaxes"));

						final Div labelContainer = new Div();
						labelContainer.setSclass(EDITOR_LABEL_CNT_SCLASS);
						labelContainer.setParent(rowCnt);
						labelContainer.appendChild(rowLabel);

						final ObjectValueContainer objectValueContainer = ((DefaultEditorSectionPanelModel) panel.getModel())
									.getEditorArea().getCurrentObjectValues();
						if (objectValueContainer != null && objectValueContainer.getObject() instanceof ProductModel)
						{
							final ProductModel product = (ProductModel)objectValueContainer.getObject();
							rowCnt.appendChild(createExternalTaxesEditor(product));
						}
						
					}
				}
			}
		}
	}

	/**
	 * Creates a special editor with custom editor listener.</p>
	 */
	protected Div createExternalTaxesEditor( final ProductModel product)
	{
		final Div editorDiv = new Div();
		editorDiv.setSclass(EXTERNAL_TAXES_CNT_SCLASS);
		final EditorListener editorListener = new EditorListener()
		{
			@Override
			public void valueChanged(final Object value)
			{
				// NOP for current editor
			}

			@Override
			public void actionPerformed(final String actionCode)
			{
				// NOP for current editor
			}
		};

		final ObjectType objectType = getTypeService().getObjectType(ProductTaxCodeModel._TYPECODE);
		final CollectionUIEditor editor = new CollectionUIEditor(objectType);
		editor.setEditable(false);
		editorDiv.appendChild(editor.createViewComponent(getTaxCodeForProduct(product), Collections.<String, Object>emptyMap(), editorListener));		
		return editorDiv;
	}


	protected Collection<TypedObject> getTaxCodeForProduct(final ProductModel product){		
		final Collection<ProductTaxCodeModel> taxes = getProductTaxCodeService().getTaxCodesForProduct(product.getCode());
		final Collection<TypedObject> wrappedTaxes = new ArrayList<TypedObject>(); 
		for(ProductTaxCodeModel tax : taxes)
		{
			wrappedTaxes.add(getTypeService().wrapItem(tax));
		}
		
		return wrappedTaxes;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	public TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setSystemService(final SystemService systemService)
	{
		this.systemService = systemService;
	}

	protected SystemService getSystemService()
	{
		return this.systemService;
	}
	
}
