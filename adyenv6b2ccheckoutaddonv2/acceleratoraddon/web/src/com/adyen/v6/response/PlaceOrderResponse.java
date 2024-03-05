package com.adyen.v6.response;

import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrderResponse {
    private String orderNumber;
    private List<ObjectError> errors = new ArrayList();

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
