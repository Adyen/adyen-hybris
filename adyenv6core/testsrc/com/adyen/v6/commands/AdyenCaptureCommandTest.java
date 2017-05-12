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

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.adyen.model.modification.ModificationResult;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.DefaultAdyenPaymentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.store.BaseStoreModel;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenCaptureCommandTest {
    private CaptureRequest captureRequest;

    @Mock
    private AdyenPaymentServiceFactory adyenPaymentServiceFactoryMock;

    @Mock
    private DefaultAdyenPaymentService adyenPaymentServiceMock;

    @Mock
    private OrderRepository orderRepositoryMock;

    private BaseStoreModel baseStore;

    AdyenCaptureCommand adyenCaptureCommand;

    @Before
    public void setUp() {
        adyenCaptureCommand = new AdyenCaptureCommand();
        captureRequest = new CaptureRequest("merchantTransactionCode", "requestId", "requestToken", Currency.getInstance("EUR"), new BigDecimal(100), "Adyen");

        PaymentInfoModel paymentInfoModel = new PaymentInfoModel();
        paymentInfoModel.setAdyenPaymentMethod("visa");

        OrderModel orderModel = new OrderModel();
        orderModel.setPaymentInfo(paymentInfoModel);

        when(orderRepositoryMock.getOrderModel(Mockito.any(String.class))).thenReturn(orderModel);

        baseStore = new BaseStoreModel();
        baseStore.setAdyenImmediateCapture(false);
        orderModel.setStore(baseStore);

        when(adyenPaymentServiceFactoryMock.createFromBaseStore(baseStore)).thenReturn(adyenPaymentServiceMock);

        adyenCaptureCommand.setOrderRepository(orderRepositoryMock);
        adyenCaptureCommand.setAdyenPaymentServiceFactory(adyenPaymentServiceFactoryMock);
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
        ModificationResult modificationResult = new ModificationResult();
        modificationResult.setPspReference("1235");
        modificationResult.setResponse(ModificationResult.ResponseEnum.CAPTURE_RECEIVED_);

        when(adyenPaymentServiceMock.capture(captureRequest.getTotalAmount(), captureRequest.getCurrency(), captureRequest.getRequestId(), captureRequest.getRequestToken())).thenReturn(
                modificationResult);

        CaptureResult result = adyenCaptureCommand.perform(captureRequest);
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.REVIEW_NEEDED, result.getTransactionStatusDetails());
    }

    /**
     * Test immediate capture
     */
    @Test
    public void testImmediateCaptureSuccess() {
        baseStore.setAdyenImmediateCapture(true);

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

        PaymentInfoModel paymentInfoModelMock = mock(PaymentInfoModel.class);
        when(paymentInfoModelMock.getAdyenPaymentMethod()).thenReturn("paysafe");
        orderModel.setPaymentInfo(paymentInfoModelMock);

        orderModel.setStore(baseStore);
        when(orderRepositoryMock.getOrderModel(Mockito.any(String.class))).thenReturn(orderModel);

        CaptureResult result = adyenCaptureCommand.perform(captureRequest);
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.SUCCESFULL, result.getTransactionStatusDetails());
    }
}
