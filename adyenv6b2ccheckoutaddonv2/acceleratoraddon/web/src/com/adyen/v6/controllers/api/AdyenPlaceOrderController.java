package com.adyen.v6.controllers.api;

import com.adyen.constants.ApiConstants;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.forms.AdyenPaymentForm;
import com.adyen.v6.util.AdyenUtil;
import com.adyen.v6.util.TerminalAPIUtil;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
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

import javax.servlet.http.HttpServletRequest;
import java.net.SocketTimeoutException;
import java.util.Objects;

import static com.adyen.model.checkout.PaymentsResponse.ResultCodeEnum.*;
import static com.adyen.v6.constants.Adyenv6coreConstants.*;


@RequestMapping("/api/checkout")
@Controller
public class AdyenPlaceOrderController {
    private static final Logger LOGGER = Logger.getLogger(AdyenPlaceOrderController.class);

    @Autowired
    private CheckoutFlowFacade checkoutFlowFacade;

    @Autowired
    private CartFacade cartFacade;

    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Autowired
    private ConfigurationService configurationService;

    @RequireHardLogIn
    @PostMapping("/place-order")
    public ResponseEntity<String> placeOrder(@RequestBody AdyenPaymentForm adyenPaymentForm, HttpServletRequest request) throws Exception {
        final boolean selectPaymentMethodSuccess = selectPaymentMethod(adyenPaymentForm);

        if (!selectPaymentMethodSuccess) {
            LOGGER.warn("Payment form is invalid.");
            return ResponseEntity.badRequest().build();
        }

        if (!isCartValid()) {
            LOGGER.warn("Cart is invalid.");
            return ResponseEntity.badRequest().build();
        }

        final CartData cartData = cartFacade.getSessionCart();
        String adyenPaymentMethod = cartData.getAdyenPaymentMethod();
        String errorMessage = "";

        switch (adyenPaymentMethod) {
            case RATEPAY: {
                return handleRatepay(request, cartData, errorMessage);
            }
            case PAYMENT_METHOD_POS: {
                return handlePOS(request, cartData, errorMessage);
            }
            default: {
                return handleOther(request, cartData, errorMessage, adyenPaymentMethod);
            }
        }
    }


