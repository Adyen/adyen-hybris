/**
 *
 */
package com.adyen.services;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedHashMap;



/**
 * @author Kenneth Zhou
 *
 */
public interface AdyenPaymentService
{
	public PaymentTransactionEntryModel authorize(final BigDecimal amount, final Currency currency, final CartModel cartModel,
			final AdyenPaymentInfoModel adyenPaymentInfoModel,
			final String securityCode, final String userAgent, final String accept);

	public PaymentTransactionEntryModel authorize3DSecure(final CartModel cartModel, final String paResponse,
			final String md, final String shopperIp, final String userAgent, final String accept);

	public PaymentTransactionEntryModel cancelOrRefund(final PaymentTransactionModel transaction, final String reference,
			final CurrencyModel currency, final Double amount);


	public void maybeClearAuthorizeHistory(CartModel cart);

	public PaymentTransactionEntryModel capture(final PaymentTransactionModel transaction, final String reference);

	void clearPaymentTransaction(final String code);

	PaymentTransactionModel createTransaction(final String cartCode);

	AdyenPaymentTransactionEntryModel createHPPAuthorisePTE(final String cartCode, final String pspReference, final String authResult);

	AdyenPaymentTransactionEntryModel createHPPAuthorisePTE(final AbstractOrderModel order, final String pspReference,
			final String authResult);

	public AbstractOrderModel getOrderByPSPReference(final String pspReference);

	public LinkedHashMap<String, String> buildHPPOpenInvoiceData();

	public LinkedHashMap<String, String> buildHPPOpenInvoiceData(final String merchantData);
}
