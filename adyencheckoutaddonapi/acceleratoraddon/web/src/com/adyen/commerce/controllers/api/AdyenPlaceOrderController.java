package com.adyen.commerce.controllers.api;

import com.adyen.commerce.exceptions.AdyenControllerException;
import com.adyen.commerce.facades.AdyenCheckoutApiFacade;
import com.adyen.commerce.request.PlaceOrderRequest;
import com.adyen.commerce.response.PlaceOrderResponse;
import com.adyen.commerce.validators.PaymentRequestValidator;
import com.adyen.model.checkout.PaymentDetailsRequest;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.model.checkout.PaymentResponse;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.util.AdyenUtil;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;

import static com.adyen.commerce.constants.AdyencheckoutaddonapiWebConstants.ADYEN_CHECKOUT_API_PREFIX;
import static com.adyen.commerce.constants.AdyencheckoutaddonapiWebConstants.AUTHORISE_3D_SECURE_PAYMENT_URL;
import static com.adyen.commerce.util.ErrorMessageUtil.getErrorMessageByRefusalReason;
import static com.adyen.commerce.util.FieldValidationUtil.getFieldCodesFromValidation;
import static com.adyen.model.checkout.PaymentResponse.ResultCodeEnum.CHALLENGESHOPPER;
import static com.adyen.model.checkout.PaymentResponse.ResultCodeEnum.IDENTIFYSHOPPER;
import static com.adyen.model.checkout.PaymentResponse.ResultCodeEnum.PENDING;
import static com.adyen.model.checkout.PaymentResponse.ResultCodeEnum.PRESENTTOSHOPPER;
import static com.adyen.model.checkout.PaymentResponse.ResultCodeEnum.REDIRECTSHOPPER;
import static com.adyen.model.checkout.PaymentResponse.ResultCodeEnum.REFUSED;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_BCMC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_SCHEME;


@RequestMapping("/api/checkout")
@Controller
public class AdyenPlaceOrderController {
    private static final Logger LOGGER = Logger.getLogger(AdyenPlaceOrderController.class);

    private static final String CHECKOUT_ERROR_AUTHORIZATION_FAILED = "checkout.error.authorization.failed";
    private static final String CHECKOUT_ERROR_POS_CONFIGURATION = "checkout.error.authorization.pos.configuration";
    private static final String CHECKOUT_ERROR_FORM_ENTRY_INVALID = "checkout.error.paymentethod.formentry.invalid";
    public static final String GET_TYPE = "getType";


    @Autowired
    private CheckoutFlowFacade checkoutFlowFacade;

    @Autowired
    private CartFacade cartFacade;

    @Autowired
    private AdyenCheckoutApiFacade adyenCheckoutApiFacade;

    @Autowired
    private ConfigurationService configurationService;

