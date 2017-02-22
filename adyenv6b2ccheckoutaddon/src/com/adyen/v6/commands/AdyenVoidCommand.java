package com.adyen.v6.commands;

import com.adyen.model.modification.ModificationResult;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.payment.commands.VoidCommand;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Issues a Cancel request
 */
public class AdyenVoidCommand implements VoidCommand {
    private static final Logger LOG = Logger.getLogger(AdyenVoidCommand.class);

    private AdyenPaymentService adyenPaymentService;

    @Override
    public VoidResult perform(VoidRequest request) {
        LOG.info("Cancellation request received " + request.getRequestId() + ", " + request.getRequestToken());

        VoidResult result = new VoidResult();
        result.setRequestTime(new Date());
        result.setTransactionStatus(TransactionStatus.ERROR);
        result.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE);

        String authReference = request.getRequestId();
        String merchantReference = request.getRequestToken();

        try {
            ModificationResult modificationResult = adyenPaymentService.cancelOrRefund(authReference, merchantReference);

            if (ModificationResult.ResponseEnum.CANCELORREFUND_RECEIVED_.equals(modificationResult.getResponse())) {
                result.setTransactionStatus(TransactionStatus.ACCEPTED);
                result.setTransactionStatusDetails(TransactionStatusDetails.REVIEW_NEEDED);
            } else {
                result.setTransactionStatus(TransactionStatus.REJECTED);
                result.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE);
            }
        } catch (Exception e) {
            LOG.error(e);
        }

        return result;
    }

    public AdyenPaymentService getAdyenPaymentService() {
        return adyenPaymentService;
    }

    public void setAdyenPaymentService(AdyenPaymentService adyenPaymentService) {
        this.adyenPaymentService = adyenPaymentService;
    }
}
