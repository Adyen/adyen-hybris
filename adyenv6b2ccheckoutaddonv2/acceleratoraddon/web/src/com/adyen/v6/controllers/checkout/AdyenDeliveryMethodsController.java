package com.adyen.v6.controllers.checkout;

import com.adyen.v6.facades.AdyenCheckoutFacade;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping(value = "/checkoutApi/deliveryMethods")
public class AdyenDeliveryMethodsController {

    private static final String CARTID_PATH_VARIABLE_PATTERN = "{cartID:.*}";

    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @GetMapping(value = "/{cartID}", produces = "application/json")
    public ResponseEntity getAllDeliveryMethods(@PathVariable("cartID") final String cartID) {
        return new ResponseEntity<>(adyenCheckoutFacade.getSupportedDeliveryModes(cartID), HttpStatus.OK);
    }
}
