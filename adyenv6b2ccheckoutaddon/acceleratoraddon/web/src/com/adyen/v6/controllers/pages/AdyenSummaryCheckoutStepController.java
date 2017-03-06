/**
 *
 */
package com.adyen.v6.controllers.pages;

import com.adyen.Util.Util;
import com.adyen.constants.ApiConstants;
import com.adyen.constants.ApiConstants.RefusalReason;
import com.adyen.constants.HPPConstants;
import com.adyen.model.Amount;
import com.adyen.model.PaymentResult;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.AdyenTransactionService;
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
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.yacceleratorstorefront.controllers.pages.checkout.steps.SummaryCheckoutStepController;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.adyen.constants.HPPConstants.Fields.*;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;

@Controller
@RequestMapping(value = AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX)
public class AdyenSummaryCheckoutStepController extends SummaryCheckoutStepController {
    private static final Logger LOGGER = Logger.getLogger(AdyenSummaryCheckoutStepController.class);

    private final static String SUMMARY = "summary";
    private static final String AUTHORISE_3D_SECURE_PAYMENT_URL = "/authorise-3d-adyen-response";
    private static final String HPP_RESULT_URL = "/hpp-adyen-response/";

    private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";

    @Resource(name = "cartService")
    private CartService cartService;

    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "siteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @Resource(name = "adyenPaymentService")
    private AdyenPaymentService adyenPaymentService;

    @Resource(name = "baseStoreService")
    private BaseStoreService baseStoreService;

    @Resource(name = "adyenTransactionService")
    private AdyenTransactionService adyenTransactionService;

