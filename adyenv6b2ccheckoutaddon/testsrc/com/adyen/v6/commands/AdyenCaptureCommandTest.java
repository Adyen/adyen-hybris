package com.adyen.v6.commands;

import com.adyen.model.ModificationResult;
import com.adyen.v6.service.AdyenPaymentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenCaptureCommandTest {
    CaptureRequest captureRequest;

    @Mock
    private AdyenPaymentService adyenPaymentServiceMock;

    @Before
    public void setUp() {
        captureRequest = new CaptureRequest("merchantTransactionCode",
                "requestId",
                "requestToken",
                Currency.getInstance("EUR"),
                new BigDecimal(100),
                "Adyen"
        );
    }

    @After
    public void tearDown() {
        // implement here code executed after each test
    }

    /**
     * Test successful capture
     * @throws Exception
     */
    @Test
    public void testSuccess() throws Exception {
        ModificationResult modificationResult = new ModificationResult();
        modificationResult.setPspReference("1235");
        modificationResult.setResponse(ModificationResult.ResponseEnum.CAPTURE_RECEIVED_);

        when(adyenPaymentServiceMock.capture(
                captureRequest.getTotalAmount(),
                captureRequest.getCurrency(),
                captureRequest.getRequestId(),
                captureRequest.getRequestToken())
        ).thenReturn(modificationResult);

        AdyenCaptureCommand adyenCaptureCommand = new AdyenCaptureCommand();
        adyenCaptureCommand.setAdyenPaymentService(adyenPaymentServiceMock);

        CaptureResult result = adyenCaptureCommand.perform(captureRequest);
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
    }
}
