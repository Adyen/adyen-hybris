package com.adyen.fulfilmentprocess.actions.consignment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.adyen.services.AdyenPaymentService;
import com.adyen.services.constants.GeneratedAdyenServicesConstants.Attributes.CMSSite;
import com.adyen.services.model.AdyenPaymentMethodModel;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;

public class CapturePaymentAction extends AbstractAction<ConsignmentProcessModel> {

	private AdyenPaymentService adyenPaymentService;
	
	public enum Transition
	{
		OK, NOT_REQUIRED, ERROR;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}
	
	@Override
	public String execute(final ConsignmentProcessModel process)
	{
		String paymentMethod = getAdyenPaymentMethod(process.getConsignment().getOrder());
		AdyenPaymentMethodModel currentPaymentMethod = getCurrentPaymentMethod((CMSSiteModel)(process.getConsignment().getOrder().getSite()), paymentMethod);
		if(null != currentPaymentMethod){
			if(currentPaymentMethod.isCaptureNeeded()){
				adyenPaymentService.capture(process.getConsignment().getOrder().getPaymentTransactions().get(0), 
						process.getConsignment().getOrder().getCode());
				return Transition.OK.toString();
			}else{
				final OrderModel order = (OrderModel) process.getConsignment().getOrder();
				order.setPaymentStatus(PaymentStatus.PAID);
				
				final ConsignmentModel consignment = process.getConsignment();
				consignment.setStatus(ConsignmentStatus.READY);
				
				getModelService().saveAll(order,consignment);
				getModelService().refresh(order);
				getModelService().refresh(consignment);
				
				return Transition.NOT_REQUIRED.toString();
			}
		}
		return Transition.ERROR.toString();
		
	}
	
	private AdyenPaymentMethodModel getCurrentPaymentMethod(CMSSiteModel site, String paymentMethod){
		for(AdyenPaymentMethodModel paymentMethodModel : site.getSiteAdyenPaymentMethods()) {
			if (paymentMethodModel.getAdyenPaymentMethodCode().equals(paymentMethod)) {
				return paymentMethodModel;
			}
		}
		return null;
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}
	
	private String getAdyenPaymentMethod(AbstractOrderModel order){
		String adyenPaymentMethod = null;
		if(order.getPaymentInfo() instanceof AdyenPaymentInfoModel) {
			adyenPaymentMethod = ((AdyenPaymentInfoModel)order.getPaymentInfo()).getAdyenPaymentBrand();
		}

		return adyenPaymentMethod;
	}


	/**
	 * @return the adyenPaymentService
	 */
	public AdyenPaymentService getAdyenPaymentService() {
		return adyenPaymentService;
	}


	/**
	 * @param adyenPaymentService the adyenPaymentService to set
	 */
	public void setAdyenPaymentService(AdyenPaymentService adyenPaymentService) {
		this.adyenPaymentService = adyenPaymentService;
	}

}
