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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Required;
import de.hybris.platform.cockpit.components.sectionpanel.SectionRenderer;
import de.hybris.platform.cockpit.model.meta.ObjectType;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.config.EditorConfiguration;
import de.hybris.platform.cockpit.services.config.EditorSectionConfiguration;
import de.hybris.platform.cockpit.services.config.UpdateAwareCustomSectionConfiguration;
import de.hybris.platform.cockpit.services.config.impl.DefaultEditorSectionConfiguration;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;


/**
 * 
 * Represents custom section which show external taxes for Products.
 */
public class ExternalTaxesSectionConfiguration extends DefaultEditorSectionConfiguration implements
		UpdateAwareCustomSectionConfiguration
{
	private SectionRenderer sectionRenderer;

	@Override
	public void allInitialized(final EditorConfiguration config, final ObjectType type, final TypedObject object)
	{
		// NOP
	}

	@Override
	public List<EditorSectionConfiguration> getAdditionalSections()
	{
		// NOP
		return null;
	}

	@Override
	public SectionRenderer getCustomRenderer()
	{
		return this.sectionRenderer;
	}

	@Override
	public void initialize(final EditorConfiguration config, final ObjectType type, final TypedObject object)
	{

		// NOP
	}

	@Override
	public void loadValues(final EditorConfiguration config, final ObjectType type, final TypedObject object,
			final ObjectValueContainer objectValues)
	{
		// NOP

	}

	@Override
	public void saveValues(final EditorConfiguration config, final ObjectType type, final TypedObject object,
			final ObjectValueContainer objectValues)
	{
		// NOP

	}

	@Required
	public void setSectionRenderer(final SectionRenderer sectionRenderer)
	{
		this.sectionRenderer = sectionRenderer;
	}

	public SectionRenderer getSectionRenderer()
	{
		return this.sectionRenderer;
	}


	@Override
	public Set<PropertyDescriptor> getUpdateTriggerProperties()
	{
		final Set<PropertyDescriptor> ret = new HashSet<PropertyDescriptor>();
		return ret;
	}

	@Override
	public Set<ObjectType> getUpdateTriggerTypes()
	{
		return Collections.EMPTY_SET;
	}
}
