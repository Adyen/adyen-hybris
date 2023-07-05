package com.adyen.v6.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import com.adyen.model.nexo.ErrorConditionType;
import com.adyen.model.nexo.ResultType;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.terminal.serialization.TerminalAPIGsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.hybris.bootstrap.annotations.UnitTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TerminalAPIUtilTest {

    public static String TEST_RESPONSE_DIR ="test/";
    @Test
    public void testGetPaymentResultFromStatusOrPaymentResponseSuccess() throws IOException {
        TerminalAPIResponse terminalAPIResponse = createResponseFromFile(TEST_RESPONSE_DIR+"SaleToPOIResponse.json");
        ResultType paymentResult = TerminalAPIUtil.getPaymentResultFromStatusOrPaymentResponse(terminalAPIResponse);
        assertNotNull(paymentResult);
        assertEquals(ResultType.SUCCESS, paymentResult);
    }

    @Test
    public void testGetPaymentResultFromStatusOrPaymentResponseFailure() throws IOException {
        TerminalAPIResponse terminalAPIResponse = createResponseFromFile(TEST_RESPONSE_DIR+"SaleToPOIResponseInProgress.json");
        ResultType statusResult = TerminalAPIUtil.getPaymentResultFromStatusOrPaymentResponse(terminalAPIResponse);
        assertNotNull(statusResult);
        assertEquals(ResultType.FAILURE, statusResult);
    }

    @Test
    public void testGetErrorConditionForPaymentResponse() throws IOException {
        TerminalAPIResponse terminalAPIResponse = createResponseFromFile(TEST_RESPONSE_DIR+"SaleToPOIResponsePaymentCancelled.json");
        assertNotNull(terminalAPIResponse);
        ErrorConditionType errorCondition = TerminalAPIUtil.getErrorConditionForPaymentResponse(terminalAPIResponse);
        assertNotNull(errorCondition);
        assertEquals(ErrorConditionType.CANCEL, errorCondition);
    }

    @Test
    public void testGetErrorConditionForPaymentFromStatusResponse() throws IOException {
        TerminalAPIResponse terminalAPIResponse = createResponseFromFile(TEST_RESPONSE_DIR+"SaleToPOIResponseStatusCancelled.json");
        assertNotNull(terminalAPIResponse);
        ErrorConditionType errorCondition = TerminalAPIUtil.getErrorConditionForPaymentFromStatusResponse(terminalAPIResponse);
        assertNotNull(errorCondition);
        assertEquals(ErrorConditionType.CANCEL, errorCondition);
    }

    @Test
    public void testGetErrorConditionForStatusFromStatusResponse() throws IOException {
        TerminalAPIResponse terminalAPIResponse = createResponseFromFile(TEST_RESPONSE_DIR+"SaleToPOIResponseInProgress.json");
        assertNotNull(terminalAPIResponse);
        ErrorConditionType errorCondition = TerminalAPIUtil.getErrorConditionForStatusFromStatusResponse(terminalAPIResponse);
        assertNotNull(errorCondition);
        assertEquals(ErrorConditionType.IN_PROGRESS, errorCondition);
    }

    @Test
    public void testGetReceiptFromPaymentResponseWithoutReceipt() throws IOException {
        TerminalAPIResponse terminalAPIResponse = createResponseFromFile(TEST_RESPONSE_DIR+"SaleToPOIResponse.json");
        String receipt = TerminalAPIUtil.getReceiptFromPaymentResponse(terminalAPIResponse);
        assertNotNull(receipt);
        assertEquals("<table class='terminal-api-receipt'></table>", receipt);
    }

    @Test
    public void testGetReceiptFromPaymentResponse() throws IOException {
        TerminalAPIResponse terminalAPIResponse = createResponseFromFile(TEST_RESPONSE_DIR+"SaleToPOIResponsePaymentCancelled.json");
        String receipt = TerminalAPIUtil.getReceiptFromPaymentResponse(terminalAPIResponse);
        assertNotNull(receipt);
        assertEquals("<table class='terminal-api-receipt'></table>", receipt);
    }

    @Test
    public void testGetReceiptFromStatusResponse() throws IOException {
        TerminalAPIResponse terminalAPIResponse = createResponseFromFile(TEST_RESPONSE_DIR+"SaleToPOIResponseStatusResponseWithReceipt.json");
        String receipt = TerminalAPIUtil.getReceiptFromStatusResponse(terminalAPIResponse);
        assertNotNull(receipt);
        assertNotEquals("<table class='terminal-api-receipt'></table>", receipt);
    }

    @Test
    public void testFormatTerminalAPIReceipt() throws IOException {
        TerminalAPIResponse terminalAPIResponse = createResponseFromFile(TEST_RESPONSE_DIR+"SaleToPOIResponseStatusResponseWithReceipt.json");
        String formattedReceipt = TerminalAPIUtil.formatTerminalAPIReceipt(terminalAPIResponse.getSaleToPOIResponse()
                                                                                              .getTransactionStatusResponse()
                                                                                              .getRepeatedMessageResponse()
                                                                                              .getRepeatedResponseMessageBody()
                                                                                              .getPaymentResponse()
                                                                                              .getPaymentReceipt());
        assertNotNull(formattedReceipt);
        assertTrue(formattedReceipt.contains("table class"));
    }

    @Test
    public void testGetErrorMessageForNonAuthorizedPosPayment() throws IOException {
        TerminalAPIResponse terminalAPIResponse = createResponseFromFile(TEST_RESPONSE_DIR+"SaleToPOIResponsePaymentCancelled.json");
        assertNotNull(terminalAPIResponse);
        String errorMessage = TerminalAPIUtil.getErrorMessageForNonAuthorizedPosPayment(terminalAPIResponse);
        assertNotNull(errorMessage);
        assertEquals("checkout.error.authorization.payment.cancelled", errorMessage);
    }

    private  TerminalAPIResponse createResponseFromFile(String fileName) throws IOException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        String json = Files.readString(Path.of(resource.getPath()), StandardCharsets.UTF_8);
        return TerminalAPIGsonBuilder.create().fromJson(json, new TypeToken<TerminalAPIResponse>() {
        }.getType());
    }
}
