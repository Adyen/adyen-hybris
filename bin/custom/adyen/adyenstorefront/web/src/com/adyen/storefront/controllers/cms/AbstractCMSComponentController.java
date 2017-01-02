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

import de.hybris.platform.acceleratorcms.data.RequestContextData;
import de.hybris.platform.acceleratorcms.services.CMSPageContextService;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import com.adyen.storefront.controllers.ControllerConstants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Abstract Controller for CMS Components
 */
public abstract class AbstractCMSComponentController<T extends AbstractCMSComponentModel> extends AbstractController
{
	protected static final Logger LOG = Logger.getLogger(AbstractCMSComponentController.class);

	protected static final String COMPONENT_UID = "componentUid";
	protected static final String COMPONENT = "component";

	@Resource(name = "cmsComponentService")
	private CMSComponentService cmsComponentService;

	@Resource(name = "cmsPageContextService")
	private CMSPageContextService cmsPageContextService;

	// Setter required for UnitTests
	public void setCmsComponentService(final CMSComponentService cmsComponentService)
	{
		this.cmsComponentService = cmsComponentService;
	}

	@RequestMapping
	public String handleGet(final HttpServletRequest request, final HttpServletResponse response, final Model model)
			throws Exception
	{

		T component = (T) request.getAttribute(COMPONENT);
		if (component != null)
		{
			// Add the component to the model
			model.addAttribute("component", component);

			// Allow subclasses to handle the component
			return handleComponent(request, response, model, component);
		}

		String componentUid = (String) request.getAttribute(COMPONENT_UID);
		if (StringUtils.isEmpty(componentUid))
		{
			componentUid = request.getParameter(COMPONENT_UID);
		}

		if (StringUtils.isEmpty(componentUid))
		{
			LOG.error("No component specified in [" + COMPONENT_UID + "]");
			throw new AbstractPageController.HttpNotFoundException();
		}

		try
		{
			component = getCmsComponentService().getAbstractCMSComponent(componentUid);
			if (component == null)
			{
				LOG.error("Component with UID [" + componentUid + "] is null");
				throw new AbstractPageController.HttpNotFoundException();
			}
			else
			{
				// Add the component to the model
				model.addAttribute("component", component);

				// Allow subclasses to handle the component
				return handleComponent(request, response, model, component);
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.error("Could not find component with UID [" + componentUid + "]");
			throw new AbstractPageController.HttpNotFoundException(e);
		}
	}

	protected String handleComponent(final HttpServletRequest request, final HttpServletResponse response, final Model model,
			final T component) throws Exception
	{
		fillModel(request, model, component);
		return getView(component);
	}

	protected abstract void fillModel(final HttpServletRequest request, final Model model, final T component);

	protected String getView(final T component)
	{
		// build a jsp response based on the component type
		return ControllerConstants.Views.Cms.ComponentPrefix + StringUtils.lowerCase(getTypeCode(component));
	}

	protected String getTypeCode(final T component)
	{
		return component.getItemtype();
	}

	protected CMSComponentService getCmsComponentService()
	{
		return cmsComponentService;
	}

	protected CMSPageContextService getCmsPageContextService()
	{
		return cmsPageContextService;
	}

	protected RequestContextData getRequestContextData(final HttpServletRequest request)
	{
		return getBean(request, "requestContextData", RequestContextData.class);
	}
}
