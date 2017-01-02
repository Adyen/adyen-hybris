/**
 *
 */
package com.adyen.services.impl;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.adyen.services.AdyenPaymentService;


/**
 * @author delli
 *
 */
public class AdyenCommerceCheckoutService extends DefaultCommerceCheckoutService
{
	private SessionService sessionService;
	private static final Logger LOG = Logger.getLogger(AdyenCommerceCheckoutService.class);

	public PaymentTransactionEntryModel authorizeAdyenPaymentAmount(final CartModel cartModel, final String securityCode,
			final String paymentProvider, BigDecimal amount)
	{
		ServicesUtil.validateParameterNotNull(cartModel, "Cart model cannot be null");
		ServicesUtil.validateParameterNotNull(cartModel.getPaymentInfo(), "Payment information on cart cannot be null");
		if (null == amount)
		{
			final Double totalPrice = cartModel.getTotalPrice();
			final Double totalTax = !cartModel.getNet().booleanValue() || cartModel.getStore() == null
					|| !cartModel.getStore().getExternalTaxEnabled().booleanValue() ? Double.valueOf(0.0D) : cartModel.getTotalTax();
			final BigDecimal totalPriceWithoutTaxBD = (new BigDecimal(totalPrice != null ? totalPrice.doubleValue() : 0.0D))
					.setScale(2, RoundingMode.HALF_EVEN);
			final BigDecimal totalPriceBD = (new BigDecimal(totalTax != null ? totalTax.doubleValue() : 0.0D)).setScale(2,
					RoundingMode.HALF_EVEN).add(totalPriceWithoutTaxBD);
			amount = totalPriceBD;
		}

		((AdyenPaymentService) getPaymentService()).maybeClearAuthorizeHistory(cartModel);
		PaymentTransactionEntryModel transactionEntryModel = null;
		final PaymentInfoModel paymentInfo = cartModel.getPaymentInfo();
		if ((paymentInfo instanceof AdyenPaymentInfoModel)
				&& (StringUtils.isNotBlank(((AdyenPaymentInfoModel) paymentInfo).getSubscriptionId())))
		{
			final Currency currency = getI18nService().getBestMatchingJavaCurrency(cartModel.getCurrency().getIsocode());

			transactionEntryModel = ((AdyenPaymentService) getPaymentService()).authorize(amount, currency, cartModel,
					((AdyenPaymentInfoModel) paymentInfo), securityCode,
					(String) sessionService.getAttribute("userAgent"), (String) sessionService.getAttribute("accept"));
			sessionService.removeAttribute("userAgent");
			sessionService.removeAttribute("accept");
		}
		return transactionEntryModel;
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
}
