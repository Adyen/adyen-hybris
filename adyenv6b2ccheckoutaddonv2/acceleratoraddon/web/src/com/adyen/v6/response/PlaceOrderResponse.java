package com.adyen.v6.response;

import com.adyen.model.checkout.CheckoutPaymentsAction;

public class PlaceOrderResponse {
    private boolean isRedirectTo3DS;
    private CheckoutPaymentsAction paymentsAction;

    public PlaceOrderResponse(boolean isRedirectTo3DS) {
        this.isRedirectTo3DS = isRedirectTo3DS;
    }

    public void setPaymentsAction(CheckoutPaymentsAction paymentsAction) {
        this.paymentsAction = paymentsAction;
    }

    public boolean isRedirectTo3DS() {
        return isRedirectTo3DS;
    }

    public CheckoutPaymentsAction getPaymentsAction() {
        return paymentsAction;
    }
}
