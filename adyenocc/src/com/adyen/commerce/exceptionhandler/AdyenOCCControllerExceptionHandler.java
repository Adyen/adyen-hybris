package com.adyen.commerce.exceptionhandler;

import com.adyen.commerce.exception.AdyenControllerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdyenOCCControllerExceptionHandler {

    @ExceptionHandler(value = AdyenControllerException.class)
    public ResponseEntity<Void> handleAdyenControllerException(AdyenControllerException exception) {
        return ResponseEntity.badRequest().build();
    }
}