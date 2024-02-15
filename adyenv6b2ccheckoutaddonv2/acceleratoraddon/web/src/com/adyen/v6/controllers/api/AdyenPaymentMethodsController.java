package com.adyen.v6.controllers.api;

import com.adyen.service.exception.ApiException;
import com.adyen.v6.dto.CheckoutConfigDTO;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.forms.AdyenPaymentForm;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value = "/api/checkout")
public class AdyenPaymentMethodsController {
    private static final Logger LOG = Logger.getLogger(AdyenPaymentMethodsController.class);


    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @RequireHardLogIn
    @PostMapping(value = "/select-payment-method")
    public ResponseEntity<HttpStatus> selectPaymentMethod(@RequestBody AdyenPaymentForm adyenPaymentForm) {
        final BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(adyenPaymentForm, "payment");
        adyenCheckoutFacade.handlePaymentForm(adyenPaymentForm, bindingResult);

        if (bindingResult.hasErrors()) {
            LOG.warn(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getCode).reduce((x, y) -> (x = x + y)));
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @RequireHardLogIn
    @GetMapping(value = "/payment-methods-configuration")
    public ResponseEntity<CheckoutConfigDTO> getPaymentMethodsConfiguration() throws ApiException {
        return ResponseEntity.ok().body(adyenCheckoutFacade.getCheckoutConfig());
    }
}
