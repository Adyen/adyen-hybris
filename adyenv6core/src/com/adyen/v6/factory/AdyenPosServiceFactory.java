
/*
 *                       ######
 *                       ######
 * ############    ####( ######  #####. ######  ############   ############
 * #############  #####( ######  #####. ######  #############  #############
 *        ######  #####( ######  #####. ######  #####  ######  #####  ######
 * ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 * ###### ######  #####( ######  #####. ######  #####          #####  ######
 * #############  #############  #############  #############  #####  ######
 *  ############   ############  #############   ############  #####  ######
 *                                      ######
 *                               #############
 *                               ############
 *
 * Adyen Hybris Extension
 *
 * Copyright (c) 2019 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 */

package com.adyen.v6.factory;

import com.adyen.v6.service.AdyenPosService;
import com.adyen.v6.service.DefaultAdyenPosService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Factory class for AdyenPosService
 */
public class AdyenPosServiceFactory {
    private AdyenRequestFactory adyenRequestFactory;

    public AdyenPosService createFromBaseStore(final BaseStoreModel baseStoreModel) {
        DefaultAdyenPosService adyenPosService = new DefaultAdyenPosService(baseStoreModel);
        adyenPosService.setAdyenRequestFactory(adyenRequestFactory);
        return adyenPosService;
    }

    public AdyenRequestFactory getAdyenRequestFactory() {
        return adyenRequestFactory;
    }

    public void setAdyenRequestFactory(AdyenRequestFactory adyenRequestFactory) {
        this.adyenRequestFactory = adyenRequestFactory;
    }
}
