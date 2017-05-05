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
        target.setAdyenCseToken(source.getAdyenCseToken());

        final PaymentInfoModel paymentInfo = source.getPaymentInfo();
        if (paymentInfo != null && isNotCreditCard(paymentInfo)) {
            target.setAdyenPaymentMethod(paymentInfo.getAdyenPaymentMethod());
            target.setAdyenIssuerId(paymentInfo.getAdyenIssuerId());
            target.setAdyenRememberTheseDetails(paymentInfo.getAdyenRememberTheseDetails());
            target.setAdyenSelectedReference(paymentInfo.getAdyenSelectedReference());
        }
    }

    protected boolean isNotCreditCard(final PaymentInfoModel paymentInfo) {
        return !(paymentInfo instanceof CreditCardPaymentInfoModel);
    }
}
