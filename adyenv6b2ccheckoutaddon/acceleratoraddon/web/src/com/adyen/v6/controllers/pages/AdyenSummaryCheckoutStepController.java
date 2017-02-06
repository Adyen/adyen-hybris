/**
 *
 */
package com.adyen.v6.controllers.pages;

import com.adyen.Client;
import com.adyen.Util.Util;
import com.adyen.constants.ApiConstants.RefusalReason;
import com.adyen.constants.HPPConstants;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.PaymentResult;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants;
import com.adyen.v6.service.AdyenPaymentService;
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
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yacceleratorstorefront.controllers.pages.checkout.steps.SummaryCheckoutStepController;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.adyen.constants.HPPConstants.Fields.*;
import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.*;
import static com.adyen.v6.forms.AdyenPaymentForm.PAYMENT_METHOD_CC;
import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;

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

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

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
                PaymentResult paymentResult = adyenPaymentService.authorise(cartData, request);

                if (paymentResult.isAuthorised()) {
                    orderData = createAuthorizedOrder(model, redirectModel, paymentResult.getPspReference());
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
                model.addAttribute("hppUrl", getHppUrl());
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
            PaymentResult paymentResult = adyenPaymentService.authorise3D(request, paRes, md);

            if (paymentResult.isAuthorised()) {
                orderData = createAuthorizedOrder(model, redirectModel, paymentResult.getPspReference());
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
                                    @RequestParam(HPPConstants.Response.PAYMENT_METHOD) final String paymentMethod,
                                    @RequestParam(HPPConstants.Response.PSP_REFERENCE) final String pspReference,
                                    @RequestParam(HPPConstants.Response.SHOPPER_LOCALE) final String shopperLocale,
                                    @RequestParam(HPPConstants.Response.SKIN_CODE) final String skinCode,
                                    @RequestParam(HPPConstants.Response.MERCHANT_SIG) final String merchantSig,
                                    final RedirectAttributes redirectModel,
                                    final Model model) {
        final Configuration configuration = configurationService.getConfiguration();
        String hmacKey = configuration.getString(CONFIG_SKIN_HMAC);

        final SortedMap<String, String> hppResponseData = new TreeMap<>();

        hppResponseData.put(HPPConstants.Response.AUTH_RESULT, authResult);
        hppResponseData.put(HPPConstants.Response.MERCHANT_REFERENCE, merchantReference);
        hppResponseData.put(HPPConstants.Response.PAYMENT_METHOD, paymentMethod);
        hppResponseData.put(HPPConstants.Response.PSP_REFERENCE, pspReference);
        hppResponseData.put(HPPConstants.Response.SHOPPER_LOCALE, shopperLocale);
        hppResponseData.put(HPPConstants.Response.SKIN_CODE, skinCode);

        LOGGER.info("Received HPP response: " + hppResponseData);

        String errorMessage = "checkout.error.authorization.payment.refused";

        String dataToSign = Util.getDataToSign(hppResponseData);
        try {
            String calculatedMerchantSig = Util.calculateHMAC(dataToSign, hmacKey);
            LOGGER.info("Calculated signature: " + calculatedMerchantSig);
            if (calculatedMerchantSig.equals(merchantSig)
                    && HPPConstants.Response.AUTH_RESULT_AUTHORISED.equals(authResult)) {
                LOGGER.info("Redirecting to order success page");
                return getConfirmationPageRedirectUrl(orderCode);
            }
        } catch (SignatureException e) {
            LOGGER.error(e);
        }

        LOGGER.info("Redirecting to order failed page");
        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, errorMessage);

        return REDIRECT_PREFIX + "/cart";
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
     * @param pspReference
     * @return
     * @throws CommerceCartModificationException
     * @throws CMSItemNotFoundException
     */
    private OrderData createAuthorizedOrder(final Model model, final RedirectAttributes redirectModel, final String pspReference)
            throws CommerceCartModificationException, CMSItemNotFoundException {
        final CartModel cartModel = cartService.getSessionCart();
        final String merchantTransactionCode = cartModel.getCode();

        //First save the transactions to the CartModel < AbstractOrderModel
        final PaymentTransactionModel paymentTransactionModel = createPaymentTransaction(
                merchantTransactionCode,
                pspReference,
                cartModel);

        modelService.save(paymentTransactionModel);

        final PaymentTransactionEntryModel transactionEntryModel = createAuthorizationPaymentTransactionEntryModel(
                paymentTransactionModel,
                merchantTransactionCode,
                cartModel);

        modelService.save(transactionEntryModel);
        modelService.refresh(paymentTransactionModel); //refresh is needed by order-process

        return createOrder(model);
    }

    private PaymentTransactionModel createPaymentTransaction(
            final String merchantCode,
            final String pspReference,
            final CartModel cartModel) {
        final PaymentTransactionModel paymentTransactionModel = modelService.create(PaymentTransactionModel.class);
        paymentTransactionModel.setCode(pspReference);
        paymentTransactionModel.setRequestId(pspReference);
        paymentTransactionModel.setRequestToken(merchantCode);
        paymentTransactionModel.setPaymentProvider(Adyenv6b2ccheckoutaddonConstants.PAYMENT_PROVIDER);
        paymentTransactionModel.setOrder(cartModel);
        paymentTransactionModel.setCurrency(cartModel.getCurrency());
        paymentTransactionModel.setInfo(cartModel.getPaymentInfo());
        paymentTransactionModel.setPlannedAmount(new BigDecimal(cartModel.getTotalPrice()));

        return paymentTransactionModel;
    }

    private PaymentTransactionEntryModel createAuthorizationPaymentTransactionEntryModel(
            final PaymentTransactionModel paymentTransaction,
            final String merchantCode,
            final CartModel cartModel) {
        final PaymentTransactionEntryModel transactionEntryModel = modelService.create(PaymentTransactionEntryModel.class);

        String code = paymentTransaction.getRequestId() + "_" + paymentTransaction.getEntries().size();

        transactionEntryModel.setType(PaymentTransactionType.AUTHORIZATION);
        transactionEntryModel.setPaymentTransaction(paymentTransaction);
        transactionEntryModel.setRequestId(paymentTransaction.getRequestId());
        transactionEntryModel.setRequestToken(merchantCode);
        transactionEntryModel.setCode(code);
        transactionEntryModel.setTime(DateTime.now().toDate());
        transactionEntryModel.setTransactionStatus(ACCEPTED.name());
        transactionEntryModel.setTransactionStatusDetails(SUCCESFULL.name());
        transactionEntryModel.setAmount(new BigDecimal(cartModel.getTotalPrice()));
        transactionEntryModel.setCurrency(cartModel.getCurrency());

        return transactionEntryModel;
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

    private String getHppUrl() {
        Client client = new Client();
        client.setEnvironment(Environment.TEST);
        return client.getConfig().getHppEndpoint() + "/details.shtml";
    }

    public Map<String, String> getHPPFormData(OrderData orderData) throws SignatureException {
        final String sessionValidity = Util.calculateSessionValidity();
        final SortedMap<String, String> hppFormData = new TreeMap<>();

        final Configuration configuration = configurationService.getConfiguration();

        String merchantAccount = configuration.getString(CONFIG_MERCHANT_ACCOUNT);
        String skinCode = configuration.getString(CONFIG_SKIN_CODE);
        String hmacKey = configuration.getString(CONFIG_SKIN_HMAC);

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
}
