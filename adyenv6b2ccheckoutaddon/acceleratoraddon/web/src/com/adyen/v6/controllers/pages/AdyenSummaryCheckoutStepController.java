package com.adyen.v6.controllers.pages;

import com.adyen.constants.ApiConstants.RefusalReason;
import com.adyen.constants.HPPConstants;
import com.adyen.model.PaymentResult;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PlaceOrderForm;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yacceleratorstorefront.controllers.pages.checkout.steps.SummaryCheckoutStepController;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Map;

import static com.adyen.v6.constants.Adyenv6coreConstants.OPENINVOICE_METHODS_API;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;

@Controller
@RequestMapping(value = AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX)
public class AdyenSummaryCheckoutStepController extends SummaryCheckoutStepController {
    private static final Logger LOGGER = Logger.getLogger(AdyenSummaryCheckoutStepController.class);

    private final static String SUMMARY = "summary";
    private static final String AUTHORISE_3D_SECURE_PAYMENT_URL = "/authorise-3d-adyen-response";
    private static final String HPP_RESULT_URL = "/hpp-adyen-response";

    @Resource(name = "siteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @Resource(name = "adyenCheckoutFacade")
    private AdyenCheckoutFacade adyenCheckoutFacade;

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
                final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode,
                        Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));
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
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.CheckoutSummaryPage;
    }

    @RequestMapping({"/placeOrder"})
    @RequireHardLogIn
    @Override
    public String placeOrder(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm, final Model model,
                             final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException, // NOSONAR
            InvalidCartException, CommerceCartModificationException {
        if (validateOrderForm(placeOrderForm, model)) {
            return enterStep(model, redirectModel);
        }

        //Validate the cart
        if (validateCart(redirectModel)) {
            // Invalid cart. Bounce back to the cart page.
            return REDIRECT_PREFIX + "/cart";
        }

        //TODO: validate Cart and selectedReference
        final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();

        String errorMessage = "checkout.error.authorization.failed";
        //Handle CreditCard/oneClick payments
        if (PAYMENT_METHOD_CC.equals(cartData.getAdyenPaymentMethod())
                || cartData.getAdyenPaymentMethod().indexOf(PAYMENT_METHOD_ONECLICK) == 0
                || (OPENINVOICE_METHODS_API.contains(cartData.getAdyenPaymentMethod()))
                ) {
            try {
                OrderData orderData = adyenCheckoutFacade.authoriseCardPayment(request, cartData);

                LOGGER.debug("Redirecting to confirmation!");
                return redirectToOrderConfirmationPage(orderData);
            } catch (ApiException e) {
                LOGGER.error("API exception " + e.getError());
            } catch (AdyenNonAuthorizedPaymentException e) {
                PaymentResult paymentResult = e.getPaymentResult();
                if (paymentResult.isRedirectShopper()) {
                    final String termUrl = getTermUrl();

                    model.addAttribute("paReq", paymentResult.getPaRequest());
                    model.addAttribute("md", paymentResult.getMd());
                    model.addAttribute("issuerUrl", paymentResult.getIssuerUrl());
                    model.addAttribute("termUrl", termUrl);

                    return AdyenControllerConstants.Views.Pages.MultiStepCheckout.Validate3DSecurePaymentPage;
                }
                if (paymentResult.isRefused()) {
                    errorMessage = getErrorMessageByRefusalReason(paymentResult.getRefusalReason());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        } else {
            //Handle APM
            try {
                Map<String, String> hppFormData = adyenCheckoutFacade.initializeHostedPayment(cartData, getHppRedirectUrl());

                //HPP data
                model.addAttribute("hppUrl", adyenCheckoutFacade.getHppUrl());
                model.addAttribute("hppFormData", hppFormData);

                return AdyenControllerConstants.Views.Pages.MultiStepCheckout.HppPaymentPage;
            } catch (SignatureException e) {
                LOGGER.error(e);
            }

        }

        LOGGER.debug("Redirecting to summary view");
        GlobalMessages.addErrorMessage(model, errorMessage);
        return enterStep(model, redirectModel);
    }

    @RequestMapping(value = AUTHORISE_3D_SECURE_PAYMENT_URL, method = RequestMethod.POST)
    @RequireHardLogIn
    public String authorise3DSecurePayment(final Model model,
                                           final RedirectAttributes redirectModel,
                                           final HttpServletRequest request)
            throws CMSItemNotFoundException, CommerceCartModificationException, UnknownHostException {
        String errorMessage = "checkout.error.authorization.failed";

        try {
            OrderData orderData = adyenCheckoutFacade.handle3DResponse(request);

            LOGGER.debug("Redirecting to confirmation");
            return redirectToOrderConfirmationPage(orderData);
        } catch (AdyenNonAuthorizedPaymentException e) {
            PaymentResult paymentResult = e.getPaymentResult();
            LOGGER.debug("AdyenNonAuthorizedPaymentException with paymentResult: " + paymentResult);

            if (paymentResult.isRefused()) {
                errorMessage = getErrorMessageByRefusalReason(paymentResult.getRefusalReason());
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
    public String handleHPPResponse(final HttpServletRequest request,
                                    final RedirectAttributes redirectModel) {
        //Compose HPP response data map
        String authResult = request.getParameter(HPPConstants.Response.AUTH_RESULT);

        try {
            OrderData orderData = adyenCheckoutFacade.handleHPPResponse(request);

            switch (authResult) {
                case HPPConstants.Response.AUTH_RESULT_AUTHORISED:
                case HPPConstants.Response.AUTH_RESULT_PENDING:
                    LOGGER.debug("Redirecting to order confirmation");
                    return redirectToOrderConfirmationPage(orderData);
                case HPPConstants.Response.AUTH_RESULT_REFUSED:
                    return redirectToSummaryWithError(redirectModel, "checkout.error.authorization.payment.refused");
                case HPPConstants.Response.AUTH_RESULT_CANCELLED:
                    return redirectToSummaryWithError(redirectModel, "checkout.error.authorization.payment.cancelled");
                case HPPConstants.Response.AUTH_RESULT_ERROR:
                default:
                    return redirectToSummaryWithError(redirectModel, "checkout.error.authorization.payment.error");
            }
        } catch (SignatureException e) {
            LOGGER.error(e);
        }

        LOGGER.debug("Redirecting to cart..");
        GlobalMessages.addFlashMessage(
                redirectModel,
                GlobalMessages.ERROR_MESSAGES_HOLDER,
                "checkout.error.authorization.payment.error");
        return REDIRECT_PREFIX + "/cart";
    }

    private String redirectToSummaryWithError(final RedirectAttributes redirectModel, final String messageKey) {
        GlobalMessages.addFlashMessage(
                redirectModel,
                GlobalMessages.ERROR_MESSAGES_HOLDER,
                messageKey);

        return REDIRECT_PREFIX + AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX + "/view";
    }

    private String getTermUrl() {
        String authorise3DSecureUrl = AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX + AUTHORISE_3D_SECURE_PAYMENT_URL;
        BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();

        return siteBaseUrlResolutionService.getWebsiteUrlForSite(
                currentBaseSite,
                true,
                authorise3DSecureUrl
        );
    }

    /**
     * return order code/uid according to the default implementation of redirectToOrderConfirmationPage()
     *
     * @param orderData
     * @return
     */
    private String getOrderCode(OrderData orderData) {
        return this.getCheckoutCustomerStrategy().isAnonymousCheckout() ? orderData.getGuid() : orderData.getCode();
    }

    /**
     * Construct the URL for HPP redirect
     *
     * @return
     */
    private String getHppRedirectUrl() {
        String url = AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX + HPP_RESULT_URL;
        BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();

        return siteBaseUrlResolutionService.getWebsiteUrlForSite(
                currentBaseSite,
                true,
                url
        );
    }

    private String getErrorMessageByRefusalReason(String refusalReason) {
        String errorMessage = "checkout.error.authorization.payment.refused";

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
        }

        return errorMessage;
    }
}
