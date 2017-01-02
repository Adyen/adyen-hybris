/**
 *
 */
package com.adyen.services.integration;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.LinkedHashMap;

import com.adyen.services.integration.data.PaymentMethods;
import com.adyen.services.integration.data.request.AdyenListRecurringDetailsRequest;
import com.adyen.services.integration.data.request.AdyenModificationRequest;
import com.adyen.services.integration.data.request.AdyenPaymentRequest;
import com.adyen.services.integration.data.response.AdyenListRecurringDetailsResponse;
import com.adyen.services.integration.data.response.AdyenModificationResponse;


/**
 * @author Kenneth Zhou
 *
 */
public interface AdyenService
{
	public AdyenListRecurringDetailsResponse requestRecurringPaymentDetails(AdyenListRecurringDetailsRequest request);

	public AdyenPaymentTransactionEntryModel authorise(final PaymentTransactionModel transaction,
			final AdyenPaymentRequest request, final boolean is3DSecure);

	public AdyenPaymentTransactionEntryModel cancelOrRefund(final PaymentTransactionModel transaction,
			final AdyenModificationRequest request);

	public AdyenModificationResponse capturePayment(final AdyenModificationRequest request, final CMSSiteModel site);

	public PaymentMethods directory();

	String calculateHMAC(final String hmacKey, final String signingString);

	String buildOpenInvoiceDataSig(final String merchantSig, LinkedHashMap<String, String> openInvoiceData, String hmacKey);

	String compressString(final String input);

	String getCountryCode();

	LinkedHashMap<String, String> getDeliveryAddrData();

	LinkedHashMap<String, String> getBillingAddressData();

}
