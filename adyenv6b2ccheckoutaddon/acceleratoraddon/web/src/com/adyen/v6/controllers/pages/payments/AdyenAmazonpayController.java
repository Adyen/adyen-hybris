package com.adyen.v6.controllers.pages.payments;

import com.adyen.constants.ApiConstants;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.checkout.details.AmazonPayDetails;
import com.adyen.v6.controllers.pages.AdyenSummaryCheckoutStepController;
import com.adyen.v6.facades.AdyenAmazonPayFacade;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.adyen.model.checkout.PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER;
import static com.adyen.v6.constants.AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX;

@Controller
@RequestMapping(value = SUMMARY_CHECKOUT_PREFIX + "/amazonpay")

public class AdyenAmazonpayController extends AdyenSummaryCheckoutStepController {

    @Resource(name = "adyenCheckoutFacade")
    protected AdyenCheckoutFacade adyenCheckoutFacade;
    @Resource
    protected CheckoutFlowFacade checkoutFlowFacade;
    @Resource(name = "adyenAmazonPayFacade")
    protected AdyenAmazonPayFacade adyenAmazonPayFacade;
    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;

    @GetMapping(value = "/placeorder")
    @RequireHardLogIn
    public String placeOrder(final Model model,
                             final RedirectAttributes redirectModel,
                             final HttpServletRequest request,
                             @RequestParam(name = "amazonCheckoutSessionId", required = false) final String amazonCheckoutSessionId)
            throws CMSItemNotFoundException, CommerceCartModificationException {


        if (StringUtils.isBlank(amazonCheckoutSessionId)) {
            GlobalMessages.addErrorMessage(model, getErrorMessageByRefusalReason(ApiConstants.RefusalReason.PAYMENT_DETAIL_NOT_FOUND));
            return enterStep(model, redirectModel);
        }

        try {
            final CartData cart = checkoutFlowFacade.getCheckoutCart();
            cart.setAdyenReturnUrl(adyenAmazonPayFacade.getReturnUrl(SUMMARY_CHECKOUT_PREFIX + CHECKOUT_RESULT_URL));

            final PaymentsResponse paymentsResponse = adyenCheckoutFacade.componentPayment(request,
                    cart,
                    new AmazonPayDetails().amazonPayToken(adyenAmazonPayFacade.getAmazonPayToken(amazonCheckoutSessionId))
            );

            if (REDIRECTSHOPPER == paymentsResponse.getResultCode()) {
                return redirectTo3DSValidation(model, paymentsResponse);
            }

            return redirectToOrderConfirmationPage(orderFacade.getOrderDetailsForCodeWithoutUser(paymentsResponse.getMerchantReference()));

        } catch (Exception e) {
            GlobalMessages.addErrorMessage(model, getErrorMessageByRefusalReason(ApiConstants.RefusalReason.REFUSED));
        }

        return enterStep(model, redirectModel);

    }

}
