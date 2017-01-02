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

import de.hybris.platform.acceleratorcms.model.restrictions.CMSUiExperienceRestrictionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserRestrictionModel;
import de.hybris.platform.cmscockpit.components.liveedit.impl.DefaultPreviewLoader;

import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;


/**
 * This class extends PreviewLoader to set the restriction values in the PreviewDataModel based on the restrictions set
 * for the page
 */
public class DefaultAcceleratorPreviewLoader extends DefaultPreviewLoader
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultAcceleratorPreviewLoader.class);

	@Override
	protected boolean loadCommonRestrictionBaseValues(final PreviewDataModel previewCtx, final AbstractPageModel page)
	{
		boolean oneRestrictionApplied = false;
		previewCtx.setPreviewCatalog(null);
		previewCtx.setPreviewCategory(null);
		previewCtx.setPreviewProduct(null);
		if (!page.getRestrictions().isEmpty())
		{
			previewCtx.setUser(null);
			previewCtx.setUserGroup(null);
			previewCtx.setTime(new Date());
		}

		final Collection<AbstractRestrictionModel> restrictions = page.getRestrictions();
		if (restrictions != null && !restrictions.isEmpty())
		{
			for (final AbstractRestrictionModel restriction : restrictions)
			{
				if (oneRestrictionApplied && page.isOnlyOneRestrictionMustApply())
				{
					return oneRestrictionApplied;
				}

				if (restriction instanceof CMSTimeRestrictionModel)
				{
					loadTimeRestrictionBaseValues(previewCtx, (CMSTimeRestrictionModel) restriction);
					oneRestrictionApplied = true;
				}
				else if (restriction instanceof CMSUserRestrictionModel)
				{
					loadUserRestrictionBaseValues(previewCtx, (CMSUserRestrictionModel) restriction);
					oneRestrictionApplied = true;
				}
				else if (restriction instanceof CMSUserGroupRestrictionModel)
				{
					loadUserGroupRestrictionBaseValues(previewCtx, ((CMSUserGroupRestrictionModel) restriction));
					oneRestrictionApplied = true;
				}
				else if (restriction instanceof CMSUiExperienceRestrictionModel)
				{
					loadUIExperienceRestrictionForProductPage(previewCtx, ((CMSUiExperienceRestrictionModel) restriction));
					oneRestrictionApplied = true;
				}
			}
		}
		return oneRestrictionApplied && page.isOnlyOneRestrictionMustApply();

	}

	protected void loadUIExperienceRestrictionForProductPage(final PreviewDataModel previewCtx,
			final CMSUiExperienceRestrictionModel uiExperienceRestrictionModel)
	{
		previewCtx.setUiExperience(uiExperienceRestrictionModel.getUiExperience());
	}

}
