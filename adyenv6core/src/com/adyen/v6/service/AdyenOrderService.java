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

import com.adyen.model.PaymentResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.fraud.model.FraudReportModel;

public interface AdyenOrderService {
    /**
     * Updates order's metadata using the API Payment response
     * Covers fraud, avs, cc authorisation data
     */
    void updateOrderFromPaymentResult(OrderModel order, PaymentResult paymentResult);

    /**
     * Creates FraudReportModel from PaymentResult
     */
    FraudReportModel createFraudReportFromPaymentResult(PaymentResult paymentResult);

    /**
     * Store FraudReportModel
     */
    void storeFraudReport(FraudReportModel fraudReport);

    /**
     * Create FraudReportModel from PaymentResult and assigns it to order
     */
    void storeFraudReportFromPaymentResult(OrderModel order, PaymentResult paymentResult);
}
