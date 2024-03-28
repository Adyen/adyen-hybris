package com.adyen.v6.factory;

import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.impl.DefaultSubscriptionAdyenPaymentService;
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
    public AdyenPaymentService createFromBaseStore(final BaseStoreModel baseStoreModel)
    {
        final DefaultSubscriptionAdyenPaymentService adyenPaymentService = new DefaultSubscriptionAdyenPaymentService(
                baseStoreModel);
        adyenPaymentService.setAdyenRequestFactory(getAdyenRequestFactory());
        adyenPaymentService.setCartFacade(cartFacade);
        adyenPaymentService.setBaseStoreService(baseStoreService);
        return adyenPaymentService;
    }
}
