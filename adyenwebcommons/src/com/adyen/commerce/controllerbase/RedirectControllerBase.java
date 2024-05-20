package com.adyen.commerce.controllerbase;

import com.adyen.model.checkout.PaymentCompletionDetails;
import com.adyen.model.checkout.PaymentDetailsRequest;
import com.adyen.model.checkout.PaymentDetailsResponse;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import static com.adyen.commerce.util.ErrorMessageUtil.getErrorMessageByRefusalReason;
import static com.adyen.model.checkout.PaymentDetailsResponse.ResultCodeEnum;
import static com.adyen.model.checkout.PaymentDetailsResponse.ResultCodeEnum.REFUSED;


public abstract class RedirectControllerBase {

    private static final Logger LOGGER = Logger.getLogger(RedirectControllerBase.class);
    private static final String REDIRECT_RESULT = "redirectResult";
    private static final String PAYLOAD = "payload";
    private static final String NON_AUTHORIZED_ERROR = "Handling AdyenNonAuthorizedPaymentException. Checking PaymentResponse.";
    private static final String CHECKOUT_ERROR_AUTHORIZATION_FAILED = "checkout.error.authorization.failed";
    private static final String REDIRECTING_TO_CART_PAGE = "Redirecting to cart page...";


    public String authoriseRedirectGetPayment(final HttpServletRequest request) {
        return authoriseRedirectGetPayment(request, null, null, null);
    }

    public String authoriseRedirectGetPayment(final HttpServletRequest request, final String baseSiteId, final String locale,
                                              final String currencyISO) {
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
        return authoriseRedirectPayment(paymentDetailsRequest, baseSiteId, locale, currencyISO);
    }

    public String authoriseRedirectPostPayment(final PaymentDetailsRequest detailsRequest) {
        return authoriseRedirectPostPayment(detailsRequest, null, null, null);
    }

    public String authoriseRedirectPostPayment(final PaymentDetailsRequest detailsRequest, final String baseSiteId, final String locale,
                                               final String currencyISO) {
        return authoriseRedirectPayment(detailsRequest, baseSiteId, locale, currencyISO);
    }

    private String authoriseRedirectPayment(final PaymentDetailsRequest details, final String baseSiteId, final String locale, final String currencyISO) {
        LOGGER.info("3DS authorization");
        try {
            OrderData orderData = getAdyenCheckoutFacade().handle3DSResponse(details);
            LOGGER.debug("Redirecting to confirmation");

            return getOrderConfirmationUrl(orderData.getCode(), baseSiteId, locale, currencyISO);

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
            return getErrorRedirectUrl(errorMessage, baseSiteId, locale, currencyISO);
        } catch (CalculationException | InvalidCartException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        LOGGER.warn(REDIRECTING_TO_CART_PAGE);
        return getCartUrl(baseSiteId, locale, currencyISO);
    }

    public abstract String getErrorRedirectUrl(String errorMessage, String baseSiteId, String locale, String currencyISO);

    public abstract String getOrderConfirmationUrl(String orderCode, String baseSiteId, String locale, String currencyISO);

    public abstract String getCartUrl(String baseSiteId, String locale, String currencyISO);

    public abstract AdyenCheckoutFacade getAdyenCheckoutFacade();
}
