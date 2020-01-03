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
package com.adyen.v6.security;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Authenticates a request that is using Basic Authentication
 */
@Component
public class AdyenNotificationAuthenticationProvider {
    @Resource(name = "baseStoreService")
    private BaseStoreService baseStoreService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    private static final Logger LOG = Logger.getLogger(AdyenNotificationAuthenticationProvider.class);

    public boolean authenticateBasic(final HttpServletRequest request, String baseSiteId) {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            final String[] values = credentials.split(":", 2);
            return tryToAuthenticate(values[0], values[1], baseSiteId);
        }
        return false;
    }

    private boolean tryToAuthenticate(String name, String password, String baseSiteId) {

        LOG.debug("Trying to authenticate for baseSiteId " + baseSiteId);
        final BaseSiteModel requestedBaseSite = getBaseSiteService().getBaseSiteForUID(baseSiteId);
        if (requestedBaseSite != null) {
            final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

            if (! requestedBaseSite.equals(currentBaseSite)) {
                getBaseSiteService().setCurrentBaseSite(requestedBaseSite, true);
            }
        }
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        if (baseStore == null) {
            return false;
        }

        String notificationUsername = baseStore.getAdyenNotificationUsername();
        String notificationPassword = baseStore.getAdyenNotificationPassword();

        Assert.notNull(notificationUsername);
        Assert.notNull(notificationPassword);

        if (notificationUsername.isEmpty() || notificationPassword.isEmpty()) {
            return false;
        }

        if (notificationUsername.equals(name) && notificationPassword.equals(password)) {
            return true;
        }

        return false;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }
}
