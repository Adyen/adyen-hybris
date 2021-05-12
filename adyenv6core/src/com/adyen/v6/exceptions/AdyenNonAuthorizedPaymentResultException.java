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
import com.adyen.model.terminal.TerminalAPIResponse;

public class AdyenNonAuthorizedPaymentResultException extends Exception {
    private PaymentResult paymentResult;
    private PaymentsResponse paymentsResponse;
    private TerminalAPIResponse terminalApiResponse;

    public AdyenNonAuthorizedPaymentResultException(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public AdyenNonAuthorizedPaymentResultException(PaymentsResponse paymentsResponse) {
        this.paymentsResponse = paymentsResponse;
    }

    public AdyenNonAuthorizedPaymentResultException(TerminalAPIResponse terminalApiResponse) {
        this.terminalApiResponse = terminalApiResponse;
    }

    public AdyenNonAuthorizedPaymentResultException(String message) {
        super(message);
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

    public TerminalAPIResponse getTerminalApiResponse() {
        return terminalApiResponse;
    }

    public void setTerminalApiResponse(TerminalAPIResponse terminalApiResponse) {
        this.terminalApiResponse = terminalApiResponse;
    }
}
