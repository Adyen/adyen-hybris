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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.adyen.model.checkout.PaymentMethod;
import org.springframework.core.convert.converter.Converter;

import com.adyen.model.checkout.InputDetail;
import com.adyen.model.hpp.Issuer;

public class PaymentMethodConverter implements Converter<com.adyen.model.checkout.PaymentMethod, PaymentMethod> {
    @Override
    public PaymentMethod convert(com.adyen.model.checkout.PaymentMethod checkoutPaymentMethod) {
        if (checkoutPaymentMethod == null) {
            throw new IllegalArgumentException("Null PaymentMethod");
        }
        final PaymentMethod paymentMethod = new PaymentMethod();

        paymentMethod.setIssuers(checkoutPaymentMethod.getIssuers());
        paymentMethod.setBrand(checkoutPaymentMethod.getType());
        return paymentMethod;
    }
}
