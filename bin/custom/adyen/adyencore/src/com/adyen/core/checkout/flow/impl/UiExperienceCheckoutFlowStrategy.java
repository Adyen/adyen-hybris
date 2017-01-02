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
package com.adyen.core.checkout.flow.impl;

import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorservices.checkout.flow.CheckoutFlowStrategy;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 */
public class UiExperienceCheckoutFlowStrategy extends AbstractCheckoutFlowStrategy
{
	private UiExperienceService uiExperienceService;
	private Map<UiExperienceLevel, CheckoutFlowStrategy> experienceMappings;

	protected UiExperienceService getUiExperienceService()
	{
		return uiExperienceService;
	}

	@Required
	public void setUiExperienceService(final UiExperienceService uiExperienceService)
	{
		this.uiExperienceService = uiExperienceService;
	}

	protected Map<UiExperienceLevel, CheckoutFlowStrategy> getExperienceMappings()
	{
		return experienceMappings;
	}

	@Required
	public void setExperienceMappings(final Map<UiExperienceLevel, CheckoutFlowStrategy> experienceMappings)
	{
		this.experienceMappings = experienceMappings;
	}

	protected boolean canSupport()
	{
		return getExperienceMappings().containsKey(getUiExperienceService().getUiExperienceLevel());
	}

	@Override
	public CheckoutFlowEnum getCheckoutFlow()
	{
		if (canSupport())
		{
			final CheckoutFlowStrategy checkoutFlowStrategy = getExperienceMappings().get(
					getUiExperienceService().getUiExperienceLevel());
			if (checkoutFlowStrategy != null)
			{
				return checkoutFlowStrategy.getCheckoutFlow();
			}
		}
		return getDefaultStrategy().getCheckoutFlow();
	}
}
