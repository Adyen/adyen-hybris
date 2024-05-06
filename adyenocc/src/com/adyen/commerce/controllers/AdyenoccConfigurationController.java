package com.adyen.commerce.controllers;

import com.adyen.service.exception.ApiException;
import com.adyen.v6.dto.CheckoutConfigDTO;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/{baseSiteId}")
public class AdyenoccConfigurationController {

    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @RequireHardLogIn
    @GetMapping(value = "/checkout-configuration")
    public ResponseEntity<CheckoutConfigDTO> getCheckoutConfig() throws ApiException {
        return ResponseEntity.ok().body(adyenCheckoutFacade.getReactCheckoutConfig());
    }

}
