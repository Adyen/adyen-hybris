package com.adyen.v6.cronjob;

import com.adyen.v6.model.NotificationItemModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;

import static com.adyen.model.notification.NotificationRequestItem.EVENT_CODE_CAPTURE;
import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenProcessNotificationCronJobTest {
    @Mock
    private ModelService modelServiceMock;

    @Mock
    private CommonI18NService commonI18NServiceMock;

    @Mock
    private FlexibleSearchService flexibleSearchServiceMock;

    @Mock
    private BusinessProcessService businessProcessServiceMock;

    private AdyenProcessNotificationCronJob adyenProcessNotificationCronJob;

    @Before
    public void setUp() {
        when(commonI18NServiceMock.getCurrency("EUR"))
                .thenReturn(new CurrencyModel("EUR", "E"));
        when(modelServiceMock.create(PaymentTransactionEntryModel.class))
                .thenReturn(new PaymentTransactionEntryModel());

        adyenProcessNotificationCronJob = new AdyenProcessNotificationCronJob();
        adyenProcessNotificationCronJob.setModelService(modelServiceMock);
        adyenProcessNotificationCronJob.setCommonI18NService(commonI18NServiceMock);
        adyenProcessNotificationCronJob.setFlexibleSearchService(flexibleSearchServiceMock);
        adyenProcessNotificationCronJob.setBusinessProcessService(businessProcessServiceMock);
    }

    @After
    public void tearDown() {
        // implement here code executed after each test
    }

    /**
     * Test successful capture
     * It should save the capture notification and mark it as successful
     * It should emmit the AdyenCaptured event
     *
     * @throws Exception
     */
    @Test
    public void testCaptureSuccess() throws Exception {
        String pspReference = "123";

        NotificationItemModel notificationItemModel = new NotificationItemModel();
        notificationItemModel.setPspReference(pspReference);
        notificationItemModel.setEventCode(EVENT_CODE_CAPTURE);
        notificationItemModel.setSuccess(true);

        PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
        paymentTransactionModel.setEntries(new ArrayList<PaymentTransactionEntryModel>());

        PaymentTransactionEntryModel paymentTransactionEntryModel = adyenProcessNotificationCronJob
                .createCapturedPaymentTransactionEntryModel(paymentTransactionModel, notificationItemModel);

        assertEquals(pspReference, paymentTransactionEntryModel.getRequestId());
        assertTrue(PaymentTransactionType.CAPTURE.equals(paymentTransactionEntryModel.getType()));
        assertTrue(ACCEPTED.name().equals(paymentTransactionEntryModel.getTransactionStatus()));

        OrderModel orderModel = createDummyOrderModel();

        paymentTransactionModel.setOrder(orderModel);

        when(flexibleSearchServiceMock
                .searchUnique(Mockito.any(FlexibleSearchQuery.class)))
                .thenReturn(paymentTransactionModel);

        adyenProcessNotificationCronJob.processNotification(notificationItemModel);

        //Verify that we emmit the event of Capture to the order processes
        verify(businessProcessServiceMock).triggerEvent("order_process_code_AdyenCaptured");

        //Verify that the capture transaction is saved
        verify(modelServiceMock).save(paymentTransactionEntryModel);
    }

    /**
     * Test failed capture
     * It should save the captured notification and mark it as rejected
     *
     * @throws Exception
     */
    @Test
    public void testCaptureFail() throws Exception {
        String pspReference = "123";

        NotificationItemModel notificationItemModel = new NotificationItemModel();
        notificationItemModel.setPspReference(pspReference);
        notificationItemModel.setEventCode(EVENT_CODE_CAPTURE);
        notificationItemModel.setSuccess(false);

        PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
        paymentTransactionModel.setEntries(new ArrayList<PaymentTransactionEntryModel>());

        PaymentTransactionEntryModel paymentTransactionEntryModel = adyenProcessNotificationCronJob
                .createCapturedPaymentTransactionEntryModel(paymentTransactionModel, notificationItemModel);

        assertEquals(pspReference, paymentTransactionEntryModel.getRequestId());
        assertTrue(PaymentTransactionType.CAPTURE.equals(paymentTransactionEntryModel.getType()));
        assertTrue(REJECTED.name().equals(paymentTransactionEntryModel.getTransactionStatus()));

        OrderModel orderModel = createDummyOrderModel();

        paymentTransactionModel.setOrder(orderModel);

        when(flexibleSearchServiceMock
                .searchUnique(Mockito.any(FlexibleSearchQuery.class)))
                .thenReturn(paymentTransactionModel);

        adyenProcessNotificationCronJob.processNotification(notificationItemModel);

        //Verify that we emmit the event of Capture to the order processes
        verify(businessProcessServiceMock).triggerEvent("order_process_code_AdyenCaptured");

        //Verify that the capture transaction is saved
        verify(modelServiceMock).save(paymentTransactionEntryModel);
    }

    private OrderModel createDummyOrderModel() {
        Collection<OrderProcessModel> orderProcessModels = new ArrayList<OrderProcessModel>();
        OrderProcessModel orderProcessModel = new OrderProcessModel();
        orderProcessModel.setCode("order_process_code");
        orderProcessModels.add(orderProcessModel);

        OrderModel orderModel = new OrderModel();
        orderModel.setOrderProcess(orderProcessModels);

        return orderModel;
    }
}
