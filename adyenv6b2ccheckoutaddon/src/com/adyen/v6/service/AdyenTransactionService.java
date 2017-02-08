package com.adyen.v6.service;

import com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants;
import com.adyen.v6.model.NotificationItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.math.BigDecimal;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.GENERAL_SYSTEM_ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;

//TODO: implement an interface
public class AdyenTransactionService {
    private ModelService modelService;
    private CommonI18NService commonI18NService;

    private static final Logger LOG = Logger.getLogger(AdyenTransactionService.class);

    /**
     * Create a capture transaction entry from NotificationItem
     *
     * @param paymentTransaction
     * @param notificationItemModel
     * @return
     */
    public PaymentTransactionEntryModel createCapturedTransactionFromNotification(
            final PaymentTransactionModel paymentTransaction,
            final NotificationItemModel notificationItemModel) {
        final PaymentTransactionEntryModel transactionEntryModel = modelService.create(PaymentTransactionEntryModel.class);

        String code = paymentTransaction.getRequestId() + "_" + paymentTransaction.getEntries().size();

        transactionEntryModel.setType(PaymentTransactionType.CAPTURE);
        transactionEntryModel.setPaymentTransaction(paymentTransaction);
        transactionEntryModel.setRequestId(notificationItemModel.getPspReference());
        transactionEntryModel.setRequestToken(notificationItemModel.getMerchantReference());
        transactionEntryModel.setCode(code);
        transactionEntryModel.setTime(notificationItemModel.getEventDate());
        transactionEntryModel.setAmount(notificationItemModel.getAmountValue());

        String currencyCode = notificationItemModel.getAmountCurrency();
        final CurrencyModel currency = getCommonI18NService().getCurrency(currencyCode);
        transactionEntryModel.setCurrency(currency);

        if (notificationItemModel.getSuccess()) {
            transactionEntryModel.setTransactionStatus(ACCEPTED.name());
            transactionEntryModel.setTransactionStatusDetails(SUCCESFULL.name());
        } else {
            transactionEntryModel.setTransactionStatus(REJECTED.name());
            transactionEntryModel.setTransactionStatusDetails(GENERAL_SYSTEM_ERROR.name());
            //TODO: store reasoning
        }

        return transactionEntryModel;
    }

    /**
     * Stores the authorization transactions for an order
     *
     * @param abstractOrderModel
     * @param merchantTransactionCode
     * @param pspReference
     * @return
     */
    public PaymentTransactionModel authorizeOrderModel(
            final AbstractOrderModel abstractOrderModel,
            final String merchantTransactionCode,
            final String pspReference) {
        //First save the transactions to the CartModel < AbstractOrderModel
        final PaymentTransactionModel paymentTransactionModel = createPaymentTransaction(
                merchantTransactionCode,
                pspReference,
                abstractOrderModel);

        modelService.save(paymentTransactionModel);

        PaymentTransactionEntryModel authorisedTransaction = createAuthorizationPaymentTransactionEntryModel(
                paymentTransactionModel,
                merchantTransactionCode,
                abstractOrderModel
        );

        LOG.info("Saving AUTH transaction entry");
        modelService.save(authorisedTransaction);

        modelService.refresh(paymentTransactionModel); //refresh is needed by order-process

        return paymentTransactionModel;
    }

    /**
     * Store failed authorization transaction entry
     *
     * @param notificationItemModel
     * @param abstractOrderModel
     * @return
     */
    public PaymentTransactionModel storeFailedAuthorizationFromNotification(
            NotificationItemModel notificationItemModel,
            AbstractOrderModel abstractOrderModel) {
        //First save the transactions to the CartModel < AbstractOrderModel
        final PaymentTransactionModel paymentTransactionModel = createPaymentTransaction(
                notificationItemModel.getMerchantReference(),
                notificationItemModel.getPspReference(),
                abstractOrderModel);

        modelService.save(paymentTransactionModel);

        PaymentTransactionEntryModel authorisedTransaction = createAuthorizationPaymentTransactionEntryModel(
                paymentTransactionModel,
                notificationItemModel.getMerchantReference(),
                abstractOrderModel
        );

        authorisedTransaction.setTransactionStatus(REJECTED.name());

        TransactionStatusDetails transactionStatusDetails = getTransactionStatusDetailsFromReason(notificationItemModel.getReason());
        authorisedTransaction.setTransactionStatusDetails(transactionStatusDetails.name());

        modelService.save(authorisedTransaction);

        return paymentTransactionModel;
    }

    /**
     * Map notification item reason to transactionStatusDetails item
     *
     * @param reason
     * @return
     */
    private TransactionStatusDetails getTransactionStatusDetailsFromReason(String reason) {
        TransactionStatusDetails transactionStatusDetails = TransactionStatusDetails.UNKNOWN_CODE;

        if (reason != null) {
            switch (reason) {
                //TODO: fill more cases
                default:
                    transactionStatusDetails = TransactionStatusDetails.UNKNOWN_CODE;
                    break;
            }
        }

        return transactionStatusDetails;
    }

    private PaymentTransactionEntryModel createAuthorizationPaymentTransactionEntryModel(
            final PaymentTransactionModel paymentTransaction,
            final String merchantCode,
            final AbstractOrderModel abstractOrderModel) {
        final PaymentTransactionEntryModel transactionEntryModel = modelService.create(PaymentTransactionEntryModel.class);

        String code = paymentTransaction.getRequestId() + "_" + paymentTransaction.getEntries().size();

        transactionEntryModel.setType(PaymentTransactionType.AUTHORIZATION);
        transactionEntryModel.setPaymentTransaction(paymentTransaction);
        transactionEntryModel.setRequestId(paymentTransaction.getRequestId());
        transactionEntryModel.setRequestToken(merchantCode);
        transactionEntryModel.setCode(code);
        transactionEntryModel.setTime(DateTime.now().toDate());
        transactionEntryModel.setTransactionStatus(ACCEPTED.name());
        transactionEntryModel.setTransactionStatusDetails(SUCCESFULL.name());
        transactionEntryModel.setAmount(new BigDecimal(abstractOrderModel.getTotalPrice()));
        transactionEntryModel.setCurrency(abstractOrderModel.getCurrency());

        return transactionEntryModel;
    }

    private PaymentTransactionModel createPaymentTransaction(
            final String merchantCode,
            final String pspReference,
            final AbstractOrderModel abstractOrderModel) {
        final PaymentTransactionModel paymentTransactionModel = modelService.create(PaymentTransactionModel.class);
        paymentTransactionModel.setCode(pspReference);
        paymentTransactionModel.setRequestId(pspReference);
        paymentTransactionModel.setRequestToken(merchantCode);
        paymentTransactionModel.setPaymentProvider(Adyenv6b2ccheckoutaddonConstants.PAYMENT_PROVIDER);
        paymentTransactionModel.setOrder(abstractOrderModel);
        paymentTransactionModel.setCurrency(abstractOrderModel.getCurrency());
        paymentTransactionModel.setInfo(abstractOrderModel.getPaymentInfo());
        paymentTransactionModel.setPlannedAmount(new BigDecimal(abstractOrderModel.getTotalPrice()));

        return paymentTransactionModel;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
