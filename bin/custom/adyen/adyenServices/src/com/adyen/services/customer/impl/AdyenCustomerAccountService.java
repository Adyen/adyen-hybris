/**
 * 
 */
package com.adyen.services.customer.impl;

import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.NewSubscription;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Currency;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.adyen.services.customer.ExtCustomerAccountService;


/**
 * @author Kenneth Zhou
 * 
 */
public class AdyenCustomerAccountService extends DefaultCustomerAccountService implements ExtCustomerAccountService
{
	private static final Logger LOG = Logger.getLogger(AdyenCustomerAccountService.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.adyen.services.customer.ExtCustomerAccountService#createAdyenPaymentSubscription(de.hybris.platform.core.model
	 * .user.CustomerModel, de.hybris.platform.payment.dto.CardInfo, de.hybris.platform.payment.dto.BillingInfo,
	 * java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public AdyenPaymentInfoModel createAdyenPaymentSubscription(final CustomerModel customerModel, final CardInfo cardInfo,
			final BillingInfo billingInfo, final String titleCode, final String paymentProvider, final boolean saveInAccount)
	{
		ServicesUtil.validateParameterNotNull(customerModel, "Customer cannot be null");
		ServicesUtil.validateParameterNotNull(paymentProvider, "PaymentProvider cannot be null");
		final CurrencyModel currencyModel = getCurrency(customerModel);
		ServicesUtil.validateParameterNotNull(currencyModel, "Customer session currency cannot be null");
		final Currency currency = getI18nService().getBestMatchingJavaCurrency(currencyModel.getIsocode());
		final AddressModel billingAddress = (AddressModel) getModelService().create(AddressModel.class);
		if (StringUtils.isNotBlank(titleCode))
		{
			final TitleModel title = new TitleModel();
			title.setCode(titleCode);
			billingAddress.setTitle(getFlexibleSearchService().getModelByExample(title));
		}
		billingAddress.setFirstname(billingInfo.getFirstName());
		billingAddress.setLastname(billingInfo.getLastName());
		billingAddress.setLine1(billingInfo.getStreet1());
		billingAddress.setLine2(billingInfo.getStreet2());
		billingAddress.setTown(billingInfo.getCity());
		billingAddress.setPostalcode(billingInfo.getPostalCode());
		billingAddress.setCountry(getCommonI18NService().getCountry(billingInfo.getCountry()));
		final String email = getCustomerEmailResolutionService().getEmailForCustomer(customerModel);
		billingAddress.setEmail(email);
		final String merchantTransactionCode = (new StringBuilder(String.valueOf(customerModel.getUid()))).append("-")
				.append(UUID.randomUUID()).toString();
		try
		{
			final NewSubscription subscription = getPaymentService().createSubscription(merchantTransactionCode, paymentProvider,
					currency, billingAddress, cardInfo);
			if (StringUtils.isNotBlank(subscription.getSubscriptionID()))
			{
				final AdyenPaymentInfoModel adyenPaymentInfoModel = (AdyenPaymentInfoModel) getModelService().create(
						AdyenPaymentInfoModel.class);
				adyenPaymentInfoModel.setCode((new StringBuilder(String.valueOf(customerModel.getUid()))).append("_")
						.append(UUID.randomUUID()).toString());
				adyenPaymentInfoModel.setUser(customerModel);
				adyenPaymentInfoModel.setSubscriptionId(subscription.getSubscriptionID());
				adyenPaymentInfoModel.setNumber(getMaskedCardNumber(cardInfo.getCardNumber()));
				adyenPaymentInfoModel.setType(cardInfo.getCardType());
				adyenPaymentInfoModel.setCcOwner(cardInfo.getCardHolderFullName());
				adyenPaymentInfoModel.setValidToMonth(String.format("%02d", new Object[]
				{ cardInfo.getExpirationMonth() }));
				adyenPaymentInfoModel.setValidToYear(String.valueOf(cardInfo.getExpirationYear()));
				if (cardInfo.getIssueMonth() != null)
				{
					adyenPaymentInfoModel.setValidFromMonth(String.valueOf(cardInfo.getIssueMonth()));
				}
				if (cardInfo.getIssueYear() != null)
				{
					adyenPaymentInfoModel.setValidFromYear(String.valueOf(cardInfo.getIssueYear()));
				}
				adyenPaymentInfoModel.setSubscriptionId(subscription.getSubscriptionID());
				adyenPaymentInfoModel.setSaved(saveInAccount);
				if (!StringUtils.isEmpty(cardInfo.getIssueNumber()))
				{
					adyenPaymentInfoModel.setIssueNumber(Integer.valueOf(cardInfo.getIssueNumber()));
				}
				billingAddress.setOwner(adyenPaymentInfoModel);
				adyenPaymentInfoModel.setBillingAddress(billingAddress);
				getModelService().saveAll(new Object[]
				{ billingAddress, adyenPaymentInfoModel });
				getModelService().refresh(customerModel);
				addPaymentInfo(customerModel, adyenPaymentInfoModel);
				return adyenPaymentInfoModel;
			}
		}
		catch (final AdapterException ae)
		{
			LOG.error(String.format("Failed to create subscription for customer %s due to error of [%s]", new Object[]
			{ customerModel.getUid(), ae.getMessage() }));
			return null;
		}
		return null;
	}

}
