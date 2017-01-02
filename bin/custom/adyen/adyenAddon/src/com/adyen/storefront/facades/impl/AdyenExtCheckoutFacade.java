/**
 *
 */
package com.adyen.storefront.facades.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.adyen.facades.flow.impl.DefaultCheckoutFlowFacade;
import com.adyen.facades.order.data.AdyenPaymentInfoData;
import com.adyen.services.customer.impl.AdyenCustomerAccountService;
import com.adyen.services.impl.AdyenCommerceCheckoutService;
import com.adyen.services.integration.AdyenService;
import com.adyen.services.integration.data.ContractType;
import com.adyen.services.integration.data.RecurringData;
import com.adyen.services.integration.data.ResultCode;
import com.adyen.services.integration.data.request.AdyenListRecurringDetailsRequest;
import com.adyen.services.integration.data.response.AdyenListRecurringDetailsResponse;
import com.adyen.storefront.facades.ExtCheckoutFacade;


/**
 * @author Kenneth Zhou
 *
 */
public class AdyenExtCheckoutFacade extends DefaultCheckoutFlowFacade implements ExtCheckoutFacade
{
	private AdyenService adyenService;
	private ConfigurationService configurationService;
	private CMSSiteService cmsSiteService;
	private SessionService sessionService;
	private FlexibleSearchService flexibleSearchService;

	private Converter<AdyenPaymentInfoModel, AdyenPaymentInfoData> adyenPaymentInfoConverter;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.adyen.facades.ExtAcceleratorCheckoutFacade#retrieveSavedPaymentMethod()
	 */
	@Override
	public AdyenListRecurringDetailsResponse retrieveSavedPaymentMethod()
	{
		final AdyenListRecurringDetailsRequest request = new AdyenListRecurringDetailsRequest();
		final CustomerModel customer = getCurrentUserForCheckout();

		request.setMerchantAccount(cmsSiteService.getCurrentSite().getAdyenMerchantAccount());
		request.setShopperReference(customer.getCustomerID());
		final RecurringData recurring = new RecurringData();
		recurring.setContract(ContractType.RECURRING.name());
		request.setRecurring(recurring);

		final AdyenListRecurringDetailsResponse response = adyenService.requestRecurringPaymentDetails(request);

		return response;
	}

	@Override
	public boolean authorizePayment(final String securityCode)
	{
		final CartModel cartModel = getCart();
		if (checkIfCurrentUserIsTheCartUser())
		{
			final AdyenPaymentInfoModel adyenPaymentInfoModel = (AdyenPaymentInfoModel) cartModel.getPaymentInfo();
			if (adyenPaymentInfoModel != null && StringUtils.isNotBlank(adyenPaymentInfoModel.getSubscriptionId()))
			{
				final PaymentTransactionEntryModel paymentTransactionEntryModel = ((AdyenCommerceCheckoutService) getCommerceCheckoutService())
						.authorizeAdyenPaymentAmount(cartModel, securityCode, getPaymentProvider(), null);

				return paymentTransactionEntryModel != null
						&& (ResultCode.Authorised.name().equals(paymentTransactionEntryModel.getTransactionStatus()) || ResultCode.Received
								.name().equals(paymentTransactionEntryModel.getTransactionStatus()));
			}
		}
		return false;
	}

	@Override
	public PaymentTransactionEntryModel authorizeAdyenPayment(final String securityCode)
	{
		final CartModel cartModel = getCart();
		if (checkIfCurrentUserIsTheCartUser())
		{
			final AdyenPaymentInfoModel adyenPaymentInfoModel = (AdyenPaymentInfoModel) cartModel.getPaymentInfo();
			if (adyenPaymentInfoModel != null && StringUtils.isNotBlank(adyenPaymentInfoModel.getSubscriptionId()))
			{
				final PaymentTransactionEntryModel paymentTransactionEntryModel = ((AdyenCommerceCheckoutService) getCommerceCheckoutService())
						.authorizeAdyenPaymentAmount(cartModel, securityCode, getPaymentProvider(), null);

				if (paymentTransactionEntryModel != null)
				{
					return paymentTransactionEntryModel;
				}
			}
		}
		return null;
	}

	@Override
	public boolean setPaymentDetails(final String paymentInfoId)
	{
		validateParameterNotNullStandardMessage("paymentInfoId", paymentInfoId);

		final CartModel cartModel = getCart();
		if (checkIfCurrentUserIsTheCartUser())
		{
			if (StringUtils.isNotBlank(paymentInfoId))
			{

				final Map queryParams = new HashMap();
				queryParams.put("customer", getCurrentUserForCheckout());
				queryParams.put("duplicate", Boolean.FALSE);
				queryParams.put("pk", PK.parse(paymentInfoId));
				final SearchResult result = getFlexibleSearchService().search(
						"SELECT {pk} FROM {AdyenPaymentInfo} WHERE {user} = ?customer AND {pk} = ?pk AND {duplicate} = ?duplicate",
						queryParams);
				final AdyenPaymentInfoModel adyenPaymentInfoModel = result.getCount() <= 0 ? null : (AdyenPaymentInfoModel) result
						.getResult().get(0);
				if (adyenPaymentInfoModel != null)
				{
					return getCommerceCheckoutService().setPaymentInfo(cartModel, adyenPaymentInfoModel);
				}
			}
		}

		return false;
	}

