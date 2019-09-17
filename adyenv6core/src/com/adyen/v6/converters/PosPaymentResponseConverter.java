/*
 *                       ######
 *                       ######
 * ############    ####( ######  #####. ######  ############   ############
 * #############  #####( ######  #####. ######  #############  #############
 *        ######  #####( ######  #####. ######  #####  ######  #####  ######
 * ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 * ###### ######  #####( ######  #####. ######  #####          #####  ######
 * #############  #############  #############  #############  #####  ######
 *  ############   ############  #############   ############  #####  ######
 *                                      ######
 *                               #############
 *                               ############
 *
 * Adyen Hybris Extension
 *
 * Copyright (c) 2019 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 */
package com.adyen.v6.converters;

import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.nexo.SaleToPOIResponse;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

public class PosPaymentResponseConverter implements Converter<SaleToPOIResponse, PaymentsResponse> {
    @Override
    public PaymentsResponse convert(SaleToPOIResponse saleToPOIResponse) {
        String pspReference = saleToPOIResponse.getPaymentResponse().getPaymentResult().getPaymentAcquirerData().getAcquirerTransactionID().getTransactionID();
        String additionalResponse = saleToPOIResponse.getPaymentResponse().getResponse().getAdditionalResponse();
        Map<String, String> additionalData = parseAdditionalResponse(additionalResponse);

        PaymentsResponse paymentsResponse = new PaymentsResponse();
        paymentsResponse.setPspReference(pspReference);

        paymentsResponse.setAdditionalData(additionalData);

        return paymentsResponse;
    }

    private Map<String, String> parseAdditionalResponse(String additionalResponse) {
        Map<String, String> additionalData = new HashMap<>();
        String[] pairs = additionalResponse.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            additionalData.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return additionalData;
    }
}
