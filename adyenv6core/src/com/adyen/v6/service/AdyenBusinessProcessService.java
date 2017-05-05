package com.adyen.v6.service;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

public class AdyenBusinessProcessService {
    private static final Logger LOG = Logger.getLogger(AdyenBusinessProcessService.class);

    private BusinessProcessService businessProcessService;

    /**
     * Trigger order-process event
     *
     * @param orderModel
     */
    public void triggerOrderProcessEvent(OrderModel orderModel, String event) {
        final Collection<OrderProcessModel> orderProcesses = orderModel.getOrderProcess();
        for (final OrderProcessModel orderProcess : orderProcesses) {
            LOG.debug("Order process code: " + orderProcess.getCode());
            //TODO: send only on "order-process-*" ?
            final String eventName = orderProcess.getCode() + "_" + event;
            LOG.debug("Sending event:" + eventName);
            businessProcessService.triggerEvent(eventName);
        }
    }

    /**
     * Trigger return-process event
     *
     * @param orderModel
     * @param event
     */
    public void triggerReturnProcessEvent(OrderModel orderModel, String event) {
        List<ReturnRequestModel> returnRequests = orderModel.getReturnRequests();
        for (ReturnRequestModel returnRequest : returnRequests) {
            Collection<ReturnProcessModel> returnProcesses = returnRequest.getReturnProcess();
            for (ReturnProcessModel returnProcess : returnProcesses) {
                LOG.debug("Return process code: " + returnProcess.getCode());
                //TODO: send only on "return-process-*" ?
                final String eventName = returnProcess.getCode() + "_" + event;
                LOG.debug("Sending event:" + eventName);
                businessProcessService.triggerEvent(eventName);
            }
        }
    }

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }
}
