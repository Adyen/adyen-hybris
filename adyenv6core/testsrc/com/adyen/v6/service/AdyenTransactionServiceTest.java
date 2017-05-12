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

import com.adyen.v6.model.NotificationItemModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;

import static com.adyen.model.notification.NotificationRequestItem.EVENT_CODE_AUTHORISATION;
import static com.adyen.model.notification.NotificationRequestItem.EVENT_CODE_CAPTURE;
import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenTransactionServiceTest {
    @Mock
    private ModelService modelServiceMock;

    @Mock
    private CommonI18NService commonI18NServiceMock;

    private DefaultAdyenTransactionService adyenTransactionService;

    @Before
    public void setUp() {
        when(modelServiceMock.create(PaymentTransactionEntryModel.class))
                .thenReturn(new PaymentTransactionEntryModel());

        PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
        paymentTransactionModel.setEntries(new ArrayList<>());
        when(modelServiceMock.create(PaymentTransactionModel.class))
                .thenReturn(paymentTransactionModel);

        adyenTransactionService = new DefaultAdyenTransactionService();

        adyenTransactionService.setModelService(modelServiceMock);
        adyenTransactionService.setCommonI18NService(commonI18NServiceMock);
    }

    @Test
    public void testCreateCapturedTransactionFromNotification() {
        String pspReference = "123";

        NotificationItemModel notificationItemModel = new NotificationItemModel();
        notificationItemModel.setPspReference(pspReference);
        notificationItemModel.setEventCode(EVENT_CODE_CAPTURE);
        notificationItemModel.setSuccess(true);

        PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
        paymentTransactionModel.setEntries(new ArrayList<PaymentTransactionEntryModel>());

        PaymentTransactionEntryModel paymentTransactionEntryModel = adyenTransactionService
                .createCapturedTransactionFromNotification(paymentTransactionModel, notificationItemModel);

        assertEquals(pspReference, paymentTransactionEntryModel.getRequestId());
        assertEquals(ACCEPTED.name(), paymentTransactionEntryModel.getTransactionStatus());

        //Test non-successful notification
        notificationItemModel.setSuccess(false);
        paymentTransactionEntryModel = adyenTransactionService
                .createCapturedTransactionFromNotification(paymentTransactionModel, notificationItemModel);

        assertEquals(REJECTED.name(), paymentTransactionEntryModel.getTransactionStatus());
    }

    /**
     * Test authorizeOrderModel
     *
     * @throws Exception
     */
    @Test
    public void testAuthorizeOrderModel() {
        String pspReference = "123";
        String merchantReference = "001";

        OrderModel orderModel = createDummyOrderModel();

        PaymentTransactionModel paymentTransactionModel = adyenTransactionService
                .authorizeOrderModel(orderModel, merchantReference, pspReference);

        //Verify that the payment transaction is saved
        verify(modelServiceMock).save(paymentTransactionModel);
    }

    @Test
    public void testStoreFailedAuthorizationFromNotification() {
        String pspReference = "123";

        NotificationItemModel notificationItemModel = new NotificationItemModel();
        notificationItemModel.setPspReference(pspReference);
        notificationItemModel.setMerchantReference("001");
        notificationItemModel.setEventCode(EVENT_CODE_AUTHORISATION);
        notificationItemModel.setSuccess(false);

        OrderModel orderModel = createDummyOrderModel();

        PaymentTransactionModel paymentTransactionModel = adyenTransactionService
                .storeFailedAuthorizationFromNotification(notificationItemModel, orderModel);

        //Verify that the payment transaction is saved
        verify(modelServiceMock).save(paymentTransactionModel);
    }

    private OrderModel createDummyOrderModel() {
        Collection<OrderProcessModel> orderProcessModels = new ArrayList<OrderProcessModel>();
        OrderProcessModel orderProcessModel = new OrderProcessModel();
        orderProcessModel.setCode("order_process_code");
        orderProcessModels.add(orderProcessModel);

        OrderModel orderModel = new OrderModel();
        orderModel.setOrderProcess(orderProcessModels);
        orderModel.setTotalPrice(1.23);
        orderModel.setCurrency(new CurrencyModel());

        PaymentInfoModel paymentInfoModel = new PaymentInfoModel();
        paymentInfoModel.setAdyenPaymentMethod("visa");

        orderModel.setPaymentInfo(paymentInfoModel);

        return orderModel;
    }
}
