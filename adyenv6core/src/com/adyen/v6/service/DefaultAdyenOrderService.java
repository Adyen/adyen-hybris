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
package com.adyen.v6.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.adyen.util.DateUtil;
import org.apache.log4j.Logger;
import com.adyen.model.FraudCheckResult;
import com.adyen.model.FraudResult;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.CheckoutPaymentsAction;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.v6.converters.PaymentsResponseConverter;
import de.hybris.platform.basecommerce.enums.FraudStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.fraud.model.FraudReportModel;
import de.hybris.platform.fraud.model.FraudSymptomScoringModel;
import de.hybris.platform.servicelayer.model.ModelService;

import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_BOLETO;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_BOLETO_SANTANDER;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_MULTIBANCO;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_PROVIDER;

public class DefaultAdyenOrderService implements AdyenOrderService {
    private static final Logger LOG = Logger.getLogger(DefaultAdyenOrderService.class);
    private static final String ADDITIONAL_DATA_CARD_TYPE = "checkout.cardAddedBrand";
    private ModelService modelService;
    private PaymentsResponseConverter paymentsResponseConverter;

    @Override
    public FraudReportModel createFraudReportFromPaymentsResponse(PaymentsResponse paymentsResponse) {
        FraudReportModel fraudReport = modelService.create(FraudReportModel.class);
        FraudResult fraudResult = paymentsResponse.getFraudResult();

        if (fraudResult == null) {
            LOG.debug("No fraud result found");
            return null;
        }

        fraudReport.setCode(paymentsResponse.getPspReference());
        fraudReport.setStatus(FraudStatus.OK);
        fraudReport.setExplanation("Score: " + fraudResult.getAccountScore());
        fraudReport.setTimestamp(new Date());
        fraudReport.setProvider(PAYMENT_PROVIDER);

        List<FraudSymptomScoringModel> fraudSymptomScorings = new ArrayList<>();
        for (FraudCheckResult fraudCheckResult : fraudResult.getFraudCheckResults()) {
            FraudSymptomScoringModel fraudSymptomScoring = modelService.create(FraudSymptomScoringModel.class);

            Integer score = fraudCheckResult.getAccountScore();
            if (score != null) {
                fraudSymptomScoring.setScore(score.doubleValue());
            } else {
                fraudSymptomScoring.setScore(0.0);
            }

            fraudSymptomScoring.setName(fraudCheckResult.getName());
            fraudSymptomScoring.setExplanation("Check id: " + fraudCheckResult.getCheckId());
            fraudSymptomScoring.setFraudReport(fraudReport);

            fraudSymptomScorings.add(fraudSymptomScoring);
        }

        fraudReport.setFraudSymptomScorings(fraudSymptomScorings);

        LOG.debug("Returning fraud report with score: " + fraudResult.getAccountScore());

        return fraudReport;

    }

    @Override
    public FraudReportModel createFraudReportFromPaymentResult(PaymentResult paymentResult) {
        PaymentsResponse paymentsResponse = paymentsResponseConverter.convert(paymentResult);
        return createFraudReportFromPaymentsResponse(paymentsResponse);
    }

    @Override
    public void storeFraudReport(FraudReportModel fraudReport) {
        List<FraudSymptomScoringModel> fraudSymptomScorings = fraudReport.getFraudSymptomScorings();
        modelService.save(fraudReport);

        for (FraudSymptomScoringModel fraudSymptomScoring : fraudSymptomScorings) {
            modelService.save(fraudSymptomScoring);
        }
    }

    @Override
    public void storeFraudReportFromPaymentsResponse(OrderModel order, PaymentsResponse paymentsResponse) {
        FraudReportModel fraudReport = createFraudReportFromPaymentsResponse(paymentsResponse);
        if(fraudReport != null) {
            fraudReport.setOrder(order);
            storeFraudReport(fraudReport);
        }
    }

    @Override
    public void storeFraudReportFromPaymentResult(OrderModel order, PaymentResult paymentResult) {
        PaymentsResponse paymentsResponse = paymentsResponseConverter.convert(paymentResult);
        storeFraudReportFromPaymentsResponse(order, paymentsResponse);
    }

    @Override
    public void updateOrderFromPaymentsResponse(OrderModel order, PaymentsResponse paymentsResponse) {
        if (order == null) {
            LOG.error("Order is null");
            return;
        }

        PaymentInfoModel paymentInfo = order.getPaymentInfo();

        if(Objects.nonNull(paymentsResponse.getAdditionalData()) && paymentsResponse.getAdditionalData().containsKey(ADDITIONAL_DATA_CARD_TYPE)){
            paymentInfo.setAdyenPaymentMethod(paymentsResponse.getAdditionalData().get(ADDITIONAL_DATA_CARD_TYPE));
        }
        else if(paymentsResponse.getPaymentMethod()!=null) {
            paymentInfo.setAdyenPaymentMethod(paymentsResponse.getPaymentMethod().getType());
        }

        //Card specific data
        paymentInfo.setAdyenAuthCode(paymentsResponse.getAuthCode());
        paymentInfo.setAdyenAvsResult(paymentsResponse.getAvsResult());
        paymentInfo.setAdyenCardBin(paymentsResponse.getCardBin());
        paymentInfo.setAdyenCardHolder(paymentsResponse.getCardHolderName());
        paymentInfo.setAdyenCardSummary(paymentsResponse.getCardSummary());
        paymentInfo.setAdyenCardExpiry(paymentsResponse.getExpiryDate());
        paymentInfo.setAdyenThreeDOffered(paymentsResponse.get3DOffered());
        paymentInfo.setAdyenThreeDAuthenticated(paymentsResponse.get3DAuthenticated());

        CheckoutPaymentsAction action = paymentsResponse.getAction();

        if (action != null) {
            if (PAYMENT_METHOD_MULTIBANCO.equals(action.getPaymentMethodType())) {
                //Multibanco data
                paymentInfo.setAdyenMultibancoEntity(action.getEntity());
                paymentInfo.setAdyenMultibancoAmount(BigDecimal.valueOf(action.getInitialAmount().getValue()));
                paymentInfo.setAdyenMultibancoDeadline(action.getExpiresAt());
                paymentInfo.setAdyenMultibancoReference(action.getReference());
            } else if (PAYMENT_METHOD_BOLETO.equals(action.getPaymentMethodType()) || PAYMENT_METHOD_BOLETO_SANTANDER.equals(action.getPaymentMethodType())) {
                //Boleto data
                paymentInfo.setAdyenBoletoUrl(action.getDownloadUrl());
                paymentInfo.setAdyenBoletoBarCodeReference(action.getReference());
                paymentInfo.setAdyenBoletoExpirationDate(DateUtil.parseYmdDate(action.getExpiresAt()));
            }
        }

        //pos receipt
        paymentInfo.setAdyenPosReceipt(paymentsResponse.getAdditionalDataByKey("pos.receipt"));

        modelService.save(paymentInfo);

        storeFraudReportFromPaymentsResponse(order, paymentsResponse);
    }

    @Override
    public void updateOrderFromPaymentResult(OrderModel order, PaymentResult paymentResult) {
        PaymentsResponse paymentsResponse = paymentsResponseConverter.convert(paymentResult);
        updateOrderFromPaymentsResponse(order, paymentsResponse);
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public PaymentsResponseConverter getPaymentsResponseConverter() {
        return paymentsResponseConverter;
    }

    public void setPaymentsResponseConverter(PaymentsResponseConverter paymentsResponseConverter) {
        this.paymentsResponseConverter = paymentsResponseConverter;
    }
}
