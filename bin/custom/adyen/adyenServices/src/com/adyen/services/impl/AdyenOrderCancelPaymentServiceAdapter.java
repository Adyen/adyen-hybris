/**
 *
 */
package com.adyen.services.impl;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.OrderCancelPaymentServiceAdapter;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import com.adyen.services.AdyenPaymentService;


/**
 * @author delli
 *
 */
public class AdyenOrderCancelPaymentServiceAdapter implements OrderCancelPaymentServiceAdapter
{
	private CalculationService calculationService;
	private AdyenPaymentService adyenPaymentService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.ordercancel.OrderCancelPaymentServiceAdapter#recalculateOrderAndModifyPayments(de.hybris.
	 * platform.core.model.order.OrderModel)
	 */
	@Override
	public void recalculateOrderAndModifyPayments(final OrderModel ordermodel)
	{
		Double modificationAmount = null;

		try
		{
			//partial cancel order
			if (!OrderStatus.CANCELLED.getCode().equals(ordermodel.getStatus().getCode()))
			{
				final Double originalPrice = ordermodel.getTotalPrice();
				calculationService.recalculate(ordermodel);
				final Double totalPrice = ordermodel.getTotalPrice();
				modificationAmount = new Double(originalPrice.doubleValue() - totalPrice.doubleValue());
			}
		}
		catch (final CalculationException e)
		{
			e.printStackTrace();
		}

		final PaymentTransactionModel transaction = ordermodel.getPaymentTransactions().get(0);
		getAdyenPaymentService().cancelOrRefund(transaction, ordermodel.getCode(), ordermodel.getCurrency(), modificationAmount);
	}

	/**
	 * @return the calculationService
	 */
	public CalculationService getCalculationService()
	{
		return calculationService;
	}

	/**
	 * @param calculationService
	 *           the calculationService to set
	 */
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	/**
	 * @return the adyenPaymentService
	 */
	public AdyenPaymentService getAdyenPaymentService()
	{
		return adyenPaymentService;
	}

	/**
	 * @param adyenPaymentService
	 *           the adyenPaymentService to set
	 */
	public void setAdyenPaymentService(final AdyenPaymentService adyenPaymentService)
	{
		this.adyenPaymentService = adyenPaymentService;
	}

}
