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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.core.convert.converter.Converter;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosPaymentResponseConverter implements Converter<SaleToPOIResponse, PaymentsResponse> {
    @Override
    public PaymentsResponse convert(SaleToPOIResponse saleToPOIResponse) {
        String pspReference = null;
        Map<String, String> additionalData = null;
        String additionalResponse = null;

        if (saleToPOIResponse != null && saleToPOIResponse.getPaymentResponse() != null) {
            pspReference = saleToPOIResponse.getPaymentResponse()
                                            .getPaymentResult()
                                            .getPaymentAcquirerData()
                                            .getAcquirerTransactionID()
                                            .getTransactionID();

            additionalResponse = saleToPOIResponse.getPaymentResponse()
                                                  .getResponse()
                                                  .getAdditionalResponse();

        } else if (saleToPOIResponse != null && saleToPOIResponse.getTransactionStatusResponse() != null) {
            pspReference = saleToPOIResponse.getTransactionStatusResponse()
                                            .getRepeatedMessageResponse()
                                            .getRepeatedResponseMessageBody()
                                            .getPaymentResponse()
                                            .getPaymentResult()
                                            .getPaymentAcquirerData()
                                            .getAcquirerTransactionID()
                                            .getTransactionID();

            additionalResponse = saleToPOIResponse.getTransactionStatusResponse()
                                                  .getRepeatedMessageResponse()
                                                  .getRepeatedResponseMessageBody()
                                                  .getPaymentResponse()
                                                  .getResponse()
                                                  .getAdditionalResponse();
        }
        if (additionalResponse != null) {
            additionalData = parseAdditionalResponse(additionalResponse);
        }
        PaymentsResponse paymentsResponse = new PaymentsResponse();
        paymentsResponse.setPspReference(pspReference);
        paymentsResponse.setAdditionalData(additionalData);

        return paymentsResponse;
    }

    /*
     * Parse additionalResponse with a query string format, i.e. "tid=123&AID=123&transactionType=GOODS_SERVICES&..."),
     * and return as a name/value map
     */
    private Map<String, String> parseAdditionalResponse(String additionalResponse) {
        Map<String, String> additionalData = new HashMap<>();
        if (StringUtils.isNotEmpty(additionalResponse)) {
            List<NameValuePair> parsedNameValues = URLEncodedUtils.parse(additionalResponse, Charset.forName("UTF-8"));
            for (NameValuePair nameValue : parsedNameValues) {
                additionalData.put(nameValue.getName(), nameValue.getValue());
            }
        }
        return additionalData;
    }
}
