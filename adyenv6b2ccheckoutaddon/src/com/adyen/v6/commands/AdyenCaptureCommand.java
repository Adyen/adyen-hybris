package com.adyen.v6.commands;

import com.adyen.model.ModificationResult;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.payment.commands.CaptureCommand;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.Currency;

import static de.hybris.platform.payment.dto.TransactionStatus.*;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.REVIEW_NEEDED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.UNKNOWN_CODE;

/**
 * Issues a Capture request
 */
public class AdyenCaptureCommand implements CaptureCommand {
    private static final Logger LOG = Logger.getLogger(AdyenCaptureCommand.class);

    private AdyenPaymentService adyenPaymentService;

    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(java.lang.Object)
     */
    @Override
    public CaptureResult perform(final CaptureRequest request) {
        LOG.info("Capture request received with requestId: " + request.getRequestId()
                + ", requestToken: " + request.getRequestToken());

        String originalPSPReference = request.getRequestId();
        String reference = request.getRequestToken();
        final BigDecimal amount = request.getTotalAmount();
        final Currency currency = request.getCurrency();

        //TODO: Do CAP only on supported methods

        try {
            ModificationResult modificationResult = adyenPaymentService.capture(
                    amount,
                    currency,
                    originalPSPReference,
                    reference
            );

            CaptureResult result = new CaptureResult();

            result.setCurrency(currency);
            result.setMerchantTransactionCode(request.getMerchantTransactionCode());
            result.setRequestId(request.getMerchantTransactionCode());
            result.setRequestToken(modificationResult.getPspReference());

            if (modificationResult.getResponse() == ModificationResult.ResponseEnum.CAPTURE_RECEIVED_) {
                result.setTransactionStatus(ACCEPTED);  //Accepted so that TakePaymentAction doesn't fail
                result.setTransactionStatusDetails(REVIEW_NEEDED);
            } else {
                result.setTransactionStatus(REJECTED);
                result.setTransactionStatusDetails(UNKNOWN_CODE);
            }

            return result;
        } catch (Exception e) {
            LOG.error("Capture Exception", e);
        }

        CaptureResult result = new CaptureResult();

        result.setCurrency(currency);
        result.setMerchantTransactionCode(request.getMerchantTransactionCode());
        result.setTransactionStatus(ERROR);

        return result;
    }

    public AdyenPaymentService getAdyenPaymentService() {
        return adyenPaymentService;
    }

    @Required
    public void setAdyenPaymentService(AdyenPaymentService adyenPaymentService) {
        this.adyenPaymentService = adyenPaymentService;
    }
}
