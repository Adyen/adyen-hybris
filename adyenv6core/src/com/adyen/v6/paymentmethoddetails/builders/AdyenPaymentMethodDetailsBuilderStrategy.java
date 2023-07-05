package com.adyen.v6.paymentmethoddetails.builders;

import com.adyen.model.checkout.PaymentMethodDetails;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;

public interface AdyenPaymentMethodDetailsBuilderStrategy<S extends CartData> {

    String SPACE = " ";

    /**
     * Validates if the request qualifies to be applied.
     *
     * @param cartData The cart to use to perform the payment.
     * @return True or false, subject to validations.
     */
    boolean isApplicable(final S cartData);

    /**
     * Build a PaymentMethodDetails based on the cartData information
     * @param cartData
     * @return
     */
    PaymentMethodDetails buildPaymentMethodDetails(final S cartData);

    //Shopper name, phone number, and email address."
    default String getPersonalDetails(final CartData cartData) {
        final AddressData addressData = cartData.getDeliveryAddress();
        final StringBuilder personalDetails = new StringBuilder();

        personalDetails.append(addressData.getFirstName() + SPACE);
        personalDetails.append(addressData.getLastName() + SPACE);
        personalDetails.append(cartData.getAdyenDob() + SPACE);
        personalDetails.append(addressData.getPhone() + SPACE);
        personalDetails.append(addressData.getEmail());

        return personalDetails.toString();
    }
}
