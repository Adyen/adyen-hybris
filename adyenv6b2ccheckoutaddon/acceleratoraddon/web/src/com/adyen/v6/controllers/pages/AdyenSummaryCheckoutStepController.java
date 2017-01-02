/**
 *
 */
package com.adyen.v6.controllers.pages;

import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
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
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.yacceleratorstorefront.controllers.pages.checkout.steps.SummaryCheckoutStepController;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;
import static org.apache.commons.lang.StringUtils.EMPTY;

@Controller
@RequestMapping(value = "checkout/multi/adyen/summary")
public class AdyenSummaryCheckoutStepController extends SummaryCheckoutStepController {
    private static final Logger LOGGER = Logger.getLogger(AdyenSummaryCheckoutStepController.class);

    private final static String SUMMARY = "summary";
    private static final String AUTHORISE_3D_SECURE_PAYMENT_URL = "/checkout/multi/adyen/summary/authorise-3d-response";

    @Resource(name = "cartService")
    private CartService cartService;


    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "customerAccountService")
    private CustomerAccountService customerAccountService;

    @Resource(name = "baseStoreService")
    private BaseStoreService baseStoreService;

    @Resource(name = "siteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @Resource(name = "adyenPaymentService")
    private AdyenPaymentService adyenPaymentService;

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
//		model.addAttribute("paymentInfo", cartData.getPaymentInfo());

        // Only request the security code if the SubscriptionPciOption is set to Default.
        final boolean requestSecurityCode = CheckoutPciOptionEnum.DEFAULT.equals(getCheckoutFlowFacade()
                .getSubscriptionPciOption());
        model.addAttribute("requestSecurityCode", Boolean.valueOf(requestSecurityCode));

        model.addAttribute(new PlaceOrderForm());

        storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        System.out.println(" >> CSE:" + cartData.getAdyenCseToken());


        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.CheckoutSummaryPage;
    }

    @RequestMapping({"/placeOrder"})
    @RequireHardLogIn
    @Override
    public String placeOrder(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm, final Model model,
                             final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException, // NOSONAR
            InvalidCartException, CommerceCartModificationException {

        System.out.println(" >> Validating..");
        if (validateOrderForm(placeOrderForm, model)) {
            return enterStep(model, redirectModel);
        }
        System.out.println(" >> Order Validated");

        //Validate the cart
        if (validateCart(redirectModel)) {
            // Invalid cart. Bounce back to the cart page.
            return REDIRECT_PREFIX + "/cart";
        }

        final CartData cartData = getCartFacade().getSessionCart();

        String resultCode = "";
        String pspReference = "";
        String errorCode = "";
        String paReq = "";
        String md = "";
        String issuerUrl = "";

        try {
            Map<String, Object> result = adyenPaymentService.authorise(cartData, request);

            if(result.get("resultCode") != null) resultCode = result.get("resultCode").toString();
            if(result.get("pspReference") != null) pspReference = result.get("pspReference").toString();
            if(result.get("errorCode") != null) errorCode = result.get("errorCode").toString();
            if(result.get("paRequest") != null) paReq = result.get("paRequest").toString();
            if(result.get("md") != null) md = result.get("md").toString();
            if(result.get("issuerUrl") != null) issuerUrl = result.get("issuerUrl").toString();

            //TODO: logging
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getClass() + " " + e.getMessage());
        }

        if(resultCode.equals("RedirectShopper")) {

            final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
            final String fullResponseUrl = siteBaseUrlResolutionService.getWebsiteUrlForSite(
                    currentBaseSite,
                    true,
                    AUTHORISE_3D_SECURE_PAYMENT_URL);

            model.addAttribute("paReq", paReq);
            model.addAttribute("md", md);
            model.addAttribute("issuerUrl", issuerUrl);
            model.addAttribute("termUrl", fullResponseUrl);

            return AdyenControllerConstants.Views.Pages.MultiStepCheckout.Validate3DSecurePaymentPage;
        }

        //TODO: const resultcodes
        if(!resultCode.equals("Authorised")) {
            //TODO: utilize error code
            GlobalMessages.addErrorMessage(model, "checkout.error.authorization.failed");
            return enterStep(model, redirectModel);
        }

        final OrderData orderData;
        try {
            orderData = getCheckoutFacade().placeOrder();
        } catch (final Exception e) {
            LOGGER.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
            return enterStep(model, redirectModel);
        }


        OrderModel orderModel = null;
        if (getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            final BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
            orderModel = customerAccountService.getOrderDetailsForGUID(orderData.getGuid(), baseStoreModel);
        } else {
            return EMPTY;
        }

        final String merchantTransactionCode = orderModel.getCode();
        final PaymentTransactionModel paymentTransactionModel = createPaymentTransaction(
                merchantTransactionCode,
                pspReference,
                orderModel);

        modelService.save(paymentTransactionModel);

        final PaymentTransactionEntryModel transactionEntryModel = createAuthorizationPaymentTransactionEntryModel(
                paymentTransactionModel,
                merchantTransactionCode,
                orderModel);

        modelService.save(transactionEntryModel);

        return redirectToOrderConfirmationPage(orderData);
    }
