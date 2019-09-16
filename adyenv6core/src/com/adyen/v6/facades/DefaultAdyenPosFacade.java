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

package com.adyen.v6.facades;

import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.nexo.ResultType;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.v6.converters.PosPaymentResponseConverter;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.factory.AdyenPosServiceFactory;
import com.adyen.v6.service.AdyenPosService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.Logger;

public class DefaultAdyenPosFacade implements AdyenPosFacade {

    private static final Logger LOGGER = Logger.getLogger(DefaultAdyenPosFacade.class);

    private AdyenPosServiceFactory adyenPosServiceFactory;
    private AdyenCheckoutFacade adyenCheckoutFacade;
    private BaseStoreService baseStoreService;
    private PosPaymentResponseConverter posPaymentResponseConverter;

    @Override
    public OrderData initiatePosPayment(CartData cartData) throws Exception {
        TerminalAPIResponse terminalApiResponse = getAdyenPosService().sync(cartData);
        ResultType resultType = getResultType(terminalApiResponse);

        if (ResultType.SUCCESS == resultType) {
            PaymentsResponse paymentsResponse = posPaymentResponseConverter.convert(terminalApiResponse.getSaleToPOIResponse());
            return getAdyenCheckoutFacade().createAuthorizedOrder(paymentsResponse);
        }

        throw new AdyenNonAuthorizedPaymentException(terminalApiResponse);
    }

    private ResultType getResultType(TerminalAPIResponse terminalApiResponse) {
        if(terminalApiResponse != null && terminalApiResponse.getSaleToPOIResponse() != null) {
            return terminalApiResponse.getSaleToPOIResponse().getPaymentResponse().getResponse().getResult();
        }

        return ResultType.FAILURE;
    }

    public AdyenPosService getAdyenPosService() {
        return adyenPosServiceFactory.createFromBaseStore(baseStoreService.getCurrentBaseStore());
    }

    public AdyenPosServiceFactory getAdyenPosServiceFactory() {
        return adyenPosServiceFactory;
    }

    public void setAdyenPosServiceFactory(AdyenPosServiceFactory adyenPosServiceFactory) {
        this.adyenPosServiceFactory = adyenPosServiceFactory;
    }

    public AdyenCheckoutFacade getAdyenCheckoutFacade() {
        return adyenCheckoutFacade;
    }

    public void setAdyenCheckoutFacade(AdyenCheckoutFacade adyenCheckoutFacade) {
        this.adyenCheckoutFacade = adyenCheckoutFacade;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public PosPaymentResponseConverter getPosPaymentResponseConverter() {
        return posPaymentResponseConverter;
    }

    public void setPosPaymentResponseConverter(PosPaymentResponseConverter posPaymentResponseConverter) {
        this.posPaymentResponseConverter = posPaymentResponseConverter;
    }
}
