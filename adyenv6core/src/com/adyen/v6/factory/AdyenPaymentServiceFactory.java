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
import com.adyen.v6.strategy.AdyenMerchantAccountStrategy;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Factory class for AdyenPaymentService
 */
public class AdyenPaymentServiceFactory {

    private final AdyenRequestFactory adyenRequestFactory;
    protected final AdyenMerchantAccountStrategy adyenMerchantAccountStrategy;


    public AdyenPaymentServiceFactory(final AdyenRequestFactory adyenRequestFactory, final AdyenMerchantAccountStrategy adyenMerchantAccountStrategy) {
        this.adyenRequestFactory = adyenRequestFactory;
        this.adyenMerchantAccountStrategy = adyenMerchantAccountStrategy;
    }
    
    public AdyenCheckoutApiService createAdyenCheckoutApiService(final BaseStoreModel baseStoreModel) {
        String webMerchantAccount = adyenMerchantAccountStrategy.getWebMerchantAccount(baseStoreModel);
        DefaultAdyenCheckoutApiService defaultAdyenCheckoutApiService = new DefaultAdyenCheckoutApiService(baseStoreModel, webMerchantAccount);
        defaultAdyenCheckoutApiService.setAdyenRequestFactory(adyenRequestFactory);
        return defaultAdyenCheckoutApiService;
    }

    public AdyenModificationsApiService createAdyenModificationsApiService(final BaseStoreModel baseStoreModel) {
        String webMerchantAccount = adyenMerchantAccountStrategy.getWebMerchantAccount(baseStoreModel);
        DefaultAdyenModificationsApiService adyenModificationsApiService = new DefaultAdyenModificationsApiService(baseStoreModel, webMerchantAccount);
        adyenModificationsApiService.setAdyenRequestFactory(adyenRequestFactory);
        return adyenModificationsApiService;
    }

    public AdyenRequestFactory getAdyenRequestFactory() {
        return adyenRequestFactory;
    }
}
