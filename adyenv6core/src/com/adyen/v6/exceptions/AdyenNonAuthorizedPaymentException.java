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
import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.terminal.TerminalAPIResponse;

public class AdyenNonAuthorizedPaymentException extends Exception {
    private PaymentResult paymentResult;
    private PaymentsResponse paymentsResponse;
    private TerminalAPIResponse terminalApiResponse;
    private PaymentsDetailsResponse paymentsDetailsResponse;

    public AdyenNonAuthorizedPaymentException(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public AdyenNonAuthorizedPaymentException(PaymentsResponse paymentsResponse) {
        this.paymentsResponse = paymentsResponse;
    }

    public AdyenNonAuthorizedPaymentException(TerminalAPIResponse terminalApiResponse) {
        this.terminalApiResponse = terminalApiResponse;
    }

    public AdyenNonAuthorizedPaymentException(PaymentsDetailsResponse paymentsDetailsResponse) {
        this.paymentsDetailsResponse = paymentsDetailsResponse;
    }

    public AdyenNonAuthorizedPaymentException(String message) {
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

    public PaymentsDetailsResponse getPaymentsDetailsResponse() {
        return paymentsDetailsResponse;
    }

    public void setPaymentsDetailsResponse(PaymentsDetailsResponse paymentsDetailsResponse) {
        this.paymentsDetailsResponse = paymentsDetailsResponse;
    }
}
