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
package com.adyen.cockpits.components.liveedit;

import java.util.Collection;
import java.util.Map;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmscockpit.components.liveedit.impl.LiveEditPopupEditDialog;

/**
 * Represents reference selector modal dialog - container for advanced search component.
 * <p/>
 * <b>Note:</b> <br/>
 * Represents a popup dialog within we display a proper editors for particular CMS component
 */
public class DefaultLiveEditPopupEditDialog extends LiveEditPopupEditDialog<DefaultLiveEditView>
{

	protected DefaultLiveEditPopupEditDialog(final Map<String, Object> currentAttributes,
			final Collection<CatalogVersionModel> catalogVersions,
			final DefaultLiveEditView liveEditView) throws InterruptedException {
		super(currentAttributes, catalogVersions, liveEditView);
	}
}
