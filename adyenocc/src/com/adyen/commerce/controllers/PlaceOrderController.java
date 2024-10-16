package com.adyen.commerce.controllers;

import com.adyen.commerce.constants.AdyenoccConstants;
import com.adyen.commerce.controllerbase.PlaceOrderControllerBase;
import com.adyen.commerce.facades.AdyenCheckoutApiFacade;
import com.adyen.commerce.request.PlaceOrderRequest;
import com.adyen.commerce.response.OCCPlaceOrderResponse;
import com.adyen.commerce.response.PlaceOrderResponse;
import com.adyen.commerce.utils.WebServicesBaseUrlResolver;
import com.adyen.model.checkout.PaymentDetailsRequest;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = AdyenoccConstants.ADYEN_USER_CART_PREFIX)
@ApiVersion("v2")
@Tag(name = "Adyen")
public class PlaceOrderController extends PlaceOrderControllerBase {

    @Autowired
    private AdyenCheckoutApiFacade adyenCheckoutApiFacade;

    @Autowired
    private CheckoutFlowFacade checkoutFlowFacade;

    @Autowired
    private CartFacade cartFacade;

    @Resource(name = "siteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Autowired
    private WebServicesBaseUrlResolver webServicesBaseUrlResolver;

    @Resource(name = "checkoutCustomerStrategy")
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Resource(name = "commerceCommonI18NService")
    private CommerceCommonI18NService commerceCommonI18NService;

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/place-order", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "placeOrder", summary = "Handle place order request", description =
            "Places order based on request data")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ResponseEntity<String> onPlaceOrder(@RequestBody String placeOrderStringRequest, HttpServletRequest request) throws Exception {
        PlaceOrderRequest placeOrderRequest = objectMapper.readValue(placeOrderStringRequest, PlaceOrderRequest.class);
        OCCPlaceOrderResponse placeOrderResponse = super.placeOrderOCC(placeOrderRequest, request);
        String response = objectMapper.writeValueAsString(placeOrderResponse);
        return ResponseEntity.ok(response);
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/additional-details", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "additionalDetails", summary = "Handle additional details action", description =
            "Places pending order based on additional details request")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ResponseEntity<String> onAdditionalDetails(@RequestBody PaymentDetailsRequest detailsRequest) throws JsonProcessingException {
        OCCPlaceOrderResponse placeOrderResponse = handleAdditionalDetailsOCC(detailsRequest);

        String response = objectMapper.writeValueAsString(placeOrderResponse);
        return ResponseEntity.ok(response);
    }


    @Override
    public String getPaymentRedirectReturnUrl() {
        String occBaseUrl = webServicesBaseUrlResolver.getOCCBaseUrl(true);
        String currency = commerceCommonI18NService.getCurrentCurrency().getIsocode();
        String language = commerceCommonI18NService.getCurrentLanguage().getIsocode();
        String baseSiteUid = baseSiteService.getCurrentBaseSite().getUid();

        return occBaseUrl + "/v2/" + baseSiteUid + "/adyen/redirect?lang=" + language + "&curr=" + currency;
    }

    @Override
    public AdyenCheckoutApiFacade getAdyenCheckoutApiFacade() {
        return adyenCheckoutApiFacade;
    }

    @Override
    public CheckoutFlowFacade getCheckoutFlowFacade() {
        return checkoutFlowFacade;
    }

    @Override
    public CartFacade getCartFacade() {
        return cartFacade;
    }

    @Override
    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Override
    public SiteBaseUrlResolutionService getSiteBaseUrlResolutionService() {
        return siteBaseUrlResolutionService;
    }

    @Override
    public AdyenCheckoutFacade getAdyenCheckoutFacade() {
        return adyenCheckoutFacade;
    }

    @Override
    public CheckoutCustomerStrategy getCheckoutCustomerStrategy() {
        return checkoutCustomerStrategy;
    }
}
