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

import java.security.SignatureException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import de.hybris.platform.order.InvalidCartException;

/**
 * Facade for initiating Paypal Express Checkout Shortcut transactions
 */
public interface AdyenPaypalFacade {
    String PAYPAL_ECS_CARD_HOLDER_NAME = "card.holderName";
    String PAYPAL_ECS_DELIVERY_ADDRESS_STATE = "deliveryAddress.state";
    String PAYPAL_ECS_DELIVERY_ADDRESS_CITY = "deliveryAddress.city";
    String PAYPAL_ECS_DELIVERY_ADDRESS_STATE_OR_PROVINCE = "deliveryAddress.stateOrProvince";
    String PAYPAL_ECS_DELIVERY_ADDRESS_POSTAL_CODE = "deliveryAddress.postalCode";
    String PAYPAL_ECS_DELIVERY_ADDRESS_STREET = "deliveryAddress.street";
    String PAYPAL_ECS_DELIVERY_ADDRESS_COUNTRY = "deliveryAddress.country";
    String PAYPAL_ECS_SHOPPER_EMAIL = "shopperEmail";
    String PAYPAL_ECS_SHOPPER_FIRST_NAME = "shopper.firstName";
    String PAYPAL_ECS_SHOPPER_LAST_NAME = "shopper.lastName";
    String PAYPAL_ECS_SHOPPER_PHONE_NUMBER = "shopper.telephoneNumber";
    String PAYPAL_ECS_BILLING_ADDRESS_STATE_OR_PROVINCE = "billingAddress.stateOrProvince";
    String PAYPAL_ECS_BILLING_ADDRESS_POSTAL_CODE = "billingAddress.postalCode";
    String PAYPAL_ECS_BILLING_ADDRESS_STATE = "billingAddress.state";
    String PAYPAL_ECS_BILLING_ADDRESS_STREET = "billingAddress.street";
    String PAYPAL_ECS_BILLING_ADDRESS_CITY = "billingAddress.city";
    String PAYPAL_ECS_BILLING_ADDRESS_COUNTRY = "billingAddress.country";
    String PAYPAL_ECS_PAYMENT_TOKEN = "payment.token";
    String PAYPAL_ECS_PAYMENT_PAYER_ID = "payment.payerid";

    /**
     * Validates Paypal ECS response
     * Restores locked cart
     *
     * @param request HTTP request object
     * @return true when the Paypal authorization succeeded
     * @throws SignatureException   In case of wrong HPP signature
     * @throws InvalidCartException In case cart cannot be restored
     */
    boolean handlePaypalECSResponse(HttpServletRequest request) throws SignatureException, InvalidCartException;

    /**
     * Updates the session cart with data coming from Paypal response
     *
     * @param request HTTP request
     * @param updateExistingDeliveryAddress Overwrite delivery address if it is already set
     */
    void updateCart(HttpServletRequest request, boolean updateExistingDeliveryAddress);

    /**
     * Initializes Paypal ECS payment
     * Locks session cart
     *
     * @param redirectUrl URL to redirect after
     * @return HPP key/value pairs
     * @throws SignatureException   In case of wrong HPP signature
     * @throws InvalidCartException In case there is an existing locked cart
     */
    Map<String, String> initializePaypalECS(String redirectUrl) throws SignatureException, InvalidCartException;
}
