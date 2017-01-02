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
package com.adyen.storefront.controllers.pages.checkout;

import de.hybris.platform.acceleratorfacades.payment.PaymentFacade;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PlaceOrderForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.SopPaymentDetailsForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.AddressValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.verification.AddressVerificationResultHandler;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.Config;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.velocity.VelocityEngineUtils;
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
import com.adyen.storefront.controllers.AdyenAddonControllerConstants;
import com.adyen.storefront.facades.impl.AdyenExtCheckoutFacade;
import com.adyen.storefront.forms.AdyenPaymentDetailsForm;
import com.adyen.storefront.forms.HPPDataForm;
import com.adyen.storefront.forms.validation.AdyenPaymentDetailsValidator;





/**
 * MultiStepCheckoutController
 */
@Controller
@RequestMapping(value = "/checkout/multi")
public class MultiStepCheckoutController extends AbstractCheckoutController
{
	private static final Logger LOG = Logger.getLogger(MultiStepCheckoutController.class);

	private static final String MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL = "multiStepCheckoutSummary";
	private static final String REDIRECT_URL_ADD_DELIVERY_ADDRESS = REDIRECT_PREFIX + "/checkout/multi/add-delivery-address";
	private static final String REDIRECT_URL_CHOOSE_DELIVERY_METHOD = REDIRECT_PREFIX + "/checkout/multi/choose-delivery-method";
	private static final String REDIRECT_URL_ADD_PAYMENT_METHOD = REDIRECT_PREFIX + "/checkout/multi/add-payment-method";
	private static final String REDIRECT_URL_CHOOSE_DELIVERY_LOCATION = REDIRECT_PREFIX
			+ "/checkout/multi/choose-delivery-location";
	private static final String REDIRECT_URL_3D_SECURE_PAYMENT_VALIDATION = REDIRECT_PREFIX
			+ "/checkout/multi/3d-secure-payment-validation";
	private static final String AUTHORISE_3D_SECURE_PAYMENT_URL = "/checkout/multi/authorise-3d-secure-payment-adyen-response";

	private static final String REDIRECT_URL_SUMMARY = REDIRECT_PREFIX + "/checkout/multi/summary";
	private static final String REDIRECT_URL_CART = REDIRECT_PREFIX + "/cart";
	private static final String REDIRECT_URL_ERROR = REDIRECT_PREFIX + "/checkout/multi/hop-error";
	private static final Map<String, String> cybersourceSopCardTypes = new HashMap<String, String>();

	static
	{
		// Map hybris card type to Cybersource SOP credit card
		cybersourceSopCardTypes.put("visa", "001");
		cybersourceSopCardTypes.put("master", "002");
		cybersourceSopCardTypes.put("amex", "003");
		cybersourceSopCardTypes.put("diners", "005");
		cybersourceSopCardTypes.put("maestro", "024");
	}

	@Resource(name = "adyenPaymentDetailsValidator")
	private AdyenPaymentDetailsValidator adyenPaymentDetailsValidator;

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

	@Resource(name = "checkoutFlowFacade")
	private AdyenExtCheckoutFacade checkoutFacade;

