package com.adyen.v6.controllers.checkout;

import com.adyen.v6.forms.AdyenPaymentForm;
import com.adyen.v6.forms.validation.AdyenPaymentFormValidator;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;

@Controller(value = "/checkout/multi/adyen")
public class AdyenPaymentMethodsController {

    @Autowired
    private CheckoutFacade checkoutFacade;

    @Autowired
    AdyenPaymentFormValidator adyenPaymentFormValidator;

    @RequireHardLogIn
    @PostMapping(value = "/select-payment-method")
    public ResponseEntity<HttpStatus> selectPaymentMethod(AdyenPaymentForm adyenPaymentForm) {
        final Errors errors = new BeanPropertyBindingResult(adyenPaymentForm, "payment");
        adyenPaymentFormValidator.validate(adyenPaymentForm, errors);

        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        checkoutFacade.setPaymentDetails(adyenPaymentForm.getPaymentMethod());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
