/**
 * 
 */
package com.adyen.services.customer;

import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;



/**
 * @author Kenneth Zhou
 * 
 */
public interface ExtCustomerAccountService
{
	public AdyenPaymentInfoModel createAdyenPaymentSubscription(CustomerModel customerModel, CardInfo cardInfo,
			BillingInfo billingInfo, String titleCode, String paymentProvider, boolean saveInAccount);
}
