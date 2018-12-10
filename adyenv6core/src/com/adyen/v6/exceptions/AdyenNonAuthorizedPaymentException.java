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
package com.adyen.v6.exceptions;

import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.PaymentsResponse;

public class AdyenNonAuthorizedPaymentException extends Exception {
    private PaymentResult paymentResult;
    private PaymentsResponse paymentsResponse;

    public AdyenNonAuthorizedPaymentException(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public AdyenNonAuthorizedPaymentException(PaymentsResponse paymentsResponse) {
        this.paymentsResponse = paymentsResponse;
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public PaymentsResponse getPaymentsResponse() {
        return paymentsResponse;
    }

    public void setPaymentsResponse(PaymentsResponse paymentsResponse) {
        this.paymentsResponse = paymentsResponse;
    }
}
