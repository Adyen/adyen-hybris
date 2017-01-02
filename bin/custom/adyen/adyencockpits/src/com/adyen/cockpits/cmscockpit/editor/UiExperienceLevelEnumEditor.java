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
package com.adyen.cockpits.cmscockpit.editor;


import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;
import de.hybris.platform.cockpit.model.editor.EditorListener;
import de.hybris.platform.cockpit.model.editor.impl.DefaultEnumUIEditor;
import de.hybris.platform.core.Registry;
import de.hybris.platform.enumeration.EnumerationService;

import java.util.Map;

import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * Editor class for the UiExperienceLevelEnum  as the DefaultEnumUIEditor doesn't set the list of available
 * values for enum in combo box for live edit in cmscockpit
 */
public class UiExperienceLevelEnumEditor extends DefaultEnumUIEditor
{
	@Override
	public HtmlBasedComponent createViewComponent(final Object initialValue, final Map<String, ?> parameters, final EditorListener listener)
	{
		final EnumerationService enumService = (EnumerationService) Registry.getApplicationContext().getBean("enumerationService");

		setAvailableValues(enumService.getEnumerationValues(UiExperienceLevel._TYPECODE));
		return super.createViewComponent(initialValue, parameters, listener);
	}
}
