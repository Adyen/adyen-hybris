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
import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.SopPaymentDetailsForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.storefront.controllers.ControllerConstants;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adyen.facades.order.data.AdyenPaymentInfoData;
import com.adyen.facades.order.data.BoletoPaymentData;
import com.adyen.services.AdyenPaymentService;
import com.adyen.services.enums.BoletoBrand;
import com.adyen.services.integration.AdyenService;
import com.adyen.services.integration.data.PaymentMethods;
import com.adyen.services.integration.data.ResultCode;
import com.adyen.services.integration.data.response.AdyenListRecurringDetailsResponse;
import com.adyen.services.integration.data.response.AdyenPaymentResponse;
import com.adyen.services.integration.exception.AdyenIs3DSecurityPaymentException;
import com.adyen.storefront.facades.impl.AdyenExtCheckoutFacade;
import com.adyen.storefront.forms.AdyenPaymentDetailsForm;
import com.adyen.storefront.forms.validation.AdyenPaymentDetailsValidator;


@Controller
@RequestMapping(value = "/checkout/multi/payment-method")
public class PaymentMethodCheckoutStepController extends AbstractCheckoutStepController
{
	protected static final Map<String, String> cybersourceSopCardTypes = new HashMap<String, String>();
	private final static String PAYMENT_METHOD = "payment-method";
	private static final String REDIRECT_URL_3D_SECURE_PAYMENT_VALIDATION = REDIRECT_PREFIX
			+ "/checkout/multi/payment-method/3d-secure-payment-validation";
	private static final String AUTHORISE_3D_SECURE_PAYMENT_URL = "/checkout/multi/payment-method/authorise-3d-secure-payment-adyen-response";


	@Resource(name = "checkoutFlowFacade")
	private AdyenExtCheckoutFacade checkoutFacade;