/*
    @RequestMapping(value = "/authorise-3d-response", method = RequestMethod.POST)
    @RequireHardLogIn
    public String authorise3DSecurePayment(@RequestParam("PaRes") final String paRes,
                                           @RequestParam("MD") final String md,
                                           final Model model,
                                           final RedirectAttributes redirectModel,
                                           final HttpServletRequest request)
            throws CMSItemNotFoundException, CommerceCartModificationException, UnknownHostException
    {
        String userAgent = request.getHeader("User-Agent");
        String acceptHeader = request.getHeader("Accept");
        String userIP = request.getRemoteAddr();

        try
        {
//            adyenPaymentService.maybeClearAuthorizeHistory(checkoutFacade.getCartModel());


            final OrderData orderData;
            try {
                orderData = getCheckoutFacade().placeOrder();
            } catch (final Exception e) {
                LOGGER.error("Failed to place Order", e);
                GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
                return enterStep(model, redirectModel);
            }


            OrderModel orderModel = null;
            if (getCheckoutCustomerStrategy().isAnonymousCheckout()) {
                final BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
                orderModel = customerAccountService.getOrderDetailsForGUID(orderData.getGuid(), baseStoreModel);
            } else {
                return EMPTY;
            }

            final String merchantTransactionCode = orderModel.getCode();
            final PaymentTransactionModel paymentTransactionModel = createPaymentTransaction(
                    merchantTransactionCode,
                    pspReference,
                    orderModel);

            modelService.save(paymentTransactionModel);

            final PaymentTransactionEntryModel transactionEntryModel = createAuthorizationPaymentTransactionEntryModel(
                    paymentTransactionModel,
                    merchantTransactionCode,
                    orderModel);

            modelService.save(transactionEntryModel);

            return redirectToOrderConfirmationPage(orderData);




            final PaymentTransactionEntryModel paymentTransactionEntry = (AdyenPaymentTransactionEntryModel) adyenPaymentService
                    .authorize3DSecure(checkoutFacade.getCartModel(), paRes, md, request.getRemoteAddr(),
                            request.getHeader("User-Agent"), request.getHeader("Accept"));
            if (isPaymentFail(paymentTransactionEntry))
            {
                if (paymentTransactionEntry != null && StringUtils.isNotBlank(paymentTransactionEntry.getAdyenMessage()))
                {
                    GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                            "checkout.multi.3d.authorise.refusedWithreason", new Object[]
                                    { paymentTransactionEntry.getAdyenMessage() });
                }
                else
                {
                    GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                            "checkout.multi.3d.authorise.refused");
                }
                return REDIRECT_URL_ADD_PAYMENT_METHOD;
            }
        }
        catch (final Exception e)
        {
            LOG.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
        }
        return getCheckoutStep().nextStep();
    }

    public PaymentTransactionEntryModel authorize3DSecure(final CartModel cartModel, final String paResponse,
                                                          final String md, final String shopperIp, final String userAgent, final String accept)
    {
        final PaymentTransactionModel transaction = (PaymentTransactionModel) this.modelService
                .create(PaymentTransactionModel.class);
        transaction.setCode(cartModel.getCode());
        transaction.setOrder(cartModel);
        modelService.save(transaction);
//        getModelService().saveAll(new Object[]
//                { cartModel, transaction });
        final AdyenPaymentRequestBuilder builder = new AdyenPaymentRequestBuilder();
        builder.create3DRequest(cartModel.getCode(), shopperIp, getCmsSiteService().getCurrentSite().getAdyenMerchantAccount(),
                paResponse, md);
        builder.browserInfo(userAgent, accept);
        final AdyenPaymentRequest paymentRequest = builder.getPaymentRequest();
        return adyenService.authorise(transaction, paymentRequest, true);
    }*/


    public PaymentTransactionModel createPaymentTransaction(
            final String merchantCode,
            final String pspReference,
            final OrderModel orderModel) {
        final PaymentTransactionModel paymentTransactionModel = modelService.create(PaymentTransactionModel.class);
//        final CartModel cartModel = commerceCheckoutParameter.getCart();
        paymentTransactionModel.setCode(pspReference);
        paymentTransactionModel.setRequestId(pspReference);
        paymentTransactionModel.setRequestToken(merchantCode);
        paymentTransactionModel.setPaymentProvider("Adyen");
        paymentTransactionModel.setOrder(orderModel);
        paymentTransactionModel.setCurrency(orderModel.getCurrency());
        paymentTransactionModel.setInfo(orderModel.getPaymentInfo());
        paymentTransactionModel.setPlannedAmount(new BigDecimal(orderModel.getTotalPrice()));

        return paymentTransactionModel;
    }

    private PaymentTransactionEntryModel createAuthorizationPaymentTransactionEntryModel(
            final PaymentTransactionModel paymentTransaction,
            final String merchantCode,
            final OrderModel orderModel) {
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
        transactionEntryModel.setAmount(new BigDecimal(orderModel.getTotalPrice()));
        transactionEntryModel.setCurrency(orderModel.getCurrency());

        return transactionEntryModel;
    }
}
