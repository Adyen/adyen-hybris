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

package com.adyen.v6.service;

import com.adyen.Client;
import com.adyen.Config;
import com.adyen.enums.Environment;
import com.adyen.model.terminal.TerminalAPIRequest;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.TerminalCloudAPI;
import com.adyen.v6.factory.AdyenRequestFactory;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.log4j.Logger;

import static com.adyen.v6.constants.Adyenv6coreConstants.PLUGIN_NAME;
import static com.adyen.v6.constants.Adyenv6coreConstants.PLUGIN_VERSION;

public class DefaultAdyenPosService implements AdyenPosService {

    private static final Logger LOGGER = Logger.getLogger(DefaultAdyenPosService.class);

    private static final int POS_REQUEST_TIMEOUT = 25000;

    private BaseStoreModel baseStore;
    private AdyenRequestFactory adyenRequestFactory;
    private Client client;

    /**
     * Prevent initialization without base store
     */
    private DefaultAdyenPosService() {
    }

    public DefaultAdyenPosService(final BaseStoreModel baseStore) {
        this.baseStore = baseStore;

        String apiKey = baseStore.getAdyenPosApiKey();
        String merchantAccount = baseStore.getAdyenPosMerchantAccount();
        Environment environment = baseStore.getAdyenTestMode() ? Environment.TEST : Environment.LIVE;

        Config config = new Config();
        config.setApiKey(apiKey);
        config.setMerchantAccount(merchantAccount);
        config.setReadTimeoutMillis(POS_REQUEST_TIMEOUT);
        config.setApplicationName(PLUGIN_NAME + " v" + PLUGIN_VERSION);

        client = new Client(config);
        client.setEnvironment(environment, null);
    }

    @Override
    public TerminalAPIResponse sync(CartData cartData) throws Exception {
        TerminalCloudAPI terminalCloudAPI = new TerminalCloudAPI(client);

        TerminalAPIRequest terminalApiRequest = adyenRequestFactory.createTerminalAPIRequest(cartData);

        LOGGER.debug(terminalApiRequest);
        TerminalAPIResponse terminalApiResponse = terminalCloudAPI.sync(terminalApiRequest);
        LOGGER.debug(terminalApiResponse);

        return terminalApiResponse;
    }

    public AdyenRequestFactory getAdyenRequestFactory() {
        return adyenRequestFactory;
    }

    public void setAdyenRequestFactory(AdyenRequestFactory adyenRequestFactory) {
        this.adyenRequestFactory = adyenRequestFactory;
    }

    public BaseStoreModel getBaseStore() {
        return baseStore;
    }

    public void setBaseStore(BaseStoreModel baseStore) {
        this.baseStore = baseStore;
    }
}