	@Override
	public CartData getCheckoutCart()
	{
		final CartData cartData = getCartFacade().getSessionCart();
		if (cartData != null)
		{
			cartData.setDeliveryAddress(getDeliveryAddress());
			cartData.setDeliveryMode(getDeliveryMode());
			cartData.setPaymentInfo(getAdyenPaymentDetails());
		}

		return cartData;
	}

	public String getBoletoUrl()
	{
		final CartModel cart = getCart();
		for (final PaymentTransactionModel pt : cart.getPaymentTransactions())
		{
			for (final PaymentTransactionEntryModel pte : pt.getEntries())
			{
				if (pte instanceof AdyenPaymentTransactionEntryModel)
				{
					if (pte.getType().equals(PaymentTransactionType.AUTHORIZATION_REQUESTED))
					{
						if (StringUtils.isNotBlank(((AdyenPaymentTransactionEntryModel) pte).getAdyenBoloToPDFUrl()))
						{
							return ((AdyenPaymentTransactionEntryModel) pte).getAdyenBoloToPDFUrl();
						}
					}
				}
			}
		}
		return null;
	}

	protected CCPaymentInfoData getAdyenPaymentDetails()
	{
		final CartModel cart = getCart();
		if (cart != null)
		{
			final PaymentInfoModel paymentInfo = cart.getPaymentInfo();
			if (paymentInfo instanceof AdyenPaymentInfoModel)
			{
				return getAdyenPaymentInfoConverter().convert((AdyenPaymentInfoModel) paymentInfo);
			}
		}

		return null;
	}

	public AdyenPaymentInfoData createAdyenPaymentSubscription(final AdyenPaymentInfoData paymentInfoData)
	{
		validateParameterNotNullStandardMessage("paymentInfoData", paymentInfoData);
		final AddressData billingAddressData = paymentInfoData.getBillingAddress();
		validateParameterNotNullStandardMessage("billingAddress", billingAddressData);

		if (checkIfCurrentUserIsTheCartUser())
		{
			final CardInfo cardInfo = new CardInfo();
			if (paymentInfoData.isUseBoleto())
			{
				validateParameterNotNullStandardMessage("boletoPaymentInfo", paymentInfoData.getBoletoPaymentInfo());
			}
			else if (!paymentInfoData.isUseHPP())
			{
				cardInfo.setCardHolderFullName(paymentInfoData.getAccountHolderName());
				cardInfo.setCardNumber(paymentInfoData.getCardNumber());
				final CardType cardType = getCommerceCardTypeService().getCardTypeForCode(paymentInfoData.getCardType());
				final CreditCardType creditCardType = (cardType == null ? CreditCardType.VISA : cardType.getCode());
				cardInfo.setCardType(creditCardType);
				cardInfo.setExpirationMonth(Integer.valueOf(paymentInfoData.getExpiryMonth()));
				cardInfo.setExpirationYear(Integer.valueOf(paymentInfoData.getExpiryYear()));
				cardInfo.setIssueNumber(paymentInfoData.getIssueNumber());
			}

			final BillingInfo billingInfo = new BillingInfo();
			billingInfo.setCity(billingAddressData.getTown());
			if (billingAddressData.getCountry() != null)
			{
				billingInfo.setCountry(billingAddressData.getCountry().getIsocode());
			}
			billingInfo.setFirstName(billingAddressData.getFirstName());
			billingInfo.setLastName(billingAddressData.getLastName());
			billingInfo.setEmail(billingAddressData.getEmail());
			billingInfo.setPhoneNumber(billingAddressData.getPhone());
			billingInfo.setPostalCode(billingAddressData.getPostalCode());
			billingInfo.setStreet1(billingAddressData.getLine1());
			billingInfo.setStreet2(billingAddressData.getLine2());

			final CustomerModel customerModel = getCurrentUserForCheckout();

			final AdyenPaymentInfoModel paymentInfoModel = ((AdyenCustomerAccountService) getCustomerAccountService())
					.createAdyenPaymentSubscription(customerModel, cardInfo, billingInfo, billingAddressData.getTitleCode(),
							getPaymentProvider(), paymentInfoData.isSaved());

			if (paymentInfoModel != null)
			{
				//ady-18
				if (StringUtils.isNotBlank(paymentInfoData.getRecurringDetailReference()))
				{
					paymentInfoModel.setRecurringDetailReference(paymentInfoData.getRecurringDetailReference());
					paymentInfoModel.setShopperIp(paymentInfoData.getShopperIp());
				}
				else
				{
					paymentInfoModel.setCardEncryptedJson(paymentInfoData.getCardEncryptedJson());
					paymentInfoModel.setShopperIp(paymentInfoData.getShopperIp());
					paymentInfoModel.setSavePayment(paymentInfoData.isSavePayment());
				}
				if (StringUtils.isNotBlank(paymentInfoData.getInstallments()))
				{
					paymentInfoModel.setInstallments(new Integer(paymentInfoData.getInstallments()));
				}
				if (paymentInfoData.isUseBoleto())
				{
					paymentInfoModel.setFirstName(paymentInfoData.getBoletoPaymentInfo().getFirstName());
					paymentInfoModel.setLastName(paymentInfoData.getBoletoPaymentInfo().getLastName());
					paymentInfoModel.setSelectedBrand(paymentInfoData.getBoletoPaymentInfo().getSelectedBrand());
					paymentInfoModel.setSocialSecurityNumber(paymentInfoData.getBoletoPaymentInfo().getSocialSecurityNumber());
					paymentInfoModel.setShopperStatement(paymentInfoData.getBoletoPaymentInfo().getShopperStatement());
				}
				if (paymentInfoData.isUseHPP())
				{
					paymentInfoModel.setUseHPP(true);
					paymentInfoModel.setAdyenPaymentBrand(paymentInfoData.getAdyenPaymentBrand());
					paymentInfoModel.setHppURL(getHPPUrl(StringUtils.isNotEmpty(paymentInfoData.getAdyenPaymentBrand())));
					paymentInfoModel.setIssuerId(paymentInfoData.getIssuerId());
				}
				getModelService().save(paymentInfoModel);
				getModelService().refresh(paymentInfoModel);
				return getAdyenPaymentInfoConverter().convert(paymentInfoModel);
			}
		}

		return null;
	}

