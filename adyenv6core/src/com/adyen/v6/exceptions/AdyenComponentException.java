package com.adyen.v6.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AdyenComponentException extends Exception {
    public AdyenComponentException(String message) {
        super(message);
    }
}
