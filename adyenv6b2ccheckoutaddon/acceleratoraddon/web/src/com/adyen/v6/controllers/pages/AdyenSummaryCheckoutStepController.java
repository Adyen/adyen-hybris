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

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.adyen.constants.ApiConstants.RefusalReason;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
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
import de.hybris.platform.site.BaseSiteService;
import static com.adyen.constants.ApiConstants.Redirect.Data.MD;
import static com.adyen.constants.ApiConstants.Redirect.Data.PAREQ;
import static com.adyen.constants.ApiConstants.Redirect.Data.PAYMENT_DATA;
import static com.adyen.constants.ApiConstants.ThreeDS2Property.CHALLENGE_TOKEN;
import static com.adyen.constants.ApiConstants.ThreeDS2Property.FINGERPRINT_TOKEN;
import static com.adyen.constants.ApiConstants.ThreeDS2Property.THREEDS2_CHALLENGE_TOKEN;
import static com.adyen.constants.ApiConstants.ThreeDS2Property.THREEDS2_FINGERPRINT_TOKEN;
import static com.adyen.constants.BrandCodes.PAYPAL_ECS;
import static com.adyen.constants.HPPConstants.Response.SHOPPER_LOCALE;
import static com.adyen.model.checkout.PaymentsResponse.ResultCodeEnum.CHALLENGESHOPPER;
import static com.adyen.model.checkout.PaymentsResponse.ResultCodeEnum.IDENTIFYSHOPPER;
import static com.adyen.model.checkout.PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER;
import static com.adyen.model.checkout.PaymentsResponse.ResultCodeEnum.REFUSED;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_BOLETO;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;
import static com.adyen.v6.constants.Adyenv6coreConstants.RATEPAY;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_MULTIBANCO;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.MODEL_CHECKOUT_SHOPPER_HOST;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.MODEL_ENVIRONMENT_MODE;

@Controller
@RequestMapping(value = AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX)
public class AdyenSummaryCheckoutStepController extends AbstractCheckoutStepController {
    private static final Logger LOGGER = Logger.getLogger(AdyenSummaryCheckoutStepController.class);

    private final static String SUMMARY = "summary";
    private static final String AUTHORISE_3D_SECURE_PAYMENT_URL = "/authorise-3d-adyen-response";
    private static final String HPP_RESULT_URL = "/hpp-adyen-response";
    private static final String ADYEN_PAYLOAD = "payload";
    private static final String REDIRECT_RESULT = "redirectResult";

