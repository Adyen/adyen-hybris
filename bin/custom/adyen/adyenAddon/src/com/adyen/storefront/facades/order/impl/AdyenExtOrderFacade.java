/**
 * 
 */
package com.adyen.storefront.facades.order.impl;

import de.hybris.platform.commercefacades.order.impl.DefaultOrderFacade;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.store.BaseStoreModel;

import org.apache.commons.lang.StringUtils;


/**
 * @author Kenneth Zhou
 * 
 */
public class AdyenExtOrderFacade extends DefaultOrderFacade
{
	public String getBoletoUrl(final String orderCode)
	{
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel order = getCheckoutCustomerStrategy().isAnonymousCheckout() ? getCustomerAccountService()
				.getOrderDetailsForGUID(orderCode, baseStoreModel) : getCustomerAccountService().getOrderForCode(
				(CustomerModel) getUserService().getCurrentUser(), orderCode, baseStoreModel);
		if (order != null)
		{
			for (final PaymentTransactionModel pt : order.getPaymentTransactions())
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
		}
		return null;
	}
}
