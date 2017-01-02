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
package com.adyen.storefront.controllers.cms;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import com.adyen.storefront.controllers.ControllerConstants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Default Controller for CMS Component.
 * This controller is used for all CMS components that don't have a specific controller
 * to handle them.
 */
@Controller("DefaultCMSComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.DefaultCMSComponent)
public class DefaultCMSComponentController extends AbstractCMSComponentController<AbstractCMSComponentModel>
{
	@Resource(name = "modelService")
	private ModelService modelService;

	// Setter required for UnitTests
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component)
	{
		// See documentation for CMSComponentService.getEditorProperties, but this will return all frontend
		// properties which we just inject into the model.
		for (final String property : getCmsComponentService().getEditorProperties(component))
		{
			try
			{
				final Object value = modelService.getAttributeValue(component, property);
				model.addAttribute(property, value);
			}
			catch(final AttributeNotSupportedException ignore)
			{
				// ignore
			}
		}
	}
}
