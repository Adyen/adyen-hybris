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
 *  Copyright (c) 2020 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.controllers.pages;

import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.checkout.details.PayPalDetails;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.order.data.CartData;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.adyen.v6.constants.AdyenControllerConstants.COMPONENT_PREFIX;

@Controller
@RequestMapping(COMPONENT_PREFIX)
public class AdyenComponentController {
    private static final Logger LOGGER = Logger.getLogger(AdyenComponentController.class);

    @Resource(name = "adyenCheckoutFacade")
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Resource(name = "checkoutFlowFacade")
    private CheckoutFlowFacade checkoutFlowFacade;

    @Resource(name = "acceleratorCheckoutFacade")
    private AcceleratorCheckoutFacade checkoutFacade;

    @RequestMapping(value = "/paypal-payment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object placeOrder(final Model model, final HttpServletRequest request) {
        try {
            if (validateOrderForm(model)) {
                return new AdyenNonAuthorizedPaymentException("invalid_order_form");
            }

            Gson gson = new Gson();
            String payPalDetailsJson = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
            PayPalDetails payPalDetails = gson.fromJson(payPalDetailsJson, PayPalDetails.class);

            final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();

            return getAdyenCheckoutFacade().componentPayment(request, cartData, payPalDetails);
        } catch (ApiException e) {
            LOGGER.error("ApiException", e);
            return new AdyenNonAuthorizedPaymentException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Exception", e);
            return new AdyenNonAuthorizedPaymentException(e.getMessage());
        }
    }

    @RequestMapping(value = "/submit-details", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object submitDetails(final HttpServletRequest request) {
        try {
            String requestJsonString = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
            JsonObject requestJson = new JsonParser().parse(requestJsonString).getAsJsonObject();

            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> details = gson.fromJson(requestJson.get("details"), mapType);
            String paymentData = gson.fromJson(requestJson.get("paymentData"), String.class);

            return getAdyenCheckoutFacade().componentDetails(request, details, paymentData);
        } catch (ApiException e) {
            LOGGER.error("ApiException", e);
            return new AdyenNonAuthorizedPaymentException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Exception", e);
            return new AdyenNonAuthorizedPaymentException(e.getMessage());
        }
    }

    /**
     * Validates the order form before to filter out invalid order states
     *
     * @param model          A spring Model
     * @return True if the order form is invalid and false if everything is valid.
     */
    protected boolean validateOrderForm(final Model model) {
        boolean invalid = false;

        if (getCheckoutFlowFacade().hasNoDeliveryAddress()) {
            GlobalMessages.addErrorMessage(model, "checkout.deliveryAddress.notSelected");
            invalid = true;
        }

        if (getCheckoutFlowFacade().hasNoDeliveryMode()) {
            GlobalMessages.addErrorMessage(model, "checkout.deliveryMethod.notSelected");
            invalid = true;
        }

        if (getCheckoutFlowFacade().hasNoPaymentInfo()) {
            GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.notSelected");
            invalid = true;
        }

        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        if (! getCheckoutFacade().containsTaxValues()) {
            LOGGER.error(String.format("Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue", cartData.getCode()));
            GlobalMessages.addErrorMessage(model, "checkout.error.tax.missing");
            invalid = true;
        }

        if (! cartData.isCalculated()) {
            LOGGER.error(String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
            GlobalMessages.addErrorMessage(model, "checkout.error.cart.notcalculated");
            invalid = true;
        }

        return invalid;
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

    public AcceleratorCheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(AcceleratorCheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }
}
