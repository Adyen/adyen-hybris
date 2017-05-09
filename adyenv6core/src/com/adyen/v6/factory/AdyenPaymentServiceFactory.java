package com.adyen.v6.factory;

import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.DefaultAdyenPaymentService;
import de.hybris.platform.store.BaseStoreModel;

public class AdyenPaymentServiceFactory {
    public AdyenPaymentService createFromBaseStore(final BaseStoreModel baseStoreModel) {
        return new DefaultAdyenPaymentService(baseStoreModel);
    }
}
