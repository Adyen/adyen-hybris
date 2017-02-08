package com.adyen.v6.commands;

import com.adyen.model.modification.ModificationResult;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;

import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.CONFIG_IMMEDIATE_CAPTURE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenCaptureCommandTest {
    private CaptureRequest captureRequest;

    @Mock
    private AdyenPaymentService adyenPaymentServiceMock;

    @Mock
    private OrderRepository orderRepositoryMock;

    @Mock
    private ConfigurationService configurationServiceMock;

    AdyenCaptureCommand adyenCaptureCommand;

    @Before
    public void setUp() {
        adyenCaptureCommand = new AdyenCaptureCommand();
        captureRequest = new CaptureRequest("merchantTransactionCode",
                "requestId",
                "requestToken",
                Currency.getInstance("EUR"),
                new BigDecimal(100),
                "Adyen"
        );

        OrderModel orderModel = new OrderModel();
        orderModel.setAdyenPaymentMethod("visa");
        when(orderRepositoryMock.getOrderModel(Mockito.any(String.class)))
                .thenReturn(orderModel);

        adyenCaptureCommand.setOrderRepository(orderRepositoryMock);
        adyenCaptureCommand.setConfigurationService(configurationServiceMock);
        adyenCaptureCommand.setAdyenPaymentService(adyenPaymentServiceMock);
    }

    @After
    public void tearDown() {
        // implement here code executed after each test
    }

    /**
     * Test successful capture
     *
     * @throws Exception
     */
    @Test
    public void testManualCaptureSuccess() throws Exception {
        Configuration config = new BaseConfiguration();
        config.setProperty(CONFIG_IMMEDIATE_CAPTURE, "false");
        when(configurationServiceMock.getConfiguration()).thenReturn(config);

        ModificationResult modificationResult = new ModificationResult();
        modificationResult.setPspReference("1235");
        modificationResult.setResponse(ModificationResult.ResponseEnum.CAPTURE_RECEIVED_);

        when(adyenPaymentServiceMock.capture(
                captureRequest.getTotalAmount(),
                captureRequest.getCurrency(),
                captureRequest.getRequestId(),
                captureRequest.getRequestToken())
        ).thenReturn(modificationResult);

        CaptureResult result = adyenCaptureCommand.perform(captureRequest);
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.REVIEW_NEEDED, result.getTransactionStatusDetails());
    }

    /**
     * Test immediate capture
     */
    @Test
    public void testImmediateCaptureSuccess() {
        Configuration config = new BaseConfiguration();
        config.setProperty(CONFIG_IMMEDIATE_CAPTURE, "true");
        when(configurationServiceMock.getConfiguration()).thenReturn(config);

        CaptureResult result = adyenCaptureCommand.perform(captureRequest);
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.SUCCESFULL, result.getTransactionStatusDetails());
    }

    /**
     * Test manual capture for a payment method that doesn't support manual capture
     */
    @Test
    public void testManualNotSupportedCaptureSuccess() {
        OrderModel orderModel = new OrderModel();
        orderModel.setAdyenPaymentMethod("paysafe");
        when(orderRepositoryMock.getOrderModel(Mockito.any(String.class)))
                .thenReturn(orderModel);

        Configuration config = new BaseConfiguration();
        config.setProperty(CONFIG_IMMEDIATE_CAPTURE, "false");
        when(configurationServiceMock.getConfiguration()).thenReturn(config);

        CaptureResult result = adyenCaptureCommand.perform(captureRequest);
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.SUCCESFULL, result.getTransactionStatusDetails());
    }
}
