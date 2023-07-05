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

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.checkout.details.*;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.constants.Adyenv6coreConstants;
import com.adyen.v6.controllers.dtos.PaymentResultDTO;
import com.adyen.v6.exceptions.AdyenComponentException;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.adyen.v6.constants.AdyenControllerConstants.COMPONENT_PREFIX;
import static com.adyen.v6.constants.AdyenControllerConstants.SUMMARY_CHECKOUT_PREFIX;
import static com.adyen.v6.constants.Adyenv6coreConstants.*;

@RestController
@RequestMapping(COMPONENT_PREFIX)
public class AdyenComponentController extends AbstractCheckoutController {
    private static final Logger LOGGER = Logger.getLogger(AdyenComponentController.class);

    @Resource(name = "adyenCheckoutFacade")
    private AdyenCheckoutFacade adyenCheckoutFacade;

    @Resource(name = "checkoutFlowFacade")
    private CheckoutFlowFacade checkoutFlowFacade;

    @Resource(name = "acceleratorCheckoutFacade")
    private AcceleratorCheckoutFacade checkoutFacade;

    @Resource(name = "siteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    private final List<String> PAYMENT_METHODS_WITH_VALIDATED_TERMS = Arrays.asList(PAYMENT_METHOD_AMAZONPAY,
            PAYMENT_METHOD_BCMC_MOBILE,
            PAYMENT_METHOD_PIX);

    @RequestMapping(value = "/resultHandler", method = RequestMethod.POST)
    @ResponseBody
    public String componentPaymentResultHandler(@RequestBody final PaymentResultDTO paymentResultDTO) throws Exception {
        final OrderData orderData = getAdyenCheckoutFacade().handleResultcomponentPayment(paymentResultDTO);
        return redirectToOrderConfirmationPage(orderData);
    }

    @RequestMapping(value = "/payment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String componentPayment(final HttpServletRequest request) throws AdyenComponentException {
        try {
            String requestJsonString = IOUtils.toString(request.getInputStream(), String.valueOf(StandardCharsets.UTF_8));
            JsonObject requestJson = new JsonParser().parse(requestJsonString).getAsJsonObject();
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();

            validateOrderForm(requestJson);

            final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();
            String paymentMethod = cartData.getAdyenPaymentMethod();

            PaymentMethodDetails paymentMethodDetails;
            if (PayPalDetails.PAYPAL.equals(paymentMethod)) {
                paymentMethodDetails = gson.fromJson(requestJson.get("paymentMethodDetails"), PayPalDetails.class);
            } else if (MbwayDetails.MBWAY.equals(paymentMethod)) {
                paymentMethodDetails = gson.fromJson(requestJson.get("paymentMethodDetails"), MbwayDetails.class);
            } else if (ApplePayDetails.APPLEPAY.equals(paymentMethod)) {
                paymentMethodDetails = gson.fromJson(requestJson.get("paymentMethodDetails"), ApplePayDetails.class);
            } else if (Adyenv6coreConstants.PAYMENT_METHOD_GOOGLE.equals(paymentMethod)) {
                paymentMethodDetails = gson.fromJson(requestJson.get("paymentMethodDetails"), GooglePayDetails.class);
            } else if (UpiCollectDetails.UPI_COLLECT.equals(paymentMethod)) {
                paymentMethodDetails = gson.fromJson(requestJson.get("paymentMethodDetails"), UpiCollectDetails.class);
            } else if (UpiDetails.UPI.equals(paymentMethod)) {
                paymentMethodDetails = gson.fromJson(requestJson.get("paymentMethodDetails"), UpiDetails.class);
            } else if (PAYMENT_METHOD_PIX.equals(paymentMethod) || PAYMENT_METHOD_BCMC_MOBILE.equals(paymentMethod)) {
                paymentMethodDetails = new CardDetails();
                paymentMethodDetails.setType(paymentMethod);
                } else {
                    throw new InvalidCartException("checkout.error.paymentethod.formentry.invalid");
                }

                cartData.setAdyenReturnUrl(getReturnUrl(paymentMethod));

                PaymentsResponse paymentsResponse = getAdyenCheckoutFacade().componentPayment(request, cartData, paymentMethodDetails);
                return gson.toJson(paymentsResponse);
            } catch(InvalidCartException e){
                LOGGER.error("InvalidCartException: " + e.getMessage());
                throw new AdyenComponentException(e.getMessage());
            }
        catch(ApiException e){
                LOGGER.error("ApiException: " + e.toString());
                throw new AdyenComponentException("checkout.error.authorization.payment.refused");
            }  catch(AdyenNonAuthorizedPaymentException e){
                LOGGER.debug("AdyenNonAuthorizedPaymentException occurred. Payment is refused.");
                throw new AdyenComponentException("checkout.error.authorization.payment.refused");
            } catch(Exception e){
                LOGGER.error("Exception", e);
                throw new AdyenComponentException("checkout.error.authorization.payment.error");
            }
        }

