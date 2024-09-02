package com.adyen.v6.factory;

import com.adyen.v6.service.AdyenCheckoutApiService;
import com.adyen.v6.service.impl.DefaultSubscriptionAdyenCheckoutApiService;
import com.adyen.v6.strategy.AdyenMerchantAccountStrategy;
import de.hybris.platform.store.BaseStoreModel;

public class SubscriptionAdyenPaymentServiceFactory extends AdyenPaymentServiceFactory {

    public SubscriptionAdyenPaymentServiceFactory(SubscriptionPaymentRequestFactory adyenRequestFactory, AdyenMerchantAccountStrategy adyenMerchantAccountStrategy) {
        super(adyenRequestFactory, adyenMerchantAccountStrategy);
    }

    @Override
    public AdyenCheckoutApiService createAdyenCheckoutApiService(final BaseStoreModel baseStoreModel) {
        String webMerchantAccount = adyenMerchantAccountStrategy.getWebMerchantAccount(baseStoreModel);

        final DefaultSubscriptionAdyenCheckoutApiService adyenPaymentService = new DefaultSubscriptionAdyenCheckoutApiService(
                baseStoreModel, webMerchantAccount);
        adyenPaymentService.setAdyenRequestFactory(getAdyenRequestFactory());
        return adyenPaymentService;
    }
}
