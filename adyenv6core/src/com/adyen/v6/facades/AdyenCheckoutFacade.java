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

import java.io.IOException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.forms.AdyenPaymentForm;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.order.InvalidCartException;

/**
 * Adyen Checkout Facade for initiating payments using CC or APM
 */
public interface AdyenCheckoutFacade {
    /**
     * Validates an HPP response based on Map
     *
     * @param hppResponseData map with hpp data
     * @param merchantSig     merchant signature
     * @throws SignatureException in case signature doesn't match
     */
    void validateHPPResponse(SortedMap<String, String> hppResponseData, String merchantSig) throws SignatureException;

    /**
     * Validates an HPP response based on the HTTP request object
     *
     * @param request HTTP request object
     * @throws SignatureException in case signature doesn't match
     */
    void validateHPPResponse(HttpServletRequest request) throws SignatureException;

    /**
     * Retrieve the WS User Origin Key
     */
    String getOriginKey(HttpServletRequest request) throws IOException, ApiException;

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
     * Retrieve the HPP base URL for the current basestore
     *
     * @return HPP url
     */
    String getHppUrl();

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
     * Handles an HPP response
     * In case of authorized, it places an order from cart
     *
     * @param request Request object containing HPP data
     * @return OrderData
     * @throws SignatureException if signature doesn't match
     */
    OrderData handleHPPResponse(HttpServletRequest request) throws SignatureException;

    /**
     * Handles Adyen Redirect Response
     * In case of authorized, it places an order from cart
     *
     * @param details consisting of parameters present in response query string
     * @return PaymentsResponse
     */
    PaymentsResponse handleRedirectPayload(HashMap<String,String> details);

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

    /**
     * Authorizes a payment using Adyen API
     * In case of authorized, it places an order from cart
     * No session handling
     *
     * @param cartData cartData object
     * @return OrderData
     * @throws Exception In case order failed to be created
     */
    OrderData authorisePayment(CartData cartData) throws Exception;

    /**
     * Add payment details to cart
     */
    PaymentDetailsWsDTO addPaymentDetails(PaymentDetailsWsDTO paymentDetails);

    /**
     * Handles an 3D response
     * In case of authorized, it places an order from cart
     *
     * @param request HTTP Request object
     * @return OrderData
     * @throws Exception In case order failed to be created
     */

    OrderData handle3DS2Response(HttpServletRequest request) throws Exception;

    OrderData handle3DResponse(HttpServletRequest request) throws Exception;

    /**
     * Initializes an HPP payment
     * Returns map of data to be submitted to Adyen HPP
     *
     * @param cartData    Shopper's cart
     * @param redirectUrl HPP result url
     * @return HPP data
     * @throws SignatureException   In case signature cannot be generated
     * @throws InvalidCartException In case there is an existing locked cart
     */
    Map<String, String> initializeHostedPayment(CartData cartData, String redirectUrl) throws SignatureException, InvalidCartException;

    /**
     * Retrieve available payment methods
     */
    void initializeCheckoutData(Model model);

    /**
     * Returns whether Boleto should be shown as an available payment method on the checkout page
     * Relevant for Brasil
     */
    boolean showBoleto();

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

    PaymentDetailsListWsDTO getPaymentDetails(String userId) throws IOException, ApiException;
}
