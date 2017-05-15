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
package com.adyen.v6.commands;

import java.util.Date;
import org.apache.log4j.Logger;
import com.adyen.model.modification.ModificationResult;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
import com.adyen.v6.repository.BaseStoreRepository;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.payment.commands.VoidCommand;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Issues a Cancel request
 */
public class AdyenVoidCommand implements VoidCommand {
    private static final Logger LOG = Logger.getLogger(AdyenVoidCommand.class);

    private AdyenPaymentServiceFactory adyenPaymentServiceFactory;
    private BaseStoreRepository baseStoreRepository;

    @Override
    public VoidResult perform(VoidRequest request) {
        LOG.info("Cancellation request received " + request.getRequestId() + ", " + request.getRequestToken());

        VoidResult result = new VoidResult();
        result.setRequestTime(new Date());
        result.setTransactionStatus(TransactionStatus.ERROR);
        result.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE);

        String authReference = request.getRequestId();
        String reference = request.getRequestToken();

        BaseStoreModel baseStore = baseStoreRepository.findByOrder(reference);
        if (baseStore == null) {
            return result;
        }
        AdyenPaymentService adyenPaymentService = adyenPaymentServiceFactory.createFromBaseStore(baseStore);

        try {
            ModificationResult modificationResult = adyenPaymentService.cancelOrRefund(authReference, reference);

            if (ModificationResult.ResponseEnum.CANCELORREFUND_RECEIVED_.equals(modificationResult.getResponse())) {
                result.setTransactionStatus(TransactionStatus.ACCEPTED);
                result.setTransactionStatusDetails(TransactionStatusDetails.REVIEW_NEEDED);
            } else {
                result.setTransactionStatus(TransactionStatus.REJECTED);
                result.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE);
            }
        } catch (Exception e) {
            LOG.error("Cancellation exception", e);
        }

        LOG.info("Cancellation status: " + result.getTransactionStatus().name() + ":" + result.getTransactionStatusDetails().name());

        return result;
    }

    public AdyenPaymentServiceFactory getAdyenPaymentServiceFactory() {
        return adyenPaymentServiceFactory;
    }

    public void setAdyenPaymentServiceFactory(AdyenPaymentServiceFactory adyenPaymentServiceFactory) {
        this.adyenPaymentServiceFactory = adyenPaymentServiceFactory;
    }

    public BaseStoreRepository getBaseStoreRepository() {
        return baseStoreRepository;
    }

    public void setBaseStoreRepository(BaseStoreRepository baseStoreRepository) {
        this.baseStoreRepository = baseStoreRepository;
    }
}
