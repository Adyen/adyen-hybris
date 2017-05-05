package com.adyen.v6.factory;

import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.store.BaseStoreModel;

public class AdyenPaymentServiceFactory {
    public AdyenPaymentService createFromBaseStore(final BaseStoreModel baseStoreModel) {
        return new AdyenPaymentService(baseStoreModel);
    }
}