	@Resource(name = "adyenService")
	private AdyenService adyenService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "paymentService")
	private AdyenPaymentService adyenPaymentService;

	@Resource(name = "generateMerchantTransactionCodeStrategy")
	private GenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy;
	@Resource(name = "velocityEngine")
	private VelocityEngine velocityEngine;

	/**
	 * @return the generateMerchantTransactionCodeStrategy
	 */
	public GenerateMerchantTransactionCodeStrategy getGenerateMerchantTransactionCodeStrategy()
	{
		return generateMerchantTransactionCodeStrategy;
	}

	/**
	 * @param generateMerchantTransactionCodeStrategy
	 *           the generateMerchantTransactionCodeStrategy to set
	 */
	public void setGenerateMerchantTransactionCodeStrategy(
			final GenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy)
	{
		this.generateMerchantTransactionCodeStrategy = generateMerchantTransactionCodeStrategy;
	}

	/**
	 * @return the adyenPaymentService
	 */
	public AdyenPaymentService getAdyenPaymentService()
	{
		return adyenPaymentService;
	}

	/**
	 * @param adyenPaymentService
	 *           the adyenPaymentService to set
	 */
	public void setAdyenPaymentService(final AdyenPaymentService adyenPaymentService)
	{
		this.adyenPaymentService = adyenPaymentService;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
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

	protected AdyenPaymentDetailsValidator getAdyenPaymentDetailsValidator()
	{
		return adyenPaymentDetailsValidator;
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

	@ModelAttribute("checkoutSteps")
	public List<CheckoutSteps> addCheckoutStepsToModel(final HttpServletRequest request)
	{
		final List<CheckoutSteps> checkoutSteps = new ArrayList<CheckoutSteps>();
		checkoutSteps.add(new CheckoutSteps("deliveryAddress", "/checkout/multi/add-delivery-address"));
		checkoutSteps.add(new CheckoutSteps("deliveryMethod", "/checkout/multi/choose-delivery-method"));
		checkoutSteps.add(new CheckoutSteps("paymentMethod", "/checkout/multi/add-payment-method"));
		checkoutSteps.add(new CheckoutSteps("confirmOrder", "/checkout/multi/summary"));

		return checkoutSteps;
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

		final String siteUrl = getConfigurationService().getConfiguration()
				.getString("website." + getCmsSiteService().getCurrentSite().getUid() + ".https");
		formData.put("resURL", siteUrl + configurationService.getConfiguration().getString("authorise.HPP.payment.return.url", "/checkout/multi/authorise-hpp-payment-adyen-response"));
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

	@RequestMapping(method = RequestMethod.GET)
	public String gotoFirstStep()
	{
		if (hasValidCart())
		{
			return (getCheckoutFacade().hasShippingItems()) ? REDIRECT_URL_ADD_DELIVERY_ADDRESS
					: REDIRECT_URL_CHOOSE_DELIVERY_LOCATION;
		}
		LOG.info("Missing, empty or unsupported cart");
		return REDIRECT_URL_CART;
	}

	/**
	 * This method gets called when the "Use this Address" button is clicked. It sets the selected delivery address on
	 * the checkout facade - if it has changed, and reloads the page highlighting the selected delivery address.
	 *
	 * @param selectedAddressCode
	 *           - the id of the delivery address.
	 * @return - a URL to the page to load.
	 */
	@RequestMapping(value = "/select-delivery-address", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doSelectDeliveryAddress(@RequestParam("selectedAddressCode") final String selectedAddressCode)
	{
		if (!hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}

		if (!getCheckoutFacade().hasShippingItems())
		{
			return REDIRECT_URL_CHOOSE_DELIVERY_LOCATION;
		}

		if (StringUtils.isNotBlank(selectedAddressCode))
		{
			final AddressData selectedAddressData = getCheckoutFacade().getDeliveryAddressForCode(selectedAddressCode);
			final boolean hasSelectedAddressData = selectedAddressData != null;
			if (hasSelectedAddressData)
			{
				final AddressData cartCheckoutDeliveryAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
				if (isAddressIdChanged(cartCheckoutDeliveryAddress, selectedAddressData))
				{
					getCheckoutFacade().setDeliveryAddress(selectedAddressData);
					if (cartCheckoutDeliveryAddress != null && !cartCheckoutDeliveryAddress.isVisibleInAddressBook())
					{ // temporary address should be removed
						getUserFacade().removeAddress(cartCheckoutDeliveryAddress);
					}
				}
			}
		}
		return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
	}

	@RequestMapping(value = "/add-delivery-address", method = RequestMethod.GET)
	@RequireHardLogIn
	public String addDeliveryAddress(final Model model) throws CMSItemNotFoundException
	{
		if (!hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}

		if (!getCheckoutFacade().hasShippingItems())
		{
			return REDIRECT_URL_CHOOSE_DELIVERY_LOCATION;
		}

		getCheckoutFacade().setDeliveryAddressIfAvailable();

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);
		model.addAttribute("deliveryAddresses", getDeliveryAddresses(cartData.getDeliveryAddress()));
		model.addAttribute("noAddress", Boolean.valueOf(hasNoDeliveryAddress()));
		model.addAttribute("addressForm", new AddressForm());
		model.addAttribute("showSaveToAddressBook", Boolean.TRUE);
		this.prepareDataForPage(model);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryAddress.breadcrumb"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
	}

	@RequestMapping(value = "/add-delivery-address", method = RequestMethod.POST)
	@RequireHardLogIn
	public String addDeliveryAddress(final AddressForm addressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		getAddressValidator().validate(addressForm, bindingResult);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);
		model.addAttribute("deliveryAddresses", getDeliveryAddresses(cartData.getDeliveryAddress()));
		this.prepareDataForPage(model);
		if (StringUtils.isNotBlank(addressForm.getCountryIso()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
			model.addAttribute("country", addressForm.getCountryIso());
		}

		model.addAttribute("noAddress", Boolean.valueOf(hasNoDeliveryAddress()));
		model.addAttribute("showSaveToAddressBook", Boolean.TRUE);

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "address.error.formentry.invalid");
			storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}
		final AddressData newAddress = new AddressData();
		newAddress.setTitleCode(addressForm.getTitleCode());
		newAddress.setFirstName(addressForm.getFirstName());
		newAddress.setLastName(addressForm.getLastName());
		newAddress.setLine1(addressForm.getLine1());
		newAddress.setLine2(addressForm.getLine2());
		newAddress.setTown(addressForm.getTownCity());
		newAddress.setPostalCode(addressForm.getPostcode());
		newAddress.setBillingAddress(false);
		newAddress.setShippingAddress(true);
		if (addressForm.getCountryIso() != null)
		{
			final CountryData countryData = getI18NFacade().getCountryForIsocode(addressForm.getCountryIso());
			newAddress.setCountry(countryData);
		}
		if (addressForm.getRegionIso() != null && !StringUtils.isEmpty(addressForm.getRegionIso()))
		{
			final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso());
			newAddress.setRegion(regionData);
		}

		if (addressForm.getSaveInAddressBook() != null)
		{
			newAddress.setVisibleInAddressBook(addressForm.getSaveInAddressBook().booleanValue());
			if (addressForm.getSaveInAddressBook().booleanValue() && getUserFacade().isAddressBookEmpty())
			{
				newAddress.setDefaultAddress(true);
			}
		}
		else if (getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);
		}

		// Verify the address data.
		final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
				.verifyAddressData(newAddress);
		final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult, newAddress,
				model, redirectModel, bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
				"checkout.multi.address.updated");

		if (addressRequiresReview)
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		getUserFacade().addAddress(newAddress);

		final AddressData previousSelectedAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
		// Set the new address as the selected checkout delivery address
		getCheckoutFacade().setDeliveryAddress(newAddress);
		if (previousSelectedAddress != null && !previousSelectedAddress.isVisibleInAddressBook())
		{ // temporary address should be removed
			getUserFacade().removeAddress(previousSelectedAddress);
		}

		// Set the new address as the selected checkout delivery address
		getCheckoutFacade().setDeliveryAddress(newAddress);

		return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
	}

	@RequestMapping(value = "/remove-address", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String removeAddress(@RequestParam("addressCode") final String addressCode, final RedirectAttributes redirectModel,
			final Model model) throws CMSItemNotFoundException
	{
		final AddressData addressData = new AddressData();
		addressData.setId(addressCode);
		getUserFacade().removeAddress(addressData);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.removed");
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute("addressForm", new AddressForm());
		return REDIRECT_URL_ADD_DELIVERY_ADDRESS;
	}

	@RequestMapping(value = "/edit-delivery-address", method = RequestMethod.POST)
	@RequireHardLogIn
	public String editDeliveryAddress(final AddressForm addressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		getAddressValidator().validate(addressForm, bindingResult);
		if (StringUtils.isNotBlank(addressForm.getCountryIso()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
			model.addAttribute("country", addressForm.getCountryIso());
		}
		model.addAttribute("noAddress", Boolean.valueOf(hasNoDeliveryAddress()));

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "address.error.formentry.invalid");
			storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		final AddressData newAddress = new AddressData();
		newAddress.setId(addressForm.getAddressId());
		newAddress.setTitleCode(addressForm.getTitleCode());
		newAddress.setFirstName(addressForm.getFirstName());
		newAddress.setLastName(addressForm.getLastName());
		newAddress.setLine1(addressForm.getLine1());
		newAddress.setLine2(addressForm.getLine2());
		newAddress.setTown(addressForm.getTownCity());
		newAddress.setPostalCode(addressForm.getPostcode());
		newAddress.setBillingAddress(false);
		newAddress.setShippingAddress(true);
		if (addressForm.getCountryIso() != null)
		{
			final CountryData countryData = getI18NFacade().getCountryForIsocode(addressForm.getCountryIso());
			newAddress.setCountry(countryData);
		}
		if (addressForm.getRegionIso() != null && !StringUtils.isEmpty(addressForm.getRegionIso()))
		{
			final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso());
			newAddress.setRegion(regionData);
		}

		if (addressForm.getSaveInAddressBook() == null)
		{
			newAddress.setVisibleInAddressBook(true);
		}
		else
		{
			newAddress.setVisibleInAddressBook(Boolean.TRUE.equals(addressForm.getSaveInAddressBook()));
		}

		newAddress.setDefaultAddress(getUserFacade().isAddressBookEmpty() || getUserFacade().getAddressBook().size() == 1
				|| Boolean.TRUE.equals(addressForm.getDefaultAddress()));

		// Verify the address data.
		final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
				.verifyAddressData(newAddress);
		final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult, newAddress,
				model, redirectModel, bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
				"checkout.multi.address.updated");

		if (addressRequiresReview)
		{
			if (StringUtils.isNotBlank(addressForm.getCountryIso()))
			{
				model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
				model.addAttribute("country", addressForm.getCountryIso());
			}
			storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));

			if (StringUtils.isNotEmpty(addressForm.getAddressId()))
			{
				final AddressData addressData = getCheckoutFacade().getDeliveryAddressForCode(addressForm.getAddressId());
				if (addressData != null)
				{
					model.addAttribute("showSaveToAddressBook", Boolean.valueOf(!addressData.isVisibleInAddressBook()));
					model.addAttribute("edit", Boolean.TRUE);
				}
			}

			return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		getUserFacade().editAddress(newAddress);
		getCheckoutFacade().setDeliveryModeIfAvailable();
		getCheckoutFacade().setDeliveryAddress(newAddress);

		return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
	}

	@RequestMapping(value = "/edit-delivery-address", method = RequestMethod.GET)
	@RequireHardLogIn
	public String editDeliveryAddress(@RequestParam("editAddressCode") final String editAddressCode, final Model model)
			throws CMSItemNotFoundException
	{
		if (!hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}

		if (!getCheckoutFacade().hasShippingItems())
		{
			return REDIRECT_URL_CHOOSE_DELIVERY_LOCATION;
		}

		AddressData addressData = null;
		if (StringUtils.isNotEmpty(editAddressCode))
		{
			addressData = getCheckoutFacade().getDeliveryAddressForCode(editAddressCode);
		}

		final AddressForm addressForm = new AddressForm();
		final boolean hasAddressData = addressData != null;
		if (hasAddressData)
		{
			addressForm.setAddressId(addressData.getId());
			addressForm.setTitleCode(addressData.getTitleCode());
			addressForm.setFirstName(addressData.getFirstName());
			addressForm.setLastName(addressData.getLastName());
			addressForm.setLine1(addressData.getLine1());
			addressForm.setLine2(addressData.getLine2());
			addressForm.setTownCity(addressData.getTown());
			addressForm.setPostcode(addressData.getPostalCode());
			addressForm.setCountryIso(addressData.getCountry().getIsocode());
			addressForm.setSaveInAddressBook(Boolean.valueOf(addressData.isVisibleInAddressBook()));
			addressForm.setShippingAddress(Boolean.valueOf(addressData.isShippingAddress()));
			addressForm.setBillingAddress(Boolean.valueOf(addressData.isBillingAddress()));
			if (addressData.getRegion() != null && !StringUtils.isEmpty(addressData.getRegion().getIsocode()))
			{
				addressForm.setRegionIso(addressData.getRegion().getIsocode());
			}
		}

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);
		model.addAttribute("deliveryAddresses", getDeliveryAddresses(cartData.getDeliveryAddress()));
		if (StringUtils.isNotBlank(addressForm.getCountryIso()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
			model.addAttribute("country", addressForm.getCountryIso());
		}
		model.addAttribute("noAddress", Boolean.valueOf(hasNoDeliveryAddress()));
		model.addAttribute("edit", Boolean.valueOf(hasAddressData));
		model.addAttribute("addressForm", addressForm);
		if (addressData != null)
		{
			model.addAttribute("showSaveToAddressBook", Boolean.valueOf(!addressData.isVisibleInAddressBook()));
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryAddress.breadcrumb"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
	}

	@RequestMapping(value = "/select-suggested-address", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doSelectSuggestedAddress(final AddressForm addressForm, final RedirectAttributes redirectModel)
	{
		final Set<String> resolveCountryRegions = org.springframework.util.StringUtils
				.commaDelimitedListToSet(Config.getParameter("resolve.country.regions"));

		final AddressData selectedAddress = new AddressData();
		selectedAddress.setId(addressForm.getAddressId());
		selectedAddress.setTitleCode(addressForm.getTitleCode());
		selectedAddress.setFirstName(addressForm.getFirstName());
		selectedAddress.setLastName(addressForm.getLastName());
		selectedAddress.setLine1(addressForm.getLine1());
		selectedAddress.setLine2(addressForm.getLine2());
		selectedAddress.setTown(addressForm.getTownCity());
		selectedAddress.setPostalCode(addressForm.getPostcode());
		selectedAddress.setBillingAddress(false);
		selectedAddress.setShippingAddress(true);
		final CountryData countryData = getI18NFacade().getCountryForIsocode(addressForm.getCountryIso());
		selectedAddress.setCountry(countryData);

		if (resolveCountryRegions.contains(countryData.getIsocode()))
		{
			if (addressForm.getRegionIso() != null && !StringUtils.isEmpty(addressForm.getRegionIso()))
			{
				final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso());
				selectedAddress.setRegion(regionData);
			}
		}

		if (addressForm.getSaveInAddressBook() != null)
		{
			selectedAddress.setVisibleInAddressBook(addressForm.getSaveInAddressBook().booleanValue());
		}

		if (Boolean.TRUE.equals(addressForm.getEditAddress()))
		{
			getUserFacade().editAddress(selectedAddress);
		}
		else
		{
			getUserFacade().addAddress(selectedAddress);
		}

		final AddressData previousSelectedAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
		// Set the new address as the selected checkout delivery address
		getCheckoutFacade().setDeliveryAddress(selectedAddress);
		if (previousSelectedAddress != null && !previousSelectedAddress.isVisibleInAddressBook())
		{ // temporary address should be removed
			getUserFacade().removeAddress(previousSelectedAddress);
		}

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "checkout.multi.address.added");

		return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
	}

	@RequestMapping(value = "/choose-delivery-method", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doChooseDeliveryModes(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		if (!hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}

		if (!getCheckoutFacade().hasShippingItems())
		{
			return REDIRECT_URL_CHOOSE_DELIVERY_LOCATION;
		}

		if (hasNoDeliveryAddress())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryAddress.notprovided");
			return REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		}

		// Try to set default delivery mode
		getCheckoutFacade().setDeliveryModeIfAvailable();

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);
		model.addAttribute("deliveryMethods", getCheckoutFacade().getSupportedDeliveryModes());
		this.prepareDataForPage(model);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.ChooseDeliveryMethodPage;
	}

	@RequestMapping(value = "/choose-delivery-location", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doChooseDeliveryLocation(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		if (!hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}
		if (hasNoDeliveryAddress())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryAddress.notprovided");
			return REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		}
		if (hasNoDeliveryMode())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryMethod.notprovided");
			return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
		}

		// Try to set default delivery mode
		getCheckoutFacade().setDeliveryModeIfAvailable();

		model.addAttribute("cartData", getCheckoutFacade().getCheckoutCart());
		model.addAttribute("pickupConsolidationOptions", getCheckoutFacade().getConsolidatedPickupOptions());
		model.addAttribute("userLocation", getCustomerLocationService().getUserLocation());
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.ChoosePickupLocationPage;
	}

	@RequestMapping(value = "/select-delivery-location", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doSelectDeliveryLocation(@RequestParam(value = "posName") final String posName, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, CommerceCartModificationException
	{
		if (!hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}
		if (hasNoDeliveryAddress())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryAddress.notprovided");
			return REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		}
		if (hasNoDeliveryMode())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryMethod.notprovided");
			return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
		}

		//Consolidate the cart and add unsuccessful modifications to page
		model.addAttribute("validationData", getCheckoutFacade().consolidateCheckoutCart(posName));
		model.addAttribute("cartData", getCheckoutFacade().getCheckoutCart());
		model.addAttribute("userLocation", getCustomerLocationService().getUserLocation());
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.ChoosePickupLocationPage;
	}

	/**
	 * This method gets called when the "Use Selected Delivery Method" button is clicked. It sets the selected delivery
	 * mode on the checkout facade and reloads the page highlighting the selected delivery Mode.
	 *
	 * @param selectedDeliveryMethod
	 *           - the id of the delivery mode.
	 * @return - a URL to the page to load.
	 */
	@RequestMapping(value = "/select-delivery-method", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doSelectDeliveryMode(@RequestParam("delivery_method") final String selectedDeliveryMethod)
	{
		if (!hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}

		if (StringUtils.isNotEmpty(selectedDeliveryMethod))
		{
			getCheckoutFacade().setDeliveryMode(selectedDeliveryMethod);
		}

		if (getCheckoutFacade().hasPickUpItems())
		{
			return REDIRECT_URL_CHOOSE_DELIVERY_LOCATION;
		}
		else
		{
			return REDIRECT_URL_ADD_PAYMENT_METHOD;
		}
	}

	/**
	 * This method gets called when the "Use These Payment Details" button is clicked. It sets the selected payment
	 * method on the checkout facade and reloads the page highlighting the selected payment method.
	 *
	 * @param selectedPaymentMethodId
	 *           - the id of the payment method to use.
	 * @return - a URL to the page to load.
	 */
	@RequestMapping(value = "/select-payment-method", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doSelectPaymentMethod(@RequestParam("selectedPaymentMethodId") final String selectedPaymentMethodId)
	{
		if (!hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}

		if (StringUtils.isNotBlank(selectedPaymentMethodId))
		{
			getCheckoutFacade().setPaymentDetails(selectedPaymentMethodId);
		}
		return REDIRECT_URL_SUMMARY;
	}

	@RequestMapping(value = "/add-payment-method", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doAddPaymentMethod(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		if (!hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}
		if (hasNoDeliveryAddress())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryAddress.notprovided");
			return REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		}
		if (hasNoDeliveryMode())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryMethod.notprovided");
			return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
		}

		//ADY-10 ADY-12 ADY-14 ADY-74 ,Flow condition branch.
		maybeEnableAdyenAPIIntegration(model);
		setupAddPaymentPage(model);

		//final Use the checkout final PCI strategy for getting final the URL for creating new subscriptions.
		final CheckoutPciOptionEnum subscriptionPciOption = getCheckoutFlowFacade().getSubscriptionPciOption();
		if (CheckoutPciOptionEnum.HOP.equals(subscriptionPciOption))
		{
			// Redirect the customer to the HOP page or show error message if it fails (e.g. no HOP configurations).
			try
			{
				final PaymentData hostedOrderPageData = getPaymentFacade().beginHopCreateSubscription("/checkout/multi/hop-response",
						"/integration/merchant_callback");
				model.addAttribute("hostedOrderPageData", hostedOrderPageData);

				final boolean hopDebugMode = getSiteConfigService().getBoolean(PaymentConstants.PaymentProperties.HOP_DEBUG_MODE,
						false);
				model.addAttribute("hopDebugMode", Boolean.valueOf(hopDebugMode));

				return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
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
				return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.SilentOrderPostPage;
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
		return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;

	}

	@RequestMapping(value = "/authorise-hpp-payment-adyen-response", method = RequestMethod.GET)
	@RequireHardLogIn
	public String authoriseHPPPaymentResponse(@RequestParam final String merchantReference,
			@RequestParam(required = false) final String pspReference, @RequestParam final String authResult, final Model model,
			final RedirectAttributes redirectModel, final HttpServletRequest request)
	{
		try
		{
			final StringBuffer responseString = new StringBuffer(
					"handling hpp authorise response by hybris hosted URL:\n");
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
					adyenPaymentService.createHPPAuthorisePTE(order, pspReference, authResult);
					if (null != order && order instanceof OrderModel)
					{
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

	private String getHPPUrl(final boolean withBrandCode)
	{
		if (withBrandCode)
		{
			return getConfigurationService().getConfiguration().getString("integration.adyen.hpp.details.url");
		}
		return getConfigurationService().getConfiguration().getString("integration.adyen.hpp.pay.url");
	}

	private String fromCalendar(final Calendar calendar)
	{
		final Date date = calendar.getTime();
		final String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
		return formatted.substring(0, 22) + ":" + formatted.substring(22);
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

	protected void setupSilentOrderPostPage(final SopPaymentDetailsForm sopPaymentDetailsForm, final Model model)
	{
		try
		{
			final PaymentData silentOrderPageData = getPaymentFacade().beginSopCreateSubscription("/checkout/multi/sop-response",
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

	protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("metaRobots", "no-index,no-follow");
		model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(hasNoPaymentInfo()));
		this.prepareDataForPage(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
		final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
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
				sopCardTypes
						.add(createCardTypeData(cybersourceSopCardTypes.get(supportedCardType.getCode()), supportedCardType.getName()));
			}
		}

		return sopCardTypes;
	}

	protected CardTypeData createCardTypeData(final String code, final String name)
	{
		final CardTypeData cardTypeData = new CardTypeData();
		cardTypeData.setCode(code);
		cardTypeData.setName(name);
		return cardTypeData;
	}

	@RequestMapping(value =
	{ "/add-payment-method" }, method = RequestMethod.POST)
	@RequireHardLogIn
	public String doSavePaymentMethod(final Model model, @Valid final AdyenPaymentDetailsForm adyenPaymentDetailsForm,
			final BindingResult bindingResult, final HttpServletRequest request, final RedirectAttributes redirectModel)
					throws CMSItemNotFoundException
	{
		getAdyenPaymentDetailsValidator().validate(adyenPaymentDetailsForm, bindingResult);
		setupAddPaymentPage(model);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);

		if (bindingResult.hasErrors())
		{
			maybeEnableAdyenAPIIntegration(model);
			maybeEnableAdyenHPPIntegration(model);
			GlobalMessages.addErrorMessage(model, "checkout.error.paymentethod.formentry.invalid");
			return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
		}

		final AdyenPaymentInfoData paymentInfoData = new AdyenPaymentInfoData();
		paymentInfoData.setId(adyenPaymentDetailsForm.getPaymentId());

		if (Boolean.TRUE.equals(adyenPaymentDetailsForm.getUseHPP()))
		{
			paymentInfoData.setUseHPP(Boolean.TRUE.booleanValue());
			paymentInfoData.setAdyenPaymentBrand(adyenPaymentDetailsForm.getAdyenPaymentBrand());
			paymentInfoData.setHppURL(getHPPUrl(StringUtils.isNotEmpty(adyenPaymentDetailsForm.getAdyenPaymentBrand())));
			paymentInfoData.setIssuerId(adyenPaymentDetailsForm.getIssuerId());
		} else {
			paymentInfoData.setShopperIp(request.getRemoteAddr());

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
				return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
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

		final AdyenPaymentInfoData newPaymentSubscription = getCheckoutFacade().createAdyenPaymentSubscription(paymentInfoData);
		if (newPaymentSubscription != null && StringUtils.isNotBlank(newPaymentSubscription.getSubscriptionId()))
		{
			if (Boolean.TRUE.equals(adyenPaymentDetailsForm.getSaveInAccount())
					&& getUserFacade().getCCPaymentInfos(true).size() <= 1)
			{
				getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
			}
			getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
		else
		{
			GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.createSubscription.failedMsg");
			maybeEnableAdyenAPIIntegration(model);
			maybeEnableAdyenHPPIntegration(model);
			return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
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
			final AdyenPaymentTransactionEntryModel entry = (AdyenPaymentTransactionEntryModel) getCheckoutFacade()
					.authorizeAdyenPayment(checkoutFacade.getSessionCVC());
			if (entry == null || (!ResultCode.Authorised.name().equals(entry.getTransactionStatus())
					&& !ResultCode.Received.name().equals(entry.getTransactionStatus())))
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
				return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
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

		return REDIRECT_URL_SUMMARY;
	}

	@RequestMapping(value = "/remove-payment-method", method = RequestMethod.POST)
	@RequireHardLogIn
	public String removePaymentMethod(@RequestParam(value = "paymentInfoId") final String paymentMethodId,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		getUserFacade().unlinkCCPaymentInfo(paymentMethodId);
		GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
				"text.account.profile.paymentCart.removed");
		return REDIRECT_URL_ADD_PAYMENT_METHOD;
	}

	@RequestMapping(value = "/hop-response", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doHandleHopResponse(final HttpServletRequest request)
	{
		final Map<String, String> resultMap = getRequestParameterMap(request);

		final PaymentSubscriptionResultData paymentSubscriptionResultData = getPaymentFacade()
				.completeHopCreateSubscription(resultMap, true);
		if (paymentSubscriptionResultData.isSuccess() && paymentSubscriptionResultData.getStoredCard() != null
				&& StringUtils.isNotBlank(paymentSubscriptionResultData.getStoredCard().getSubscriptionId()))
		{
			final CCPaymentInfoData newPaymentSubscription = paymentSubscriptionResultData.getStoredCard();

			if (getUserFacade().getCCPaymentInfos(true).size() <= 1)
			{
				getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
			}
			getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
		else
		{
			// HOP ERROR!
			LOG.error("Failed to create subscription.  Please check the log files for more information");
			return REDIRECT_URL_ERROR + "/?decision=" + paymentSubscriptionResultData.getDecision() + "&reasonCode="
					+ paymentSubscriptionResultData.getResultCode();
		}

		return REDIRECT_URL_SUMMARY;
	}

	@RequestMapping(value = "/hop-error", method = RequestMethod.GET)
	public String doHostedOrderPageError(@RequestParam(required = true) final String decision,
			@RequestParam(required = true) final String reasonCode, final Model model, final RedirectAttributes redirectAttributes)
					throws CMSItemNotFoundException
	{

		String redirectUrl = REDIRECT_URL_ADD_PAYMENT_METHOD;
		if (!hasValidCart())
		{
			redirectUrl = REDIRECT_URL_CART;
		}
		if (StringUtils.isBlank(redirectUrl) && hasNoDeliveryAddress())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryAddress.notprovided");
			redirectUrl = REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		}
		if (StringUtils.isBlank(redirectUrl) && hasNoDeliveryMode())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryMethod.notprovided");
			redirectUrl = REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
		}
		model.addAttribute("decision", decision);
		model.addAttribute("reasonCode", reasonCode);
		model.addAttribute("redirectUrl", redirectUrl.replace(REDIRECT_PREFIX, ""));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.hostedOrderPageError.breadcrumb"));
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));

		GlobalMessages.addErrorMessage(model, "checkout.multi.hostedOrderPageError.globalError");

		return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPageErrorPage;
	}

	@RequestMapping(value = "/sop-response", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doHandleSopResponse(final HttpServletRequest request, @Valid final SopPaymentDetailsForm sopPaymentDetailsForm,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectAttributes)
					throws CMSItemNotFoundException
	{
		final Map<String, String> resultMap = getRequestParameterMap(request);

		final boolean savePaymentInfo = sopPaymentDetailsForm.isSavePaymentInfo()
				|| getCheckoutCustomerStrategy().isAnonymousCheckout();
		final PaymentSubscriptionResultData paymentSubscriptionResultData = this.getPaymentFacade()
				.completeSopCreateSubscription(resultMap, savePaymentInfo);

		if (paymentSubscriptionResultData.isSuccess() && paymentSubscriptionResultData.getStoredCard() != null
				&& StringUtils.isNotBlank(paymentSubscriptionResultData.getStoredCard().getSubscriptionId()))
		{
			final CCPaymentInfoData newPaymentSubscription = paymentSubscriptionResultData.getStoredCard();

			if (getUserFacade().getCCPaymentInfos(true).size() <= 1)
			{
				getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
			}
			getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
		else if ((paymentSubscriptionResultData.getDecision() != null
				&& paymentSubscriptionResultData.getDecision().equalsIgnoreCase("error"))
				|| (paymentSubscriptionResultData.getErrors() != null && !paymentSubscriptionResultData.getErrors().isEmpty()))
		{
			// Have SOP errors that we can display

			if (!hasValidCart())
			{
				LOG.info("Missing, empty or unsupported cart");
				return REDIRECT_URL_CART;
			}

			setupAddPaymentPage(model);

			// Build up the SOP form data and render page containing form
			try
			{
				setupSilentOrderPostPage(sopPaymentDetailsForm, model);
			}
			catch (final Exception e)
			{
				LOG.error("Failed to build beginCreateSubscription request", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
				return doAddPaymentMethod(model, redirectAttributes);
			}

			if (paymentSubscriptionResultData.getErrors() != null && !paymentSubscriptionResultData.getErrors().isEmpty())
			{
				GlobalMessages.addErrorMessage(model, "checkout.error.paymentethod.formentry.invalid");
				// Add in specific errors for invalid fields
				for (final PaymentErrorField paymentErrorField : paymentSubscriptionResultData.getErrors().values())
				{
					if (paymentErrorField.isMissing())
					{
						bindingResult.rejectValue(paymentErrorField.getName(),
								"checkout.error.paymentethod.formentry.sop.missing." + paymentErrorField.getName(),
								"Please enter a value for this field");
					}
					if (paymentErrorField.isInvalid())
					{
						bindingResult.rejectValue(paymentErrorField.getName(),
								"checkout.error.paymentethod.formentry.sop.invalid." + paymentErrorField.getName(),
								"This value is invalid for this field");
					}
				}
			}
			else if (paymentSubscriptionResultData.getDecision() != null
					&& paymentSubscriptionResultData.getDecision().equalsIgnoreCase("error"))
			{
				LOG.error("Failed to create subscription. Error occurred while contacting external payment services.");
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
			}

			return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.SilentOrderPostPage;
		}
		else
		{
			// SOP ERROR!
			LOG.error("Failed to create subscription.  Please check the log files for more information");
			return REDIRECT_URL_ERROR + "/?decision=" + paymentSubscriptionResultData.getDecision() + "&reasonCode="
					+ paymentSubscriptionResultData.getResultCode();
		}

		return REDIRECT_URL_SUMMARY;
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

	@RequestMapping(value = "/summary", method = RequestMethod.GET)
	@RequireHardLogIn
	public String checkoutSummary(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException, CommerceCartModificationException
	{
		if (!hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}

		if (hasNoDeliveryAddress())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryAddress.notprovided");
			return REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		}
		if (hasNoDeliveryMode())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryMethod.notprovided");
			return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
		}
		if (hasNoPaymentInfo())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.paymentDetails.notprovided");
			return REDIRECT_URL_ADD_PAYMENT_METHOD;
		}

		final CartData cartData = getCheckoutFacade().getCheckoutCart();

		if (!getCheckoutFacade().hasShippingItems())
		{
			cartData.setDeliveryAddress(null);
		}
		if (!getCheckoutFacade().hasPickUpItems() && cartData.getDeliveryMode().getCode().equals("pickup"))
		{
			return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
		}

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

		model.addAttribute("boletoUrl", getCheckoutFacade().getBoletoUrl());

		if (((AdyenPaymentInfoData) cartData.getPaymentInfo()).isUseHPP())
		{
			final HPPDataForm hppForm = getHPPFormData();
			model.addAttribute("hppFormData", hppForm);
		}

		// Only request the security code if the SubscriptionPciOption is set to Default.
		final boolean requestSecurityCode = (CheckoutPciOptionEnum.DEFAULT
				.equals(getCheckoutFlowFacade().getSubscriptionPciOption()));
		model.addAttribute("requestSecurityCode", Boolean.valueOf(requestSecurityCode));

		model.addAttribute(new PlaceOrderForm());

		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.CheckoutSummaryPage;
	}

	@RequestMapping(value = "/termsAndConditions")
	@RequireHardLogIn
	public String getTermsAndConditions(final Model model) throws CMSItemNotFoundException
	{
		final ContentPageModel pageForRequest = getCmsPageService().getPageForLabel("/termsAndConditions");
		storeCmsPageInModel(model, pageForRequest);
		setUpMetaDataForContentPage(model, pageForRequest);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, contentPageBreadcrumbBuilder.getBreadcrumbs(pageForRequest));
		return AdyenAddonControllerConstants.Views.Fragments.Checkout.TermsAndConditionsPopup;
	}

	@RequestMapping(value = "/placeOrder")
	@RequireHardLogIn
	public String placeOrder(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel)
					throws CMSItemNotFoundException, InvalidCartException, CommerceCartModificationException
	{
		if (validateOrderForm(placeOrderForm, model))
		{
			return checkoutSummary(model, redirectModel);
		}

		//Validate the cart
		if (validateCart(redirectModel))
		{
			// Invalid cart. Bounce back to the cart page.
			return REDIRECT_PREFIX + "/cart";
		}

		// authorize, if failure occurs don't allow to place the order
		final boolean isPaymentUthorized = getCheckoutFacade().isPaymentUthorized();


		if (!isPaymentUthorized)
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.authorization.failed");
			return checkoutSummary(model, redirectModel);
		}

		return placeOrderInternal(model, redirectModel);
	}

	/**
	 * @param model
	 * @param redirectModel
	 * @return
	 * @throws CMSItemNotFoundException
	 * @throws CommerceCartModificationException
	 */
	private String placeOrderInternal(final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, CommerceCartModificationException
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
			return checkoutSummary(model, redirectModel);
		}

		return redirectToOrderConfirmationPage(orderData);
	}

	@RequestMapping(value = "/3d-secure-payment-validation", method = RequestMethod.GET)
	@RequireHardLogIn
	public String validate3DSecurePayment(@RequestParam("paReq") final String paReq, @RequestParam("md") final String md,
			@RequestParam("issuerUrl") final String issuerUrl, final Model model, final HttpServletRequest request)
	{
		model.addAttribute("paReq", paReq);
		model.addAttribute("md", md);
		final String siteUrl = getConfigurationService().getConfiguration()
				.getString("website." + getCmsSiteService().getCurrentSite().getUid() + "." + request.getScheme());
		model.addAttribute("termUrl", siteUrl + AUTHORISE_3D_SECURE_PAYMENT_URL);
		model.addAttribute("issuerUrl", issuerUrl);
		return AdyenAddonControllerConstants.Views.Pages.MultiStepCheckout.Validate3DSecurePaymentPage;
	}

	@RequestMapping(value = "/authorise-3d-secure-payment-adyen-response", method = RequestMethod.POST)
	@RequireHardLogIn
	public String authorise3DSecurePayment(@RequestParam("PaRes") final String paRes, @RequestParam("MD") final String md,
			final Model model, final RedirectAttributes redirectModel, final HttpServletRequest request)
					throws CMSItemNotFoundException, CommerceCartModificationException, UnknownHostException
	{
		try
		{
			adyenPaymentService.maybeClearAuthorizeHistory(getCheckoutFacade().getCartModel());
			final AdyenPaymentTransactionEntryModel paymentTransactionEntry = (AdyenPaymentTransactionEntryModel) adyenPaymentService
					.authorize3DSecure(getCheckoutFacade().getCartModel(), paRes, md, request.getRemoteAddr(),
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
		return REDIRECT_URL_SUMMARY;
	}

	/**
	 * @param paymentTransactionEntry
	 * @return
	 */
	public boolean isPaymentFail(final PaymentTransactionEntryModel paymentTransactionEntry)
	{
		return (paymentTransactionEntry == null)
				|| !((ResultCode.Authorised.name().equals(paymentTransactionEntry.getTransactionStatus()))
						|| (ResultCode.Received.name().equals(paymentTransactionEntry.getTransactionStatus())));
	}

	@RequestMapping(value = "/billingaddressform", method = RequestMethod.GET)
	public String getCountryAddressForm(@RequestParam("countryIsoCode") final String countryIsoCode,
			@RequestParam("useDeliveryAddress") final boolean useDeliveryAddress, final Model model)
	{
		model.addAttribute("supportedCountries", getCountries());
		model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(countryIsoCode));
		model.addAttribute("country", countryIsoCode);

		final SopPaymentDetailsForm sopPaymentDetailsForm = new SopPaymentDetailsForm();
		model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
		if (useDeliveryAddress)
		{
			final AddressData deliveryAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();

			if (deliveryAddress.getRegion() != null && !StringUtils.isEmpty(deliveryAddress.getRegion().getIsocode()))
			{
				sopPaymentDetailsForm.setBillTo_state(deliveryAddress.getRegion().getIsocodeShort());
			}

			sopPaymentDetailsForm.setBillTo_firstName(deliveryAddress.getFirstName());
			sopPaymentDetailsForm.setBillTo_lastName(deliveryAddress.getLastName());
			sopPaymentDetailsForm.setBillTo_street1(deliveryAddress.getLine1());
			sopPaymentDetailsForm.setBillTo_street2(deliveryAddress.getLine2());
			sopPaymentDetailsForm.setBillTo_city(deliveryAddress.getTown());
			sopPaymentDetailsForm.setBillTo_postalCode(deliveryAddress.getPostalCode());
			sopPaymentDetailsForm.setBillTo_country(deliveryAddress.getCountry().getIsocode());
		}
		return AdyenAddonControllerConstants.Views.Fragments.Checkout.BillingAddressForm;
	}


	@RequestMapping(value = "/express", method = RequestMethod.GET)
	@RequireHardLogIn
	public String performExpressCheckout(final RedirectAttributes redirectModel)
	{
		if (hasValidCart())
		{
			switch (getCheckoutFacade().performExpressCheckout())
			{
				case SUCCESS:
					return REDIRECT_URL_SUMMARY;

				case ERROR_DELIVERY_ADDRESS:
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"checkout.express.error.deliveryAddress");
					return REDIRECT_URL_ADD_DELIVERY_ADDRESS;

				case ERROR_DELIVERY_MODE:
				case ERROR_CHEAPEST_DELIVERY_MODE:
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"checkout.express.error.deliveryMode");
					return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;

				case ERROR_PAYMENT_INFO:
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"checkout.express.error.paymentInfo");
					return REDIRECT_URL_ADD_PAYMENT_METHOD;

				default:
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"checkout.express.error.notAvailable");
			}
		}

		return gotoFirstStep();
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
		//		final String securityCode = placeOrderForm.getSecurityCode();
		boolean invalid = false;

		if (hasNoDeliveryAddress())
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryAddress.notSelected");
			invalid = true;
		}

		if (hasNoDeliveryMode())
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryMethod.notSelected");
			invalid = true;
		}

		if (hasNoPaymentInfo())
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
		final CartData cartData = getCheckoutFacade().getCheckoutCart();

		if (!getCheckoutFacade().containsTaxValues())
		{
			LOG.error(String.format(
					"Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue",
					cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.tax.missing");
			invalid = true;
		}

		if (!cartData.isCalculated())
		{
			LOG.error(
					String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.cart.notcalculated");
			invalid = true;
		}

		return invalid;
	}

	protected void prepareDataForPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("isOmsEnabled", Boolean.valueOf(getSiteConfigService().getBoolean("oms.enabled", false)));
		model.addAttribute("supportedCountries", cartFacade.getDeliveryCountries());
		model.addAttribute("expressCheckoutAllowed", Boolean.valueOf(checkoutFacade.isExpressCheckoutAllowedForCart()));
		model.addAttribute("taxEstimationEnabled", Boolean.valueOf(checkoutFacade.isTaxEstimationEnabledForCart()));
	}

	/**
	 * @return the checkoutFacade
	 */
	@Override
	public AdyenExtCheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	/**
	 * @param checkoutFacade
	 *           the checkoutFacade to set
	 */
	public void setCheckoutFacade(final AdyenExtCheckoutFacade checkoutFacade)
	{
		this.checkoutFacade = checkoutFacade;
	}



}
