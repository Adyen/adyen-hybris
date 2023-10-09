package com.adyen.v6.controllers.checkout;

import com.adyen.v6.request.ApplePayExpressPDPRequest;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AdyenApplePayExpressCheckoutController {
    private static final Logger LOG = Logger.getLogger(AdyenApplePayExpressCheckoutController.class);

    @PostMapping("/expressCheckout/applePayPDP")
    public ResponseEntity applePayExpressPDP(@RequestBody ApplePayExpressPDPRequest applePayExpressPDPRequest) {

        LOG.info("applePayExpressPDPRequest");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/expressCheckout/cart")
    public ResponseEntity cartExpressCheckout() {

        LOG.info("applePayExpressCartRequest");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
