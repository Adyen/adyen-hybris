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
import com.adyen.v6.service.DefaultAdyenPaymentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.commands.request.PartialCaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenPartialCaptureCommandTest {
    private PartialCaptureRequest partialCaptureRequest;

    @Mock
    private AdyenPaymentServiceFactory adyenPaymentServiceFactoryMock;

    @Mock
    private DefaultAdyenPaymentService adyenPaymentServiceMock;

    @Mock
    private OrderRepository orderRepositoryMock;

    private BaseStoreModel baseStore;

    AdyenPartialCaptureCommand adyenPartialCaptureCommand;

    @Before
    public void setUp() {
        adyenPartialCaptureCommand = new AdyenPartialCaptureCommand();
        partialCaptureRequest = new PartialCaptureRequest("merchantTransactionCode", "requestId", "requestToken", Currency.getInstance("EUR"), new BigDecimal(100), "Adyen", "Adyen");

        PaymentInfoModel paymentInfoModel = new PaymentInfoModel();
        paymentInfoModel.setAdyenPaymentMethod("visa");

        OrderModel orderModel = new OrderModel();
        orderModel.setPaymentInfo(paymentInfoModel);

        when(orderRepositoryMock.getOrderModel(Mockito.any(String.class))).thenReturn(orderModel);

        baseStore = new BaseStoreModel();
        baseStore.setAdyenImmediateCapture(false);
        orderModel.setStore(baseStore);

        when(adyenPaymentServiceFactoryMock.createFromBaseStore(baseStore)).thenReturn(adyenPaymentServiceMock);

        adyenPartialCaptureCommand.setOrderRepository(orderRepositoryMock);
        adyenPartialCaptureCommand.setAdyenPaymentServiceFactory(adyenPaymentServiceFactoryMock);
    }

    @After
    public void tearDown() {
        // implement here code executed after each test
    }

    /**
     * Test successful partial capture
     *
     * @throws Exception
     */
    @Test
    public void testManualPartialCaptureSuccess() throws Exception {
        ModificationResult modificationResult = new ModificationResult();
        modificationResult.setPspReference("1235");
        modificationResult.setResponse("[capture-received]");

        when(adyenPaymentServiceMock.capture(partialCaptureRequest.getTotalAmount(), partialCaptureRequest.getCurrency(), partialCaptureRequest.getRequestId(), partialCaptureRequest.getRequestToken())).thenReturn(
                modificationResult);

        CaptureResult result = adyenPartialCaptureCommand.perform(partialCaptureRequest);
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.REVIEW_NEEDED, result.getTransactionStatusDetails());
    }

    /**
     * Test immediate partial capture
     */
    @Test
    public void testImmediateCaptureSuccess() {
        baseStore.setAdyenImmediateCapture(true);

        CaptureResult result = adyenPartialCaptureCommand.perform(partialCaptureRequest);
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.SUCCESFULL, result.getTransactionStatusDetails());
    }

    /**
     * Test manual partial capture for a payment method that doesn't support manual capture
     */
    @Test
    public void testManualNotSupportedCaptureSuccess() {
        OrderModel orderModel = new OrderModel();

        PaymentInfoModel paymentInfoModelMock = mock(PaymentInfoModel.class);
        when(paymentInfoModelMock.getAdyenPaymentMethod()).thenReturn("paysafe");
        orderModel.setPaymentInfo(paymentInfoModelMock);

        orderModel.setStore(baseStore);
        when(orderRepositoryMock.getOrderModel(Mockito.any(String.class))).thenReturn(orderModel);

        CaptureResult result = adyenPartialCaptureCommand.perform(partialCaptureRequest);
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.SUCCESFULL, result.getTransactionStatusDetails());
    }
}
