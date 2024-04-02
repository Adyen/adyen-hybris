package com.adyen.v6.factory;

import com.adyen.v6.service.AdyenCheckoutApiService;
import com.adyen.v6.service.impl.DefaultSubscriptionAdyenCheckoutApiService;
import de.hybris.platform.store.BaseStoreModel;

public class SubscriptionAdyenPaymentServiceFactory extends AdyenPaymentServiceFactory {


    public SubscriptionAdyenPaymentServiceFactory(SubscriptionPaymentRequestFactory adyenRequestFactory) {
        super(adyenRequestFactory);
    }

    @Override
    public AdyenCheckoutApiService createAdyenCheckoutApiService(final BaseStoreModel baseStoreModel)
    {
        final DefaultSubscriptionAdyenCheckoutApiService adyenPaymentService = new DefaultSubscriptionAdyenCheckoutApiService(
                baseStoreModel);
        adyenPaymentService.setAdyenRequestFactory(getAdyenRequestFactory());
        return adyenPaymentService;
    }
}
