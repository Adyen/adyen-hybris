package com.adyen.cscockpit.widgets.renderers.impl;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.adyen.services.AdyenPaymentService;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.widgets.ListboxWidget;
import de.hybris.platform.cockpit.widgets.models.impl.DefaultListWidgetModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cscockpit.utils.LabelUtils;
import de.hybris.platform.cscockpit.utils.SafeUnbox;
import de.hybris.platform.cscockpit.widgets.controllers.ReturnsController;
import de.hybris.platform.cscockpit.widgets.renderers.impl.RefundConfirmationWidgetRenderer;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.refund.RefundService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class AdyenRefundConfirmationWidgetRenderer extends RefundConfirmationWidgetRenderer {

	private AdyenPaymentService adyenPaymentService;
	private CalculationService calculationService;
	private RefundService refundService;
	private ModelService modelService;

	@Override
	protected void handleRefundConfirmedEvent(
			ListboxWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget, Event event)
					throws Exception {
		double amount = getRefundAmount(widget);
		TypedObject returnRequest = ((ReturnsController) widget.getWidgetController()).createRefundRequest();
		if (returnRequest != null) {
			if (getPopupWidgetHelper().getCurrentPopup() != null) {
				List<Component> children = getPopupWidgetHelper().getCurrentPopup().getParent().getChildren();
				for (Component c : children) {
					if (!(c instanceof Window))
						continue;
					Events.postEvent(new Event("onClose", c));
				}
			}

			ReturnRequestModel returnRequestModel = (ReturnRequestModel) returnRequest.getObject();
			Messagebox.show(LabelUtils.getLabel(widget, "rmaNumber", new Object[] { returnRequestModel.getRMA() }),
					LabelUtils.getLabel(widget, "rmaNumberTitle", new Object[0]), 1, "z-msgbox z-msgbox-information");

			((ReturnsController) widget.getWidgetController()).dispatchEvent(null, widget, null);
		} else {
			Messagebox.show(LabelUtils.getLabel(widget, "error", new Object[0]),
					LabelUtils.getLabel(widget, "failed", new Object[0]), 1, "z-msgbox z-msgbox-error");
		}

		// invoke adyen refund api
		OrderModel order = (OrderModel) ((ReturnsController) widget.getWidgetController()).getCurrentOrder()
				.getObject();

		getAdyenPaymentService().cancelOrRefund(order.getPaymentTransactions().get(0),
				((ReturnRequestModel) returnRequest.getObject()).getCode(), order.getCurrency(), amount);
	}

	private double getRefundAmount(ListboxWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget) {
		TypedObject refundOrder = ((ReturnsController) widget.getWidgetController()).getRefundOrderPreview();
		if (refundOrder != null) {
			OrderModel originalOrderModel = (OrderModel) ((ReturnsController) widget.getWidgetController())
					.getCurrentOrder().getObject();
			double originalPrice = SafeUnbox.toDouble(originalOrderModel.getTotalPrice());
			double newTotal = SafeUnbox.toDouble(((OrderModel) refundOrder.getObject()).getTotalPrice());
			return originalPrice - newTotal;
		}
		return 0d;
	}

	public AdyenPaymentService getAdyenPaymentService() {
		return adyenPaymentService;
	}

	public void setAdyenPaymentService(AdyenPaymentService adyenPaymentService) {
		this.adyenPaymentService = adyenPaymentService;
	}

	/**
	 * @return the calculationService
	 */
	public CalculationService getCalculationService() {
		return calculationService;
	}

	/**
	 * @param calculationService
	 *            the calculationService to set
	 */
	public void setCalculationService(CalculationService calculationService) {
		this.calculationService = calculationService;
	}

	/**
	 * @return the refundService
	 */
	public RefundService getRefundService() {
		return refundService;
	}

	/**
	 * @param refundService
	 *            the refundService to set
	 */
	public void setRefundService(RefundService refundService) {
		this.refundService = refundService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService() {
		return modelService;
	}

	/**
	 * @param modelService
	 *            the modelService to set
	 */
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

}
