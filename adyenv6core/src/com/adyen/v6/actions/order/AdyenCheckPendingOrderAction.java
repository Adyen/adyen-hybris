/*
 *                       ######
 *                       ######
 * ############    ####( ######  #####. ######  ############   ############
 * #############  #####( ######  #####. ######  #############  #############
 *        ######  #####( ######  #####. ######  #####  ######  #####  ######
 * ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 * ###### ######  #####( ######  #####. ######  #####          #####  ######
 * #############  #############  #############  #############  #####  ######
 *  ############   ############  #############   ############  #####  ######
 *                                      ######
 *                               #############
 *                               ############
 *
 * Adyen Hybris Extension
 *
 * Copyright (c) 2020 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 */

package com.adyen.v6.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import org.apache.log4j.Logger;

public class AdyenCheckPendingOrderAction extends AbstractSimpleDecisionAction<OrderProcessModel> {
	private static final Logger LOG = Logger.getLogger(AdyenCheckPendingOrderAction.class);

	@Override
	public Transition executeAction(OrderProcessModel process) {
		LOG.debug("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final OrderModel order = process.getOrder();

		if (order == null) {
			LOG.error("Missing the order, exiting the process");
			return Transition.NOK;
		}

		if (OrderStatus.PAYMENT_PENDING.equals(order.getStatus()) && order.getPaymentTransactions().isEmpty()) {
			LOG.debug("Process: " + process.getCode()
					+ " Order still on Payment Pending, will update status to PROCESSING_ERROR...");
			order.setStatus(OrderStatus.PROCESSING_ERROR);
			modelService.save(order);
			return Transition.NOK;

		} else if (OrderStatus.CANCELLED.equals(order.getStatus())
				|| OrderStatus.PROCESSING_ERROR.equals(order.getStatus())) {
			LOG.debug("Process: " + process.getCode() + " Order Cancelled");
			return Transition.NOK;
		}

		return Transition.OK;
	}
}
