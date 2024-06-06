package com.adyen.commerce.controllers;

import com.adyen.commerce.constants.AdyenoccConstants;
import com.adyen.commerce.controllerbase.PlaceOrderControllerBase;
import com.adyen.commerce.facades.AdyenCheckoutApiFacade;
import com.adyen.commerce.response.PlaceOrderResponse;
import com.adyen.model.checkout.PaymentDetailsRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = AdyenoccConstants.ADYEN_USER_CART_PREFIX)
@ApiVersion("v2")
@Tag(name = "Adyen")
public class PlaceOrderController extends PlaceOrderControllerBase {

    @Autowired
    private AdyenCheckoutApiFacade adyenCheckoutApiFacade;


    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
    @PostMapping(value = "/additional-details", produces= MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "additionalDetails", summary = "Handle additional details action", description =
            "Places pending order based on additional details request")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ResponseEntity<String> onAdditionalDetails(@RequestBody PaymentDetailsRequest detailsRequest) throws JsonProcessingException {
        PlaceOrderResponse placeOrderResponse = handleAdditionalDetails(detailsRequest);

        String response = objectMapper.writeValueAsString(placeOrderResponse);
        return ResponseEntity.ok(response);
    }

    @Override
    public AdyenCheckoutApiFacade getAdyenCheckoutApiFacade() {
        return adyenCheckoutApiFacade;
    }
}
