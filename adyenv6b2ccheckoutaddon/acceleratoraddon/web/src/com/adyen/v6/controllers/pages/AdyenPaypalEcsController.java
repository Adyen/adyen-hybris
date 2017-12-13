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

import java.security.SignatureException;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.adyen.constants.HPPConstants;
import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.facades.AdyenPaypalFacade;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import static com.adyen.constants.HPPConstants.Response.AUTH_RESULT;
import static com.adyen.v6.constants.AdyenControllerConstants.PAYPAL_ECS_PREFIX;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_SHOPPER_EMAIL;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_SHOPPER_FIRST_NAME;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_SHOPPER_LAST_NAME;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController.REDIRECT_PREFIX;

@Controller
@RequestMapping(PAYPAL_ECS_PREFIX)
public class AdyenPaypalEcsController {
    private static final Logger LOG = Logger.getLogger(AdyenPaypalEcsController.class);
    private static final String PROCESS_URL = "/process";

    @Resource(name = "siteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @Resource(name = "adyenPaypalFacade")
    private AdyenPaypalFacade adyenPaypalFacade;

    @Resource(name = "adyenCheckoutFacade")
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Resource(name = "checkoutFlowFacade")
    private CheckoutFlowFacade checkoutFlowFacade;

    @Resource(name = "guidCookieStrategy")
    private GUIDCookieStrategy guidCookieStrategy;

    @Resource(name = "customerFacade")
    private CustomerFacade customerFacade;

    @Resource(name = "sessionService")
    private SessionService sessionService;

    @Resource(name = "userFacade")
    private UserFacade userFacade;

    @RequestMapping(value = "/initialize", method = RequestMethod.GET)
    public String initialize(final Model model, final RedirectAttributes redirectModel, final HttpServletRequest request) {
        try {
            Map<String, String> hppFormData = adyenPaypalFacade.initializePaypalECS(getRedirectUrl());

            //Building HPP data
            model.addAttribute("hppUrl", adyenCheckoutFacade.getHppUrl());
            model.addAttribute("hppFormData", hppFormData);

            return AdyenControllerConstants.Views.Pages.MultiStepCheckout.HppPaymentPage;
        } catch (SignatureException | InvalidCartException e) {
            LOG.error(e);
        }

        LOG.debug("Redirecting to cart..");
        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "checkout.error.authorization.payment.error");
        return REDIRECT_PREFIX + "/cart";
    }

    @RequestMapping(value = PROCESS_URL)
    public String process(final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel) {
        try {
            //Handle PP ECS response
            boolean success = adyenPaypalFacade.handlePaypalECSResponse(request);

            //On success, update user and cart
            if (success) {
                //If the user is anonymous, convert him to guest
                if (userFacade.isAnonymousUser()) {
                    getCustomerFacade().createGuestUserForAnonymousCheckout(request.getParameter(PAYPAL_ECS_SHOPPER_EMAIL),
                                                                            request.getParameter(PAYPAL_ECS_SHOPPER_FIRST_NAME) + request.getParameter(PAYPAL_ECS_SHOPPER_LAST_NAME));
                    getGuidCookieStrategy().setCookie(request, response);
                    getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
                }

                //Update the cart
                adyenPaypalFacade.updateCart(request, false);

                //Pick the cheapest delivery mode and redirect to the last step of checkout
                checkoutFlowFacade.setCheapestDeliveryModeForCheckout();

                return REDIRECT_PREFIX + "/checkout/multi/adyen/summary/view";
            }

            //In case of error, display the appropriate flash message
            switch (request.getParameter(AUTH_RESULT)) {
                case HPPConstants.Response.AUTH_RESULT_REFUSED:
                    GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "checkout.error.authorization.payment.refused");
                    break;
                case HPPConstants.Response.AUTH_RESULT_CANCELLED:
                    GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "checkout.error.authorization.payment.cancelled");
                    break;
                case HPPConstants.Response.AUTH_RESULT_ERROR:
                default:
                    GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "checkout.error.authorization.payment.error");
                    break;
            }
        } catch (SignatureException | InvalidCartException | DuplicateUidException e) {
            LOG.warn(e);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "checkout.error.authorization.payment.error");
        }

        LOG.debug("Redirecting to cart..");
        return REDIRECT_PREFIX + "/cart";
    }

    /**
     * Construct the URL for PP ECS redirect
     */
    private String getRedirectUrl() {
        String url = PAYPAL_ECS_PREFIX + PROCESS_URL;
        BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();

        return siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSite, true, url);
    }

    public SiteBaseUrlResolutionService getSiteBaseUrlResolutionService() {
        return siteBaseUrlResolutionService;
    }

    public void setSiteBaseUrlResolutionService(SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
        this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public AdyenPaypalFacade getAdyenPaypalFacade() {
        return adyenPaypalFacade;
    }

    public void setAdyenPaypalFacade(AdyenPaypalFacade adyenPaypalFacade) {
        this.adyenPaypalFacade = adyenPaypalFacade;
    }

    public AdyenCheckoutFacade getAdyenCheckoutFacade() {
        return adyenCheckoutFacade;
    }

    public void setAdyenCheckoutFacade(AdyenCheckoutFacade adyenCheckoutFacade) {
        this.adyenCheckoutFacade = adyenCheckoutFacade;
    }

    public CheckoutFlowFacade getCheckoutFlowFacade() {
        return checkoutFlowFacade;
    }

    public void setCheckoutFlowFacade(CheckoutFlowFacade checkoutFlowFacade) {
        this.checkoutFlowFacade = checkoutFlowFacade;
    }

    public GUIDCookieStrategy getGuidCookieStrategy() {
        return guidCookieStrategy;
    }

    public void setGuidCookieStrategy(GUIDCookieStrategy guidCookieStrategy) {
        this.guidCookieStrategy = guidCookieStrategy;
    }

    public CustomerFacade getCustomerFacade() {
        return customerFacade;
    }

    public void setCustomerFacade(CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(UserFacade userFacade) {
        this.userFacade = userFacade;
    }
}
