/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.controllers.pages;

import com.adyen.constants.ApiConstants.RefusalReason;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.CheckoutPaymentsAction;
import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.util.TerminalAPIUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PlaceOrderForm;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.*;

import static com.adyen.constants.HPPConstants.Response.SHOPPER_LOCALE;
import static com.adyen.model.checkout.PaymentsResponse.ResultCodeEnum.CHALLENGESHOPPER;
import static com.adyen.model.checkout.PaymentsResponse.ResultCodeEnum.IDENTIFYSHOPPER;
import static com.adyen.model.checkout.PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER;
import static com.adyen.model.checkout.PaymentsResponse.ResultCodeEnum.REFUSED;
import static com.adyen.v6.constants.AdyenControllerConstants.CART_PREFIX;
import static com.adyen.v6.constants.AdyenControllerConstants.SELECT_PAYMENT_METHOD_PREFIX;
import static com.adyen.v6.constants.AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX;
import static com.adyen.v6.constants.Adyenv6coreConstants.AFTERPAY_TOUCH;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_APPLEPAY;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_BCMC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_BOLETO;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_MULTIBANCO;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_POS;
import static com.adyen.v6.constants.Adyenv6coreConstants.RATEPAY;
import static com.adyen.v6.facades.impl.DefaultAdyenCheckoutFacade.DETAILS;
import static com.adyen.v6.facades.impl.DefaultAdyenCheckoutFacade.MODEL_CHECKOUT_SHOPPER_HOST;
import static com.adyen.v6.facades.impl.DefaultAdyenCheckoutFacade.MODEL_CLIENT_KEY;
import static com.adyen.v6.facades.impl.DefaultAdyenCheckoutFacade.MODEL_ENVIRONMENT_MODE;

@Controller
@RequestMapping(value = SUMMARY_CHECKOUT_PREFIX)
public class AdyenSummaryCheckoutStepController extends AbstractCheckoutStepController {
    private static final Logger LOGGER = Logger.getLogger(AdyenSummaryCheckoutStepController.class);

    private static final String SUMMARY = "summary";
    private static final String AUTHORISE_3D_SECURE_PAYMENT_URL = "/authorise-3d-adyen-response";
    private static final String CHECKOUT_RESULT_URL = "/checkout-adyen-response";
    private static final String REDIRECT_RESULT = "redirectResult";
    private static final String ACTION = "action";
    private static final String PAYLOAD = "payload";

    private static final int POS_TOTALTIMEOUT_DEFAULT = 130;
    private static final String POS_TOTALTIMEOUT_KEY = "pos.totaltimeout";

    @Resource(name = "siteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @Resource(name = "adyenCheckoutFacade")
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    @PreValidateCheckoutStep(checkoutStep = SUMMARY)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, // NOSONAR
            CommerceCartModificationException {

        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        if (cartData.getEntries() != null && !cartData.getEntries().isEmpty()) {
            for (final OrderEntryData entry : cartData.getEntries()) {
                final String productCode = entry.getProduct().getCode();
                final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode, Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));
                entry.setProduct(product);
            }
        }

        model.addAttribute("cartData", cartData);
        model.addAttribute("allItems", cartData.getEntries());
        model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
        model.addAttribute("deliveryMode", cartData.getDeliveryMode());

        model.addAttribute(new PlaceOrderForm());

        storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        try {
            adyenCheckoutFacade.initializeSummaryData(model);
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.CheckoutSummaryPage;
    }

