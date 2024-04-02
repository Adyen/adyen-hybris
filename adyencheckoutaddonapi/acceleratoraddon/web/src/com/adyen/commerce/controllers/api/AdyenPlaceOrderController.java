package com.adyen.commerce.controllers.api;

import com.adyen.commerce.response.PlaceOrderResponse;
import com.adyen.constants.ApiConstants;
import com.adyen.model.checkout.PaymentResponse;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.forms.AdyenPaymentForm;
import com.adyen.v6.util.AdyenUtil;
import com.adyen.v6.util.TerminalAPIUtil;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.CartFacade;
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
import java.net.SocketTimeoutException;
import java.util.Objects;

import static com.adyen.commerce.constants.AdyencheckoutaddonapiWebConstants.ADYEN_CHECKOUT_API_PREFIX;
import static com.adyen.commerce.constants.AdyencheckoutaddonapiWebConstants.AUTHORISE_3D_SECURE_PAYMENT_URL;
import static com.adyen.commerce.util.ErrorMessageUtil.getErrorMessageByRefusalReason;


import static com.adyen.model.checkout.PaymentResponse.ResultCodeEnum.CHALLENGESHOPPER;
import static com.adyen.model.checkout.PaymentResponse.ResultCodeEnum.IDENTIFYSHOPPER;
import static com.adyen.model.checkout.PaymentResponse.ResultCodeEnum.REDIRECTSHOPPER;
import static com.adyen.model.checkout.PaymentResponse.ResultCodeEnum.REFUSED;
import static com.adyen.v6.constants.Adyenv6coreConstants.*;


@RequestMapping("/api/checkout")
@Controller
public class AdyenPlaceOrderController {
    private static final Logger LOGGER = Logger.getLogger(AdyenPlaceOrderController.class);

    private static final String CHECKOUT_ERROR_AUTHORIZATION_FAILED = "checkout.error.authorization.failed";
    private static final String CHECKOUT_ERROR_POS_CONFIGURATION = "checkout.error.authorization.pos.configuration";
    private static final String CHECKOUT_ERROR_FORM_ENTRY_INVALID = "checkout.error.paymentethod.formentry.invalid";


    @Autowired
    private CheckoutFlowFacade checkoutFlowFacade;

    @Autowired
    private CartFacade cartFacade;

    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Autowired
    private ConfigurationService configurationService;

