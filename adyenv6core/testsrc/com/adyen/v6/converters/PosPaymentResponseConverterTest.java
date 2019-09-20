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
import com.adyen.model.nexo.Response;
import com.adyen.model.nexo.SaleToPOIResponse;
import com.adyen.model.nexo.TransactionIdentification;
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
                { "tid=1234&expiryYear=2028&expiryDate=2%2f2028&cardHolderName=test&avsResult=0%20Unknown&authCode=1234", 6, "1234" },
                { "authCode=1234", 1, "1234" },
                { "", 0, null },
                { null, 0, null }
        });
    }

    private String additionalResponse;
    private Integer expectedAdditionalDataSize;
    private String expectedAuthCode;

    private PosPaymentResponseConverter posPaymentResponseConverter;

    @Mock
    private SaleToPOIResponse saleToPoiResponse;
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

    public PosPaymentResponseConverterTest(String additionalResponse, Integer expectedAdditionalDataSize, String expectedAuthCode) {
        this.additionalResponse = additionalResponse;
        this.expectedAdditionalDataSize = expectedAdditionalDataSize;
        this.expectedAuthCode = expectedAuthCode;
    }

    @Before
    public void setUp() {
        initMocks(this);
        posPaymentResponseConverter = new PosPaymentResponseConverter();

        when(saleToPoiResponse.getPaymentResponse()).thenReturn(paymentResponse);
        when(paymentResponse.getPaymentResult()).thenReturn(paymentResult);
        when(paymentResult.getPaymentAcquirerData()).thenReturn(paymentAcquirerData);
        when(paymentAcquirerData.getAcquirerTransactionID()).thenReturn(acquirerTransactionId);
        when(acquirerTransactionId.getTransactionID()).thenReturn("psp");

        when(paymentResponse.getResponse()).thenReturn(response);
        when(response.getAdditionalResponse()).thenReturn(additionalResponse);
    }

    @Test
    public void testConverter() {
        PaymentsResponse paymentsResponse = posPaymentResponseConverter.convert(saleToPoiResponse);

        assertNotNull(paymentsResponse);
        assertEquals("psp", paymentsResponse.getPspReference());
        assertNotNull(paymentsResponse.getAdditionalData());
        assertEquals((int) expectedAdditionalDataSize, paymentsResponse.getAdditionalData().size());
        assertEquals(expectedAuthCode, paymentsResponse.getAuthCode());
    }
}
