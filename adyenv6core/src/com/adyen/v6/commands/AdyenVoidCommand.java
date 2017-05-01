package com.adyen.v6.commands;

import com.adyen.model.modification.ModificationResult;
import com.adyen.v6.repository.BaseStoreRepository;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.payment.commands.VoidCommand;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Issues a Cancel request
 */
public class AdyenVoidCommand implements VoidCommand {
    private static final Logger LOG = Logger.getLogger(AdyenVoidCommand.class);

    private AdyenPaymentService adyenPaymentService;
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
        adyenPaymentService.setBaseStore(baseStore);

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
            LOG.error("Cancellation exception",e);
        }

        LOG.info("Cancellation status: " + result.getTransactionStatus().name() + ":" + result.getTransactionStatusDetails().name());

        return result;
    }

    public AdyenPaymentService getAdyenPaymentService() {
        return adyenPaymentService;
    }

    public void setAdyenPaymentService(AdyenPaymentService adyenPaymentService) {
        this.adyenPaymentService = adyenPaymentService;
    }

    public BaseStoreRepository getBaseStoreRepository() {
        return baseStoreRepository;
    }

    public void setBaseStoreRepository(BaseStoreRepository baseStoreRepository) {
        this.baseStoreRepository = baseStoreRepository;
    }
}