    @Resource(name = "siteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @RequireHardLogIn
    @PostMapping("/place-order")
    public ResponseEntity<PlaceOrderResponse> placeOrder(@RequestBody AdyenPaymentForm adyenPaymentForm, HttpServletRequest request) throws Exception {

        final boolean selectPaymentMethodSuccess = selectPaymentMethod(adyenPaymentForm);

        if (!selectPaymentMethodSuccess) {
            PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
            placeOrderResponse.setError(CHECKOUT_ERROR_FORM_ENTRY_INVALID);
            LOGGER.warn("Payment form is invalid.");
            return ResponseEntity.badRequest().body(placeOrderResponse);
        }

        if (!isCartValid()) {
            LOGGER.warn("Cart is invalid.");
            PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
            placeOrderResponse.setError(CHECKOUT_ERROR_AUTHORIZATION_FAILED);
            return ResponseEntity.badRequest().body(placeOrderResponse);
        }

        final CartData cartData = cartFacade.getSessionCart();
        String adyenPaymentMethod = cartData.getAdyenPaymentMethod();

        switch (adyenPaymentMethod) {
            case RATEPAY: {
                return handleRatepay(request, cartData);
            }
            case PAYMENT_METHOD_POS: {
                return handlePOS(request, cartData);
            }
            default: {
                return handleOther(request, cartData, adyenPaymentMethod);
            }
        }
    }


    private ResponseEntity<PlaceOrderResponse> handleRatepay(HttpServletRequest request, CartData cartData) {
        String errorMessage = CHECKOUT_ERROR_AUTHORIZATION_FAILED;

        try {
            OrderData orderData = adyenCheckoutFacade.authorisePayment(request, cartData);
            LOGGER.debug("Redirecting to confirmation!");
            PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
            placeOrderResponse.setOrderNumber(orderData.getCode());
            return ResponseEntity.status(HttpStatus.OK).body(placeOrderResponse);
        } catch (ApiException e) {
            LOGGER.error("API Exception: " + e.getError(), e);
        } catch (AdyenNonAuthorizedPaymentException e) {
            LOGGER.info("Handling AdyenNonAuthorizedPaymentException");
            PaymentResponse paymentResult = e.getPaymentsResponse();
            if (Objects.nonNull(paymentResult)) {
                if (REFUSED.equals(paymentResult.getResultCode())) {
                    errorMessage = getErrorMessageByRefusalReason(paymentResult.getRefusalReason());
                    LOGGER.info("Payment " + paymentResult.getPspReference() + " is refused " + errorMessage);
                }
                if (PaymentResponse.ResultCodeEnum.ERROR.equals(paymentResult.getResultCode())) {
                    LOGGER.error("Payment " + paymentResult.getPspReference() + " result is error, reason:  "
                            + paymentResult.getRefusalReason());
                }
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
        placeOrderResponse.setError(errorMessage);
        return ResponseEntity.badRequest().body(placeOrderResponse);
    }

    private ResponseEntity<PlaceOrderResponse> handlePOS(HttpServletRequest request, CartData cartData) {
        String errorMessage = CHECKOUT_ERROR_AUTHORIZATION_FAILED;

        try {
            String originalServiceId = Long.toString(System.currentTimeMillis() % 10000000000L);
            request.setAttribute("originalServiceId", originalServiceId);
            Long paymentStartTime = System.currentTimeMillis();
            request.setAttribute("paymentStartTime", paymentStartTime);
            OrderData orderData = adyenCheckoutFacade.initiatePosPayment(request, cartData);
            LOGGER.debug("Redirecting to confirmation.");
            PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
            placeOrderResponse.setOrderNumber(orderData.getCode());
            return ResponseEntity.status(HttpStatus.OK).body(placeOrderResponse);

        } catch (SocketTimeoutException e) {
            try {
                LOGGER.debug("POS request timed out. Checking POS Payment status ");
                int totalTimeout = 130;
                if (configurationService.getConfiguration().containsKey("pos.totaltimeout")) {
                    totalTimeout = configurationService.getConfiguration().getInt("pos.totaltimeout");
                }
                request.setAttribute("totalTimeout", totalTimeout);
                OrderData orderData = adyenCheckoutFacade.checkPosPaymentStatus(request, cartData);
                LOGGER.debug("Redirecting to confirmation.");
                PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
                placeOrderResponse.setOrderNumber(orderData.getCode());
                return ResponseEntity.status(HttpStatus.OK).body(placeOrderResponse);
            } catch (AdyenNonAuthorizedPaymentException nx) {
                errorMessage = TerminalAPIUtil.getErrorMessageForNonAuthorizedPosPayment(nx.getTerminalApiResponse());
                LOGGER.warn("AdyenNonAuthorizedPaymentException " + errorMessage + " pspReference: " + nx.getPaymentResult().getPspReference());
            } catch (SocketTimeoutException to) {
                LOGGER.error("POS Status request timed out. Returning error message.");
                errorMessage = CHECKOUT_ERROR_POS_CONFIGURATION;
            } catch (Exception ex) {
                LOGGER.error("Exception", ex);
            }
        } catch (ApiException e) {
            LOGGER.error("API exception: " + e.getError(), e);
        } catch (AdyenNonAuthorizedPaymentException e) {
            errorMessage = TerminalAPIUtil.getErrorMessageForNonAuthorizedPosPayment(e.getTerminalApiResponse());
            LOGGER.warn("AdyenNonAuthorizedPaymentException" + errorMessage + " pspReference: " + e.getPaymentResult().getPspReference());
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }

        PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
        placeOrderResponse.setError(errorMessage);
        return ResponseEntity.badRequest().body(placeOrderResponse);
    }

    private ResponseEntity<PlaceOrderResponse> handleOther(HttpServletRequest request, CartData cartData, String adyenPaymentMethod) {
        String errorMessage = CHECKOUT_ERROR_AUTHORIZATION_FAILED;

        try {
            cartData.setAdyenReturnUrl(get3DSReturnUrl());
            OrderData orderData = adyenCheckoutFacade.authorisePayment(request, cartData);
            //In case of Boleto, show link to pdf
            if (PAYMENT_METHOD_BOLETO.equals(cartData.getAdyenPaymentMethod())) {
                LOGGER.info("Boleto.");

            } else if (PAYMENT_METHOD_MULTIBANCO.equals(cartData.getAdyenPaymentMethod())) {
                LOGGER.info("Multibanco.");

            }
            PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
            placeOrderResponse.setOrderNumber(orderData.getCode());
            return ResponseEntity.status(HttpStatus.OK).body(placeOrderResponse);

        } catch (ApiException e) {
            LOGGER.error("API exception: ", e);
        } catch (AdyenNonAuthorizedPaymentException e) {
            LOGGER.info("Handling AdyenNonAuthorizedPaymentException. Checking PaymentResponse.");
            PaymentResponse paymentsResponse = e.getPaymentsResponse();
            if (REDIRECTSHOPPER == paymentsResponse.getResultCode()) {
                return handleRedirect(adyenPaymentMethod, paymentsResponse);
            }
            if (REFUSED == paymentsResponse.getResultCode()) {
                LOGGER.info("PaymentResponse is REFUSED, pspReference: " + paymentsResponse.getPspReference());
                errorMessage = getErrorMessageByRefusalReason(paymentsResponse.getRefusalReason());
            }
            if (CHALLENGESHOPPER == paymentsResponse.getResultCode() || IDENTIFYSHOPPER == paymentsResponse.getResultCode()) {
                LOGGER.debug("PaymentResponse is " + paymentsResponse.getResultCode() + ", redirecting to 3DS2 flow");
                return redirectTo3DSValidation(paymentsResponse);
            }
            if (PaymentResponse.ResultCodeEnum.ERROR == paymentsResponse.getResultCode()) {
                LOGGER.error("PaymentResponse is ERROR, reason: " + paymentsResponse.getRefusalReason() + " pspReference: " + paymentsResponse.getPspReference());
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
        placeOrderResponse.setError(errorMessage);
        return ResponseEntity.badRequest().body(placeOrderResponse);
    }

    private boolean selectPaymentMethod(AdyenPaymentForm adyenPaymentForm) {
        final BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(adyenPaymentForm, "payment");
        adyenCheckoutFacade.handlePaymentForm(adyenPaymentForm, bindingResult);


        if (bindingResult.hasErrors()) {
            LOGGER.warn(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getCode).reduce((x, y) -> (x = x + y)));
            return false;
        }
        return true;
    }

    private ResponseEntity<PlaceOrderResponse> handleRedirect(String adyenPaymentMethod, PaymentResponse paymentResponse) {
        if (is3DSPaymentMethod(adyenPaymentMethod)) {
            LOGGER.debug("PaymentResponse resultCode is REDIRECTSHOPPER, redirecting shopper to 3DS flow");
            return redirectTo3DSValidation(paymentResponse);
        }
        if (AFTERPAY_TOUCH.equals(adyenPaymentMethod)) {
            LOGGER.debug("PaymentResponse resultCode is REDIRECTSHOPPER, redirecting shopper to afterpaytouch page");
            return ResponseEntity.status(HttpStatus.FOUND).build();

        }
        LOGGER.debug("PaymentResponse resultCode is REDIRECTSHOPPER, redirecting shopper to local payment method page");
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    private ResponseEntity<PlaceOrderResponse> redirectTo3DSValidation(PaymentResponse paymentsResponse) {
        PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
        placeOrderResponse.setRedirectTo3DS(true);
        //placeOrderResponse.setPaymentsAction(paymentsResponse.getAction());

        return ResponseEntity.ok(placeOrderResponse);
    }

    private boolean is3DSPaymentMethod(String adyenPaymentMethod) {
        return adyenPaymentMethod.equals(PAYMENT_METHOD_CC) || adyenPaymentMethod.equals(PAYMENT_METHOD_BCMC) || AdyenUtil.isOneClick(adyenPaymentMethod);
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
