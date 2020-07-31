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
import de.hybris.platform.processengine.action.AbstractAction;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class AdyenCheckPendingOrderAction extends AbstractAction<OrderProcessModel> {
	private static final Logger LOG = Logger.getLogger(AdyenCheckPendingOrderAction.class);

	public enum Transition {
		OK, NOK;

		public static Set<String> getStringValues() {
			Set<String> res = new HashSet<>();
			for (final Transition transitions : Transition.values()) {
				res.add(transitions.toString());
			}
			return res;
		}
	}

	@Override
	public Set<String> getTransitions() {
		return AdyenCheckPendingOrderAction.Transition.getStringValues();
	}

	@Override
	public String execute(final OrderProcessModel process) {
		final OrderModel order = process.getOrder();

		if (order == null) {
			LOG.error("Missing the order, exiting the process");
			return Transition.NOK.toString();
		}

		if (OrderStatus.PAYMENT_PENDING.equals(order.getStatus())) {
			LOG.debug("Process: " + process.getCode()
					+ " Order still on Payment Pending, will update status to PROCESSING_ERROR...");
			order.setStatus(OrderStatus.PROCESSING_ERROR);
			modelService.save(order);
			return Transition.NOK.toString();

		} else if (OrderStatus.CANCELLED.equals(order.getStatus())
				|| OrderStatus.PROCESSING_ERROR.equals(order.getStatus())) {
			LOG.debug("Process: " + process.getCode() + " Order Cancelled");
			return Transition.NOK.toString();
		}

		return Transition.OK.toString();
	}
}
