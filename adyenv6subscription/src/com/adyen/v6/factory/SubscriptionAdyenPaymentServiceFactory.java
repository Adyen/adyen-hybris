package com.adyen.v6.factory;

import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.impl.DefaultSubscriptionAdyenPaymentService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.store.BaseStoreModel;

public class SubscriptionAdyenPaymentServiceFactory extends AdyenPaymentServiceFactory {

    private CartFacade cartFacade;

    public SubscriptionAdyenPaymentServiceFactory(SubscriptionPaymentRequestFactory adyenRequestFactory, CartFacade cartFacade) {
        super(adyenRequestFactory);
        this.cartFacade = cartFacade;
    }

    @Override
    public AdyenPaymentService createFromBaseStore(final BaseStoreModel baseStoreModel)
    {
        final DefaultSubscriptionAdyenPaymentService adyenPaymentService = new DefaultSubscriptionAdyenPaymentService(
                baseStoreModel);
        adyenPaymentService.setAdyenRequestFactory(getAdyenRequestFactory());
        adyenPaymentService.setCartFacade(cartFacade);
        return adyenPaymentService;
    }
}
