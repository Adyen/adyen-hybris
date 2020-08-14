/*
 *                       ######
 *                       ######
 * ############    ####( ######  #####. ######  ############   ############
 * #############  #####( ######  #####. ######  #############  #############
 *        ######  #####( ######  #####. ######  #####  ######  #####  ######
 * ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 * ###### ######  #####( ######  #####. ######  #####          #####  ######
 * #############  #############  #############  #############  #####  ######
 *  ############   ############  #############   ############  #####  ######
 *                                      ######
 *                               #############
 *                               ############
 *
 * Adyen Hybris Extension
 *
 * Copyright (c) 2019 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 */
package com.adyen.v6.converters;

import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.nexo.PaymentAcquirerData;
import com.adyen.model.nexo.PaymentResponse;
import com.adyen.model.nexo.PaymentResult;
import com.adyen.model.nexo.RepeatedMessageResponse;
import com.adyen.model.nexo.RepeatedResponseMessageBody;
import com.adyen.model.nexo.Response;
import com.adyen.model.nexo.SaleToPOIResponse;
import com.adyen.model.nexo.TransactionIdentification;
import com.adyen.model.nexo.TransactionStatusRequest;
import com.adyen.model.nexo.TransactionStatusResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@UnitTest
@RunWith(Parameterized.class)
public class PosPaymentResponseConverterTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "ewogICJhZGRpdGlvbmFsRGF0YSI6IHsKICAgICJhdXRoQ29kZSI6ICIxMjM0IiwKICAgICJ0aWQiOiAiNDMyMSIsCiAgICAibWlkIjogIjEwMDAiLAogICAgInRyYW5zYWN0aW9uUmVmZXJlbmNlTnVtYmVyIjogIjg4MjU5NzQwMDQ5MDkyNUoiLAogICAgImV4cGlyeVllYXIiOiAiMjAyOCIsCiAgICAiYXZzUmVzdWx0IjogIjAgVW5rbm93biIKICB9LAogICJzdG9yZSI6ICJNeVN0b3JlIgp9", true, 6, "1234" },
                { "ewogICJhZGRpdGlvbmFsRGF0YSI6IHsKICAgICJhdXRoQ29kZSI6ICIxMjM0IgogIH0sCiAgInN0b3JlIjogIk15U3RvcmUiCn0=", true, 1, "1234" },
                { "", true, 0, null },
                { null, false, null, null }
        });
    }

    private String additionalResponse;
    private Boolean shouldExpectAdditionalData;
    private Integer expectedAdditionalDataSize;
    private String expectedAuthCode;

    private PosPaymentResponseConverter posPaymentResponseConverter;

    @Mock
    private SaleToPOIResponse saleToPoiResponse;

    //Payment Response
    @Mock
    private PaymentResponse paymentResponse;
    @Mock
    private Response response;
    @Mock
    private PaymentResult paymentResult;
    @Mock
    private PaymentAcquirerData paymentAcquirerData;
    @Mock
    private TransactionIdentification acquirerTransactionId;

    //Status response
    @Mock
    TransactionStatusResponse transactionStatusResponse;
    @Mock
    RepeatedMessageResponse repeatedMessageResponse;
    @Mock
    RepeatedResponseMessageBody repeatedResponseMessageBody;

    public PosPaymentResponseConverterTest(String additionalResponse, Boolean shouldExpectAdditionalData, Integer expectedAdditionalDataSize, String expectedAuthCode) {
        this.additionalResponse = additionalResponse;
        this.shouldExpectAdditionalData = shouldExpectAdditionalData;
        this.expectedAdditionalDataSize = expectedAdditionalDataSize;
        this.expectedAuthCode = expectedAuthCode;
    }

    @Before
    public void setUp() {
        initMocks(this);
        posPaymentResponseConverter = new PosPaymentResponseConverter();

        when(paymentResponse.getPaymentResult()).thenReturn(paymentResult);
        when(paymentResult.getPaymentAcquirerData()).thenReturn(paymentAcquirerData);
        when(paymentAcquirerData.getAcquirerTransactionID()).thenReturn(acquirerTransactionId);
        when(acquirerTransactionId.getTransactionID()).thenReturn("psp");

        when(paymentResponse.getResponse()).thenReturn(response);
        when(response.getAdditionalResponse()).thenReturn(additionalResponse);
    }

    @Test
    public void testConverterForPaymentResponse() {
        when(saleToPoiResponse.getPaymentResponse()).thenReturn(paymentResponse);

        PaymentsResponse paymentsResponse = posPaymentResponseConverter.convert(saleToPoiResponse);

        assertNotNull(paymentsResponse);
        assertEquals("psp", paymentsResponse.getPspReference());
        assertEquals(shouldExpectAdditionalData, paymentsResponse.getAdditionalData() != null);

        if(shouldExpectAdditionalData) {
            assertEquals((int) expectedAdditionalDataSize, paymentsResponse.getAdditionalData().size());
            assertEquals(expectedAuthCode, paymentsResponse.getAuthCode());
        }
    }

    @Test
    public void testConverterForStatusResponse() {
        when(saleToPoiResponse.getPaymentResponse()).thenReturn(null);

        when(saleToPoiResponse.getTransactionStatusResponse()).thenReturn(transactionStatusResponse);
        when(transactionStatusResponse.getRepeatedMessageResponse()).thenReturn(repeatedMessageResponse);
        when(repeatedMessageResponse.getRepeatedResponseMessageBody()).thenReturn(repeatedResponseMessageBody);
        when(repeatedResponseMessageBody.getPaymentResponse()).thenReturn(paymentResponse);

        PaymentsResponse paymentsResponse = posPaymentResponseConverter.convert(saleToPoiResponse);

        assertNotNull(paymentsResponse);
        assertEquals("psp", paymentsResponse.getPspReference());
        assertEquals(shouldExpectAdditionalData, paymentsResponse.getAdditionalData() != null);

        if (shouldExpectAdditionalData) {
            assertEquals((int) expectedAdditionalDataSize, paymentsResponse.getAdditionalData().size());
            assertEquals(expectedAuthCode, paymentsResponse.getAuthCode());
        }
    }
}
