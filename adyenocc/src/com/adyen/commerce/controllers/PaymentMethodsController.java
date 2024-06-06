/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adyen.commerce.controllers;

import com.adyen.commerce.constants.AdyenoccConstants;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.dto.CheckoutConfigDTO;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = AdyenoccConstants.ADYEN_USER_CART_PREFIX)
@ApiVersion("v2")
@Tag(name = "Adyen")
public class PaymentMethodsController
{
    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @GetMapping(value = "/checkout-configuration")
    @Operation(operationId = "getCheckoutConfiguration", summary = "Get checkout configuration", description =
            "Returns configuration for Adyen dropin component")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ResponseEntity<CheckoutConfigDTO> getCheckoutConfiguration() throws ApiException {
        return ResponseEntity.ok().body(adyenCheckoutFacade.getReactCheckoutConfig());
    }
}
