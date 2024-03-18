package com.adyen.v6.factory;

import com.adyen.v6.service.AdyenCheckoutApiService;
import com.adyen.v6.service.impl.DefaultSubscriptionAdyenCheckoutApiService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

public class SubscriptionAdyenPaymentServiceFactory extends AdyenPaymentServiceFactory {

    private final CartFacade cartFacade;
    private final BaseStoreService baseStoreService;

    public SubscriptionAdyenPaymentServiceFactory(SubscriptionPaymentRequestFactory adyenRequestFactory,
                                                  CartFacade cartFacade, BaseStoreService baseStoreService) {
        super(adyenRequestFactory);
        this.cartFacade = cartFacade;
        this.baseStoreService = baseStoreService;
    }

    @Override
    public AdyenCheckoutApiService createAdyenCheckoutApiService(final BaseStoreModel baseStoreModel)
    {
        final DefaultSubscriptionAdyenCheckoutApiService adyenPaymentService = new DefaultSubscriptionAdyenCheckoutApiService(
                baseStoreModel);
        adyenPaymentService.setAdyenRequestFactory(getAdyenRequestFactory());
        adyenPaymentService.setCartFacade(cartFacade);
        adyenPaymentService.setBaseStoreService(baseStoreService);
        return adyenPaymentService;
    }
}