    @Resource(name = "orderCancelService")
    private OrderCancelService orderCancelService;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "adyenOrderRepository")
    private OrderRepository orderRepository;

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
        LOGGER.info(" >> Order Validated");

        //Validate the cart
        if (validateCart(redirectModel)) {
            // Invalid cart. Bounce back to the cart page.
            return REDIRECT_PREFIX + "/cart";
        }

        final CartData cartData = getCartFacade().getSessionCart();

        OrderData orderData = null;
        String errorMessage = "checkout.error.authorization.failed";
        try {
            if (PAYMENT_METHOD_CC.equals(cartData.getAdyenPaymentMethod())) {
                //CSE
                PaymentResult paymentResult = getAdyenPaymentService().authorise(cartData, request);

                if (paymentResult.isAuthorised()) {
                    orderData = createAuthorizedOrder(model, redirectModel, paymentResult);
                } else if (paymentResult.isRedirectShopper()) {
                    final String termUrl = getTermUrl();

                    model.addAttribute("paReq", paymentResult.getPaRequest());
                    model.addAttribute("md", paymentResult.getMd());
                    model.addAttribute("issuerUrl", paymentResult.getIssuerUrl());
                    model.addAttribute("termUrl", termUrl);

                    return AdyenControllerConstants.Views.Pages.MultiStepCheckout.Validate3DSecurePaymentPage;
                } else if (paymentResult.isRefused()) {
                    errorMessage = getErrorMessageByRefusalReason(paymentResult.getRefusalReason());
                }
            } else {
                orderData = createOrder(model);

                //HPP data
                model.addAttribute("hppUrl", getAdyenPaymentService().getHppUrl());
                model.addAttribute("hppFormData", getHPPFormData(orderData));

                return AdyenControllerConstants.Views.Pages.MultiStepCheckout.HppPaymentPage;
            }
        } catch (ApiException e) {
            LOGGER.error("API Exception " + e.getError());
        } catch (Exception e) {
            LOGGER.error("Exception ", e);
        }

        if (orderData != null) {
            LOGGER.info("Redirecting to confirmation");
            return redirectToOrderConfirmationPage(orderData);
        }

        LOGGER.info("Redirecting to summary view");
        GlobalMessages.addErrorMessage(model, errorMessage);
        return enterStep(model, redirectModel);
    }

    @RequestMapping(value = AUTHORISE_3D_SECURE_PAYMENT_URL, method = RequestMethod.POST)
    @RequireHardLogIn
    public String authorise3DSecurePayment(@RequestParam("PaRes") final String paRes,
                                           @RequestParam("MD") final String md,
                                           final Model model,
                                           final RedirectAttributes redirectModel,
                                           final HttpServletRequest request)
            throws CMSItemNotFoundException, CommerceCartModificationException, UnknownHostException {
        OrderData orderData = null;
        String errorMessage = "checkout.error.authorization.failed";
        try {
            PaymentResult paymentResult = getAdyenPaymentService().authorise3D(request, paRes, md);

            if (paymentResult.isAuthorised()) {
                orderData = createAuthorizedOrder(model, redirectModel, paymentResult);
            } else if (paymentResult.isRefused()) {
                errorMessage = getErrorMessageByRefusalReason(paymentResult.getRefusalReason());
            }
        } catch (ApiException e) {
            LOGGER.error("API Exception " + e.getError());
        } catch (Exception e) {
            LOGGER.error("Exception ", e);
        }

        if (orderData != null) {
            return redirectToOrderConfirmationPage(orderData);
        }

        GlobalMessages.addErrorMessage(model, errorMessage);
        return enterStep(model, redirectModel);
    }

    @RequestMapping(value = HPP_RESULT_URL + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    @RequireHardLogIn
    public String handleHPPResponse(@PathVariable("orderCode") final String orderCode,
                                    @RequestParam(HPPConstants.Response.AUTH_RESULT) final String authResult,
                                    @RequestParam(HPPConstants.Response.MERCHANT_REFERENCE) final String merchantReference,
                                    @RequestParam(value = HPPConstants.Response.PAYMENT_METHOD, required = false) final String paymentMethod,
                                    @RequestParam(value = HPPConstants.Response.PSP_REFERENCE, required = false) final String pspReference,
                                    @RequestParam(HPPConstants.Response.SHOPPER_LOCALE) final String shopperLocale,
                                    @RequestParam(HPPConstants.Response.SKIN_CODE) final String skinCode,
                                    @RequestParam(HPPConstants.Response.MERCHANT_SIG) final String merchantSig,
                                    final RedirectAttributes redirectModel,
                                    final Model model) {
        final SortedMap<String, String> hppResponseData = new TreeMap<>();

        hppResponseData.put(HPPConstants.Response.AUTH_RESULT, authResult);
        hppResponseData.put(HPPConstants.Response.MERCHANT_REFERENCE, merchantReference);

        if (paymentMethod != null) {
            hppResponseData.put(HPPConstants.Response.PAYMENT_METHOD, paymentMethod);
        }

        if (pspReference != null) {
            hppResponseData.put(HPPConstants.Response.PSP_REFERENCE, pspReference);
        }

        hppResponseData.put(HPPConstants.Response.SHOPPER_LOCALE, shopperLocale);
        hppResponseData.put(HPPConstants.Response.SKIN_CODE, skinCode);

        LOGGER.info("Received HPP response: " + hppResponseData);

        if (isValidHPPResponse(hppResponseData, merchantSig)) {
            switch (authResult) {
                case HPPConstants.Response.AUTH_RESULT_AUTHORISED:
                case HPPConstants.Response.AUTH_RESULT_PENDING:
                    LOGGER.info("Redirecting to confirmation page");
                    return getConfirmationPageRedirectUrl(orderCode);
                case HPPConstants.Response.AUTH_RESULT_REFUSED:
                    GlobalMessages.addFlashMessage(
                            redirectModel,
                            GlobalMessages.ERROR_MESSAGES_HOLDER,
                            "checkout.error.authorization.payment.refused");
                    cancelOrder(merchantReference);
                    break;
                case HPPConstants.Response.AUTH_RESULT_CANCELLED:
                    GlobalMessages.addFlashMessage(
                            redirectModel,
                            GlobalMessages.ERROR_MESSAGES_HOLDER,
                            "checkout.error.authorization.payment.cancelled");
                    cancelOrder(merchantReference);
                    break;
                case HPPConstants.Response.AUTH_RESULT_ERROR:
                    GlobalMessages.addFlashMessage(
                            redirectModel,
                            GlobalMessages.ERROR_MESSAGES_HOLDER,
                            "checkout.error.authorization.payment.error");
                    cancelOrder(merchantReference);
                    break;
                default:
                    LOGGER.error("Unknown AuthResult received: " + authResult);
                    break;
            }
        }

        LOGGER.info("Redirecting to cart..");
        return REDIRECT_PREFIX + "/cart";
    }

    private boolean isValidHPPResponse(SortedMap<String, String> hppResponseData, String merchantSig) {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        String hmacKey = baseStore.getAdyenSkinHMAC();
        Assert.notNull(hmacKey);

        String dataToSign = Util.getDataToSign(hppResponseData);
        try {
            String calculatedMerchantSig = Util.calculateHMAC(dataToSign, hmacKey);
            LOGGER.info("Calculated signature: " + calculatedMerchantSig);
            if (calculatedMerchantSig.equals(merchantSig)) {
                return true;
            } else {
                LOGGER.error("Signature does not match!");
            }
        } catch (SignatureException e) {
            LOGGER.error(e);
        }

        return false;
    }

    private boolean cancelOrder(String orderCode) {
        OrderModel orderModel = orderRepository.getOrderModel(orderCode);
        final OrderCancelRequest orderCancelRequest = new OrderCancelRequest(orderModel);
        try {
            orderCancelService.requestOrderCancel(orderCancelRequest, userService.getCurrentUser());
        } catch (OrderCancelException e) {
            LOGGER.error(e);
            return false;
        }
        orderModel.setStatus(OrderStatus.CANCELLED);
        modelService.save(orderModel);
        LOGGER.info("Order cancelled");

        return true;
    }

    protected String getConfirmationPageRedirectUrl(String orderCode) {
        return "redirect:/checkout/orderConfirmation/" + orderCode;
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
     * Construct the URL for HPP redirect
     *
     * @param orderData
     * @return
     */
    private String getHppRedirectUrl(OrderData orderData) {
        //grab order code/uid according to the default implementation of redirectToOrderConfirmationPage()
        String orderCode = this.getCheckoutCustomerStrategy().isAnonymousCheckout() ? orderData.getGuid() : orderData.getCode();

        String url = AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX + HPP_RESULT_URL + orderCode;
        BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();

        return siteBaseUrlResolutionService.getWebsiteUrlForSite(
                currentBaseSite,
                true,
                url
        );
    }

    /**
     * Create Order and save it
     *
     * @param model
     * @return
     */
    private OrderData createOrder(final Model model) {
        OrderData orderData = null;
        try {
            orderData = getCheckoutFacade().placeOrder();
        } catch (final Exception e) {
            LOGGER.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
        }

        return orderData;
    }

    /**
     * Create order and authorized TX
     *
     * @param model
     * @param redirectModel
     * @param paymentResult
     * @return
     * @throws CommerceCartModificationException
     * @throws CMSItemNotFoundException
     */
    private OrderData createAuthorizedOrder(final Model model,
                                            final RedirectAttributes redirectModel,
                                            final PaymentResult paymentResult)
            throws CommerceCartModificationException, CMSItemNotFoundException {
        final CartModel cartModel = cartService.getSessionCart();
        final String merchantTransactionCode = cartModel.getCode();

        //First save the transactions to the CartModel < AbstractOrderModel
        final PaymentTransactionModel paymentTransactionModel = adyenTransactionService.authorizeOrderModel(
                cartModel,
                merchantTransactionCode,
                paymentResult.getPspReference());

        //Retrieve payment method from API if provided
        String authorizationPaymentMethod = paymentResult.getAdditionalData().get(ApiConstants.AdditionalData.PAYMENT_METHOD);
        if (authorizationPaymentMethod != null) {
            cartModel.setAdyenPaymentMethod(authorizationPaymentMethod);
        }

        return createOrder(model);
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

    public Map<String, String> getHPPFormData(OrderData orderData) throws SignatureException {
        final String sessionValidity = Util.calculateSessionValidity();
        final SortedMap<String, String> hppFormData = new TreeMap<>();

        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        String merchantAccount = baseStore.getAdyenMerchantAccount();
        String skinCode = baseStore.getAdyenSkinCode();
        String hmacKey = baseStore.getAdyenSkinHMAC();

        Assert.notNull(merchantAccount);
        Assert.notNull(skinCode);
        Assert.notNull(hmacKey);

        //todo: with vat?
        Amount amount = Util.createAmount(
                orderData.getTotalPrice().getValue().toString(),
                orderData.getTotalPrice().getCurrencyIso()
        );

        String countryCode = "";
        CountryData deliveryCountry = orderData.getDeliveryAddress().getCountry();
        if (deliveryCountry != null) {
            countryCode = deliveryCountry.getIsocode();
        }

        hppFormData.put(PAYMENT_AMOUNT, amount.getValue().toString());
        hppFormData.put(CURRENCY_CODE, orderData.getTotalPrice().getCurrencyIso());
        hppFormData.put(SHIP_BEFORE_DATE, sessionValidity);
        hppFormData.put(MERCHANT_REFERENCE, orderData.getCode());
        hppFormData.put(SKIN_CODE, skinCode);
        hppFormData.put(MERCHANT_ACCOUNT, merchantAccount);
        hppFormData.put(SESSION_VALIDITY, sessionValidity);
        hppFormData.put(BRAND_CODE, orderData.getAdyenBrandCode());
        hppFormData.put(ISSUER_ID, orderData.getAdyenIssuerId());
        hppFormData.put(COUNTRY_CODE, countryCode);
        hppFormData.put(RES_URL, getHppRedirectUrl(orderData));

        String dataToSign = Util.getDataToSign(hppFormData);
        String merchantSig = Util.calculateHMAC(dataToSign, hmacKey);

        hppFormData.put(MERCHANT_SIG, merchantSig);

        return hppFormData;
    }

    private AdyenPaymentService getAdyenPaymentService() {
        adyenPaymentService.setBaseStore(baseStoreService.getCurrentBaseStore());
        return adyenPaymentService;
    }
}
