package com.adyen.v6.response;

import com.adyen.model.checkout.CheckoutPaymentsAction;

public class PlaceOrderResponse {
    private String orderNumber;
    private String error;
    private boolean isRedirectTo3DS;
    private CheckoutPaymentsAction paymentsAction;

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
    
}
