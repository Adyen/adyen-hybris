package com.adyen.v6.exceptions;

import com.adyen.model.PaymentResult;

public class AdyenNonAuthorizedPaymentException extends Exception {
    private PaymentResult paymentResult;

    public AdyenNonAuthorizedPaymentException(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }
}
