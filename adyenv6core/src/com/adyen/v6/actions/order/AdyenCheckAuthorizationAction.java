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
package com.adyen.v6.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Check if order is authorized
 */
public class AdyenCheckAuthorizationAction extends AbstractAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(AdyenCheckAuthorizationAction.class);

    public enum Transition {
        OK, NOK, WAIT;

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
        return Transition.getStringValues();
    }

    @Override
    public String execute(final OrderProcessModel process) {
        LOG.debug("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

        final OrderModel order = process.getOrder();

        //Fail when no order
        if (order == null) {
            LOG.error("Order is null!");
            return Transition.NOK.toString();
        }
        final PaymentInfoModel paymentInfo = order.getPaymentInfo();

        //Continue if it's not Adyen payment
        if (paymentInfo.getAdyenPaymentMethod() == null || paymentInfo.getAdyenPaymentMethod().isEmpty()) {
            LOG.debug("Not Adyen Payment");
            return Transition.OK.toString();
        }

        //No transactions means that is not authorized yet
        if (order.getPaymentTransactions().isEmpty()) {
            LOG.debug("Process: " + process.getCode() + " Order Waiting");
            return Transition.WAIT.toString();
        }

        boolean orderAuthorized = isOrderAuthorized(order);

        //Continue if all transactions are authorised
        if (orderAuthorized) {
            LOG.debug("Process: " + process.getCode() + " Order Authorized");
            order.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
            modelService.save(order);

            return Transition.OK.toString();
        }

        LOG.debug("Process: " + process.getCode() + " Order Not Authorized");
        order.setStatus(OrderStatus.PAYMENT_NOT_AUTHORIZED);
        modelService.save(order);

        return Transition.NOK.toString();
    }

    private boolean isTransactionAuthorized(final PaymentTransactionModel paymentTransactionModel) {
        for (final PaymentTransactionEntryModel entry : paymentTransactionModel.getEntries()) {
            if (entry.getType().equals(PaymentTransactionType.AUTHORIZATION)
                    && TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())) {
                return true;
            }
        }

        return false;
    }

    private boolean isOrderAuthorized(final OrderModel order) {
        //A single not authorized transaction means not authorized
        for (final PaymentTransactionModel paymentTransactionModel : order.getPaymentTransactions()) {
            if (!isTransactionAuthorized(paymentTransactionModel)) {
                return false;
            }
        }

        return true;
    }
}