        @RequestMapping(value = "/submit-details", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
        @ResponseBody
        public String submitDetails ( final HttpServletRequest request) throws AdyenComponentException {
            try {
                String requestJsonString = IOUtils.toString(request.getInputStream(), String.valueOf(StandardCharsets.UTF_8));
                JsonObject requestJson = new JsonParser().parse(requestJsonString).getAsJsonObject();

                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                Type mapType = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> details = gson.fromJson(requestJson.get("details"), mapType);
                String paymentData = gson.fromJson(requestJson.get("paymentData"), String.class);

                PaymentsDetailsResponse paymentsResponse = getAdyenCheckoutFacade().componentDetails(request, details, paymentData);
                return gson.toJson(paymentsResponse);
            } catch (ApiException e) {
                LOGGER.error("ApiException: " + e.toString());
                throw new AdyenComponentException("checkout.error.authorization.payment.refused");
            } catch (Exception e) {
                LOGGER.error("Exception", e);
                throw new AdyenComponentException("checkout.error.authorization.payment.error");
            }
        }

        @ResponseStatus(value = HttpStatus.BAD_REQUEST)
        @ExceptionHandler(value = AdyenComponentException.class)
        public String adyenComponentExceptionHandler (AdyenComponentException e){
            return e.getMessage();
        }

        /**
         * Validates the order form before to filter out invalid order states
         *
         * @return True if the order form is invalid and false if everything is valid.
         * @param requestJson
         */
        protected void validateOrderForm (JsonObject requestJson) throws InvalidCartException {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            Boolean termsCheck = gson.fromJson(requestJson.get("termsCheck"), Boolean.class);
            JsonObject paymentMethodDetails = requestJson.get("paymentMethodDetails").getAsJsonObject();
            String paymentMethod = gson.fromJson(paymentMethodDetails.get("type"), String.class);

            // Some methods already have the terms validated on a previous step
            if (!PAYMENT_METHODS_WITH_VALIDATED_TERMS.contains(paymentMethod)
                    && (termsCheck == null || !termsCheck)) {
                throw new InvalidCartException("checkout.error.terms.not.accepted");
            }

            if (getCheckoutFlowFacade().hasNoDeliveryAddress()) {
                throw new InvalidCartException("checkout.deliveryAddress.notSelected");
            }

            if (getCheckoutFlowFacade().hasNoDeliveryMode()) {
                throw new InvalidCartException("checkout.deliveryMethod.notSelected");
            }

            if (getCheckoutFlowFacade().hasNoPaymentInfo()) {
                throw new InvalidCartException("checkout.paymentMethod.notSelected");
            }

            final CartData cartData = getCheckoutFacade().getCheckoutCart();

            if (!getCheckoutFacade().containsTaxValues()) {
                LOGGER.error(String.format("Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue", cartData.getCode()));
                throw new InvalidCartException("checkout.error.tax.missing");
            }

            if (!cartData.isCalculated()) {
                LOGGER.error(String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
                throw new InvalidCartException("checkout.error.cart.notcalculated");
            }
        }

        private String getReturnUrl (String paymentMethod){
            String url;
            if (GooglePayDetails.GOOGLEPAY.equals(paymentMethod)) {
                //Google Pay will only use returnUrl if redirected to 3DS authentication
                url = SUMMARY_CHECKOUT_PREFIX + "/authorise-3d-adyen-response";
            } else {
                url = COMPONENT_PREFIX + "/submit-details";
            }
            BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
            return siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSite, true, url);
        }

        public AdyenCheckoutFacade getAdyenCheckoutFacade () {
            return adyenCheckoutFacade;
        }

        public void setAdyenCheckoutFacade (AdyenCheckoutFacade adyenCheckoutFacade){
            this.adyenCheckoutFacade = adyenCheckoutFacade;
        }

        public CheckoutFlowFacade getCheckoutFlowFacade () {
            return checkoutFlowFacade;
        }

        public void setCheckoutFlowFacade (CheckoutFlowFacade checkoutFlowFacade){
            this.checkoutFlowFacade = checkoutFlowFacade;
        }

        public AcceleratorCheckoutFacade getCheckoutFacade () {
            return checkoutFacade;
        }

        public void setCheckoutFacade (AcceleratorCheckoutFacade checkoutFacade){
            this.checkoutFacade = checkoutFacade;
        }

        private boolean isValidateSessionCart () {
            CartData cart = getCheckoutFacade().getCheckoutCart();
            final AddressData deliveryAddress = cart.getDeliveryAddress();
            if (deliveryAddress == null || deliveryAddress.getCountry() == null || deliveryAddress.getCountry().getIsocode() == null) {
                return false;
            }
            return true;

        }
    }
