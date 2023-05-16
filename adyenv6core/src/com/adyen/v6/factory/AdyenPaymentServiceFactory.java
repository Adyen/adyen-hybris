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

import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.DefaultAdyenPaymentService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Factory class for AdyenPaymentService
 */
public class AdyenPaymentServiceFactory {

    private final AdyenRequestFactory adyenRequestFactory;

    public AdyenPaymentServiceFactory(final AdyenRequestFactory adyenRequestFactory) {
        this.adyenRequestFactory = adyenRequestFactory;
    }

    public AdyenPaymentService createFromBaseStore(final BaseStoreModel baseStoreModel) {
        DefaultAdyenPaymentService adyenPaymentService = new DefaultAdyenPaymentService(baseStoreModel);
        adyenPaymentService.setAdyenRequestFactory(adyenRequestFactory);
        return adyenPaymentService;
    }
}
