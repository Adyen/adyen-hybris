package com.adyen.v6.response;

import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrderResponse {
    private String orderNumber;
    private String error;

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
