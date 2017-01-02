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

import de.hybris.platform.acceleratorcms.model.components.ProductFeatureComponentModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import com.adyen.storefront.controllers.ControllerConstants;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for CMS ProductFeatureComponent
 */
@Controller("ProductFeatureComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.ProductFeatureComponent)
public class ProductFeatureComponentController extends AbstractCMSComponentController<ProductFeatureComponentModel>
{
	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
			ProductOption.SUMMARY);

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final ProductFeatureComponentModel component)
	{
		final ProductModel product = component.getProduct();
		if (product != null)
		{
			final ProductData productData = productFacade.getProductForOptions(product, PRODUCT_OPTIONS);
			model.addAttribute("product", productData);
		}
	}
}