	private String getHPPUrl(final boolean withBrandCode)
	{
		if (withBrandCode)
		{
			return getConfigurationService().getConfiguration().getString("integration.adyen.hpp.details.url");
		}
		return getConfigurationService().getConfiguration().getString("integration.adyen.hpp.pay.url");
	}

	@Override
	public CartModel getCartModel()
	{
		return getCart();
	}

	/**
	 * @return the cmsSiteService
	 */
	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	/**
	 * @param cmsSiteService
	 *           the cmsSiteService to set
	 */
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	/**
	 * @return the adyenService
	 */
	public AdyenService getAdyenService()
	{
		return adyenService;
	}

	/**
	 * @param adyenService
	 *           the adyenService to set
	 */
	public void setAdyenService(final AdyenService adyenService)
	{
		this.adyenService = adyenService;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see com.adyen.storefront.facades.ExtCheckoutFacade#storeCVC(java.lang.String)
	 */
	@Override
	public void storeCVC(final String CVC)
	{
		sessionService.setAttribute("STORED_RECURRING_CARD_CVC", CVC);

	}

	@Override
	public void clearCVC()
	{
		sessionService.removeAttribute("STORED_RECURRING_CARD_CVC");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.adyen.storefront.facades.ExtCheckoutFacade#isPaymentUthorized()
	 */
	@Override
	public boolean isPaymentUthorized()
	{
		final CartModel cart = getCart();
		for (final PaymentTransactionModel payment : cart.getPaymentTransactions())
		{
			for (final PaymentTransactionEntryModel entry : payment.getEntries())
			{
				if ((entry.getType().equals(PaymentTransactionType.AUTHORIZATION_REQUESTED) || entry.getType().equals(
						PaymentTransactionType.AUTHORIZATION_REQUESTED_3DSECURE))
						&& (ResultCode.Authorised.name().equals(entry.getTransactionStatus()) || ResultCode.Received.name().equals(
								entry.getTransactionStatus())))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getSessionCVC()
	{
		final Object o = sessionService.getAttribute("STORED_RECURRING_CARD_CVC");
		if (null != o)
		{
			return (String) o;
		}
		return null;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the adyenPaymentInfoConverter
	 */
	public Converter<AdyenPaymentInfoModel, AdyenPaymentInfoData> getAdyenPaymentInfoConverter()
	{
		return adyenPaymentInfoConverter;
	}

	/**
	 * @param adyenPaymentInfoConverter
	 *           the adyenPaymentInfoConverter to set
	 */
	public void setAdyenPaymentInfoConverter(final Converter<AdyenPaymentInfoModel, AdyenPaymentInfoData> adyenPaymentInfoConverter)
	{
		this.adyenPaymentInfoConverter = adyenPaymentInfoConverter;
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}







}
