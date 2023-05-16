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
package com.adyen.v6.facades;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.controllers.dtos.PaymentResultDTO;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.forms.AdyenPaymentForm;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adyen Checkout Facade for initiating payments using CC or APM
 */
public interface AdyenCheckoutFacade {

    String getShopperLocale();

    /**
     * Retrieve the host of Secured Fields
     */
    String getCheckoutShopperHost();

    /**
     * Retrieve the environment is running in test mode or live mode
     */
    String getEnvironmentMode();

    /**
     * Removes cart from the session so that users can't update it while being in a payment page
     */
    void lockSessionCart();

    /**
     * Restores the sessionCart that has been previously locked
     *
     * @return session cart
     * @throws InvalidCartException if cart cannot be retrieved
     */
    CartModel restoreSessionCart() throws InvalidCartException;

    /**
     * Handles Adyen Redirect Response
     * In case of authorized, it places an order from cart
     *
     * @param details consisting of parameters present in response query string
     * @return PaymentsResponse
     */
    PaymentsDetailsResponse handleRedirectPayload(HashMap<String,String> details) throws Exception;

    /**
     * Authorizes a payment using Adyen API
     * In case of authorized, it places an order from cart
     *
     * @param request  HTTP Request info
     * @param cartData cartData object
     * @return OrderData
     * @throws Exception In case order failed to be created
     */
    OrderData authorisePayment(HttpServletRequest request, CartData cartData) throws Exception;

    OrderData handleResultcomponentPayment(PaymentResultDTO paymentResultDTO) throws Exception;

    /**
     * Creates a payment coming from an Adyen Checkout Component
     * No session handling
     *
     * @param request               HTTP Request info
     * @param cartData              cartData object
     * @param paymentMethodDetails  paymentMethodDetails object
     * @return PaymentsResponse
     * @throws Exception In case payment failed
     */
    PaymentsResponse componentPayment(HttpServletRequest request, CartData cartData, PaymentMethodDetails paymentMethodDetails) throws Exception;

    /**
     * Submit details from a payment made on an Adyen Checkout Component
     * No session handling
     *
     * @param request               HTTP Request info
     * @param details               details
     * @param paymentData           paymentData
     * @return PaymentsResponse
     * @throws Exception In case request failed
     */
    PaymentsDetailsResponse componentDetails(HttpServletRequest request, Map<String, String> details, String paymentData) throws Exception;

    /**
     * Add payment details to cart
     */
    PaymentDetailsWsDTO addPaymentDetails(PaymentDetailsWsDTO paymentDetails);

    /**
     * Handles an 3D response
     * In case of authorized, it places an order from cart
     *
     * @param details HTTP Request object
     * @return OrderData
     * @throws Exception In case order failed to be created
     */

    OrderData handle3DSResponse(Map<String, String> details) throws Exception;

    /**
     * Retrieve available payment methods
     */
    void initializeCheckoutData(Model model) throws ApiException;

    void initializeSummaryData(Model model) throws ApiException;

    /**
     * Returns whether Boleto should be shown as an available payment method on the checkout page
     * Relevant for Brasil
     */
    boolean showBoleto();

    boolean showComboCard();
    /**
     * Returns whether POS should be shown as an available payment method on the checkout page
     */
    boolean showPos();

    /**
     * Returns whether CC can be stored depending on the recurring contract settings
     */
    boolean showRememberDetails();

    /**
     * Returns whether Social Security Number should be shown on the checkout page
     * Relevant for openinvoice methods
     */
    boolean showSocialSecurityNumber();

    /**
     * Creates PaymentInfoModel based on cart and form data
     */
    PaymentInfoModel createPaymentInfo(CartModel cartModel, AdyenPaymentForm adyenPaymentForm);

    /**
     * Handles payment form submission
     * Validates the form and updates the cart based on form data
     * Updates BindingResult
     */
    void handlePaymentForm(AdyenPaymentForm adyenPaymentForm, BindingResult bindingResult);

    List<CountryData> getBillingCountries();

    PaymentDetailsListWsDTO getPaymentDetails(String userId) throws IOException, ApiException;

    /**
     * Initiate POS Payment using Adyen Terminal API
     */
    OrderData initiatePosPayment(HttpServletRequest request, CartData cartData) throws Exception;

    /**
     * Check POS Payment status using Adyen Terminal API
     */
    OrderData checkPosPaymentStatus(HttpServletRequest request, CartData cartData) throws Exception;

    /**
     * Returns whether payments have Immediate Capture or not
     */
    boolean isImmediateCapture();

    /**
     * Handles payment result from component
     * Validates the result and updates the cart based on it
     */
    OrderData handleComponentResult(String resultJson) throws Exception;

    void restoreCartFromOrderCodeInSession() throws InvalidCartException, CalculationException;

    String getClientKey();
}
