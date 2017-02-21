package com.adyen.v6.commands;

import com.adyen.model.modification.ModificationResult;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.commands.CaptureCommand;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.PAYMENT_METHOD_CC;
import static de.hybris.platform.payment.dto.TransactionStatus.*;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.*;

/**
 * Issues a Capture request
 */
public class AdyenCaptureCommand implements CaptureCommand {
    private static final Logger LOG = Logger.getLogger(AdyenCaptureCommand.class);

    private AdyenPaymentService adyenPaymentService;
    private BaseStoreService baseStoreService;
    private OrderRepository orderRepository;

    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(java.lang.Object)
     */
    @Override
    public CaptureResult perform(final CaptureRequest request) {
        CaptureResult result = createCaptureResultFromRequest(request);

        LOG.info("Capture request received with requestId: " + request.getRequestId()
                + ", requestToken: " + request.getRequestToken());

        String originalPSPReference = request.getRequestId();
        String reference = request.getRequestToken();
        final BigDecimal amount = request.getTotalAmount();
        final Currency currency = request.getCurrency();

        OrderModel orderModel = orderRepository.getOrderModel(reference);
        if (orderModel == null) {
            LOG.error("Order model with code: " + reference + " cannot be found");
            result.setTransactionStatus(ERROR);
            return result;
        }

        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        boolean isImmediateCapture = baseStore.getAdyenImmediateCapture();

        Assert.notNull(isImmediateCapture);
        boolean autoCapture = isImmediateCapture || !supportsManualCapture(orderModel.getAdyenPaymentMethod());

        if (autoCapture) {
            result.setTransactionStatus(ACCEPTED);
            result.setTransactionStatusDetails(SUCCESFULL);
        } else {
            try {
                ModificationResult modificationResult = adyenPaymentService.capture(
                        amount,
                        currency,
                        originalPSPReference,
                        reference
                );

                if (modificationResult.getResponse() == ModificationResult.ResponseEnum.CAPTURE_RECEIVED_) {
                    result.setTransactionStatus(ACCEPTED);  //Accepted so that TakePaymentAction doesn't fail
                    result.setTransactionStatusDetails(REVIEW_NEEDED);
                } else {
                    result.setTransactionStatus(REJECTED);
                    result.setTransactionStatusDetails(UNKNOWN_CODE);
                }
            } catch (Exception e) {
                LOG.error("Capture Exception", e);
            }
        }

        return result;
    }

    private CaptureResult createCaptureResultFromRequest(CaptureRequest request) {
        CaptureResult result = new CaptureResult();

        result.setCurrency(request.getCurrency());
        result.setTotalAmount(request.getTotalAmount());
        result.setRequestTime(new Date());
        result.setMerchantTransactionCode(request.getMerchantTransactionCode());
        result.setRequestId(request.getRequestId());
        result.setRequestToken(request.getRequestToken());

        //Default status = ERROR
        result.setTransactionStatus(ERROR);

        return result;
    }

    private boolean supportsManualCapture(String paymentMethod) {
        //todo: implement
        switch (paymentMethod) {
            case "cup":
            case "cartebancaire":
            case "visa":
            case "mc":
            case "uatp":
            case "amex":
            case "maestro":
            case "maestrouk":
            case "diners":
            case "discover":
            case "jcb":
            case "laser":
            case "paypal":
            case "klarna":
            case "afterpay_default":
            case "sepadirectdebit":
            case PAYMENT_METHOD_CC: //todo: remove
                return true;
        }

//        // To be sure check if it payment method starts with afterpay_ then manualCapture is allowed
//        if (strlen($this->_paymentMethod) >= 9 && substr($this->_paymentMethod, 0, 9) == "afterpay_") {
//            $manualCaptureAllowed = true;
//        }

        return false;
    }


    public AdyenPaymentService getAdyenPaymentService() {
        return adyenPaymentService;
    }

    @Required
    public void setAdyenPaymentService(AdyenPaymentService adyenPaymentService) {
        this.adyenPaymentService = adyenPaymentService;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }
}
