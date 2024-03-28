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
package com.adyen.v6.actions.returns.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.warehousing.returns.service.impl.DefaultRefundAmountCalculationService;

/**
 * Class to override DefaultRefundAmountCalculationService to set scale at RoundingMode.HALF_DOWN
 */
public class AdyenRefundAmountCalculationService extends DefaultRefundAmountCalculationService {
    @Override
    public BigDecimal getOriginalRefundAmount(ReturnRequestModel returnRequest) {
        ServicesUtil.validateParameterNotNull(returnRequest, "Parameter returnRequest cannot be null");
        ServicesUtil.validateParameterNotNull(returnRequest.getReturnEntries(), "Parameter Return Entries cannot be null");
        BigDecimal refundAmount = returnRequest.getReturnEntries().stream().map(this::getOriginalRefundEntryAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if(Boolean.TRUE.equals(returnRequest.getRefundDeliveryCost())) {
            refundAmount = refundAmount.add(BigDecimal.valueOf(returnRequest.getOrder().getDeliveryCost()));
        }

        return refundAmount.setScale(this.getNumberOfDigits(returnRequest), RoundingMode.HALF_DOWN);
    }

    @Override
    public BigDecimal getCustomRefundAmount(ReturnRequestModel returnRequest) {
        ServicesUtil.validateParameterNotNull(returnRequest, "Parameter returnRequest cannot be null");
        ServicesUtil.validateParameterNotNull(returnRequest.getReturnEntries(), "Parameter Return Entries cannot be null");
        BigDecimal refundAmount = returnRequest.getReturnEntries().stream().map(this::getCustomRefundEntryAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if(Boolean.TRUE.equals(returnRequest.getRefundDeliveryCost())) {
            refundAmount = refundAmount.add(BigDecimal.valueOf(returnRequest.getOrder().getDeliveryCost()));
        }

        return refundAmount.setScale(this.getNumberOfDigits(returnRequest), RoundingMode.HALF_DOWN);
    }
}
