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
package com.adyen.v6.populator;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class CartPopulator implements Populator<CartModel, CartData> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final CartModel source, final CartData target) throws ConversionException {
        target.setAdyenDfValue(source.getAdyenDfValue());

        final PaymentInfoModel paymentInfo = source.getPaymentInfo();
        if (paymentInfo != null && isAdyenPaymentInfo(paymentInfo)) {
            target.setAdyenPaymentMethod(paymentInfo.getAdyenPaymentMethod());
            target.setAdyenIssuerId(paymentInfo.getAdyenIssuerId());
            target.setAdyenRememberTheseDetails(paymentInfo.getAdyenRememberTheseDetails());
            target.setAdyenSelectedReference(paymentInfo.getAdyenSelectedReference());
            target.setAdyenDob(paymentInfo.getAdyenDob());
            target.setAdyenSocialSecurityNumber(paymentInfo.getAdyenSocialSecurityNumber());
            target.setAdyenFirstName(paymentInfo.getAdyenFirstName());
            target.setAdyenLastName(paymentInfo.getAdyenLastName());
            target.setAdyenPaymentToken(paymentInfo.getAdyenPaypalEcsToken());
            target.setAdyenCardHolder(paymentInfo.getAdyenCardHolder());
            target.setAdyenCardBrand(paymentInfo.getCardBrand());
            target.setAdyenEncryptedCardNumber(paymentInfo.getEncryptedCardNumber());
            target.setAdyenEncryptedExpiryMonth(paymentInfo.getEncryptedExpiryMonth());
            target.setAdyenEncryptedExpiryYear(paymentInfo.getEncryptedExpiryYear());
            target.setAdyenEncryptedSecurityCode(paymentInfo.getEncryptedSecurityCode());
            target.setAdyenInstallments(paymentInfo.getAdyenInstallments());
            target.setAdyenBrowserInfo(paymentInfo.getAdyenBrowserInfo());
        }
    }

    protected boolean isAdyenPaymentInfo(final PaymentInfoModel paymentInfo) {
        return ! (paymentInfo instanceof CreditCardPaymentInfoModel);
    }
}
