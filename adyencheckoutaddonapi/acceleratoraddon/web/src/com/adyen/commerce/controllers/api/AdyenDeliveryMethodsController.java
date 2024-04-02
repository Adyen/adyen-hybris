package com.adyen.commerce.controllers.api;

import com.adyen.v6.response.ErrorResponse;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value = "/api/checkout")
public class AdyenDeliveryMethodsController {

    @Autowired
    private CheckoutFacade checkoutFacade;

    @RequireHardLogIn
    @GetMapping(value = "/delivery-methods", produces = "application/json")
    public ResponseEntity<List<? extends DeliveryModeData>> getAllDeliveryMethods() {
        return new ResponseEntity<>(checkoutFacade.getSupportedDeliveryModes(), HttpStatus.OK);
    }

    @RequireHardLogIn
    @PostMapping(value = "/select-delivery-method")
    public ResponseEntity selectDeliveryMethod(@RequestBody String deliveryMethodCode) {
        if (StringUtils.isNotEmpty(deliveryMethodCode)) {
            checkoutFacade.setDeliveryMode(deliveryMethodCode);
            return ResponseEntity.ok(checkoutFacade.getCheckoutCart());
        } else {
            ErrorResponse errorResponse = new ErrorResponse("checkout.deliveryMethod.notSelected");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
