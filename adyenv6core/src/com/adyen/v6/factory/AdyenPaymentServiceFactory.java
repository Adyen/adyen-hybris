/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.factory;

import com.adyen.v6.service.AdyenCheckoutApiService;
import com.adyen.v6.service.AdyenModificationsApiService;
import com.adyen.v6.service.DefaultAdyenCheckoutApiService;
import com.adyen.v6.service.DefaultAdyenModificationsApiService;
import de.hybris.platform.store.BaseStoreModel;
import org.springframework.cache.annotation.Cacheable;

/**
 * Factory class for AdyenPaymentService
 */
public class AdyenPaymentServiceFactory {

    private final AdyenRequestFactory adyenRequestFactory;


    public AdyenPaymentServiceFactory(final AdyenRequestFactory adyenRequestFactory) {
        this.adyenRequestFactory = adyenRequestFactory;
    }

    @Cacheable("adyenCheckoutApiService")
    public AdyenCheckoutApiService createAdyenCheckoutApiService(final BaseStoreModel baseStoreModel) {
        DefaultAdyenCheckoutApiService defaultAdyenCheckoutApiService = new DefaultAdyenCheckoutApiService(baseStoreModel);
        defaultAdyenCheckoutApiService.setAdyenRequestFactory(adyenRequestFactory);
        return defaultAdyenCheckoutApiService;
    }

    public AdyenModificationsApiService createAdyenModificationsApiService(final BaseStoreModel baseStoreModel) {
        DefaultAdyenModificationsApiService adyenModificationsApiService = new DefaultAdyenModificationsApiService(baseStoreModel);
        adyenModificationsApiService.setAdyenRequestFactory(adyenRequestFactory);
        return adyenModificationsApiService;
    }

    public AdyenRequestFactory getAdyenRequestFactory() {
        return adyenRequestFactory;
    }
}
