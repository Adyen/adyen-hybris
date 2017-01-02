/**
 *
 */
package com.adyen.services.integration.data.request;

import java.math.BigDecimal;
import java.util.Currency;

import org.apache.commons.lang.StringUtils;

import com.adyen.services.integration.data.AdditionalData;
import com.adyen.services.integration.data.AmountData;
import com.adyen.services.integration.data.BrowserInfo;
import com.adyen.services.integration.data.CardData;
import com.adyen.services.integration.data.ContractType;
import com.adyen.services.integration.data.Installments;
import com.adyen.services.integration.data.RecurringData;
import com.adyen.services.integration.data.ShopperName;


/**
 * @author delli
 * 
 */
public class AdyenPaymentRequestBuilder
{
	private AdyenPaymentRequest paymentRequest;

	public AdyenPaymentRequest getPaymentRequest()
	{
		return paymentRequest;
	}

	public void createBaseRequest(final String cartGuid, final String shopperEmail, final String shopperIp,
			final String merchantAccount, final String shopperReference)
	{
		paymentRequest = new AdyenPaymentRequest();
		paymentRequest.setReference(cartGuid);
		paymentRequest.setShopperEmail(shopperEmail);
		paymentRequest.setShopperIP(shopperIp);
		paymentRequest.setMerchantAccount(merchantAccount);
		paymentRequest.setShopperReference(shopperReference);
		paymentRequest.setFraudOffset(new Integer(0));
	}

	public void create3DRequest(final String cartGuid, final String shopperIp, final String merchantAccount,
			final String paResponse, final String md)
	{
		paymentRequest = new AdyenPaymentRequest();
		paymentRequest.setReference(cartGuid);
		paymentRequest.setShopperIP(shopperIp);
		paymentRequest.setMerchantAccount(merchantAccount);
		paymentRequest.setMd(md);
		paymentRequest.setPaResponse(paResponse);
	}

	public void amount(final BigDecimal amount, final Currency currency)
	{
		final AmountData amountData = new AmountData();
		amountData.setCurrency(currency.getCurrencyCode());
		amountData.setValue(new Integer(new BigDecimal(100).multiply((amount == null) ? BigDecimal.ZERO : amount).intValue()));
		paymentRequest.setAmount(amountData);
	}

	public void cardNeedUpdate(final boolean updateRecurringCondition, final String securityCode, final String expiryMonth,
			final String expiryYear, final String recurringReference)
	{
		if (updateRecurringCondition)
		{
			final CardData card = new CardData();
			card.setCvc(securityCode);
			card.setExpiryMonth(expiryMonth);
			card.setExpiryYear(expiryYear);
			paymentRequest.setCard(card);
			paymentRequest.setSelectedRecurringDetailReference(recurringReference);
			paymentRequest.setShopperInteraction(AdyenPaymentRequest.SHOPPER_INTERACTION_DEFAULT_VALUE);
		}
	}

	public void cardEncryptedJson(final String cardEncryptedJson)
	{
		if (cardEncryptedJson != null)
		{
			AdditionalData additionalData = null;
			if (paymentRequest.getAdditionalData() == null)
			{
				additionalData = new AdditionalData();
			}
			else
			{
				additionalData = paymentRequest.getAdditionalData();
			}
			additionalData.setCardEncryptedJson(cardEncryptedJson);
			paymentRequest.setAdditionalData(additionalData);
		}
	}

	public void recurringAndOneclick(final boolean condition)
	{
		if (condition)
		{
			final RecurringData recurring = new RecurringData();
			recurring.setContract(ContractType.RECURRING.name() + "," + ContractType.ONECLICK.name());
			paymentRequest.setRecurring(recurring);
		}
	}

	public void installments(final Integer installments)
	{
		if (installments != null && installments.intValue() > 0)
		{
			final Installments installmentsData = new Installments();
			installmentsData.setValue(installments.intValue());
			paymentRequest.setInstallments(installmentsData);

		}
	}

	public void browserInfo(final String userAgent, final String acceptHeader)
	{
		final BrowserInfo browserInfo = new BrowserInfo();
		browserInfo.setUserAgent(StringUtils.isNotEmpty(userAgent) ? userAgent : "User-Agent");
		browserInfo.setAcceptHeader(StringUtils.isNotEmpty(acceptHeader) ? userAgent : "Accept");
		paymentRequest.setBrowserInfo(browserInfo);
	}

	public void boletoInfo(final String firstName, final String lastName, final String ssn, final String selectedBrand,
			final String shopperStatement)
	{
		final ShopperName name = new ShopperName();
		name.setFirstName(firstName);
		name.setLastName(lastName);
		paymentRequest.setShopperName(name);
		paymentRequest.setSelectedBrand(selectedBrand);
		paymentRequest.setSocialSecurityNumber(ssn);
		paymentRequest.setShopperStatement(shopperStatement);
	}
}
