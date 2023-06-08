package com.adyen.v6.paymentmethoddetails.executors.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.v6.paymentmethoddetails.builders.AdyenPaymentMethodDetailsBuilderStrategy;
import com.adyen.v6.paymentmethoddetails.builders.impl.GenericAdyenPaymentMethodDetailsBuilderStrategy;
import com.adyen.v6.paymentmethoddetails.executors.AdyenPaymentMethodDetailsBuilderExecutor;
import de.hybris.platform.commercefacades.order.data.CartData;

import java.util.List;

public class AdyenPaymentMethodDetailsStrategyExecutorImpl implements AdyenPaymentMethodDetailsBuilderExecutor {

    private final List<AdyenPaymentMethodDetailsBuilderStrategy<CartData>> strategies;
    private final GenericAdyenPaymentMethodDetailsBuilderStrategy genericStrategy;

    public AdyenPaymentMethodDetailsStrategyExecutorImpl(final List<AdyenPaymentMethodDetailsBuilderStrategy<CartData>> strategies,
                                                         final GenericAdyenPaymentMethodDetailsBuilderStrategy genericStrategy) {
        this.strategies = strategies;
        this.genericStrategy = genericStrategy;
    }

    @Override
    public PaymentMethodDetails createPaymentMethodDetails(final CartData cartData) {
        return strategies.stream()
                .filter(strategy -> strategy.isApplicable(cartData))
                .findAny()
                .map(strategy -> strategy.buildPaymentMethodDetails(cartData))
                .orElse(genericStrategy.buildPaymentMethodDetails(cartData));
    }
}
