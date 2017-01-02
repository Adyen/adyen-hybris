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

import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PlaceOrderForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.storefront.controllers.ControllerConstants;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adyen.facades.order.data.AdyenPaymentInfoData;
import com.adyen.services.AdyenPaymentService;
import com.adyen.services.integration.AdyenService;
import com.adyen.storefront.facades.impl.AdyenExtCheckoutFacade;
import com.adyen.storefront.forms.HPPDataForm;


@Controller
@RequestMapping(value = "/checkout/multi/summary")
public class SummaryCheckoutStepController extends AbstractCheckoutStepController
{
	private final static String SUMMARY = "summary";

	@Resource(name = "checkoutFlowFacade")
	private AdyenExtCheckoutFacade checkoutFacade;

	@Resource(name = "adyenService")
	private AdyenService adyenService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "velocityEngine")
	private VelocityEngine velocityEngine;

	@Resource(name = "paymentService")
	private AdyenPaymentService adyenPaymentService;


	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	@PreValidateCheckoutStep(checkoutStep = SUMMARY)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException,
			CommerceCartModificationException
	{
		final CartData cartData = checkoutFacade.getCheckoutCart();
		if (cartData.getEntries() != null && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				final String productCode = entry.getProduct().getCode();
				final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode,
						Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));
				entry.setProduct(product);
			}
		}

		model.addAttribute("cartData", cartData);
		model.addAttribute("allItems", cartData.getEntries());
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		model.addAttribute("deliveryMode", cartData.getDeliveryMode());
		model.addAttribute("paymentInfo", cartData.getPaymentInfo());

		model.addAttribute("boletoUrl", checkoutFacade.getBoletoUrl());

		if (((AdyenPaymentInfoData) cartData.getPaymentInfo()).isUseHPP())
		{
			final HPPDataForm hppForm = getHPPFormData();
			model.addAttribute("hppFormData", hppForm);
		}

		// Only request the security code if the SubscriptionPciOption is set to Default.
		final boolean requestSecurityCode = (CheckoutPciOptionEnum.DEFAULT.equals(getCheckoutFlowFacade()
				.getSubscriptionPciOption()));
		model.addAttribute("requestSecurityCode", Boolean.valueOf(requestSecurityCode));

		model.addAttribute(new PlaceOrderForm());

		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		return ControllerConstants.Views.Pages.MultiStepCheckout.CheckoutSummaryPage;
	}


	@RequestMapping(value = "/placeOrder")
	@RequireHardLogIn
	public String placeOrder(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException,
			InvalidCartException, CommerceCartModificationException
	{
		if (validateOrderForm(placeOrderForm, model))
		{
			return enterStep(model, redirectModel);
		}

		//Validate the cart
		if (validateCart(redirectModel))
		{
			// Invalid cart. Bounce back to the cart page.
			return REDIRECT_PREFIX + "/cart";
		}

		// authorize, if failure occurs don't allow to place the order
		final boolean isPaymentUthorized = checkoutFacade.isPaymentUthorized();


		if (!isPaymentUthorized)
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.authorization.failed");
			return enterStep(model, redirectModel);
		}

		final OrderData orderData;
		try
		{
			orderData = getCheckoutFacade().placeOrder();
		}
		catch (final Exception e)
		{
			LOG.error("Failed to place Order", e);
			GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
			return enterStep(model, redirectModel);
		}

		return redirectToOrderConfirmationPage(orderData);
	}

	/**
	 * Validates the order form before to filter out invalid order states
	 * 
	 * @param placeOrderForm
	 *           The spring form of the order being submitted
	 * @param model
	 *           A spring Model
	 * @return True if the order form is invalid and false if everything is valid.
	 */
	protected boolean validateOrderForm(final PlaceOrderForm placeOrderForm, final Model model)
	{
		//final String securityCode = placeOrderForm.getSecurityCode();
		boolean invalid = false;

		if (getCheckoutFlowFacade().hasNoDeliveryAddress())
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryAddress.notSelected");
			invalid = true;
		}

		if (getCheckoutFlowFacade().hasNoDeliveryMode())
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryMethod.notSelected");
			invalid = true;
		}

		if (getCheckoutFlowFacade().hasNoPaymentInfo())
		{
			GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.notSelected");
			invalid = true;
		}
		//		else
		//		{
		//			// Only require the Security Code to be entered on the summary page if the SubscriptionPciOption is set to Default.
		//			if (CheckoutPciOptionEnum.DEFAULT.equals(getCheckoutFlowFacade().getSubscriptionPciOption())
		//					&& StringUtils.isBlank(securityCode))
		//			{
		//				GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
		//				invalid = true;
		//			}
		//		}

		if (!placeOrderForm.isTermsCheck())
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.terms.not.accepted");
			invalid = true;
			return invalid;
		}
		final CartData cartData = checkoutFacade.getCheckoutCart();

		if (!checkoutFacade.containsTaxValues())
		{
			LOG.error(String
					.format(
							"Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue",
							cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.tax.missing");
			invalid = true;
		}

		if (!cartData.isCalculated())
		{
			LOG.error(String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.cart.notcalculated");
			invalid = true;
		}

		return invalid;
	}

	public HPPDataForm getHPPFormData()
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		final HPPDataForm hppDataForm = new HPPDataForm();
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		final Date sessionDate = calendar.getTime(); // current date + 1 day
		final String sessionValidity = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(sessionDate);
		final LinkedHashMap<String, String> merchantData = new LinkedHashMap<String, String>();
		merchantData.put("paymentAmount",
				new Integer(new BigDecimal(100).multiply(cartData.getTotalPrice().getValue()).intValue()).toString());
		merchantData.put("currencyCode", cartData.getTotalPrice().getCurrencyIso());
		merchantData.put("shipBeforeDate", sessionValidity);
		merchantData.put("merchantReference", cartData.getCode());
		merchantData.put("skinCode", getCmsSiteService().getCurrentSite().getAdyenSkinCode());
		merchantData.put("merchantAccount", getCmsSiteService().getCurrentSite().getAdyenMerchantAccount());
		merchantData.put("sessionValidity", sessionValidity);
		merchantData.put("shopperReference", "");
		merchantData.put("recurringContract", "");
		merchantData.put("offset", "");
		merchantData.put("brandCode", "");
		merchantData.put("issuerId", "");

		final String merchantSig = adyenService.calculateHMAC(getCmsSiteService().getCurrentSite().getAdyenHmacKey(),
				getSigningString(merchantData));
		merchantData.put("merchantSig", merchantSig);
		/* ADY-115 start */
		merchantData.put("countryCode", adyenService.getCountryCode());
		/* ADY-115 end */
		final LinkedHashMap<String, String> shopperData = new LinkedHashMap<String, String>();
		shopperData.put("shopperFirstName", getUser().getFirstName());
		shopperData.put("shopperLastName", getUser().getLastName());

		final LinkedHashMap<String, String> formData = new LinkedHashMap<String, String>();
		formData.putAll(merchantData);
		formData.putAll(shopperData);
		
		/* ADY-125 start */
		final LinkedHashMap<String, String> openInvoiceData = adyenPaymentService.buildHPPOpenInvoiceData();
		final String openInvoiceDataSig = adyenService.buildOpenInvoiceDataSig(merchantSig, openInvoiceData, getCmsSiteService()
				.getCurrentSite().getAdyenHmacKey());
		formData.putAll(openInvoiceData);
		formData.put("openinvoicedata.sig", openInvoiceDataSig);
		/* ADY-125 end */

		final String siteUrl = configurationService.getConfiguration().getString(
				"website." + getCmsSiteService().getCurrentSite().getUid() + ".https");
		formData.put(
				"resURL",
				siteUrl
						+ configurationService.getConfiguration().getString("authorise.HPP.payment.return.url",
								"/checkout/multi/summary/authorise-hpp-payment-adyen-response"));
		formData.put("orderData", renderCartData(cartData));
		hppDataForm.setHppDataMap(formData);
		return hppDataForm;
	}

	private String renderCartData(final CartData cartData)
	{
		final String templatePath = "/adyencore/import/template/orderDetails.vm";
		final HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("order", cartData);
		final String details = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", context);
		if (StringUtils.isEmpty(details))
		{
			return "";
		}
		return adyenService.compressString(details);
	}

	@RequestMapping(value = "/authorise-hpp-payment-adyen-response", method = RequestMethod.GET)
	@RequireHardLogIn
	public String authoriseHPPPaymentResponse(@RequestParam final String merchantReference,
			@RequestParam(required = false) final String pspReference, @RequestParam final String authResult, final Model model,
			final RedirectAttributes redirectModel, final HttpServletRequest request)
	{
		try
		{
			final StringBuffer responseString = new StringBuffer("handling hpp authorise response by hybris hosted URL:\n");
			final Enumeration paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements())
			{
				final String paramName = (String) paramNames.nextElement();

				final String[] paramValues = request.getParameterValues(paramName);
				if (paramValues.length == 1)
				{
					final String paramValue = paramValues[0];
					if (paramValue.length() != 0)
					{
						responseString.append(paramName + "=" + paramValue + "\n");
					}
				}
			}
			LOG.info(responseString.toString());
			if ("AUTHORISED".equals(authResult) || "PENDING".equals(authResult))
			{

				if (null != checkoutFacade.getCartModel() && checkoutFacade.getCartModel().getEntries().size() > 0)
				{
					adyenPaymentService.createHPPAuthorisePTE(merchantReference, pspReference, authResult);
					LOG.info("HPP Response shows payment success, place order here.");
					return placeOrderInternal(model, redirectModel);
				}
				else
				{
					//Try to get order using pspReference
					LOG.info("Cart doesn't not exist anymore, try find order using PSP reference.");
					final AbstractOrderModel order = adyenPaymentService.getOrderByPSPReference(pspReference);
					if (null != order && order instanceof OrderModel)
					{
						adyenPaymentService.createHPPAuthorisePTE(order, pspReference, authResult);
						return REDIRECT_URL_ORDER_CONFIRMATION
								+ (getCheckoutCustomerStrategy().isAnonymousCheckout() ? order.getGuid() : order.getCode());
					}
				}
			}
			else if ("CANCELLED".equals(authResult))
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"checkout.multi.hpp.authorise.cancelled");
				return REDIRECT_URL_ADD_PAYMENT_METHOD;
			}
			else
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"checkout.multi.3d.authorise.refused");
				return REDIRECT_URL_ADD_PAYMENT_METHOD;
			}
		}
		catch (final Exception e)
		{
			LOG.error("Failed to place Order", e);
			GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
		}
		return REDIRECT_URL_SUMMARY;
	}

	private String getSigningString(final LinkedHashMap map)
	{
		final StringBuffer buffer = new StringBuffer();
		for (final Iterator it = map.keySet().iterator(); it.hasNext();)
		{
			final Object key = it.next();
			if (StringUtils.isNotEmpty(map.get(key) + ""))
			{
				buffer.append(map.get(key));
			}
		}
		return buffer.toString();
	}

	/**
	 * @param model
	 * @param redirectModel
	 * @return
	 * @throws CMSItemNotFoundException
	 * @throws CommerceCartModificationException
	 */
	private String placeOrderInternal(final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException,
			CommerceCartModificationException
	{
		final OrderData orderData;
		try
		{
			orderData = getCheckoutFacade().placeOrder();
		}
		catch (final Exception e)
		{
			LOG.error("Failed to place Order", e);
			GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
			return enterStep(model, redirectModel);
		}

		return redirectToOrderConfirmationPage(orderData);
	}

	@RequestMapping(value = "/back", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().previousStep();
	}

	@RequestMapping(value = "/next", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(SUMMARY);
	}


}
