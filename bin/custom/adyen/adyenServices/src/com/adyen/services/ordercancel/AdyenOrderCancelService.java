/**
 *
 */
package com.adyen.services.ordercancel;

import de.hybris.platform.basecommerce.constants.GeneratedBasecommerceConstants.Enumerations.OrderCancelEntryStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.CancelDecision;
import de.hybris.platform.ordercancel.OrderCancelDenialStrategy;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.adyen.services.AdyenPaymentService;


/**
 * @author delli
 *
 */
public class AdyenOrderCancelService extends DefaultOrderCancelService
{
	private AdyenPaymentService adyenPaymentService;
	private CalculationService calculationService;

	@Override
	public OrderCancelRecordEntryModel requestOrderCancel(final OrderCancelRequest orderCancelRequest,
			final PrincipalModel requestor) throws OrderCancelException
	{
		final OrderCancelRecordEntryModel orderRequestRecord = super.requestOrderCancel(orderCancelRequest, requestor);
		Double modificationAmount = null;
		final OrderModel order = orderCancelRequest.getOrder();
		final Double originalPrice = order.getTotalPrice();
		try
		{
			if (OrderCancelEntryStatus.PARTIAL.equals(orderRequestRecord.getCancelResult().getCode()))
			{
				calculationService.recalculate(order);
				final Double totalPrice = order.getTotalPrice();
				modificationAmount = new Double(originalPrice.doubleValue() - totalPrice.doubleValue());
			}
		}
		catch (final CalculationException e)
		{
			e.printStackTrace();
		}
		final PaymentTransactionModel transaction = order.getPaymentTransactions().get(0);
		getAdyenPaymentService().cancelOrRefund(transaction, order.getCode(), order.getCurrency(), modificationAmount);
		return orderRequestRecord;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.ordercancel.impl.DefaultOrderCancelService#isCancelPossible(de.hybris.platform.core.model.order
	 * .OrderModel, de.hybris.platform.core.model.security.PrincipalModel, boolean, boolean)
	 */
	@Override
	public CancelDecision isCancelPossible(final OrderModel order, final PrincipalModel requestor, final boolean partialCancel,
			final boolean partialEntryCancel)
	{
		final OrderCancelConfigModel configuration = getConfiguration();
		final List reasons = new ArrayList();
		for (final Iterator iterator = getCancelDenialStrategies().iterator(); iterator.hasNext();)
		{
			final OrderCancelDenialStrategy ocas = (OrderCancelDenialStrategy) iterator.next();
			final de.hybris.platform.ordercancel.OrderCancelDenialReason result = ocas.getCancelDenialReason(configuration, order,
					requestor, partialCancel, partialEntryCancel);
			if (result != null)
			{
				reasons.add(result);
			}
		}
		return super.isCancelPossible(order, requestor, partialCancel, partialEntryCancel);
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

}
