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

import com.adyen.v6.actions.AbstractWaitableAction;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import org.apache.log4j.Logger;

public class AdyenCheckNewOrderAction extends AbstractWaitableAction<OrderProcessModel> {
	private static final Logger LOG = Logger.getLogger(AdyenCheckNewOrderAction.class);

	@Override
	public String execute(final OrderProcessModel process) {
		LOG.debug("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final OrderModel order = process.getOrder();

		if (order == null) {
			LOG.error("Missing the order, exiting the process");
			return Transition.NOK.toString();
		}

		if (OrderStatus.PAYMENT_PENDING.equals(order.getStatus())) {
			LOG.debug("Process: " + process.getCode() + " Order Payment Pending, will wait...");
			return Transition.WAIT.toString();

		} else if (OrderStatus.CANCELLED.equals(order.getStatus())
				|| OrderStatus.PROCESSING_ERROR.equals(order.getStatus())) {
			LOG.debug("Process: " + process.getCode() + " Order Cancelled");
			return Transition.NOK.toString();
		}

		return Transition.OK.toString();
	}
}
