/**
 * 
 */
package com.adyen.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;

import com.adyen.facades.order.data.AdyenPaymentInfoData;


/**
 * @author Kenneth Zhou
 * 
 */
public class AdyenPaymentInfoPopulator implements Populator<AdyenPaymentInfoModel, AdyenPaymentInfoData>
{
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<CreditCardType, CardTypeData> cardTypeConverter;

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected Converter<CreditCardType, CardTypeData> getCardTypeConverter()
	{
		return cardTypeConverter;
	}

	@Required
	public void setCardTypeConverter(final Converter<CreditCardType, CardTypeData> cardTypeConverter)
	{
		this.cardTypeConverter = cardTypeConverter;
	}

	@Override
	public void populate(final AdyenPaymentInfoModel source, final AdyenPaymentInfoData target)
	{
		target.setId(source.getPk().toString());
		target.setCardNumber(source.getNumber());

		if (source.getType() != null)
		{
			final CardTypeData cardTypeData = getCardTypeConverter().convert(source.getType());
			target.setCardType(cardTypeData.getCode());
			target.setCardTypeData(cardTypeData);
		}

		target.setAccountHolderName(source.getCcOwner());
		target.setExpiryMonth(source.getValidToMonth());
		target.setExpiryYear(source.getValidToYear());
		target.setStartMonth(source.getValidFromMonth());
		target.setStartYear(source.getValidFromYear());

		target.setSubscriptionId(source.getSubscriptionId());
		target.setSaved(source.isSaved());

		target.setUseHPP(source.isUseHPP());
		target.setAdyenPaymentBrand(source.getAdyenPaymentBrand());
		target.setIssuerId(source.getIssuerId());
		target.setHppURL(source.getHppURL());
		target.setBoletoPdfUrl(source.getAdyenBoletoPDFUrl());

		if (source.getBillingAddress() != null)
		{
			target.setBillingAddress(getAddressConverter().convert(source.getBillingAddress()));
		}
		if (source.getIssueNumber() != null)
		{
			target.setIssueNumber(source.getIssueNumber().toString());
		}
	}
}
