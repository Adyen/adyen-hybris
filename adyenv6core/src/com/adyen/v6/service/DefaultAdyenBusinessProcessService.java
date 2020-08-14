/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.service;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

public class DefaultAdyenBusinessProcessService implements AdyenBusinessProcessService {
    private static final Logger LOG = Logger.getLogger(DefaultAdyenBusinessProcessService.class);

    private BusinessProcessService businessProcessService;

    @Override
    public void triggerOrderProcessEvent(OrderModel orderModel, String event) {
        final Collection<OrderProcessModel> orderProcesses = orderModel.getOrderProcess();
        for (final OrderProcessModel orderProcess : orderProcesses) {
            LOG.debug("Order process code: " + orderProcess.getCode());

            final String eventName = orderProcess.getCode() + "_" + event;
            LOG.debug("Sending event:" + eventName);
            businessProcessService.triggerEvent(eventName);
        }
    }

    @Override
    public void triggerReturnProcessEvent(OrderModel orderModel, String event) {
        List<ReturnRequestModel> returnRequests = orderModel.getReturnRequests();
        for (ReturnRequestModel returnRequest : returnRequests) {
            Collection<ReturnProcessModel> returnProcesses = returnRequest.getReturnProcess();
            for (ReturnProcessModel returnProcess : returnProcesses) {
                LOG.debug("Return process code: " + returnProcess.getCode());

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
