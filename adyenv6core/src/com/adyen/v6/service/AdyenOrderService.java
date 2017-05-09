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