	@Resource(name = "adyenService")
	private AdyenService adyenService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "paymentService")
	private AdyenPaymentService adyenPaymentService;

	@Resource(name = "adyenPaymentDetailsValidator")
	private AdyenPaymentDetailsValidator adyenPaymentDetailsValidator;

	@ModelAttribute("billingCountries")
	public Collection<CountryData> getBillingCountries()
	{
		return getCheckoutFacade().getBillingCountries();
	}

	@ModelAttribute("cardTypes")
	public Collection<CardTypeData> getCardTypes()
	{
		return getCheckoutFacade().getSupportedCardTypes();
	}

	@ModelAttribute("months")
	public List<SelectOption> getMonths()
	{
		final List<SelectOption> months = new ArrayList<SelectOption>();

		months.add(new SelectOption("01", "01"));
		months.add(new SelectOption("02", "02"));
		months.add(new SelectOption("03", "03"));
		months.add(new SelectOption("04", "04"));
		months.add(new SelectOption("05", "05"));
		months.add(new SelectOption("06", "06"));
		months.add(new SelectOption("07", "07"));
		months.add(new SelectOption("08", "08"));
		months.add(new SelectOption("09", "09"));
		months.add(new SelectOption("10", "10"));
		months.add(new SelectOption("11", "11"));
		months.add(new SelectOption("12", "12"));

		return months;
	}

	@ModelAttribute("startYears")
	public List<SelectOption> getStartYears()
	{
		final List<SelectOption> startYears = new ArrayList<SelectOption>();
		final Calendar calender = new GregorianCalendar();

		for (int i = calender.get(Calendar.YEAR); i > (calender.get(Calendar.YEAR) - 6); i--)
		{
			startYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
		}

		return startYears;
	}

	@ModelAttribute("expiryYears")
	public List<SelectOption> getExpiryYears()
	{
		final List<SelectOption> expiryYears = new ArrayList<SelectOption>();
		final Calendar calender = new GregorianCalendar();

		for (int i = calender.get(Calendar.YEAR); i < (calender.get(Calendar.YEAR) + 11); i++)
		{
			expiryYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
		}

		return expiryYears;
	}

	@Override
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = PAYMENT_METHOD)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		//ADY-10 ADY-12 ADY-14 ADY-74 ,Flow condition branch.
		maybeEnableAdyenAPIIntegration(model);
		setupAddPaymentPage(model);

		// Use the checkout PCI strategy for getting the URL for creating new subscriptions.
		final CheckoutPciOptionEnum subscriptionPciOption = getCheckoutFlowFacade().getSubscriptionPciOption();
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		if (CheckoutPciOptionEnum.HOP.equals(subscriptionPciOption))
		{
			// Redirect the customer to the HOP page or show error message if it fails (e.g. no HOP configurations).
			try
			{
				final PaymentData hostedOrderPageData = getPaymentFacade().beginHopCreateSubscription("/checkout/multi/hop/response",
						"/integration/merchant_callback");
				model.addAttribute("hostedOrderPageData", hostedOrderPageData);

				final boolean hopDebugMode = getSiteConfigService().getBoolean(PaymentConstants.PaymentProperties.HOP_DEBUG_MODE,
						false);
				model.addAttribute("hopDebugMode", Boolean.valueOf(hopDebugMode));

				return ControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
			}
			catch (final Exception e)
			{
				LOG.error("Failed to build beginCreateSubscription request", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
			}
		}
		else if (CheckoutPciOptionEnum.SOP.equals(subscriptionPciOption))
		{
			// Build up the SOP form data and render page containing form
			final SopPaymentDetailsForm sopPaymentDetailsForm = new SopPaymentDetailsForm();
			try
			{
				setupSilentOrderPostPage(sopPaymentDetailsForm, model);
				return ControllerConstants.Views.Pages.MultiStepCheckout.SilentOrderPostPage;
			}
			catch (final Exception e)
			{
				LOG.error("Failed to build beginCreateSubscription request", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
				model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
			}
		}

		// If not using HOP or SOP we need to build up the payment details form
		final AdyenPaymentDetailsForm adyenPaymentDetailsForm = new AdyenPaymentDetailsForm();
		final AddressForm addressForm = new AddressForm();
		adyenPaymentDetailsForm.setBillingAddress(addressForm);
		model.addAttribute(adyenPaymentDetailsForm);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);
		maybeEnableAdyenHPPIntegration(model);
		return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
	}

	private void maybeEnableAdyenHPPIntegration(final Model model)
	{
		if (getCmsSiteService().getCurrentSite().isAdyenUseHPP())
		{
			PaymentMethods hppPaymentMethods = new PaymentMethods();
			if (getCmsSiteService().getCurrentSite().isAdyenUseDirectoryLookup())
			{
				hppPaymentMethods = getHPPPaymentMethods();
			}

			model.addAttribute("hppPaymentMethods", hppPaymentMethods.getPaymentMethods());
			model.addAttribute("hppEnabled", Boolean.TRUE);
		}
	}

	private PaymentMethods getHPPPaymentMethods()
	{
		final PaymentMethods paymentMethods = adyenService.directory();
		return (paymentMethods == null) ? new PaymentMethods() : paymentMethods;
	}

	private String getHPPUrl(final boolean withBrandCode)
	{
		if (withBrandCode)
		{
			return configurationService.getConfiguration().getString("integration.adyen.hpp.details.url");
		}
		return configurationService.getConfiguration().getString("integration.adyen.hpp.pay.url");
	}

	@RequestMapping(value =
	{ "/add" }, method = RequestMethod.POST)
	@RequireHardLogIn
	public String add(final Model model, @Valid final AdyenPaymentDetailsForm adyenPaymentDetailsForm,
			final BindingResult bindingResult, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		adyenPaymentDetailsValidator.validate(adyenPaymentDetailsForm, bindingResult);
		setupAddPaymentPage(model);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);

		if (bindingResult.hasErrors())
		{
			maybeEnableAdyenAPIIntegration(model);
			maybeEnableAdyenHPPIntegration(model);
			GlobalMessages.addErrorMessage(model, "checkout.error.paymentethod.formentry.invalid");
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
		}
		final AdyenPaymentInfoData paymentInfoData = new AdyenPaymentInfoData();
		paymentInfoData.setId(adyenPaymentDetailsForm.getPaymentId());
		paymentInfoData.setShopperIp(request.getRemoteAddr());

		if (Boolean.TRUE.equals(adyenPaymentDetailsForm.getUseHPP()))
		{
			paymentInfoData.setUseHPP(Boolean.TRUE.booleanValue());
			paymentInfoData.setAdyenPaymentBrand(adyenPaymentDetailsForm.getAdyenPaymentBrand());
			paymentInfoData.setHppURL(getHPPUrl(StringUtils.isNotEmpty(adyenPaymentDetailsForm.getAdyenPaymentBrand())));
			paymentInfoData.setIssuerId(adyenPaymentDetailsForm.getIssuerId());
		}
		else
		{
			if (Boolean.TRUE.equals(adyenPaymentDetailsForm.getUseBoleto()))
			{
				paymentInfoData.setUseBoleto(true);
				final BoletoPaymentData boletoPaymentInfo = new BoletoPaymentData();
				boletoPaymentInfo.setFirstName(adyenPaymentDetailsForm.getFirstName());
				boletoPaymentInfo.setLastName(adyenPaymentDetailsForm.getLastName());
				boletoPaymentInfo.setSelectedBrand(adyenPaymentDetailsForm.getSelectedBrand());
				boletoPaymentInfo.setShopperStatement(adyenPaymentDetailsForm.getShopperStatement());
				boletoPaymentInfo.setSocialSecurityNumber(adyenPaymentDetailsForm.getSocialSecurityNumber());
				paymentInfoData.setBoletoPaymentInfo(boletoPaymentInfo);
			}
			else
			{

				if (Boolean.TRUE.equals(adyenPaymentDetailsForm.getUseSavedPayment()))
				{
					paymentInfoData.setRecurringDetailReference(adyenPaymentDetailsForm.getSavedPaymentMethodId());
					paymentInfoData.setExpiryMonth(adyenPaymentDetailsForm.getSavePaymentMethodExpiryMonth());
					paymentInfoData.setExpiryYear(adyenPaymentDetailsForm.getSavePaymentMethodExpiryYear());
					paymentInfoData.setCardType(adyenPaymentDetailsForm.getSavedPaymentMethodType());
					paymentInfoData.setCardNumber(adyenPaymentDetailsForm.getSavedPaymentMethodCardNumber());
					paymentInfoData.setAccountHolderName(adyenPaymentDetailsForm.getSavedPaymentMethodOwner());
					checkoutFacade.storeCVC(adyenPaymentDetailsForm.getSavedPaymentMethodCVC());
				}
				else
				{
					//ADY-18
					paymentInfoData.setCardEncryptedJson(request.getParameter("adyen-encrypted-data"));
					paymentInfoData.setSavePayment(adyenPaymentDetailsForm.isSavePayment());
					paymentInfoData.setCardType(adyenPaymentDetailsForm.getCardTypeCode());
					paymentInfoData.setAccountHolderName(adyenPaymentDetailsForm.getNameOnCard());
					paymentInfoData.setCardNumber(adyenPaymentDetailsForm.getCardNumber());
					paymentInfoData.setStartMonth(adyenPaymentDetailsForm.getStartMonth());
					paymentInfoData.setStartYear(adyenPaymentDetailsForm.getStartYear());
					paymentInfoData.setExpiryMonth(adyenPaymentDetailsForm.getExpiryMonth());
					paymentInfoData.setExpiryYear(adyenPaymentDetailsForm.getExpiryYear());
				}
			}
			maybeSetInstallments(paymentInfoData, adyenPaymentDetailsForm.getInstallments());
		}

		if (Boolean.TRUE.equals(adyenPaymentDetailsForm.getSaveInAccount()) || getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			paymentInfoData.setSaved(true);
		}
		paymentInfoData.setIssueNumber(adyenPaymentDetailsForm.getIssueNumber());

		final AddressData addressData;
		if (Boolean.FALSE.equals(adyenPaymentDetailsForm.getNewBillingAddress()))
		{
			addressData = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
			if (addressData == null)
			{
				GlobalMessages.addErrorMessage(model,
						"checkout.multi.paymentMethod.createSubscription.billingAddress.noneSelectedMsg");
				maybeEnableAdyenAPIIntegration(model);
				maybeEnableAdyenHPPIntegration(model);
				return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
			}

			addressData.setBillingAddress(true); // mark this as billing address
		}
		else
		{
			final AddressForm addressForm = adyenPaymentDetailsForm.getBillingAddress();
			addressData = new AddressData();
			if (addressForm != null)
			{
				addressData.setId(addressForm.getAddressId());
				addressData.setTitleCode(addressForm.getTitleCode());
				addressData.setFirstName(addressForm.getFirstName());
				addressData.setLastName(addressForm.getLastName());
				addressData.setLine1(addressForm.getLine1());
				addressData.setLine2(addressForm.getLine2());
				addressData.setTown(addressForm.getTownCity());
				addressData.setPostalCode(addressForm.getPostcode());
				addressData.setCountry(getI18NFacade().getCountryForIsocode(addressForm.getCountryIso()));
				if (addressForm.getRegionIso() != null)
				{
					addressData.setRegion(getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso()));
				}
				addressData.setShippingAddress(Boolean.TRUE.equals(addressForm.getShippingAddress()));
				addressData.setBillingAddress(Boolean.TRUE.equals(addressForm.getBillingAddress()));
			}
		}

		getAddressVerificationFacade().verifyAddressData(addressData);
		paymentInfoData.setBillingAddress(addressData);

		final AdyenPaymentInfoData newPaymentSubscription = checkoutFacade.createAdyenPaymentSubscription(paymentInfoData);
		if (newPaymentSubscription != null && StringUtils.isNotBlank(newPaymentSubscription.getSubscriptionId()))
		{
			if (Boolean.TRUE.equals(adyenPaymentDetailsForm.getSaveInAccount())
					&& getUserFacade().getCCPaymentInfos(true).size() <= 1)
			{
				getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
			}
			checkoutFacade.setPaymentDetails(newPaymentSubscription.getId());
		}
		else
		{
			GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.createSubscription.failedMsg");
			maybeEnableAdyenAPIIntegration(model);
			maybeEnableAdyenHPPIntegration(model);
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
		}

		model.addAttribute("paymentId", newPaymentSubscription.getId());

		if (newPaymentSubscription.isUseHPP())
		{
			adyenPaymentService.clearPaymentTransaction(cartData.getCode());
			adyenPaymentService.createTransaction(cartData.getCode());
			return REDIRECT_URL_SUMMARY;
		}

		// authorize, if failure occurs don't allow to place the order
		checkoutFacade.getSessionService().setAttribute("userAgent", request.getHeader("User-Agent"));
		checkoutFacade.getSessionService().setAttribute("accept", request.getHeader("Accept"));
		try
		{
			final AdyenPaymentTransactionEntryModel entry = (AdyenPaymentTransactionEntryModel) checkoutFacade
					.authorizeAdyenPayment(checkoutFacade.getSessionCVC());
			if (entry == null
					|| (!ResultCode.Authorised.name().equals(entry.getTransactionStatus()) && !ResultCode.Received.name().equals(
							entry.getTransactionStatus())))
			{
				if (entry != null && StringUtils.isNotBlank(entry.getAdyenMessage()))
				{
					GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"checkout.multi.authorise.refusedWithreason", new Object[]
							{ entry.getAdyenMessage() });
				}
				else
				{
					GlobalMessages.addErrorMessage(model, "checkout.multi.authorise.refused");
				}
				maybeEnableAdyenAPIIntegration(model);
				maybeEnableAdyenHPPIntegration(model);
				return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
			}
		}
		catch (final AdapterException ae)
		{
			// handle a case where a wrong paymentProvider configurations on the store see getCommerceCheckoutService().getPaymentProvider()
			LOG.error(ae.getMessage(), ae);
		}
		catch (final AdyenIs3DSecurityPaymentException e)
		{
			//redirect to 3d security url
			final AdyenPaymentResponse response = e.getPaymentResponse();
			return validate3DSecurePayment(response.getPaRequest(), response.getMd(), response.getIssuerUrl(), model, request);
		}
		finally
		{
			checkoutFacade.clearCVC();
		}

		model.addAttribute("paymentId", newPaymentSubscription.getId());
		setCheckoutStepLinksForModel(model, getCheckoutStep());

		return getCheckoutStep().nextStep();
	}


	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	@RequireHardLogIn
	public String remove(@RequestParam(value = "paymentInfoId") final String paymentMethodId,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		getUserFacade().unlinkCCPaymentInfo(paymentMethodId);
		GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
				"text.account.profile.paymentCart.removed");
		return getCheckoutStep().currentStep();
	}

	@RequestMapping(value = "/3d-secure-payment-validation", method = RequestMethod.GET)
	@RequireHardLogIn
	public String validate3DSecurePayment(@RequestParam("paReq") final String paReq, @RequestParam("md") final String md,
			@RequestParam("issuerUrl") final String issuerUrl, final Model model, final HttpServletRequest request)
	{
		model.addAttribute("paReq", paReq);
		model.addAttribute("md", md);
		final String siteUrl = configurationService.getConfiguration().getString(
				"website." + getCmsSiteService().getCurrentSite().getUid() + "." + request.getScheme());
		model.addAttribute("termUrl", siteUrl + AUTHORISE_3D_SECURE_PAYMENT_URL);
		model.addAttribute("issuerUrl", issuerUrl);
		return ControllerConstants.Views.Pages.MultiStepCheckout.Validate3DSecurePaymentPage;
	}

	@RequestMapping(value = "/authorise-3d-secure-payment-adyen-response", method = RequestMethod.POST)
	@RequireHardLogIn
	public String authorise3DSecurePayment(@RequestParam("PaRes") final String paRes, @RequestParam("MD") final String md,
			final Model model, final RedirectAttributes redirectModel, final HttpServletRequest request)
			throws CMSItemNotFoundException, CommerceCartModificationException, UnknownHostException
	{
		try
		{
			adyenPaymentService.maybeClearAuthorizeHistory(checkoutFacade.getCartModel());
			final AdyenPaymentTransactionEntryModel paymentTransactionEntry = (AdyenPaymentTransactionEntryModel) adyenPaymentService
					.authorize3DSecure(checkoutFacade.getCartModel(), paRes, md, request.getRemoteAddr(),
							request.getHeader("User-Agent"), request.getHeader("Accept"));
			if (isPaymentFail(paymentTransactionEntry))
			{
				if (paymentTransactionEntry != null && StringUtils.isNotBlank(paymentTransactionEntry.getAdyenMessage()))
				{
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"checkout.multi.3d.authorise.refusedWithreason", new Object[]
							{ paymentTransactionEntry.getAdyenMessage() });
				}
				else
				{
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"checkout.multi.3d.authorise.refused");
				}
				return REDIRECT_URL_ADD_PAYMENT_METHOD;
			}
		}
		catch (final Exception e)
		{
			LOG.error("Failed to place Order", e);
			GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
		}
		return getCheckoutStep().nextStep();
	}

	/**
	 * @param paymentTransactionEntry
	 * @return
	 */
	public boolean isPaymentFail(final PaymentTransactionEntryModel paymentTransactionEntry)
	{
		return (paymentTransactionEntry == null)
				|| !((ResultCode.Authorised.name().equals(paymentTransactionEntry.getTransactionStatus())) || (ResultCode.Received
						.name().equals(paymentTransactionEntry.getTransactionStatus())));
	}

	/**
	 * This method gets called when the "Use These Payment Details" button is clicked. It sets the selected payment
	 * method on the checkout facade and reloads the page highlighting the selected payment method.
	 * 
	 * @param selectedPaymentMethodId
	 *           - the id of the payment method to use.
	 * @return - a URL to the page to load.
	 */
	@RequestMapping(value = "/choose", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doSelectPaymentMethod(@RequestParam("selectedPaymentMethodId") final String selectedPaymentMethodId)
	{
		if (StringUtils.isNotBlank(selectedPaymentMethodId))
		{
			getCheckoutFacade().setPaymentDetails(selectedPaymentMethodId);
		}
		return getCheckoutStep().nextStep();
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

	protected CardTypeData createCardTypeData(final String code, final String name)
	{
		final CardTypeData cardTypeData = new CardTypeData();
		cardTypeData.setCode(code);
		cardTypeData.setName(name);
		return cardTypeData;
	}

	protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
		prepareDataForPage(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
		final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}

	protected void setupSilentOrderPostPage(final SopPaymentDetailsForm sopPaymentDetailsForm, final Model model)
	{
		try
		{
			final PaymentData silentOrderPageData = getPaymentFacade().beginSopCreateSubscription("/checkout/multi/sop/response",
					"/integration/merchant_callback");
			model.addAttribute("silentOrderPageData", silentOrderPageData);
			sopPaymentDetailsForm.setParameters(silentOrderPageData.getParameters());
			model.addAttribute("paymentFormUrl", silentOrderPageData.getPostUrl());
		}
		catch (final IllegalArgumentException e)
		{
			model.addAttribute("paymentFormUrl", "");
			model.addAttribute("silentOrderPageData", null);
			LOG.warn("Failed to set up silent order post page " + e.getMessage());
			GlobalMessages.addErrorMessage(model, "checkout.multi.sop.globalError");
		}

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("silentOrderPostForm", new PaymentDetailsForm());
		model.addAttribute("cartData", cartData);
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
		model.addAttribute("paymentInfos", getUserFacade().getCCPaymentInfos(true));
		model.addAttribute("sopCardTypes", getSopCardTypes());
		if (StringUtils.isNotBlank(sopPaymentDetailsForm.getBillTo_country()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(sopPaymentDetailsForm.getBillTo_country()));
			model.addAttribute("country", sopPaymentDetailsForm.getBillTo_country());
		}
	}

	protected Collection<CardTypeData> getSopCardTypes()
	{
		final Collection<CardTypeData> sopCardTypes = new ArrayList<CardTypeData>();

		final List<CardTypeData> supportedCardTypes = getCheckoutFacade().getSupportedCardTypes();
		for (final CardTypeData supportedCardType : supportedCardTypes)
		{
			// Add credit cards for all supported cards that have mappings for cybersource SOP
			if (cybersourceSopCardTypes.containsKey(supportedCardType.getCode()))
			{
				sopCardTypes.add(createCardTypeData(cybersourceSopCardTypes.get(supportedCardType.getCode()),
						supportedCardType.getName()));
			}
		}
		return sopCardTypes;
	}

	private void getSavedPaymentMethods(final Model model)
	{
		if (getCmsSiteService().getCurrentSite().isAdyenUseAPI() && getCmsSiteService().getCurrentSite().isAdyenUseSavedPayment())
		{
			//ADY-11
			final AdyenListRecurringDetailsResponse listRecurringDetailsResponse = checkoutFacade.retrieveSavedPaymentMethod();
			if (null != listRecurringDetailsResponse)
			{
				if (!CollectionUtils.isEmpty(listRecurringDetailsResponse.getDetails()))
				{
					model.addAttribute("savedAdyenPaymentMethods", listRecurringDetailsResponse.getDetails());
				}
			}
		}
	}

	private void maybeEnableAdyenAPIIntegration(final Model model)
	{
		if (getCmsSiteService().getCurrentSite().isAdyenUseAPI())
		{
			model.addAttribute("apiEnabled", Boolean.TRUE);
			model.addAttribute("apiCESKey", getCmsSiteService().getCurrentSite().getAdyenCSEKey());
			model.addAttribute("generationTime", fromCalendar(GregorianCalendar.getInstance()));
		}
		getSavedPaymentMethods(model);
		maybeSetupBoletoForm(model);
		maybeEnableInstallment(model);

	}

	private String fromCalendar(final Calendar calendar)
	{
		final Date date = calendar.getTime();
		final String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
		return formatted.substring(0, 22) + ":" + formatted.substring(22);
	}

	private void maybeSetupBoletoForm(final Model model)
	{
		if (getCmsSiteService().getCurrentSite().isAdyenUseAPI() && getCmsSiteService().getCurrentSite().isAdyenBoletoAvailable())
		{
			model.addAttribute("boletoEnabled", Boolean.TRUE);
			model.addAttribute("boletoBrands", getBoletoBrands());
		}
		else
		{
			model.addAttribute("boletoEnabled", Boolean.FALSE);
		}
	}

	private void maybeEnableInstallment(final Model model)
	{
		if (getCmsSiteService().getCurrentSite().isAdyenUseAPI() && getCmsSiteService().getCurrentSite().isAdyenEnableInstallment())
		{
			model.addAttribute("installments", getInstallments());
		}
	}

	private void maybeSetInstallments(final AdyenPaymentInfoData data, final String formInstallments)
	{
		if (getCmsSiteService().getCurrentSite().isAdyenUseAPI() && getCmsSiteService().getCurrentSite().isAdyenEnableInstallment()
				&& StringUtils.isNotBlank(formInstallments))
		{
			data.setInstallments(formInstallments);
		}
	}

	private List<SelectOption> getInstallments()
	{
		final List<SelectOption> installments = new ArrayList<SelectOption>();

		installments.add(new SelectOption("3", "3"));
		installments.add(new SelectOption("6", "6"));
		installments.add(new SelectOption("12", "12"));
		installments.add(new SelectOption("24", "24"));

		return installments;
	}

	private List<SelectOption> getBoletoBrands()
	{
		final List<BoletoBrand> availableBrands = getCmsSiteService().getCurrentSite().getAvailableBoletoTypes();
		final List<SelectOption> boletoBrands = new ArrayList<SelectOption>();
		for (final BoletoBrand brand : availableBrands)
		{
			boletoBrands.add(new SelectOption(brand.getCode(), brand.getCode()));
		}
		return boletoBrands;
	}

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(PAYMENT_METHOD);
	}

	static
	{
		// Map hybris card type to Cybersource SOP credit card
		cybersourceSopCardTypes.put("visa", "001");
		cybersourceSopCardTypes.put("master", "002");
		cybersourceSopCardTypes.put("amex", "003");
		cybersourceSopCardTypes.put("diners", "005");
		cybersourceSopCardTypes.put("maestro", "024");
	}

}
