/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adyen.commerce.controllers;

import com.adyen.commerce.constants.AdyenoccConstants;
import com.adyen.commerce.controllerbase.RedirectControllerBase;
import com.adyen.model.checkout.PaymentDetailsRequest;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.adyen.commerce.constants.AdyenwebcommonsConstants.REDIRECT_PREFIX;

@Controller
@RequestMapping(value = AdyenoccConstants.ADYEN_PREFIX)
@ApiVersion("v2")
@Tag(name = "Adyen")
public class RedirectController extends RedirectControllerBase {
    private static final String REDIRECT_URL = "/redirect/{locale}/{currencyISO}";

    @Resource(name = "adyenCheckoutFacade")
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @GetMapping(value = REDIRECT_URL)
    @Operation(operationId = "adyenRedirect", summary = "Handle redirect payment method", description =
            "Handles return after payment method redirect flow returns")
    public String authorizeRedirectPaymentGet(@Parameter(description = "Base site identifier", required = true) @PathVariable final String baseSiteId,
                                              @Parameter(description = "Locale", required = true) @PathVariable final String locale,
                                              @Parameter(description = "Currency isocode", required = true) @PathVariable final String currencyISO,
                                              final HttpServletRequest request) {
        return super.authoriseRedirectGetPayment(request, baseSiteId, locale, currencyISO);
    }

    @PostMapping(value = REDIRECT_URL)
    @Operation(operationId = "adyenRedirect", summary = "Handle redirect payment method", description =
            "Handles return after payment method redirect flow returns")
    public String authorizeRedirectPaymentPost(@Parameter(description = "Base site identifier", required = true) @PathVariable final String baseSiteId,
                                               @Parameter(description = "Locale", required = true) @PathVariable final String locale,
                                               @Parameter(description = "Currency isocode", required = true) @PathVariable final String currencyISO,
                                               @Parameter(description = "Payment details data", required = true) @RequestBody PaymentDetailsRequest detailsRequest) {
        return super.authoriseRedirectPostPayment(detailsRequest, baseSiteId, locale, currencyISO);
    }

    @Override
    public String getErrorRedirectUrl(String errorMessage, String baseSiteId, String locale, String currencyISO) {
        //TODO: will be implemented
        return "error url";
    }

    @Override
    public String getOrderConfirmationUrl(String orderCode, String baseSiteId, String locale, String currencyISO) {
        //TODO: will be implemented
        return "order confirmation";
    }

    @Override
    public String getCartUrl(String baseSiteId, String locale, String currencyISO) {
        String baseUrl = configurationService.getConfiguration().getString("adyen.spartacus.baseurl");
        return REDIRECT_PREFIX + baseUrl + "/" + baseSiteId + "/" + locale + "/" + currencyISO + "/cart";
    }

    @Override
    public AdyenCheckoutFacade getAdyenCheckoutFacade() {
        return adyenCheckoutFacade;
    }
}
