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
package com.adyen.v6.service;

import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.*;
import com.adyen.model.modification.ModificationResult;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.model.terminal.ConnectedTerminalsResponse;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.model.RequestInfo;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AdyenPaymentService {
    /**
     * Performs authorization request via Adyen API
     */
    PaymentResult authorise(CartData cartData, HttpServletRequest request, CustomerModel customerModel) throws Exception;

    ConnectedTerminalsResponse getConnectedTerminals() throws IOException, ApiException;

    PaymentsResponse authorisePayment(CartData cartData, RequestInfo requestInfo, CustomerModel customerModel) throws Exception;

    PaymentsResponse componentPayment(CartData cartData, PaymentMethodDetails paymentMethodDetails, RequestInfo requestInfo, CustomerModel customerModel) throws Exception;

    PaymentsDetailsResponse authorise3DSPayment(Map<String, String> details) throws Exception;

    /**
     * Performs Capture request via Adyen API
     */
    ModificationResult capture(BigDecimal amount, Currency currency, String authReference, String merchantReference) throws Exception;

    /**
     * Performs cancelOrRefund request via Adyen API
     */
    ModificationResult cancelOrRefund(String authReference, String merchantReference) throws Exception;

    /**
     * Performs refund request via Adyen API
     */
    ModificationResult refund(BigDecimal amount, Currency currency, String authReference, String merchantReference) throws Exception;

    /**
     * Get payment methods using /paymentMethods - Checkout API
     */
    List<PaymentMethod> getPaymentMethods(BigDecimal amount, String currency, String countryCode, String shopperLocale, String shopperReference) throws IOException, ApiException;

    PaymentMethodsResponse getPaymentMethodsResponse(BigDecimal amount, String currency, String countryCode, String shopperLocale, String shopperReference) throws IOException, ApiException;

    /**
     * @deprecated use getPaymentMethods including shopperReference instead {@link #getPaymentMethods(BigDecimal amount, String currency, String countryCode, String shopperLocale, String shopperReference)
     */
    @Deprecated
    List<PaymentMethod> getPaymentMethods(BigDecimal amount, String currency, String countryCode, String shopperLocale) throws HTTPClientException, SignatureException, IOException;

    /**
     * Retrieve stored cards from recurring contracts via Adyen API
     *
     * @deprecated use getPaymentMethodsResponse instead {@link #getPaymentMethodsResponse(BigDecimal amount, String currency, String countryCode, String shopperLocale, String shopperReference)} ()
     */
    @Deprecated
    List<RecurringDetail> getStoredCards(String customerId) throws IOException, ApiException;

    /**
     * Disables a recurring contract via Adyen API
     */
    boolean disableStoredCard(String customerId, String recurringReference) throws IOException, ApiException;

    /**
     * Retrieves payment response from /payments/details for redirect methods like klarna
     */
    PaymentsDetailsResponse getPaymentDetailsFromPayload(Map<String, String> details, String paymentData) throws Exception;

    /**
     * Retrieves payment response from /payments/details
     */
    PaymentsDetailsResponse getPaymentDetailsFromPayload(HashMap<String, String> details) throws Exception;

    /**
     * Returns the Device Fingerprint url
     */
    String getDeviceFingerprintUrl();

    /**
     * Send POS Payment Request using Adyen Terminal API
     */
    TerminalAPIResponse sendSyncPosPaymentRequest(CartData cartData, CustomerModel customer, String serviceId) throws Exception;

    /**
     * Send POS Status Request using Adyen Terminal API
     */
    TerminalAPIResponse sendSyncPosStatusRequest(CartData cartData, String serviceId) throws Exception;

    /**
     * Performs Refund request via new Adyen API
     */
    PaymentRefundResource refunds(final BigDecimal amount, final Currency currency, final String authReference, final String merchantReference) throws Exception;

    /**
     * Performs Capture request via new Adyen API
     */
    PaymentCaptureResource captures(final BigDecimal amount, final Currency currency, final String authReference, final String merchantReference) throws Exception;

    /**
     * Performs Cancel or Refunds request via new Adyen API
     */
    PaymentReversalResource cancelOrRefunds(final String authReference, final String merchantReference) throws Exception;

    BigDecimal calculateAmountWithTaxes(final AbstractOrderModel abstractOrderModel);

    CreateCheckoutSessionResponse getPaymentSessionData(final CartData cartData) throws IOException, ApiException;
}
