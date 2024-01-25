package com.adyen.v6.controllers.api;

import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.forms.AdyenPaymentForm;
import com.adyen.v6.forms.validation.AdyenPaymentFormValidator;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;

@Controller(value = "/api/checkout")
public class AdyenPaymentMethodsController {

    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Autowired
    AdyenPaymentFormValidator adyenPaymentFormValidator;

    @RequireHardLogIn
    @PostMapping(value = "/select-payment-method")
    public ResponseEntity<Errors> selectPaymentMethod(AdyenPaymentForm adyenPaymentForm) {
        final Errors errors = new BeanPropertyBindingResult(adyenPaymentForm, "payment");
        //todo check controller
        adyenCheckoutFacade.handlePaymentForm(adyenPaymentForm, errors);


        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }
}
