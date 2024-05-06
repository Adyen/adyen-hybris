package com.adyen.commerce.controllers;

import com.adyen.service.exception.ApiException;
import com.adyen.v6.dto.CheckoutConfigDTO;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping(value = "/{baseSiteId}")
public class AdyenoccConfigurationController {

    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @GetMapping(value = "/checkout-configuration")
    @ApiBaseSiteIdParam
    public ResponseEntity<CheckoutConfigDTO> getCheckoutConfiguration() throws ApiException {
        return ResponseEntity.ok().body(adyenCheckoutFacade.getReactCheckoutConfig());
    }

}
