/**
 * 
 */
package com.adyen.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.OrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import com.adyen.facades.order.data.AdyenPaymentInfoData;


/**
 * @author Kenneth Zhou
 * 
 */
public class ExtOrderPopulator extends OrderPopulator
{
	private Converter<AdyenPaymentInfoModel, AdyenPaymentInfoData> adyenPaymentInfoConverter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator#addPaymentInformation(de.
	 * hybris.platform.core.model.order.AbstractOrderModel,
	 * de.hybris.platform.commercefacades.order.data.AbstractOrderData)
	 */
	@Override
	protected void addPaymentInformation(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		// YTODO Auto-generated method stub
		super.addPaymentInformation(source, prototype);
		final PaymentInfoModel paymentInfo = source.getPaymentInfo();
		if (paymentInfo instanceof AdyenPaymentInfoModel)
		{
			final AdyenPaymentInfoData paymentInfoData = getAdyenPaymentInfoConverter().convert((AdyenPaymentInfoModel) paymentInfo);

			final boolean isDefaultPayment = isDefaultPaymentInfo(source.getUser(), paymentInfo);
			paymentInfoData.setDefaultPaymentInfo(isDefaultPayment);
			prototype.setPaymentInfo(paymentInfoData);
		}
	}

	private boolean isDefaultPaymentInfo(final UserModel abstractOrderUser, final PaymentInfoModel abstractOrderPayment)
	{
		boolean isDefault = false;
		if (abstractOrderUser instanceof CustomerModel)
		{
			final PaymentInfoModel defaultPaymentInfo = ((CustomerModel) abstractOrderUser).getDefaultPaymentInfo();
			if (defaultPaymentInfo != null)
			{
				if (abstractOrderPayment.equals(defaultPaymentInfo))
				{
					isDefault = true;
				}
			}
		}

		return isDefault;
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

}
