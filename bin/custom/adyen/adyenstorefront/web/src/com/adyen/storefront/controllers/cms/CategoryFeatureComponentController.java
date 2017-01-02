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
package com.adyen.storefront.controllers.cms;

import de.hybris.platform.acceleratorcms.model.components.CategoryFeatureComponentModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import com.adyen.storefront.controllers.ControllerConstants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for CMS ProductFeatureComponent.
 */
@Controller("CategoryFeatureComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.CategoryFeatureComponent)
public class CategoryFeatureComponentController extends AbstractCMSComponentController<CategoryFeatureComponentModel>
{
	@Resource(name = "categoryUrlConverter")
	private Converter<CategoryModel, CategoryData> categoryUrlConverter;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final CategoryFeatureComponentModel component)
	{
		final CategoryModel category = component.getCategory();
		if (category != null)
		{
			final String url = categoryUrlConverter.convert(category).getUrl();
			model.addAttribute("url", url);
		}
	}
}
