package com.adyen.v6.controllers.checkout;

import com.adyen.v6.facades.AdyenExpressCheckoutFacade;
import com.adyen.v6.request.ApplePayExpressCartRequest;
import com.adyen.v6.request.ApplePayExpressPDPRequest;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AdyenApplePayExpressCheckoutController {

    @Autowired
    private AdyenExpressCheckoutFacade adyenExpressCheckoutFacade;

    @PostMapping("/expressCheckout/applePayPDP")
    public ResponseEntity applePayExpressPDP(@RequestBody ApplePayExpressPDPRequest applePayExpressPDPRequest) throws DuplicateUidException {

        adyenExpressCheckoutFacade.expressPDPCheckout(applePayExpressPDPRequest.getAddressData(), applePayExpressPDPRequest.getProductCode(),
                applePayExpressPDPRequest.getAdyenApplePayMerchantIdentifier(), applePayExpressPDPRequest.getAdyenApplePayMerchantName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/expressCheckout/cart")
    public ResponseEntity cartExpressCheckout(@RequestBody ApplePayExpressCartRequest applePayExpressCartRequest) throws DuplicateUidException {

        adyenExpressCheckoutFacade.expressCartCheckout(applePayExpressCartRequest.getAddressData(),
                applePayExpressCartRequest.getAdyenApplePayMerchantIdentifier(), applePayExpressCartRequest.getAdyenApplePayMerchantName());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
