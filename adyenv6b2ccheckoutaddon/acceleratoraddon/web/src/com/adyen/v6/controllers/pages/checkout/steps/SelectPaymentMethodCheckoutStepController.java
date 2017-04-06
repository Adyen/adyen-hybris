package com.adyen.v6.controllers.pages.checkout.steps;

import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.hpp.PaymentMethod;
import com.adyen.model.recurring.Recurring;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.enums.AdyenCardTypeEnum;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.forms.AdyenPaymentForm;
import com.adyen.v6.forms.validation.AdyenPaymentFormValidator;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
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
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.security.SignatureException;
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

    private List<PaymentMethod> alternativePaymentMethods;
    private Set<AdyenCardTypeEnum> allowedCards;
    private List<RecurringDetail> storedCards;
    private boolean showRememberTheseDetails = false;

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

        setupAvailablePaymentMethods();

        //Set HPP payment methods
        model.addAttribute("paymentMethods", alternativePaymentMethods);

        //Set allowed Credit Cards
        model.addAttribute("allowedCards", allowedCards);

        model.addAttribute("showRememberTheseDetails", showRememberTheseDetails);
        model.addAttribute("storedCards", storedCards);

        //Set the url for CSE script
        String cseUrl = getAdyenPaymentService().getCSEUrl();
        model.addAttribute("cseUrl", cseUrl);

        super.prepareDataForPage(model);

        final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
        super.storeCmsPageInModel(model, contentPage);
        super.setUpMetaDataForContentPage(model, contentPage);
        super.setCheckoutStepLinksForModel(model, getCheckoutStep());

        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.SelectPaymentMethod;
    }

    @RequestMapping(value = "", method = POST)
    @RequireHardLogIn
    public String setPaymentMethod(final Model model,
                                   final RedirectAttributes redirectAttributes,
                                   @Valid final AdyenPaymentForm adyenPaymentForm,
                                   final BindingResult bindingResult) throws CMSItemNotFoundException {
        setupAvailablePaymentMethods();

        LOGGER.info("PaymentForm: " + adyenPaymentForm);

        AdyenPaymentFormValidator adyenPaymentFormValidator = new AdyenPaymentFormValidator(
                alternativePaymentMethods,
                allowedCards,
                storedCards,
                showRememberTheseDetails
        );

        adyenPaymentFormValidator.validate(adyenPaymentForm, bindingResult);
        if (bindingResult.hasErrors()) {
            LOGGER.info(bindingResult.getAllErrors().stream().map(error -> (error.getCode())).reduce((x, y) -> (x = x + y)));
            GlobalMessages.addErrorMessage(model, "checkout.error.paymentethod.formentry.invalid");
            return enterStep(model, redirectAttributes);
        }

        //TODO: Billing address
        model.addAttribute(CSE_DATA, adyenPaymentForm);
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        //Update CartModel
        final CartModel cartModel = cartService.getSessionCart();
        cartModel.setAdyenCseToken(adyenPaymentForm.getCseToken());

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

        if (addressData.getCountry() != null && !addressData.getCountry().getIsocode().isEmpty()) {

            // countryModel from service
            country = commonI18NService.getCountry(addressData.getCountry().getIsocode());
            addressModel.setCountry(country);
        }

        addressModel.setEmail(getCheckoutCustomerStrategy().getCurrentUserForCheckout().getContactEmail());

        addressModel.setStreetname(addressData.getLine1());
        addressModel.setLine2(addressData.getLine2());
        addressModel.setPostalcode(addressData.getPostalCode());
        addressModel.setTown(addressData.getTown());

        if (addressData.getRegion() != null && !addressData.getRegion().getIsocode().isEmpty() && country != null) {
            final RegionModel regionModel = commonI18NService.getRegion(country, addressData.getRegion().getIsocode());
            addressModel.setRegion(regionModel);
        }

        addressModel.setBillingAddress(true);
        addressModel.setOwner(paymentInfo);

        paymentInfo.setBillingAddress(addressModel);

        paymentInfo.setAdyenPaymentMethod(adyenPaymentForm.getPaymentMethod());
        paymentInfo.setAdyenIssuerId(adyenPaymentForm.getIssuerId());

        paymentInfo.setAdyenRememberTheseDetails(adyenPaymentForm.getRememberTheseDetails());
        paymentInfo.setAdyenSelectedAlias(adyenPaymentForm.getSelectedAlias());

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
     * Setup the available payment methods
     */
    private void setupAvailablePaymentMethods() {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        //Set APMs from Adyen HPP Directory Lookup
        try {
            alternativePaymentMethods = getAdyenPaymentService().getPaymentMethods(
                    cartData.getTotalPrice().getValue(),
                    cartData.getTotalPrice().getCurrencyIso(),
                    cartData.getDeliveryAddress().getCountry().getIsocode()
            );
        } catch (HTTPClientException e) {
            LOGGER.error("HTTPClientException: " + e);
        } catch (SignatureException e) {
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
        }

        //Set allowed cards from BaseStore configuration
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        allowedCards = baseStore.getAdyenAllowedCards();

        /**
         * The show remember me checkout should only be shown as the
         * user is logged in and the recurirng mode is set to ONECLICK or ONECLICK,RECURRING
         */
        RecurringContractMode recurringContractMode = baseStore.getAdyenRecurringContractMode();

        storedCards = new ArrayList<>();
        showRememberTheseDetails = false;
        if (!this.getCheckoutCustomerStrategy().isAnonymousCheckout() &&
                (Recurring.ContractEnum.ONECLICK_RECURRING.name().equals(recurringContractMode.getCode()) ||
                        Recurring.ContractEnum.ONECLICK.name().equals(recurringContractMode.getCode()))) {
            showRememberTheseDetails = true;

            //Include stored cards
            CustomerModel customerModel = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
            try {
                storedCards = getAdyenPaymentService().getStoredCards(customerModel);
            } catch (ApiException e) {
                LOGGER.error("API Exception " + e.getError());
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
        }
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
