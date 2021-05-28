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

import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.InvocationTargetException;

public class PaymentsDetailsResponseConverter implements Converter<PaymentsDetailsResponse, PaymentsResponse> {
    @Override
    public PaymentsResponse convert(PaymentsDetailsResponse paymentsDetailsResponse) {
        if (paymentsDetailsResponse == null) {
            throw new IllegalArgumentException("Null PaymentsDetailsResponse");
        }
        PaymentsResponse paymentsResponse = new PaymentsResponse();

        try {
            BeanUtils.copyProperties(paymentsResponse, paymentsDetailsResponse);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Bean copy error, Cannot convert from PaymentsDetailsResponse to PaymentsResponse", e);
        }
        return paymentsResponse;
    }
}
