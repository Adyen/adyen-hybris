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
package com.adyen.v6.converters;

import org.springframework.core.convert.converter.Converter;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.PaymentsResponse;
import static com.adyen.constants.ApiConstants.AdditionalData.AUTH_CODE;

public class PaymentsResponseConverter implements Converter<PaymentResult, PaymentsResponse> {
    @Override
    public PaymentsResponse convert(PaymentResult paymentResult) {
        if (paymentResult == null) {
            throw new IllegalArgumentException("Null PaymentResult");
        }
        PaymentsResponse paymentsResponse = new PaymentsResponse();
        paymentsResponse.setPspReference(paymentResult.getPspReference());
        paymentsResponse.setFraudResult(paymentResult.getFraudResult());

        paymentsResponse.setAdditionalData(paymentResult.getAdditionalData());
        paymentsResponse.putAdditionalDataItem(AUTH_CODE, paymentResult.getAuthCode());

        return paymentsResponse;
    }
}