    @Resource(name = "siteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @Resource(name = "adyenCheckoutFacade")
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    @PreValidateCheckoutStep(checkoutStep = SUMMARY)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, // NOSONAR
            CommerceCartModificationException {

        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        if (cartData.getEntries() != null && ! cartData.getEntries().isEmpty()) {
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

        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.CheckoutSummaryPage;
    }

    @RequestMapping({"/placeOrder"})
    @RequireHardLogIn
    public String placeOrder(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm,
                             final Model model,
                             final HttpServletRequest request,
                             final RedirectAttributes redirectModel) throws CMSItemNotFoundException, // NOSONAR
            InvalidCartException, CommerceCartModificationException {
        if (validateOrderForm(placeOrderForm, model)) {
            return enterStep(model, redirectModel);
        }

        //Validate the cart
        if (validateCart(redirectModel)) {
            // Invalid cart. Bounce back to the cart page.
            return REDIRECT_PREFIX + "/cart";
        }

        final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();

        String errorMessage = "checkout.error.authorization.failed";

        String adyenPaymentMethod = cartData.getAdyenPaymentMethod();

        if (canUseAPI(adyenPaymentMethod)) {
            try {
                OrderData orderData = adyenCheckoutFacade.authorisePayment(request, cartData);
                LOGGER.debug("Redirecting to confirmation!");
                return redirectToOrderConfirmationPage(orderData);
            } catch (ApiException e) {
                LOGGER.error("API exception " + e.getError(), e);
            } catch (AdyenNonAuthorizedPaymentException e) {
                LOGGER.debug("AdyenNonAuthorizedPaymentException", e);
                PaymentResult paymentResult = e.getPaymentResult();
                if (paymentResult.isRefused()) {
                    errorMessage = getErrorMessageByRefusalReason(paymentResult.getRefusalReason());
                }
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
        } else {
            try {
                cartData.setAdyenReturnUrl(getReturnUrl());
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
                LOGGER.debug("AdyenNonAuthorizedPaymentException", e);
                PaymentsResponse paymentsResponse = e.getPaymentsResponse();
                if (REDIRECTSHOPPER == paymentsResponse.getResultCode()) {
                    if (adyenPaymentMethod.equals(PAYMENT_METHOD_CC) || adyenPaymentMethod.indexOf(PAYMENT_METHOD_ONECLICK) == 0) {
                        final String termUrl = getTermUrl();
                        model.addAttribute("paReq", paymentsResponse.getRedirect().getData().get(PAREQ));
                        model.addAttribute("md", paymentsResponse.getRedirect().getData().get(MD));
                        model.addAttribute("issuerUrl", paymentsResponse.getRedirect().getUrl());
                        model.addAttribute("termUrl", termUrl);
                        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.Validate3DSecurePaymentPage;
                    }
                    return REDIRECT_PREFIX + paymentsResponse.getRedirect().getUrl();
                }
                if (REFUSED == paymentsResponse.getResultCode()) {
                    errorMessage = getErrorMessageByRefusalReason(paymentsResponse.getRefusalReason());
                }
                if (IDENTIFYSHOPPER == paymentsResponse.getResultCode()) {
                    if (adyenPaymentMethod.equals(PAYMENT_METHOD_CC) || adyenPaymentMethod.indexOf(PAYMENT_METHOD_ONECLICK) == 0) {
                        model.addAttribute(MODEL_CHECKOUT_SHOPPER_HOST, adyenCheckoutFacade.getCheckoutShopperHost());
                        model.addAttribute(MODEL_ENVIRONMENT_MODE, adyenCheckoutFacade.getEnvironmentMode());
                        model.addAttribute(SHOPPER_LOCALE, adyenCheckoutFacade.getShopperLocale());
                        model.addAttribute(PAYMENT_DATA, paymentsResponse.getPaymentData());
                        model.addAttribute(FINGERPRINT_TOKEN, paymentsResponse.getAuthentication().get(THREEDS2_FINGERPRINT_TOKEN));
                        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.Validate3DS2PaymentPage;
                    }
                }
                if (CHALLENGESHOPPER == paymentsResponse.getResultCode()) {
                    if (adyenPaymentMethod.equals(PAYMENT_METHOD_CC) || adyenPaymentMethod.indexOf(PAYMENT_METHOD_ONECLICK) == 0) {
                        model.addAttribute(MODEL_CHECKOUT_SHOPPER_HOST, adyenCheckoutFacade.getCheckoutShopperHost());
                        model.addAttribute(MODEL_ENVIRONMENT_MODE, adyenCheckoutFacade.getEnvironmentMode());
                        model.addAttribute(SHOPPER_LOCALE, adyenCheckoutFacade.getShopperLocale());
                        model.addAttribute(PAYMENT_DATA, paymentsResponse.getPaymentData());
                        model.addAttribute(CHALLENGE_TOKEN, paymentsResponse.getAuthentication().get(THREEDS2_CHALLENGE_TOKEN));
                        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.Validate3DS2PaymentPage;
                    }
                }
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
        }
        LOGGER.debug("Redirecting to summary view");
        GlobalMessages.addErrorMessage(model, errorMessage);
        return enterStep(model, redirectModel);
    }

    @RequestMapping(value = "/3ds2-adyen-response", method = RequestMethod.POST)
    @RequireHardLogIn
    public String authorise3DS2Payment(final Model model,
                                       final RedirectAttributes redirectModel,
                                       final HttpServletRequest request) throws CMSItemNotFoundException, CommerceCartModificationException, UnknownHostException {

        String errorMessage = "checkout.error.authorization.failed";
        try {
            OrderData orderData = adyenCheckoutFacade.handle3DS2Response(request);
            LOGGER.debug("Redirecting to confirmation");
            return redirectToOrderConfirmationPage(orderData);
        } catch (AdyenNonAuthorizedPaymentException e) {
            PaymentsResponse paymentsResponse = e.getPaymentsResponse();
            if (paymentsResponse != null && paymentsResponse.getResultCode() == CHALLENGESHOPPER) {
                model.addAttribute(MODEL_CHECKOUT_SHOPPER_HOST, adyenCheckoutFacade.getCheckoutShopperHost());
                model.addAttribute(MODEL_ENVIRONMENT_MODE, adyenCheckoutFacade.getEnvironmentMode());
                model.addAttribute(SHOPPER_LOCALE, adyenCheckoutFacade.getShopperLocale());
                model.addAttribute(PAYMENT_DATA, paymentsResponse.getPaymentData());
                model.addAttribute(CHALLENGE_TOKEN, paymentsResponse.getAuthentication().get("threeds2.challengeToken"));
                return AdyenControllerConstants.Views.Pages.MultiStepCheckout.Validate3DS2PaymentPage;
            }
            if (paymentsResponse != null && paymentsResponse.getResultCode() == PaymentsResponse.ResultCodeEnum.REFUSED) {
                errorMessage = getErrorMessageByRefusalReason(paymentsResponse.getRefusalReason());
            }
        } catch (Exception e) {
            LOGGER.debug("Redirecting to summary" + errorMessage);
            return redirectToSummaryWithError(redirectModel, errorMessage);
        }
        LOGGER.debug("Redirecting to final step of checkout" + errorMessage);
        return redirectToSummaryWithError(redirectModel, errorMessage);
    }

    @RequestMapping(value = AUTHORISE_3D_SECURE_PAYMENT_URL, method = RequestMethod.POST)
    @RequireHardLogIn
    public String authorise3DSecurePayment(final Model model,
                                           final RedirectAttributes redirectModel,
                                           final HttpServletRequest request) throws CMSItemNotFoundException, CommerceCartModificationException, UnknownHostException {
        String errorMessage = "checkout.error.authorization.failed";

        try {
            OrderData orderData = adyenCheckoutFacade.handle3DResponse(request);

            LOGGER.debug("Redirecting to confirmation");
            return redirectToOrderConfirmationPage(orderData);
        } catch (AdyenNonAuthorizedPaymentException e) {
            PaymentsResponse paymentsResponse = e.getPaymentsResponse();
            if (paymentsResponse != null && paymentsResponse.getResultCode() == PaymentsResponse.ResultCodeEnum.REFUSED) {
                LOGGER.debug("AdyenNonAuthorizedPaymentException with paymentsResponse: " + paymentsResponse, e);
                errorMessage = getErrorMessageByRefusalReason(paymentsResponse.getRefusalReason());
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            return REDIRECT_PREFIX + "/cart";
        }

        LOGGER.debug("Redirecting to final step of checkout");
        return redirectToSummaryWithError(redirectModel, errorMessage);
    }

    @RequestMapping(value = HPP_RESULT_URL, method = RequestMethod.GET)
    @RequireHardLogIn
    public String handleAdyenResponse(final HttpServletRequest request, final RedirectAttributes redirectModel) {
        String payload = request.getParameter(ADYEN_PAYLOAD);
        String redirectResult = request.getParameter(REDIRECT_RESULT);
        HashMap<String, String> details = new HashMap<>();

        if (payload != null && ! payload.isEmpty()) {
            details.put(ADYEN_PAYLOAD, payload);
        }
        if (redirectResult != null && ! redirectResult.isEmpty()) {
            details.put(REDIRECT_RESULT, redirectResult);
        }

        try {
            PaymentsResponse response = adyenCheckoutFacade.handleRedirectPayload(details);

            switch (response.getResultCode()) {
                case AUTHORISED:
                case RECEIVED:
                    LOGGER.debug("Redirecting to order confirmation");
                    OrderData orderData = orderFacade.getOrderDetailsForCode(response.getMerchantReference());
                    if (orderData == null) {
                        throw new Exception("Order not found");
                    }
                    return redirectToOrderConfirmationPage(orderData);
                case REFUSED:
                    return redirectToSummaryWithError(redirectModel, "checkout.error.authorization.payment.refused");
                case CANCELLED:
                    return redirectToSummaryWithError(redirectModel, "checkout.error.authorization.payment.cancelled");
                default:
                    return redirectToSummaryWithError(redirectModel, "checkout.error.authorization.payment.error");
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }

        LOGGER.debug("Redirecting to cart..");
        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "checkout.error.authorization.payment.error");
        return REDIRECT_PREFIX + "/cart";
    }

    /**
     * Adds a flash message containing the Boleto pdf
     */
    private void addBoletoMessage(RedirectAttributes redirectModel, final String orderCode) {
        //Use OrderFacade to force execution of AbstractOrder populators
        OrderData orderData = orderFacade.getOrderDetailsForCode(orderCode);

        GlobalMessages.addFlashMessage(redirectModel,
                                       GlobalMessages.INFO_MESSAGES_HOLDER,
                                       "Boleto PDf: <a target=\"_blank\" href=\"" + orderData.getAdyenBoletoUrl() + "\" title=\"Boleto PDF\">Download</a>");
    }


    /**
     * Adds a flash message containing the Multibanco response fields
     */
    private void addMultibancoMessage(RedirectAttributes redirectModel, final String orderCode) {
        //Use OrderFacade to force execution of AbstractOrder populators

        OrderData orderData = orderFacade.getOrderDetailsForCode(orderCode);

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

    /**
     * Methods supported via API
     * Credit Cards/OneClick
     * OpenInvoice
     * Boleto
     */
    private boolean canUseAPI(String paymentMethod) {
        Set<String> apiPaymentMethods = new HashSet<String>();

        apiPaymentMethods.add(PAYPAL_ECS);
        apiPaymentMethods.add(RATEPAY);

        return apiPaymentMethods.contains(paymentMethod);
    }

    private String redirectToSummaryWithError(final RedirectAttributes redirectModel, final String messageKey) {
        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, messageKey);

        return REDIRECT_PREFIX + AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX + "/view";
    }

    private String getTermUrl() {
        String authorise3DSecureUrl = AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX + AUTHORISE_3D_SECURE_PAYMENT_URL;
        BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();

        return siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSite, true, authorise3DSecureUrl);
    }

    /**
     * Construct the URL for HPP redirect
     */
    private String getReturnUrl() {
        String url = AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX + HPP_RESULT_URL;
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

        if (! placeOrderForm.isTermsCheck()) {
            GlobalMessages.addErrorMessage(model, "checkout.error.terms.not.accepted");
            invalid = true;
            return invalid;
        }
        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        if (! getCheckoutFacade().containsTaxValues()) {
            LOGGER.error(String.format("Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue", cartData.getCode()));
            GlobalMessages.addErrorMessage(model, "checkout.error.tax.missing");
            invalid = true;
        }

        if (! cartData.isCalculated()) {
            LOGGER.error(String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
            GlobalMessages.addErrorMessage(model, "checkout.error.cart.notcalculated");
            invalid = true;
        }

        return invalid;
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