    @RequestMapping({"/placeOrder"})
    @RequireHardLogIn
    public String placeOrder(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm,
                             final Model model,
                             final HttpServletRequest request,
                             final RedirectAttributes redirectModel) throws CMSItemNotFoundException, CommerceCartModificationException {
        if (validateOrderForm(placeOrderForm, model)) {
            return enterStep(model, redirectModel);
        }

        //Validate the cart
        if (validateCart(redirectModel)) {
            // Invalid cart. Bounce back to the cart page.
            return REDIRECT_PREFIX + CART_PREFIX;
        }

        final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();

        String errorMessage = "checkout.error.authorization.failed";

        String adyenPaymentMethod = cartData.getAdyenPaymentMethod();
        if (adyenPaymentMethod.equals(RATEPAY)) {
            try {
                OrderData orderData = adyenCheckoutFacade.authorisePayment(request, cartData);
                LOGGER.debug("Redirecting to confirmation!");
                return redirectToOrderConfirmationPage(orderData);
            } catch (ApiException e) {
                LOGGER.error("API exception " + e.getError(), e);
            } catch (AdyenNonAuthorizedPaymentException e) {
                LOGGER.debug("Handling AdyenNonAuthorizedPaymentException");
                PaymentResult paymentResult = e.getPaymentResult();
                if (Objects.nonNull(paymentResult) && paymentResult.isRefused()) {
                    errorMessage = getErrorMessageByRefusalReason(paymentResult.getRefusalReason());
                    LOGGER.debug("Payment is refused " + errorMessage);
                }
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
        } else if (PAYMENT_METHOD_POS.equals(adyenPaymentMethod)) {
            try {
                String originalServiceId = Long.toString(System.currentTimeMillis() % 10000000000L);
                request.setAttribute("originalServiceId", originalServiceId);
                Long paymentStartTime = System.currentTimeMillis();
                request.setAttribute("paymentStartTime", paymentStartTime);
                OrderData orderData = adyenCheckoutFacade.initiatePosPayment(request, cartData);
                LOGGER.debug("Redirecting to confirmation!");
                return redirectToOrderConfirmationPage(orderData);
            } catch (SocketTimeoutException e) {
                try {
                    LOGGER.debug("POS request timed out. Checking POS Payment status ");
                    int totalTimeout = POS_TOTALTIMEOUT_DEFAULT;
                    if (configurationService.getConfiguration().containsKey(POS_TOTALTIMEOUT_KEY)) {
                        totalTimeout = configurationService.getConfiguration().getInt(POS_TOTALTIMEOUT_KEY);
                    }
                    request.setAttribute("totalTimeout", totalTimeout);
                    OrderData orderData = adyenCheckoutFacade.checkPosPaymentStatus(request, cartData);
                    LOGGER.debug("Redirecting to confirmation!");
                    return redirectToOrderConfirmationPage(orderData);
                } catch (AdyenNonAuthorizedPaymentException nx) {
                    errorMessage = TerminalAPIUtil.getErrorMessageForNonAuthorizedPosPayment(nx.getTerminalApiResponse());
                    LOGGER.debug("AdyenNonAuthorizedPaymentException " + errorMessage);
                } catch (SocketTimeoutException to) {
                    LOGGER.debug("POS Status request timed out. Returning error message.");
                    errorMessage = "checkout.error.authorization.pos.configuration";
                } catch (Exception ex) {
                    LOGGER.error("Exception", ex);
                }
            } catch (ApiException e) {
                LOGGER.error("API exception " + e.getError(), e);
            } catch (AdyenNonAuthorizedPaymentException e) {
                errorMessage = TerminalAPIUtil.getErrorMessageForNonAuthorizedPosPayment(e.getTerminalApiResponse());
                LOGGER.debug("AdyenNonAuthorizedPaymentException" + errorMessage);
            } catch (Exception e) {
                LOGGER.error("Exception", e);
            }
        } else {
            try {
                cartData.setAdyenReturnUrl(getReturnUrl(cartData.getAdyenPaymentMethod()));
                OrderData orderData = adyenCheckoutFacade.authorisePayment(request, cartData);
                //In case of Boleto, show link to pdf
                if (PAYMENT_METHOD_BOLETO.equals(cartData.getAdyenPaymentMethod())) {
                    addBoletoMessage(redirectModel, orderData.getCode());
                } else if (PAYMENT_METHOD_MULTIBANCO.equals(cartData.getAdyenPaymentMethod())) {
                    addMultibancoMessage(redirectModel, orderData.getCode());
                }
                return redirectToOrderConfirmationPage(orderData);
            } catch (ApiException e) {
                LOGGER.error("API exception ", e);
            } catch (AdyenNonAuthorizedPaymentException e) {
                LOGGER.debug("Handling AdyenNonAuthorizedPaymentException. Checking PaymentResponse.");
                PaymentsResponse paymentsResponse = e.getPaymentsResponse();
                if (REDIRECTSHOPPER == paymentsResponse.getResultCode()) {
                    if (is3DSPaymentMethod(adyenPaymentMethod)) {
                        LOGGER.debug("PaymentResponse resultCode is REDIRECTSHOPPER, redirecting shopper to 3DS flow");
                        return redirectTo3DSValidation(model, paymentsResponse);
                    }
                    if (AFTERPAY_TOUCH.equals(adyenPaymentMethod)) {
                        LOGGER.debug("PaymentResponse resultCode is REDIRECTSHOPPER, redirecting shopper to afterpaytouch page");
                        return REDIRECT_PREFIX + paymentsResponse.getAction().getUrl();
                    }
                    LOGGER.debug("PaymentResponse resultCode is REDIRECTSHOPPER, redirecting shopper to local payment method page");
                    return REDIRECT_PREFIX + paymentsResponse.getAction().getUrl();
                }
                if (REFUSED == paymentsResponse.getResultCode()) {
                    LOGGER.debug("PaymentResponse is REFUSED");
                    errorMessage = getErrorMessageByRefusalReason(paymentsResponse.getRefusalReason());
                }
                if (CHALLENGESHOPPER == paymentsResponse.getResultCode() || IDENTIFYSHOPPER == paymentsResponse.getResultCode()) {
                    LOGGER.debug("PaymentResponse is " + paymentsResponse.getResultCode() + ", redirecting to 3DS2 flow");
                    return redirectTo3DSValidation(model, paymentsResponse);
                }
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
        }
        LOGGER.debug("Redirecting to summary view");
        GlobalMessages.addErrorMessage(model, errorMessage);
        return enterStep(model, redirectModel);
    }

    @RequestMapping(value = AUTHORISE_3D_SECURE_PAYMENT_URL, method = RequestMethod.GET)
    @RequireHardLogIn
    public String authorise3DS1Payment(final RedirectAttributes redirectModel,
                                       final HttpServletRequest request) {
        String redirectResult = request.getParameter(REDIRECT_RESULT);
        try {
            OrderData orderData = adyenCheckoutFacade.handle3DSResponse(Collections.singletonMap(REDIRECT_RESULT, redirectResult));
            LOGGER.debug("Redirecting to confirmation");
            return redirectToOrderConfirmationPage(orderData);
        } catch (AdyenNonAuthorizedPaymentException e) {
            LOGGER.debug("Handling AdyenNonAuthorizedPaymentException");
            String errorMessage = "checkout.error.authorization.failed";
            PaymentsDetailsResponse response = e.getPaymentsDetailsResponse();
            if (response != null && response.getResultCode() == PaymentsResponse.ResultCodeEnum.REFUSED) {
                LOGGER.debug("PaymentResponse is REFUSED: " + response);
                errorMessage = getErrorMessageByRefusalReason(response.getRefusalReason());
            }
            LOGGER.debug("Redirecting to select payment method..");
            return redirectToSelectPaymentMethodWithError(redirectModel, errorMessage);
        } catch (CalculationException | InvalidCartException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        LOGGER.warn("Redirecting to cart page...");
        return REDIRECT_PREFIX + CART_PREFIX;
    }

    @RequestMapping(value = AUTHORISE_3D_SECURE_PAYMENT_URL, method = RequestMethod.POST)
    @RequireHardLogIn
    public String authorise3DSPayment(final RedirectAttributes redirectModel,
                                      final HttpServletRequest request) {

        String detailsJson = request.getParameter(DETAILS);
        try {
            Map<String, String> details = parseDetailsFromComponent(detailsJson);
            OrderData orderData = adyenCheckoutFacade.handle3DSResponse(details);
            LOGGER.debug("Redirecting to confirmation");
            return redirectToOrderConfirmationPage(orderData);
        } catch (AdyenNonAuthorizedPaymentException e) {
            LOGGER.debug("Handling AdyenNonAuthorizedPaymentException. Checking PaymentResponse.");
            String errorMessage = "checkout.error.authorization.failed";
            PaymentsDetailsResponse paymentsDetailsResponse = e.getPaymentsDetailsResponse();
            if (paymentsDetailsResponse != null) {
                if (paymentsDetailsResponse.getResultCode() == PaymentsResponse.ResultCodeEnum.REFUSED) {
                    errorMessage = getErrorMessageByRefusalReason(paymentsDetailsResponse.getRefusalReason());
                    LOGGER.debug("PaymentResponse is REFUSED: " + errorMessage);
                }
            }
            return redirectToSelectPaymentMethodWithError(redirectModel, errorMessage);
        } catch (CalculationException | InvalidCartException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        LOGGER.warn("Redirecting to cart page...");
        return REDIRECT_PREFIX + CART_PREFIX;
    }

    @RequestMapping(value = CHECKOUT_RESULT_URL, method = RequestMethod.GET)
    @RequireHardLogIn
    public String handleAdyenResponse(final HttpServletRequest request, final RedirectAttributes redirectModel) {
        String redirectResult = request.getParameter(REDIRECT_RESULT);
        HashMap<String, String> details = new HashMap<>();

        if (redirectResult != null && !redirectResult.isEmpty()) {
            details.put(REDIRECT_RESULT, redirectResult);
        } else if (StringUtils.isNotEmpty(request.getParameter(PAYLOAD))) {
            details.put(REDIRECT_RESULT, request.getParameter(PAYLOAD));
        }

        try {
            PaymentsDetailsResponse response = adyenCheckoutFacade.handleRedirectPayload(details);

            switch (response.getResultCode()) {
                case AUTHORISED:
                case RECEIVED:
                    LOGGER.debug("Redirecting to order confirmation");
                    OrderData orderData = orderFacade.getOrderDetailsForCodeWithoutUser(response.getMerchantReference());
                    if (orderData == null) {
                        throw new Exception("Order not found");
                    }
                    return redirectToOrderConfirmationPage(orderData);
                case REFUSED:
                    return redirectToSelectPaymentMethodWithError(redirectModel, "checkout.error.authorization.payment.refused");
                case CANCELLED:
                    return redirectToSelectPaymentMethodWithError(redirectModel, "checkout.error.authorization.payment.cancelled");
                default:
                    return redirectToSelectPaymentMethodWithError(redirectModel, "checkout.error.authorization.payment.error");
            }
        } catch (CalculationException | InvalidCartException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        LOGGER.warn("Redirecting to cart page...");
        return REDIRECT_PREFIX + CART_PREFIX;
    }

    /**
     * Adds a flash message containing the Boleto pdf
     */
    private void addBoletoMessage(RedirectAttributes redirectModel, final String orderCode) {
        //Use OrderFacade to force execution of AbstractOrder populators
        OrderData orderData = orderFacade.getOrderDetailsForCodeWithoutUser(orderCode);

        GlobalMessages.addFlashMessage(redirectModel,
                GlobalMessages.INFO_MESSAGES_HOLDER,
                "Boleto PDf: <a target=\"_blank\" href=\"" + orderData.getAdyenBoletoUrl() + "\" title=\"Boleto PDF\">Download</a>");
    }


    /**
     * Adds a flash message containing the Multibanco response fields
     */
    private void addMultibancoMessage(RedirectAttributes redirectModel, final String orderCode) {
        //Use OrderFacade to force execution of AbstractOrder populators

        OrderData orderData = orderFacade.getOrderDetailsForCodeWithoutUser(orderCode);

        GlobalMessages.addFlashMessage(redirectModel,
                GlobalMessages.INFO_MESSAGES_HOLDER,
                "<p> Multibanco order summary "
                        + "</p>"
                        + "<p> Amount: "
                        + orderData.getAdyenMultibancoAmount()
                        + "</p>"
                        + "<p> Entity: "
                        + orderData.getAdyenMultibancoEntity()
                        + "</p>"
                        + "<p> Deadline: "
                        + orderData.getAdyenMultibancoDeadline()
                        + "</p>"
                        + "<p> Reference: "
                        + orderData.getAdyenMultibancoReference()
                        + "</p>");
    }


    private boolean is3DSPaymentMethod(String adyenPaymentMethod) {
        return adyenPaymentMethod.equals(PAYMENT_METHOD_CC) || adyenPaymentMethod.equals(PAYMENT_METHOD_BCMC) || adyenPaymentMethod.indexOf(PAYMENT_METHOD_ONECLICK) == 0;
    }

    private String redirectToSelectPaymentMethodWithError(final RedirectAttributes redirectModel, final String messageKey) {
        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, messageKey);

        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        if (cartData == null || cartData.getDeliveryAddress() == null) {
            LOGGER.debug("Redirecting to cart with error: " + messageKey);
            return REDIRECT_PREFIX + CART_PREFIX;
        }

        LOGGER.debug("Redirecting to payment method with error: " + messageKey);
        return REDIRECT_PREFIX + SELECT_PAYMENT_METHOD_PREFIX;
    }

    private String redirectTo3DSValidation(Model model, PaymentsResponse paymentsResponse) {
        CheckoutPaymentsAction action = paymentsResponse.getAction();

        model.addAttribute(MODEL_CLIENT_KEY, adyenCheckoutFacade.getClientKey());
        model.addAttribute(MODEL_CHECKOUT_SHOPPER_HOST, adyenCheckoutFacade.getCheckoutShopperHost());
        model.addAttribute(MODEL_ENVIRONMENT_MODE, adyenCheckoutFacade.getEnvironmentMode());
        model.addAttribute(SHOPPER_LOCALE, adyenCheckoutFacade.getShopperLocale());
        model.addAttribute(ACTION, new Gson().toJson(action));
        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.Validate3DSPaymentPage;
    }

    private String getReturnUrl(String adyenPaymentMethod) {
        String url;
        if (is3DSPaymentMethod(adyenPaymentMethod)) {
            url = SUMMARY_CHECKOUT_PREFIX + AUTHORISE_3D_SECURE_PAYMENT_URL;
        } else {
            url = SUMMARY_CHECKOUT_PREFIX + CHECKOUT_RESULT_URL;
        }
        BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();

        return siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSite, true, url);
    }

    private String getErrorMessageByRefusalReason(String refusalReason) {
        String errorMessage;

        switch (refusalReason) {
            case RefusalReason.TRANSACTION_NOT_PERMITTED:
                errorMessage = "checkout.error.authorization.transaction.not.permitted";
                break;
            case RefusalReason.CVC_DECLINED:
                errorMessage = "checkout.error.authorization.cvc.declined";
                break;
            case RefusalReason.RESTRICTED_CARD:
                errorMessage = "checkout.error.authorization.restricted.card";
                break;
            case RefusalReason.PAYMENT_DETAIL_NOT_FOUND:
                errorMessage = "checkout.error.authorization.payment.detail.not.found";
                break;
            default:
                errorMessage = "checkout.error.authorization.payment.refused";
        }

        return errorMessage;
    }

    /**
     * Validates the order form before to filter out invalid order states
     *
     * @param placeOrderForm The spring form of the order being submitted
     * @param model          A spring Model
     * @return True if the order form is invalid and false if everything is valid.
     */
    protected boolean validateOrderForm(final PlaceOrderForm placeOrderForm, final Model model) {
        final String securityCode = placeOrderForm.getSecurityCode();
        boolean invalid = false;

        if (getCheckoutFlowFacade().hasNoDeliveryAddress()) {
            GlobalMessages.addErrorMessage(model, "checkout.deliveryAddress.notSelected");
            invalid = true;
        }

        if (getCheckoutFlowFacade().hasNoDeliveryMode()) {
            GlobalMessages.addErrorMessage(model, "checkout.deliveryMethod.notSelected");
            invalid = true;
        }

        if (getCheckoutFlowFacade().hasNoPaymentInfo()) {
            GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.notSelected");
            invalid = true;
        } else {
            // Only require the Security Code to be entered on the summary page if the SubscriptionPciOption is set to Default.
            if (CheckoutPciOptionEnum.DEFAULT.equals(getCheckoutFlowFacade().getSubscriptionPciOption()) && StringUtils.isBlank(securityCode)) {
                GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
                invalid = true;
            }
        }

        if (!placeOrderForm.isTermsCheck()) {
            GlobalMessages.addErrorMessage(model, "checkout.error.terms.not.accepted");
            invalid = true;
            return invalid;
        }
        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        if (!getCheckoutFacade().containsTaxValues()) {
            LOGGER.error(String.format("Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue", cartData.getCode()));
            GlobalMessages.addErrorMessage(model, "checkout.error.tax.missing");
            invalid = true;
        }

        if (!cartData.isCalculated()) {
            LOGGER.error(String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
            GlobalMessages.addErrorMessage(model, "checkout.error.cart.notcalculated");
            invalid = true;
        }

        return invalid;
    }

    @RequestMapping(value = "/component-result", method = RequestMethod.POST)
    @RequireHardLogIn
    public String handleComponentResult(final HttpServletRequest request,
                                        final Model model,
                                        final RedirectAttributes redirectAttributes)
            throws CMSItemNotFoundException, InvalidCartException, CalculationException, CommerceCartModificationException {
        String resultData = request.getParameter("resultData");
        String isResultError = request.getParameter("isResultError");

        LOGGER.debug("isResultError=" + isResultError + "\nresultData=" + resultData);

        String errorMessageKey = "checkout.error.authorization.payment.error";

        if (isValidResult(resultData, isResultError)) {
            try {
                OrderData orderData = adyenCheckoutFacade.handleComponentResult(resultData);
                return redirectToOrderConfirmationPage(orderData);
            } catch (AdyenNonAuthorizedPaymentException e) {
                LOGGER.debug("Handling AdyenNonAuthorizedPaymentException");
                PaymentsResponse paymentsResponse = e.getPaymentsResponse();
                if (paymentsResponse != null && paymentsResponse.getResultCode() != null) {
                    switch (paymentsResponse.getResultCode()) {
                        case REDIRECTSHOPPER:
                            LOGGER.debug("Component PaymentResponse resultCode is REDIRECTSHOPPER, redirecting shopper to 3DS flow");
                            return redirectTo3DSValidation(model, paymentsResponse);
                        case REFUSED:
                            errorMessageKey = "checkout.error.authorization.payment.refused";
                            break;
                        case CANCELLED:
                            errorMessageKey = "checkout.error.authorization.payment.cancelled";
                            break;
                        default:
                            break;
                    }
                }
            } catch (InvalidCartException e) {
                LOGGER.error("Error retrieving order", e);
            } catch (Exception e) {
                LOGGER.error("Unexpected error while validating component payment result", e);
            }
        } else {
            if (StringUtils.isNotBlank(resultData)) {
                errorMessageKey = resultData;
                adyenCheckoutFacade.restoreCartFromOrderCodeInSession();
            }
        }

        return redirectToOrderSummaryWithError(model, redirectAttributes, errorMessageKey);
    }

    private String redirectToOrderSummaryWithError(final Model model,
                                                   final RedirectAttributes redirectAttributes,
                                                   final String messageKey) throws CommerceCartModificationException, CMSItemNotFoundException {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        if (cartData == null || cartData.getAdyenPaymentMethod() == null
                || PAYMENT_METHOD_APPLEPAY.equals(cartData.getAdyenPaymentMethod())) {
            return redirectToSelectPaymentMethodWithError(redirectAttributes, messageKey);
        }

        LOGGER.debug("Redirecting to summary view with error: " + messageKey);
        GlobalMessages.addErrorMessage(model, messageKey);
        return enterStep(model, redirectAttributes);
    }

    private boolean isValidResult(String resultData, String isResultError) {
        return (StringUtils.isBlank(isResultError) || !Boolean.parseBoolean(isResultError))
                && StringUtils.isNotBlank(resultData);
    }

    private Map<String, String> parseDetailsFromComponent(String details) {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        return gson.fromJson(details, mapType);
    }

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String back(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().previousStep();
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String next(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().nextStep();
    }

    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(SUMMARY);
    }
}
