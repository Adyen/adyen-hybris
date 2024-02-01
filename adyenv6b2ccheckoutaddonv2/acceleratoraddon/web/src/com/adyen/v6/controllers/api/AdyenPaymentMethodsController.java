package com.adyen.v6.controllers.api;

import com.adyen.v6.dto.CheckoutConfigDTO;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.forms.AdyenPaymentForm;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value = "/api/checkout")
public class AdyenPaymentMethodsController {

    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @RequireHardLogIn
    @PostMapping(value = "/select-payment-method")
    public ResponseEntity<Errors> selectPaymentMethod(AdyenPaymentForm adyenPaymentForm) {
        final Errors errors = new BeanPropertyBindingResult(adyenPaymentForm, "payment");
        adyenCheckoutFacade.handlePaymentForm(adyenPaymentForm, errors);

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }

    @RequireHardLogIn
    @GetMapping(value = "/payment-methods-configuration")
    public ResponseEntity<CheckoutConfigDTO> getPaymentMethodsConfiguration() {
        return ResponseEntity.ok().body(adyenCheckoutFacade.getCheckoutConfig());
    }
}
