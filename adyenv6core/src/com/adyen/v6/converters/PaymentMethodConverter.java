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
import org.springframework.core.convert.converter.Converter;
import com.adyen.model.checkout.InputDetail;
import com.adyen.model.hpp.Issuer;

public class PaymentMethodConverter implements Converter<com.adyen.model.checkout.PaymentMethod, com.adyen.model.hpp.PaymentMethod> {
    @Override
    public com.adyen.model.hpp.PaymentMethod convert(com.adyen.model.checkout.PaymentMethod checkoutPaymentMethod) {
        if (checkoutPaymentMethod == null) {
            throw new IllegalArgumentException("Null PaymentMethod");
        }
        com.adyen.model.hpp.PaymentMethod hppPaymentMethod = new com.adyen.model.hpp.PaymentMethod();
        hppPaymentMethod.setBrandCode(checkoutPaymentMethod.getType());

        Optional<InputDetail> issuersInputDetail = checkoutPaymentMethod.getDetails().stream().filter(i -> "issuer".equals(i.getType())).findFirst();
        if (issuersInputDetail.isPresent()) {
            List<Issuer> issuers = issuersInputDetail.get().getItems().stream().map(checkoutIssuer -> {
                Issuer issuer = new Issuer();
                issuer.setIssuerId(checkoutIssuer.getId());
                issuer.setName(checkoutIssuer.getName());
                return issuer;
            }).collect(Collectors.toList());
            hppPaymentMethod.setIssuers(issuers);
        }

        return hppPaymentMethod;
    }
}
