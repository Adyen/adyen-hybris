package com.adyen.v6.paymentmethoddetails.executors.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import com.adyen.v6.paymentmethoddetails.executors.AdyenPaymentMethodDetailsBuilderExecutor;
import de.hybris.platform.commercefacades.order.data.CartData;

import java.util.List;

public class AdyenPaymentMethodDetailsStrategyExecutorImpl implements AdyenPaymentMethodDetailsBuilderExecutor {

    protected final List<AdyenPaymentMethodDetailsBuilderStrategy<CartData>> strategies;

    public AdyenPaymentMethodDetailsStrategyExecutorImpl(final List<AdyenPaymentMethodDetailsBuilderStrategy<CartData>> strategies) {
        this.strategies = strategies;
    }

    @Override
    public PaymentMethodDetails createPaymentMethodDetails(final CartData cartData) {
        return strategies.stream()
                .filter(strategy -> strategy.isApplicable(cartData))
                .findAny()
                .map(strategy -> strategy.buildPaymentMethodDetails(cartData))
                .orElseThrow(() -> new RuntimeException("Not strategies were found for command request with payment type: [" + cartData.getAdyenPaymentMethod() + "]"));
    }
}
