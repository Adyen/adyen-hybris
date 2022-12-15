package com.adyen.v6.paymentmethoddetails.executors;

import com.adyen.model.checkout.PaymentMethodDetails;
import de.hybris.platform.commercefacades.order.data.CartData;

public interface AdyenPaymentMethodDetailsBuilderExecutor<S extends CartData, T extends PaymentMethodDetails> {

    /**
     * Validate if is applicable and create the PaymentMethodDetails
     *
     * @param source {@link CartData}
     * @return The {@link PaymentMethodDetails}.
     */
    T createPaymentMethodDetails(S source);
}

