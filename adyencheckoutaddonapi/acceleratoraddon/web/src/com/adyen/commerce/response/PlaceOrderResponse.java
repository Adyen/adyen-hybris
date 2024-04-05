package com.adyen.commerce.response;

import com.adyen.model.checkout.PaymentResponseAction;

public class PlaceOrderResponse {
    private String orderNumber;
    private String error;
    private boolean isRedirectTo3DS;
    private PaymentResponseAction paymentsAction;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isRedirectTo3DS() {
        return isRedirectTo3DS;
    }

    public void setRedirectTo3DS(boolean redirectTo3DS) {
        isRedirectTo3DS = redirectTo3DS;
    }

    public PaymentResponseAction getPaymentsAction() {
        return paymentsAction;
    }

    public void setPaymentsAction(PaymentResponseAction paymentsAction) {
        this.paymentsAction = paymentsAction;
    }
}