    @Resource(name = "siteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @RequireHardLogIn
    @PostMapping("/place-order")
    public ResponseEntity<PlaceOrderResponse> placeOrder(@RequestBody PlaceOrderRequest placeOrderRequest, HttpServletRequest request) throws Exception {

        String adyenPaymentMethodType = extractPaymentMethodType(placeOrderRequest);

        preHandleAndValidateRequest(placeOrderRequest, adyenPaymentMethodType);

        if (!isCartValid()) {
            LOGGER.warn("Cart is invalid.");
            throw new AdyenControllerException(CHECKOUT_ERROR_AUTHORIZATION_FAILED);
        }

        return handlePayment(request, placeOrderRequest, adyenPaymentMethodType);
    }

    @RequireHardLogIn
    @PostMapping("/additional-details")
    public ResponseEntity<PlaceOrderResponse> onAdditionalDetails(@RequestBody PaymentDetailsRequest detailsRequest, HttpServletRequest request) throws Exception {
        try {
            OrderData orderData = adyenCheckoutApiFacade.placeOrderWithAdditionalDetails(detailsRequest);
            PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
            placeOrderResponse.setOrderNumber(orderData.getCode());
            return ResponseEntity.status(HttpStatus.OK).body(placeOrderResponse);
        } catch (ApiException e) {
            LOGGER.error("ApiException: " + e);
            throw new AdyenControllerException(CHECKOUT_ERROR_AUTHORIZATION_FAILED);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
            throw new AdyenControllerException(CHECKOUT_ERROR_AUTHORIZATION_FAILED);
        }
    }

    private static String extractPaymentMethodType(PlaceOrderRequest placeOrderRequest) throws AdyenControllerException {
        if (placeOrderRequest == null || placeOrderRequest.getPaymentRequest() == null || placeOrderRequest.getPaymentRequest().getPaymentMethod() == null) {
            throw new AdyenControllerException(CHECKOUT_ERROR_AUTHORIZATION_FAILED);
        }
        Object actualInstance = placeOrderRequest.getPaymentRequest().getPaymentMethod().getActualInstance();
        if (actualInstance == null) {
            throw new AdyenControllerException(CHECKOUT_ERROR_AUTHORIZATION_FAILED);
        }
        Class<?> aClass = actualInstance.getClass();
        try {
            return aClass.getMethod(GET_TYPE).invoke(actualInstance).toString();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new AdyenControllerException(CHECKOUT_ERROR_AUTHORIZATION_FAILED);
        }
    }

    private ResponseEntity<PlaceOrderResponse> handlePayment(HttpServletRequest request,  PlaceOrderRequest placeOrderRequest, String adyenPaymentMethod) {
        final CartData cartData = cartFacade.getSessionCart();

        String errorMessage = CHECKOUT_ERROR_AUTHORIZATION_FAILED;

        try {
            cartData.setAdyenReturnUrl(get3DSReturnUrl());
            OrderData orderData = adyenCheckoutApiFacade.placeOrderWithPayment(request, cartData, placeOrderRequest.getPaymentRequest());

            PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
            placeOrderResponse.setOrderNumber(orderData.getCode());
            return ResponseEntity.status(HttpStatus.OK).body(placeOrderResponse);

        } catch (ApiException e) {
            LOGGER.error("API exception: ", e);
        } catch (AdyenNonAuthorizedPaymentException e) {
            LOGGER.info("Handling AdyenNonAuthorizedPaymentException. Checking PaymentResponse.");
            PaymentResponse paymentsResponse = e.getPaymentsResponse();
            if (REDIRECTSHOPPER == paymentsResponse.getResultCode() || CHALLENGESHOPPER == paymentsResponse.getResultCode() ||
                    IDENTIFYSHOPPER == paymentsResponse.getResultCode() || PENDING == paymentsResponse.getResultCode() ||
                    PRESENTTOSHOPPER == paymentsResponse.getResultCode()) {
                LOGGER.debug("PaymentResponse is " + paymentsResponse.getResultCode() + ", executing action for pspReference: " + paymentsResponse.getPspReference());
                return executeAction(paymentsResponse);
            } else if (REFUSED == paymentsResponse.getResultCode()) {
                LOGGER.info("PaymentResponse is REFUSED, pspReference: " + paymentsResponse.getPspReference());
                errorMessage = getErrorMessageByRefusalReason(paymentsResponse.getRefusalReason());
            } else if (PaymentResponse.ResultCodeEnum.ERROR == paymentsResponse.getResultCode()) {
                LOGGER.error("PaymentResponse is ERROR, reason: " + paymentsResponse.getRefusalReason() + " pspReference: " + paymentsResponse.getPspReference());
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        throw new AdyenControllerException(errorMessage);
    }

    private void preHandleAndValidateRequest(PlaceOrderRequest placeOrderRequest, String adyenPaymentMethod) {
        final BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(placeOrderRequest, "placeOrderRequest");

        boolean showRememberDetails = adyenCheckoutApiFacade.showRememberDetails();
        boolean holderNameRequired = adyenCheckoutApiFacade.getHolderNameRequired();

        PaymentRequestValidator paymentRequestValidator = new PaymentRequestValidator(adyenCheckoutApiFacade.getStoredCards(), showRememberDetails, holderNameRequired);
        paymentRequestValidator.validate(placeOrderRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            LOGGER.warn("Payment form is invalid.");
            LOGGER.warn(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getCode).reduce((x, y) -> (x + " " + y)));
            throw new AdyenControllerException(CHECKOUT_ERROR_FORM_ENTRY_INVALID, getFieldCodesFromValidation(bindingResult));
        }

        adyenCheckoutApiFacade.preHandlePlaceOrder(placeOrderRequest.getPaymentRequest(),adyenPaymentMethod,
                placeOrderRequest.getBillingAddress(), placeOrderRequest.isUseAdyenDeliveryAddress());
    }

    private ResponseEntity<PlaceOrderResponse> executeAction(PaymentResponse paymentsResponse) {
        PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
        placeOrderResponse.setPaymentsResponse(paymentsResponse);
        placeOrderResponse.setExecuteAction(true);
        placeOrderResponse.setPaymentsAction(paymentsResponse.getAction());
        return ResponseEntity.ok(placeOrderResponse);
    }

    private boolean is3DSPaymentMethod(String adyenPaymentMethod) {
        return adyenPaymentMethod.equals(PAYMENT_METHOD_SCHEME) || adyenPaymentMethod.equals(PAYMENT_METHOD_CC) || adyenPaymentMethod.equals(PAYMENT_METHOD_BCMC) || AdyenUtil.isOneClick(adyenPaymentMethod);
    }

    @RequireHardLogIn
    @PostMapping("/payment-canceled")
    public ResponseEntity<Void> onCancel() throws InvalidCartException, CalculationException {
        adyenCheckoutFacade.restoreCartFromOrderCodeInSession();
        return ResponseEntity.ok().build();
    }

    private boolean isCartValid() {

        if (checkoutFlowFacade.hasNoDeliveryAddress()) {
            LOGGER.error("No delivery address.");
            return false;
        }

        if (checkoutFlowFacade.hasNoDeliveryMode()) {
            LOGGER.error("No delivery mode.");
            return false;
        }

        if (checkoutFlowFacade.hasNoPaymentInfo()) {
            LOGGER.error("No payment info.");
            return false;
        }

        final CartData cartData = checkoutFlowFacade.getCheckoutCart();

        if (!checkoutFlowFacade.containsTaxValues()) {
            LOGGER.error(String.format("Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue", cartData.getCode()));
            LOGGER.error("Tax missing.");
            return false;

        }

        if (!cartData.isCalculated()) {
            LOGGER.error(String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
            LOGGER.error("Cart not calculated.");
            return false;

        }

        return true;
    }

    private String get3DSReturnUrl() {
        String url = ADYEN_CHECKOUT_API_PREFIX + AUTHORISE_3D_SECURE_PAYMENT_URL;

        BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();

        return siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSite, true, url);
    }
}
