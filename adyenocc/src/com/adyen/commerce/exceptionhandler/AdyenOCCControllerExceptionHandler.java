package com.adyen.commerce.exceptionhandler;

import com.adyen.commerce.exception.AdyenOCCControllerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdyenOCCControllerExceptionHandler {

    @ExceptionHandler(value = AdyenOCCControllerException.class)
    public ResponseEntity<Void> handleAdyenControllerException(AdyenOCCControllerException exception) {
        return ResponseEntity.badRequest().build();
    }
}