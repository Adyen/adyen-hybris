package com.adyen.v6.controllers.checkout;

import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.controllers.pages.checkout.steps.AdyenDeliveryAddressCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateQuoteCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/checkout/multi/")
public class AdyenPageCheckoutStepController extends AbstractCheckoutStepController {
    private static final String DELIVERY_ADDRESS = "delivery-address";
    private static final String SHOW_SAVE_TO_ADDRESS_BOOK_ATTR = "showSaveToAddressBook";

    @Resource(name = "addressDataUtil")
    private AddressDataUtil addressDataUtil;


    @RequestMapping(value = "/adyen", method = RequestMethod.GET)
    @RequireHardLogIn
    @PreValidateQuoteCheckoutStep
    @PreValidateCheckoutStep(checkoutStep = DELIVERY_ADDRESS)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        getCheckoutFacade().setDeliveryAddressIfAvailable();
        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        populateCommonModelAttributes(model, cartData, new AddressForm());

        return  "addon:/adyenv6b2ccheckoutaddonv2/pages/adyenSPACheckout";
    }

    protected void populateCommonModelAttributes(final Model model, final CartData cartData, final AddressForm addressForm)
            throws CMSItemNotFoundException {
        model.addAttribute("cartData", cartData);
        model.addAttribute("addressForm", addressForm);
        model.addAttribute("deliveryAddresses", getDeliveryAddresses(cartData.getDeliveryAddress()));
        model.addAttribute("noAddress", Boolean.valueOf(getCheckoutFlowFacade().hasNoDeliveryAddress()));
        model.addAttribute("addressFormEnabled", Boolean.valueOf(getCheckoutFacade().isNewAddressEnabledForCart()));
        model.addAttribute("removeAddressEnabled", Boolean.valueOf(getCheckoutFacade().isRemoveAddressEnabledForCart()));
        model.addAttribute(SHOW_SAVE_TO_ADDRESS_BOOK_ATTR, Boolean.TRUE);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(getBreadcrumbKey()));
        model.addAttribute("metaRobots", "noindex,nofollow");
        if (StringUtils.isNotBlank(addressForm.getCountryIso())) {
            model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
            model.addAttribute("country", addressForm.getCountryIso());
        }
        prepareDataForPage(model);
        final ContentPageModel multiCheckoutSummaryPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
        storeCmsPageInModel(model, multiCheckoutSummaryPage);
        setUpMetaDataForContentPage(model, multiCheckoutSummaryPage);
        setCheckoutStepLinksForModel(model, getCheckoutStep());
    }
    protected String getBreadcrumbKey() {
        return "checkout.multi." + getCheckoutStep().getProgressBarId() + ".breadcrumb";
    }

    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(DELIVERY_ADDRESS);
    }

    @Override
    public String back(RedirectAttributes redirectAttributes) {
        return null;
    }

    @Override
    public String next(RedirectAttributes redirectAttributes) {
        return null;
    }
}
