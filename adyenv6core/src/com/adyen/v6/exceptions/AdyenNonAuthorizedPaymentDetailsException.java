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
 *  Copyright (c) 2021 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.exceptions;

import com.adyen.model.checkout.PaymentsDetailsResponse;

public class AdyenNonAuthorizedPaymentDetailsException extends Exception {
    private PaymentsDetailsResponse paymentDetails;

    public AdyenNonAuthorizedPaymentDetailsException(PaymentsDetailsResponse paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public AdyenNonAuthorizedPaymentDetailsException(String message) {
        super(message);
    }

    public PaymentsDetailsResponse getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentsDetailsResponse paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
}
