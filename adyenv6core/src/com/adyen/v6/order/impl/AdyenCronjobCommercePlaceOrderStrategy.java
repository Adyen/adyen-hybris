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
package com.adyen.v6.order.impl;

import de.hybris.platform.commerceservices.order.impl.DefaultCommercePlaceOrderStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * CommercePlaceOrderStrategy for Adyen notifications cronjob
 * Set base site, store, language from cart instead of session
 */
public class AdyenCronjobCommercePlaceOrderStrategy extends DefaultCommercePlaceOrderStrategy {
    @Override
    public CommerceOrderResult placeOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException {
        final CartModel cartModel = parameter.getCart();

        BaseSiteService baseSiteServiceMock = mock(BaseSiteService.class);
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(cartModel.getSite());
        setBaseSiteService(baseSiteServiceMock);

        BaseStoreService baseStoreServiceMock = mock(BaseStoreService.class);
        when(baseStoreServiceMock.getCurrentBaseStore()).thenReturn(cartModel.getStore());
        setBaseStoreService(baseStoreServiceMock);

        CommonI18NService commonI18NServiceMock = mock(CommonI18NService.class);
        when(commonI18NServiceMock.getCurrentLanguage()).thenReturn(cartModel.getSite().getDefaultLanguage());
        setCommonI18NService(commonI18NServiceMock);

        return super.placeOrder(parameter);
    }
}
