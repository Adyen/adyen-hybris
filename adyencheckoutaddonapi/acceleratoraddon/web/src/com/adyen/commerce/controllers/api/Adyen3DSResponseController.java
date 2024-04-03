package com.adyen.commerce.controllers.api;

import com.adyen.model.checkout.PaymentCompletionDetails;
import com.adyen.model.checkout.PaymentDetailsRequest;
import com.adyen.model.checkout.PaymentDetailsResponse;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

import static com.adyen.commerce.constants.AdyencheckoutaddonapiWebConstants.*;
import static com.adyen.commerce.util.ErrorMessageUtil.getErrorMessageByRefusalReason;
import static com.adyen.model.checkout.PaymentDetailsResponse.ResultCodeEnum;
import static com.adyen.model.checkout.PaymentDetailsResponse.ResultCodeEnum.REFUSED;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController.REDIRECT_PREFIX;

@Controller
@RequestMapping(value = ADYEN_CHECKOUT_API_PREFIX)
public class Adyen3DSResponseController {
    private static final Logger LOGGER = Logger.getLogger(Adyen3DSResponseController.class);
    private static final String REDIRECT_RESULT = "redirectResult";
    private static final String PAYLOAD = "payload";
    private static final String NON_AUTHORIZED_ERROR = "Handling AdyenNonAuthorizedPaymentException. Checking PaymentResponse.";
    private static final String CHECKOUT_ERROR_AUTHORIZATION_FAILED = "checkout.error.authorization.failed";
    private static final String REDIRECTING_TO_CART_PAGE = "Redirecting to cart page...";
    private static final String ORDER_CONFIRMATION_URL = ADYEN_CHECKOUT_PAGE_PREFIX + ADYEN_CHECKOUT_ORDER_CONFIRMATION;
    private static final String SELECT_PAYMENT_METHOD_URL = ADYEN_CHECKOUT_PAGE_PREFIX + ADYEN_CHECKOUT_SELECT_PAYMENT;


    @Resource(name = "adyenCheckoutFacade")
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @GetMapping(value = AUTHORISE_3D_SECURE_PAYMENT_URL)
    @RequireHardLogIn
    public String authorise3DSGetPayment(final HttpServletRequest request) {
        String redirectResult = request.getParameter(REDIRECT_RESULT);
        PaymentDetailsRequest paymentDetailsRequest = new PaymentDetailsRequest();
        if (redirectResult != null && !redirectResult.isEmpty()) {
            PaymentCompletionDetails details = new PaymentCompletionDetails();
            details.setRedirectResult(redirectResult);
            paymentDetailsRequest.details(details);
        } else {
            String payload = request.getParameter(PAYLOAD);
            if (payload != null && !payload.isEmpty()) {
                PaymentCompletionDetails details = new PaymentCompletionDetails();
                details.setPayload(payload);
                paymentDetailsRequest.details(details);
            }
        }
        return authorise3DSPayment(paymentDetailsRequest);
    }


    @PostMapping(value = AUTHORISE_3D_SECURE_PAYMENT_URL)
    @RequireHardLogIn
    public String authorise3DSPostPayment(@RequestBody PaymentDetailsRequest detailsRequest) {
        return authorise3DSPayment(detailsRequest);
    }

    public String authorise3DSPayment(final PaymentDetailsRequest details) {
        LOGGER.info("3DS authorization");
        try {
            OrderData orderData = adyenCheckoutFacade.handle3DSResponse(details);
            LOGGER.debug("Redirecting to confirmation");

            return REDIRECT_PREFIX + ORDER_CONFIRMATION_URL + '/' + orderData.getCode();

        } catch (AdyenNonAuthorizedPaymentException e) {
            LOGGER.debug(NON_AUTHORIZED_ERROR);
            String errorMessage = CHECKOUT_ERROR_AUTHORIZATION_FAILED;
            PaymentDetailsResponse response = e.getPaymentsDetailsResponse();
            if (response != null) {
                if (REFUSED.equals(response.getResultCode())) {
                    LOGGER.info("PaymentResponse " + response.getPspReference() + " is REFUSED: " + response);
                    errorMessage = getErrorMessageByRefusalReason(response.getRefusalReason());
                }
                if (ResultCodeEnum.ERROR.equals(response.getResultCode())) {
                    LOGGER.error("Payment " + response.getPspReference() + " result is error, reason:  "
                            + response.getRefusalReason());
                }
            }
            return getErrorRedirectUrl(errorMessage);
        } catch (CalculationException | InvalidCartException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        LOGGER.warn(REDIRECTING_TO_CART_PAGE);
        return REDIRECT_PREFIX + CART_PREFIX;
    }

    private String getErrorRedirectUrl(String errorMessage) {
        return REDIRECT_PREFIX + SELECT_PAYMENT_METHOD_URL + "/error/" + Base64.getUrlEncoder().encodeToString(errorMessage.getBytes());
    }
}
