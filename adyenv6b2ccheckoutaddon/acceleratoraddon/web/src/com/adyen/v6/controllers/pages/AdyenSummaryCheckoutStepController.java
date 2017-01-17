/**
 *
 */
package com.adyen.v6.controllers.pages;

import com.adyen.constants.ApiConstants.RefusalReason;
import com.adyen.model.PaymentResult;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants;
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
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.yacceleratorstorefront.controllers.pages.checkout.steps.SummaryCheckoutStepController;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Arrays;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;

@Controller
@RequestMapping(value = AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX)
public class AdyenSummaryCheckoutStepController extends SummaryCheckoutStepController {
    private static final Logger LOGGER = Logger.getLogger(AdyenSummaryCheckoutStepController.class);

    private final static String SUMMARY = "summary";
    private static final String AUTHORISE_3D_SECURE_PAYMENT_URL = "/authorise-3d-adyen-response";

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

        LOGGER.info(" >> CSE:" + cartData.getAdyenCseToken());


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

        String errorMessage = "checkout.error.authorization.failed";
        try {
            PaymentResult paymentResult = adyenPaymentService.authorise(cartData, request);

            if (paymentResult.isAuthorised()) {
                return placeOrder(model, redirectModel, paymentResult.getPspReference());
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
        } catch (ApiException e) {
            LOGGER.error("API Exception " + e.getError());
        } catch (Exception e) {
            LOGGER.error("Exception ", e);
        }

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
        String errorMessage = "checkout.error.authorization.failed";
        try {
            PaymentResult paymentResult = adyenPaymentService.authorise3D(request, paRes, md);

            if (paymentResult.isAuthorised()) {
                return placeOrder(model, redirectModel, paymentResult.getPspReference());
            } else if (paymentResult.isRefused()) {
                errorMessage = getErrorMessageByRefusalReason(paymentResult.getRefusalReason());
            }
        } catch (ApiException e) {
            LOGGER.error("API Exception " + e.getError());
        } catch (Exception e) {
            LOGGER.error("Exception ", e);
        }

        GlobalMessages.addErrorMessage(model, errorMessage);
        return enterStep(model, redirectModel);
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
     * Returns the redirect page
     *
     * @param model
     * @param redirectModel
     * @param pspReference
     * @return
     * @throws CommerceCartModificationException
     * @throws CMSItemNotFoundException
     */
    private String placeOrder(final Model model, final RedirectAttributes redirectModel, final String pspReference)
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

        //Place the order
        final OrderData orderData;
        try {
            orderData = getCheckoutFacade().placeOrder();
        } catch (final Exception e) {
            LOGGER.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
            return enterStep(model, redirectModel);
        }


        return redirectToOrderConfirmationPage(orderData);
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
}
