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
package com.adyen.storefront.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorfacades.payment.PaymentFacade;
import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutGroup;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.AddressValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.PaymentDetailsValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.verification.AddressVerificationResultHandler;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.TitleData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;


public abstract class AbstractCheckoutStepController extends AbstractCheckoutController implements CheckoutStepController
{
	protected static final String MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL = "multiStepCheckoutSummary";
	protected static final String REDIRECT_URL_ADD_DELIVERY_ADDRESS = REDIRECT_PREFIX + "/checkout/multi/delivery-address/add";
	protected static final String REDIRECT_URL_CHOOSE_DELIVERY_METHOD = REDIRECT_PREFIX + "/checkout/multi/delivery-method/choose";
	protected static final String REDIRECT_URL_ADD_PAYMENT_METHOD = REDIRECT_PREFIX + "/checkout/multi/payment-method/add";
	protected static final String REDIRECT_URL_SUMMARY = REDIRECT_PREFIX + "/checkout/multi/summary/view";
	protected static final String REDIRECT_URL_CART = REDIRECT_PREFIX + "/cart";
	protected static final String REDIRECT_URL_ERROR = REDIRECT_PREFIX + "/checkout/multi/hop/error";

	@Resource(name = "paymentDetailsValidator")
	private PaymentDetailsValidator paymentDetailsValidator;

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "multiStepCheckoutBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "paymentFacade")
	private PaymentFacade paymentFacade;

	@Resource(name = "addressValidator")
	private AddressValidator addressValidator;

	@Resource(name = "customerLocationService")
	private CustomerLocationService customerLocationService;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource(name = "addressVerificationResultHandler")
	private AddressVerificationResultHandler addressVerificationResultHandler;

	@Resource(name = "contentPageBreadcrumbBuilder")
	private ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder;

	@Resource(name = "checkoutFlowGroupMap")
	private Map<String, CheckoutGroup> checkoutFlowGroupMap;


	@ModelAttribute("titles")
	public Collection<TitleData> getTitles()
	{
		return getUserFacade().getTitles();
	}

	@ModelAttribute("countries")
	public Collection<CountryData> getCountries()
	{
		return getCheckoutFacade().getDeliveryCountries();
	}

	@ModelAttribute("countryDataMap")
	public Map<String, CountryData> getCountryDataMap()
	{
		final Map<String, CountryData> countryDataMap = new HashMap<String, CountryData>();
		for (final CountryData countryData : getCountries())
		{
			countryDataMap.put(countryData.getIsocode(), countryData);
		}
		return countryDataMap;
	}

	@ModelAttribute("checkoutSteps")
	public List<CheckoutSteps> addCheckoutStepsToModel()
	{
		final CheckoutGroup checkoutGroup = getCheckoutFlowGroupMap().get(getCheckoutFacade().getCheckoutFlowGroupForCheckout());
		final Map<String, CheckoutStep> progressBarMap = checkoutGroup.getCheckoutProgressBar();
		final List<CheckoutSteps> checkoutSteps = new ArrayList<CheckoutSteps>(progressBarMap.size());

		for (final Map.Entry<String, CheckoutStep> entry : progressBarMap.entrySet())
		{
			final CheckoutStep checkoutStep = entry.getValue();
			checkoutSteps.add(new CheckoutSteps(checkoutStep.getProgressBarId(), StringUtils.remove(checkoutStep.currentStep(),
					"redirect:"), Integer.valueOf(entry.getKey())));
		}
		return checkoutSteps;
	}

	protected void prepareDataForPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("isOmsEnabled", Boolean.valueOf(getSiteConfigService().getBoolean("oms.enabled", false)));
		model.addAttribute("supportedCountries", getCartFacade().getDeliveryCountries());
		model.addAttribute("expressCheckoutAllowed", Boolean.valueOf(getCheckoutFacade().isExpressCheckoutAllowedForCart()));
		model.addAttribute("taxEstimationEnabled", Boolean.valueOf(getCheckoutFacade().isTaxEstimationEnabledForCart()));
	}

	protected CheckoutStep getCheckoutStep(final String currentController)
	{
		final CheckoutGroup checkoutGroup = getCheckoutFlowGroupMap().get(getCheckoutFacade().getCheckoutFlowGroupForCheckout());
		return checkoutGroup.getCheckoutStepMap().get(currentController);
	}

	protected void setCheckoutStepLinksForModel(final Model model, final CheckoutStep checkoutStep)
	{
		model.addAttribute("previousStepUrl", StringUtils.remove(checkoutStep.previousStep(), "redirect:"));
		model.addAttribute("nextStepUrl", StringUtils.remove(checkoutStep.nextStep(), "redirect:"));
		model.addAttribute("currentStepUrl", StringUtils.remove(checkoutStep.currentStep(), "redirect:"));
		model.addAttribute("progressBarId", checkoutStep.getProgressBarId());
	}

	protected Map<String, String> getRequestParameterMap(final HttpServletRequest request)
	{
		final Map<String, String> map = new HashMap<String, String>();

		final Enumeration myEnum = request.getParameterNames();
		while (myEnum.hasMoreElements())
		{
			final String paramName = (String) myEnum.nextElement();
			final String paramValue = request.getParameter(paramName);
			map.put(paramName, paramValue);
		}

		return map;
	}

	public static class CheckoutSteps
	{
		private final String progressBarId;
		private final String url;
		private final Integer stepNumber;

		public CheckoutSteps(final String progressBarId, final String url, final Integer stepNumber)
		{
			this.progressBarId = progressBarId;
			this.url = url;
			this.stepNumber = stepNumber;
		}

		public String getProgressBarId()
		{
			return progressBarId;
		}

		public String getUrl()
		{
			return url;
		}

		public Integer getStepNumber()
		{
			return stepNumber;
		}
	}

	@Override
	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	protected PaymentDetailsValidator getPaymentDetailsValidator()
	{
		return paymentDetailsValidator;
	}

	protected ResourceBreadcrumbBuilder getResourceBreadcrumbBuilder()
	{
		return resourceBreadcrumbBuilder;
	}

	protected PaymentFacade getPaymentFacade()
	{
		return paymentFacade;
	}

	protected AddressValidator getAddressValidator()
	{
		return addressValidator;
	}

	protected CustomerLocationService getCustomerLocationService()
	{
		return customerLocationService;
	}

	protected AddressVerificationResultHandler getAddressVerificationResultHandler()
	{
		return addressVerificationResultHandler;
	}

	public ContentPageBreadcrumbBuilder getContentPageBreadcrumbBuilder()
	{
		return contentPageBreadcrumbBuilder;
	}

	public Map<String, CheckoutGroup> getCheckoutFlowGroupMap()
	{
		return checkoutFlowGroupMap;
	}

}
