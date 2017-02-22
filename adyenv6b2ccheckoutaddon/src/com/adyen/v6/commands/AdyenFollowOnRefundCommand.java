package com.adyen.v6.commands;

import com.adyen.model.modification.ModificationResult;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.payment.commands.FollowOnRefundCommand;
import de.hybris.platform.payment.commands.request.FollowOnRefundRequest;
import de.hybris.platform.payment.commands.result.RefundResult;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.REVIEW_NEEDED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.UNKNOWN_CODE;

/**
 * Issues a Refund Request
 */
public class AdyenFollowOnRefundCommand implements FollowOnRefundCommand<FollowOnRefundRequest> {
    private static final Logger LOG = Logger.getLogger(AdyenFollowOnRefundCommand.class);

    private AdyenPaymentService adyenPaymentService;

    @Override
    public RefundResult perform(FollowOnRefundRequest request) {
        RefundResult result = new RefundResult();
        result.setCurrency(request.getCurrency());
        result.setTotalAmount(request.getTotalAmount());
        result.setRequestTime(new Date());

        //Assign error status by default
        result.setTransactionStatus(ERROR);
        result.setTransactionStatusDetails(UNKNOWN_CODE);

        LOG.info("Refund request received with requestId: " + request.getRequestId()
                + ", requestToken: " + request.getRequestToken());

        String originalPSPReference = request.getRequestId();
        String reference = request.getRequestToken();
        final BigDecimal amount = request.getTotalAmount();
        final Currency currency = request.getCurrency();

        try {
            //Do the /refund API call
            ModificationResult modificationResult = adyenPaymentService.refund(
                    amount,
                    currency,
                    originalPSPReference,
                    reference
            );

            LOG.info("Refund response: " + modificationResult.getResponse());
            //change status to ACCEPTED if there is no error
            if (modificationResult.getResponse() == ModificationResult.ResponseEnum.REFUND_RECEIVED_) {
                result.setTransactionStatus(ACCEPTED);
                result.setTransactionStatusDetails(REVIEW_NEEDED);
            }
        } catch (Exception e) {
            LOG.error("Refund Exception", e);
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
