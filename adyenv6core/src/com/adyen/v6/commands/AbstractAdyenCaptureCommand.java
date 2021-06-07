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

import com.adyen.model.modification.ModificationResult;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * Issues a Capture request
 */
public class AbstractAdyenCaptureCommand {
    private static final Logger LOG = Logger.getLogger(AdyenCaptureCommand.class);

    private final String CAPTURE_RECEIVED_RESPONSE = "[capture-received]";

    private AdyenPaymentServiceFactory adyenPaymentServiceFactory;
    private OrderRepository orderRepository;

    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(java.lang.Object)
     */
    public CaptureResult perform(final CaptureRequest request) {
        CaptureResult result = createCaptureResultFromRequest(request);

        LOG.info("Capture request received with requestId: " + request.getRequestId() + ", requestToken: " + request.getRequestToken());

        String originalPSPReference = request.getRequestId();
        String reference = request.getRequestToken();
        final BigDecimal amount = request.getTotalAmount();
        final Currency currency = request.getCurrency();

        OrderModel order = orderRepository.getOrderModel(reference);
        if (order == null) {
            LOG.error("Order model with code: " + reference + " cannot be found");
            result.setTransactionStatus(TransactionStatus.ERROR);
            return result;
        }

        BaseStoreModel baseStore = order.getStore();
        Assert.notNull(baseStore);
        AdyenPaymentService adyenPaymentService = adyenPaymentServiceFactory.createFromBaseStore(baseStore);

        final PaymentInfoModel paymentInfo = order.getPaymentInfo();
        Assert.notNull(paymentInfo);

        boolean isImmediateCapture = baseStore.getAdyenImmediateCapture();
        Assert.notNull(isImmediateCapture);

        boolean autoCapture = isImmediateCapture || ! supportsManualCapture(paymentInfo.getAdyenPaymentMethod());

        if (autoCapture) {
            result.setTransactionStatus(TransactionStatus.ACCEPTED);
            result.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);
        } else {
            try {
                ModificationResult modificationResult = adyenPaymentService.capture(amount, currency, originalPSPReference, reference);

                if (modificationResult.getResponse() == CAPTURE_RECEIVED_RESPONSE) {
                    result.setTransactionStatus(TransactionStatus.ACCEPTED);  //Accepted so that TakePaymentAction doesn't fail
                    result.setTransactionStatusDetails(TransactionStatusDetails.REVIEW_NEEDED);
                } else {
                    result.setTransactionStatus(TransactionStatus.REJECTED);
                    result.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE);
                }
            } catch (Exception e) {
                LOG.error("Capture Exception: " + e, e);
            }
        }

        LOG.info("Capture status: " + result.getTransactionStatus().name() + ":" + result.getTransactionStatusDetails().name());

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
        result.setTransactionStatus(TransactionStatus.ERROR);
        result.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE);

        return result;
    }

    private boolean supportsManualCapture(String paymentMethod) {
        switch (paymentMethod) {
            case "cup":
            case "cartebancaire":
            case "visa":
            case "visadankort":
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
            case "afterpay":
            case "afterpaytouch":
            case "clearpay":
            case "ratepay":
            case "afterpay_default":
            case "sepadirectdebit":
            case "dankort":
            case "elo":
            case "hipercard":
            case "mc_applepay":
            case "visa_applepay":
            case "amex_applepay":
            case "discover_applepay":
            case "maestro_applepay":
            case "paywithgoogle":
                return true;
        }

        return false;
    }

    public AdyenPaymentServiceFactory getAdyenPaymentServiceFactory() {
        return adyenPaymentServiceFactory;
    }

    public void setAdyenPaymentServiceFactory(AdyenPaymentServiceFactory adyenPaymentServiceFactory) {
        this.adyenPaymentServiceFactory = adyenPaymentServiceFactory;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