    private ResponseEntity<String> handleRatepay(HttpServletRequest request, CartData cartData, String errorMessage) {
        try {
            OrderData orderData = adyenCheckoutFacade.authorisePayment(request, cartData);
            LOGGER.debug("Redirecting to confirmation!");
            return ResponseEntity.status(HttpStatus.FOUND).build();
        } catch (ApiException e) {
            LOGGER.error("API Exception: " + e.getError(), e);
        } catch (AdyenNonAuthorizedPaymentException e) {
            LOGGER.info("Handling AdyenNonAuthorizedPaymentException");
            PaymentResult paymentResult = e.getPaymentResult();
            if (Objects.nonNull(paymentResult)) {
                if (paymentResult.isRefused()) {
                    errorMessage = getErrorMessageByRefusalReason(paymentResult.getRefusalReason());
                    LOGGER.info("Payment " + paymentResult.getPspReference() + " is refused " + errorMessage);
                }
                if (PaymentResult.ResultCodeEnum.ERROR.equals(paymentResult.getResultCode())) {
                    LOGGER.error("Payment " + paymentResult.getPspReference() + " result is error, reason:  "
                            + paymentResult.getRefusalReason());
                }
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<String> handlePOS(HttpServletRequest request, CartData cartData, String errorMessage) {
        try {
            String originalServiceId = Long.toString(System.currentTimeMillis() % 10000000000L);
            request.setAttribute("originalServiceId", originalServiceId);
            Long paymentStartTime = System.currentTimeMillis();
            request.setAttribute("paymentStartTime", paymentStartTime);
            OrderData orderData = adyenCheckoutFacade.initiatePosPayment(request, cartData);
            LOGGER.debug("Redirecting to confirmation.");
            return ResponseEntity.status(HttpStatus.FOUND).build();

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
                return ResponseEntity.status(HttpStatus.FOUND).build();
            } catch (AdyenNonAuthorizedPaymentException nx) {
                errorMessage = TerminalAPIUtil.getErrorMessageForNonAuthorizedPosPayment(nx.getTerminalApiResponse());
                LOGGER.warn("AdyenNonAuthorizedPaymentException " + errorMessage + " pspReference: " + nx.getPaymentResult().getPspReference());
            } catch (SocketTimeoutException to) {
                LOGGER.error("POS Status request timed out. Returning error message.");
                errorMessage = "checkout.error.authorization.pos.configuration";
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
        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<String> handleOther(HttpServletRequest request, CartData cartData, String errorMessage, String adyenPaymentMethod) {

        try {
            cartData.setAdyenReturnUrl("Todo in 3Ds.");
            OrderData orderData = adyenCheckoutFacade.authorisePayment(request, cartData);
            //In case of Boleto, show link to pdf
            if (PAYMENT_METHOD_BOLETO.equals(cartData.getAdyenPaymentMethod())) {
                LOGGER.info("Boleto.");

            } else if (PAYMENT_METHOD_MULTIBANCO.equals(cartData.getAdyenPaymentMethod())) {
                LOGGER.info("Multibanco.");

            }
            return ResponseEntity.status(HttpStatus.FOUND).build();

        } catch (ApiException e) {
            LOGGER.error("API exception: ", e);
            return ResponseEntity.badRequest().build();
        } catch (AdyenNonAuthorizedPaymentException e) {
            LOGGER.info("Handling AdyenNonAuthorizedPaymentException. Checking PaymentResponse.");
            PaymentsResponse paymentsResponse = e.getPaymentsResponse();
            if (REDIRECTSHOPPER == paymentsResponse.getResultCode()) {
                return handleRedirect(adyenPaymentMethod);
            }
            if (REFUSED == paymentsResponse.getResultCode()) {
                LOGGER.info("PaymentResponse is REFUSED, pspReference: " + paymentsResponse.getPspReference());
                errorMessage = getErrorMessageByRefusalReason(paymentsResponse.getRefusalReason());     //will be used in error handling
                return ResponseEntity.badRequest().build();
            }
            if (CHALLENGESHOPPER == paymentsResponse.getResultCode() || IDENTIFYSHOPPER == paymentsResponse.getResultCode()) {
                LOGGER.debug("PaymentResponse is " + paymentsResponse.getResultCode() + ", redirecting to 3DS2 flow");
                return ResponseEntity.status(HttpStatus.FOUND).build();

            }
            if (ERROR == paymentsResponse.getResultCode()) {
                LOGGER.error("PaymentResponse is ERROR, reason: " + paymentsResponse.getRefusalReason() + " pspReference: " + paymentsResponse.getPspReference());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
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

    private ResponseEntity<String> handleRedirect(String adyenPaymentMethod) {
        if (is3DSPaymentMethod(adyenPaymentMethod)) {
            LOGGER.debug("PaymentResponse resultCode is REDIRECTSHOPPER, redirecting shopper to 3DS flow");
            return ResponseEntity.status(HttpStatus.FOUND).build();

        }
        if (AFTERPAY_TOUCH.equals(adyenPaymentMethod)) {
            LOGGER.debug("PaymentResponse resultCode is REDIRECTSHOPPER, redirecting shopper to afterpaytouch page");
            return ResponseEntity.status(HttpStatus.FOUND).build();

        }
        LOGGER.debug("PaymentResponse resultCode is REDIRECTSHOPPER, redirecting shopper to local payment method page");
        return ResponseEntity.status(HttpStatus.FOUND).build();
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

    protected String getErrorMessageByRefusalReason(String refusalReason) {
        String errorMessage = "Payment refused.";
        if (refusalReason != null) {
            switch (refusalReason) {
                case ApiConstants.RefusalReason.TRANSACTION_NOT_PERMITTED:
                    errorMessage = "The transaction is not permitted.";
                    break;
                case ApiConstants.RefusalReason.CVC_DECLINED:
                    errorMessage = "The payment is REFUSED. Please check your Card details.";
                    break;
                case ApiConstants.RefusalReason.RESTRICTED_CARD:
                    errorMessage = "The card is restricted.";
                    break;
                case ApiConstants.RefusalReason.PAYMENT_DETAIL_NOT_FOUND:
                    errorMessage = "The payment is REFUSED because the saved card is removed. Please try an other payment method.";
                    break;
                default:
                    errorMessage = "Payment refused.";
            }
        }
        return errorMessage;
    }
}
