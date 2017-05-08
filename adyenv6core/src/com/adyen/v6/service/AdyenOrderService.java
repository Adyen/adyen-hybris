package com.adyen.v6.service;

import com.adyen.model.FraudCheckResult;
import com.adyen.model.FraudResult;
import com.adyen.model.PaymentResult;
import de.hybris.platform.basecommerce.enums.FraudStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.fraud.model.FraudReportModel;
import de.hybris.platform.fraud.model.FraudSymptomScoringModel;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_PROVIDER;

public class AdyenOrderService extends DefaultPaymentServiceImpl {
    private static final Logger LOG = Logger.getLogger(AdyenOrderService.class);
    private ModelService modelService;

    public FraudReportModel createFraudReportFromPaymentResult(PaymentResult paymentResult) {
        FraudReportModel fraudReport = modelService.create(FraudReportModel.class);
        FraudResult fraudResult = paymentResult.getFraudResult();

        if (fraudResult == null) {
            LOG.debug("No fraud result found");
            return null;
        }

        fraudReport.setCode(paymentResult.getPspReference());
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

    public void storeFraudReport(FraudReportModel fraudReport) {
        List<FraudSymptomScoringModel> fraudSymptomScorings = fraudReport.getFraudSymptomScorings();
        modelService.save(fraudReport);

        for (FraudSymptomScoringModel fraudSymptomScoring : fraudSymptomScorings) {
            modelService.save(fraudSymptomScoring);
        }
    }

    public void storeFraudReportFromPaymentResult(OrderModel order, PaymentResult paymentResult) {
        FraudReportModel fraudReport = createFraudReportFromPaymentResult(paymentResult);
        fraudReport.setOrder(order);
        storeFraudReport(fraudReport);
    }

    public void updateOrderFromPaymentResult(OrderModel order, PaymentResult paymentResult) {
        if (order == null) {
            LOG.error("Order is null");
            return;
        }

        PaymentInfoModel paymentInfo = order.getPaymentInfo();

        paymentInfo.setAdyenPaymentMethod(paymentResult.getPaymentMethod());
        paymentInfo.setAdyenAuthCode(paymentResult.getAuthCode());
        paymentInfo.setAdyenAvsResult(paymentResult.getAvsResult());
        paymentInfo.setAdyenCardBin(paymentResult.getCardBin());
        paymentInfo.setAdyenCardHolder(paymentResult.getCardHolderName());
        paymentInfo.setAdyenCardSummary(paymentResult.getCardSummary());
        paymentInfo.setAdyenCardExpiry(paymentResult.getExpiryDate());
        paymentInfo.setAdyenThreeDOffered(paymentResult.get3DOffered());
        paymentInfo.setAdyenThreeDAuthenticated(paymentResult.get3DAuthenticated());

        modelService.save(paymentInfo);

        storeFraudReportFromPaymentResult(order, paymentResult);
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
