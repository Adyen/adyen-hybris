/**
 *
 */
package com.adyen.v6.controllers.pages.checkout.steps;


import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.forms.CSEPaymentForm;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.yacceleratorstorefront.controllers.ControllerConstants;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;



import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import static de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants.BREADCRUMBS_KEY;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/checkout/multi/adyen/select-payment-method")
public class SelectPaymentMethodCheckoutStepController extends AbstractCheckoutStepController {
    protected static final String PAYMENT_METHOD_STEP_NAME = "payment-method";

    protected static final String CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB = "checkout.multi.paymentMethod.breadcrumb";

    protected static final String CSE_PAYMENT_FORM = "csePaymentForm";
    protected static final String CSE_GENERATION_TIME = "generationTime";

    private static final String CART_DATA_ATTR = "cartData";
    private static final String CSE_DATA = "cseData";

    @Resource(name = "cartService")
    private CartService cartService;

    @Resource(name = "modelService")
    private ModelService modelService;

    /**
     * {@inheritDoc}
     */
    @Override
    @RequestMapping(value = "", method = GET)
    @RequireHardLogIn
//    @PreValidateCheckoutStep (checkoutStep = PAYMENT_METHOD_STEP_NAME)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        System.out.println(this.getClass() + " called");

        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        model.addAttribute("metaRobots", "noindex,nofollow");
        model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
        model.addAttribute(BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB));
        model.addAttribute(CSE_PAYMENT_FORM, new CSEPaymentForm());
        model.addAttribute(CSE_GENERATION_TIME, fromCalendar(GregorianCalendar.getInstance()));
        model.addAttribute(CART_DATA_ATTR, cartData);

        super.prepareDataForPage(model);

        final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
        super.storeCmsPageInModel(model, contentPage);
        super.setUpMetaDataForContentPage(model, contentPage);
        super.setCheckoutStepLinksForModel(model, getCheckoutStep());

        super.setCheckoutStepLinksForModel(model, getCheckoutStep());

        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.SelectPaymentMethod;
    }


    protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("metaRobots", "noindex,nofollow");
        model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
        prepareDataForPage(model);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
        final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        setCheckoutStepLinksForModel(model, getCheckoutStep());
    }

    @RequestMapping(value = "/cse", method = POST)
    @RequireHardLogIn
    public String setCse(final Model model, @Valid final CSEPaymentForm csePaymentForm, final BindingResult bindingResult)
            throws CMSItemNotFoundException {
        //TODO: Validator
        setupAddPaymentPage(model);

        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        model.addAttribute(CART_DATA_ATTR, cartData);

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "checkout.error.paymentethod.formentry.invalid");
            return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
        }

        //TODO: Billing address
        model.addAttribute(CSE_DATA, csePaymentForm);
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        String cseToken = csePaymentForm.getCseToken();
        System.out.println(cseToken);

//        final CartData sessionCart = getCartFacade().getSessionCart();
//        sessionCart.setAdyenCseToken(cseToken);

        final CartModel cartModel = cartService.getSessionCart();
        cartModel.setAdyenCseToken(cseToken);

        final PaymentInfoModel paymentInfoModel = createPaymentInfo(cartModel);

        cartModel.setPaymentInfo(paymentInfoModel);
        modelService.save(cartModel);

        return getCheckoutStep().nextStep();
    }


    public PaymentInfoModel createPaymentInfo(final CartModel cartModel) {
        final PaymentInfoModel paymentInfoModel = modelService.create(PaymentInfoModel.class);
        paymentInfoModel.setUser(cartModel.getUser());
        paymentInfoModel.setSaved(false);
        paymentInfoModel.setCode(generateCcPaymentInfoCode(cartModel));

        final AddressData addressData = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
        addressData.setEmail(getCheckoutCustomerStrategy().getCurrentUserForCheckout().getContactEmail());

        AddressModel addressModel = new AddressModel();
        addressModel.setEmail(getCheckoutCustomerStrategy().getCurrentUserForCheckout().getContactEmail());
        addressModel.setStreetname(addressData.getLine1());
        addressModel.setBillingAddress(true);
        addressModel.setPostalcode(addressData.getPostalCode());
        addressModel.setOwner(paymentInfoModel);


        System.out.println(" >> set billing address");

        paymentInfoModel.setBillingAddress(addressModel);
        //TODO: add payment details (amounts, etc)

		modelService.save(paymentInfoModel);

        return paymentInfoModel;
    }

    protected String generateCcPaymentInfoCode(final CartModel cartModel) {
        return cartModel.getCode() + "_" + UUID.randomUUID();
    }

    private String fromCalendar(final Calendar calendar) {
        final Date date = calendar.getTime();
        final String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * {@inheritDoc}
     */
    @RequestMapping(value = "/back", method = GET)
    @RequireHardLogIn
    @Override
    public String back(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().previousStep();
    }

    /**
     * {@inheritDoc}
     */
    @RequestMapping(value = "/next", method = GET)
    @RequireHardLogIn
    @Override
    public String next(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().nextStep();
    }

    /**
     * {@inheritDoc}
     */
    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(PAYMENT_METHOD_STEP_NAME);
    }


}
