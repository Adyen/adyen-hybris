package com.adyen.cscockpit.widgets.controllers.impl;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cscockpit.widgets.controllers.impl.DefaultOrderManagementActionsWidgetController;

public class AdyenOrderManagementActionsWidgetController extends DefaultOrderManagementActionsWidgetController {

	@Override
	public boolean isFullCancelPossible() {

		TypedObject order = getOrder();
		if (order == null || !(order.getObject() instanceof OrderModel)
				|| !StringUtils.isBlank(((OrderModel) order.getObject()).getVersionID())) {
			return false;
		}
		OrderModel orderModel = (OrderModel) order.getObject();
		if (OrderStatus.PAYMENT_AUTHORIZED.getCode().equals(orderModel.getStatus().getCode())
				|| OrderStatus.PAYMENT_AMOUNT_RESERVED.getCode().equals(orderModel.getStatus().getCode())
				|| OrderStatus.SUSPENDED.getCode().equals(orderModel.getStatus().getCode())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isPartialCancelPossible() {
		return isFullCancelPossible();
	}

}
