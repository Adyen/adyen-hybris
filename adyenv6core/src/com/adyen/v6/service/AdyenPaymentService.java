package com.adyen.v6.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Currency;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.PaymentResult;
import com.adyen.model.hpp.PaymentMethod;
import com.adyen.model.modification.ModificationResult;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.service.exception.ApiException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.user.CustomerModel;

public interface AdyenPaymentService {
    /**
     * Performs authorization request via Adyen API
     */
    PaymentResult authorise(CartData cartData, HttpServletRequest request, CustomerModel customerModel) throws Exception;

    /**
     * Performs 3D secure authorization request via Adyen API
     */
    PaymentResult authorise3D(HttpServletRequest request, String paRes, String md) throws Exception;

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
     * Get Payment methods using HPP Directory Lookup
     */
    List<PaymentMethod> getPaymentMethods(BigDecimal amount, String currency, String countryCode) throws HTTPClientException, SignatureException, IOException;

    /**
     * Retrieve stored cards from recurring contracts via Adyen API
     */
    List<RecurringDetail> getStoredCards(String customerId) throws IOException, ApiException;

    /**
     * Disables a recurring contract via Adyen API
     */
    boolean disableStoredCard(String customerId, String recurringReference) throws IOException, ApiException;

    /**
     * Returns the HPP base URL for the current basestore
     */
    String getHppEndpoint();
}
