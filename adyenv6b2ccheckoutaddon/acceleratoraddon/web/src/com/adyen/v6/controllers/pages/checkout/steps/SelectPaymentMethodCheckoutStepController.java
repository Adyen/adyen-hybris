/**
 *
 */
package com.adyen.v6.controllers.pages.checkout.steps;


import com.adyen.model.Recurring;
import com.adyen.model.hpp.PaymentMethod;
import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.forms.AdyenPaymentForm;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.yacceleratorstorefront.controllers.ControllerConstants;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

import static de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants.BREADCRUMBS_KEY;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/checkout/multi/adyen/select-payment-method")
public class SelectPaymentMethodCheckoutStepController extends AbstractCheckoutStepController {
    private static final Logger LOGGER = Logger.getLogger(SelectPaymentMethodCheckoutStepController.class);

    protected static final String PAYMENT_METHOD_STEP_NAME = "payment-method";

    protected static final String CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB = "checkout.multi.paymentMethod.breadcrumb";

    protected static final String ADYEN_PAYMENT_FORM = "adyenPaymentForm";
    protected static final String CSE_GENERATION_TIME = "generationTime";

    private static final String CART_DATA_ATTR = "cartData";
    private static final String CSE_DATA = "cseData";

    @Resource(name = "cartService")
    private CartService cartService;

    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "baseStoreService")
    private BaseStoreService baseStoreService;

    @Resource(name = "adyenPaymentService")
    private AdyenPaymentService adyenPaymentService;

    @Resource(name = "commonI18NService")
    private CommonI18NService commonI18NService;

    @Resource(name = "userService")
    private UserService userService;

    /**
     * {@inheritDoc}
     */
    @Override
    @RequestMapping(value = "", method = GET)
    @RequireHardLogIn
//    @PreValidateCheckoutStep (checkoutStep = PAYMENT_METHOD_STEP_NAME)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        model.addAttribute("metaRobots", "noindex,nofollow");
        model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
        model.addAttribute(BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB));
        model.addAttribute(ADYEN_PAYMENT_FORM, new AdyenPaymentForm());
        model.addAttribute(CSE_GENERATION_TIME, fromCalendar(GregorianCalendar.getInstance()));
        model.addAttribute(CART_DATA_ATTR, cartData);
        model.addAttribute("expiryYears", getExpiryYears());

        List<PaymentMethod> paymentMethods = null;
        try {
            paymentMethods = getAdyenPaymentService().getPaymentMethods(
                    cartData.getTotalPriceWithTax().getValue(),
                    cartData.getTotalPriceWithTax().getCurrencyIso(),
                    cartData.getDeliveryAddress().getCountry().getIsocode()
            );
        } catch (Exception e) {
            LOGGER.error(e);
            GlobalMessages.addErrorMessage(model, "No payment methods found");
            return getCheckoutStep().previousStep();
        }

        //Set HPP payment methods
        model.addAttribute("paymentMethods", paymentMethods);

        //Set allowed Credit Cards
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        model.addAttribute("allowedCards", baseStore.getAdyenAllowedCards());

        //Set the url for CSE script
        String cseUrl = getAdyenPaymentService().getCSEUrl();
        model.addAttribute("cseUrl", cseUrl);

        /**
         * The show remember me checkout should only be shown as the
         * user is logged in and the recurirng mode is set to ONECLICK or ONECLICK,RECURRING
         */
        RecurringContractMode recurringContractMode = baseStore.getAdyenRecurringContractMode();

        if(!this.getCheckoutCustomerStrategy().isAnonymousCheckout() &&
                (Recurring.ContractEnum.ONECLICK_RECURRING.name().equals(recurringContractMode.getCode()) ||
                Recurring.ContractEnum.ONECLICK.name().equals(recurringContractMode.getCode())))
        {
            model.addAttribute("showRememberTheseDetails", true);
        } else {
            model.addAttribute("showRememberTheseDetails", false);
        }

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

    @RequestMapping(value = "", method = POST)
    @RequireHardLogIn
    public String setPaymentMethod(final Model model, @Valid final AdyenPaymentForm adyenPaymentForm, final BindingResult bindingResult)
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
        model.addAttribute(CSE_DATA, adyenPaymentForm);
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        LOGGER.info("PaymentForm: " + adyenPaymentForm);

        //Update CartModel
        final CartModel cartModel = cartService.getSessionCart();
        cartModel.setAdyenCseToken(adyenPaymentForm.getCseToken());
        cartModel.setAdyenRememberTheseDetails(adyenPaymentForm.getRememberTheseDetails());

        final PaymentInfoModel paymentInfo = createPaymentInfo(cartModel, adyenPaymentForm);

        cartModel.setPaymentInfo(paymentInfo);
        modelService.save(cartModel);

        return getCheckoutStep().nextStep();
    }

    public PaymentInfoModel createPaymentInfo(final CartModel cartModel, AdyenPaymentForm adyenPaymentForm) {
        final PaymentInfoModel paymentInfo = modelService.create(PaymentInfoModel.class);
        paymentInfo.setUser(cartModel.getUser());
        paymentInfo.setSaved(false);
        paymentInfo.setCode(generateCcPaymentInfoCode(cartModel));

        final AddressData addressData = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
        addressData.setEmail(getCheckoutCustomerStrategy().getCurrentUserForCheckout().getContactEmail());

        AddressModel addressModel = new AddressModel();

        CountryModel country = null;

        if(addressData.getCountry() != null && addressData.getCountry().getIsocode() != "") {

            // countryModel from service
            country = commonI18NService.getCountry(addressData.getCountry().getIsocode());
            addressModel.setCountry(country);
        }

        addressModel.setEmail(getCheckoutCustomerStrategy().getCurrentUserForCheckout().getContactEmail());

        addressModel.setStreetname(addressData.getLine1());
        addressModel.setLine2(addressData.getLine2());
        addressModel.setPostalcode(addressData.getPostalCode());
        addressModel.setTown(addressData.getTown());

        if(addressData.getRegion() != null && addressData.getRegion().getIsocode() != "" && country != null) {
            final RegionModel regionModel = commonI18NService.getRegion(country, addressData.getRegion().getIsocode());
            addressModel.setRegion(regionModel);
        }

        addressModel.setBillingAddress(true);
        addressModel.setOwner(paymentInfo);

        paymentInfo.setBillingAddress(addressModel);

        paymentInfo.setAdyenPaymentMethod(adyenPaymentForm.getPaymentMethod());
        paymentInfo.setAdyenBrandCode(adyenPaymentForm.getBrandCode());
        paymentInfo.setAdyenIssuerId(adyenPaymentForm.getIssuerId());

        modelService.save(paymentInfo);

        return paymentInfo;
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
     * Returns a list with CC expiry years
     *
     * @return
     */
    public List<String> getExpiryYears() {
        final List<String> expiryYears = new ArrayList<>();
        final Calendar calender = new GregorianCalendar();

        for (int i = calender.get(Calendar.YEAR); i < calender.get(Calendar.YEAR) + 11; i++) {
            expiryYears.add(String.valueOf(i));
        }

        return expiryYears;
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

    private AdyenPaymentService getAdyenPaymentService() {
        adyenPaymentService.setBaseStore(baseStoreService.getCurrentBaseStore());
        return adyenPaymentService;
    }
}
