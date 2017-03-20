package com.adyen.v6.cronjob;

import com.adyen.v6.model.NotificationItemModel;
import com.adyen.v6.repository.NotificationItemRepository;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.repository.PaymentTransactionRepository;
import com.adyen.v6.service.AdyenBusinessProcessService;
import com.adyen.v6.service.AdyenTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.adyen.model.notification.NotificationRequestItem.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenProcessNotificationCronJobTest {
    @Mock
    private ModelService modelServiceMock;

    @Mock
    private AdyenTransactionService adyenTransactionServiceMock;

    @Mock
    private OrderRepository orderRepositoryMock;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepositoryMock;

    @Mock
    private NotificationItemRepository notificationItemRepositoryMock;

    private AdyenBusinessProcessService adyenBusinessProcessService;

    @Mock
    private BusinessProcessService businessProcessServiceMock;

    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;

    private AdyenProcessNotificationCronJob adyenProcessNotificationCronJob;

    @Before
    public void setUp() {
        when(modelServiceMock.create(PaymentTransactionEntryModel.class))
                .thenReturn(new PaymentTransactionEntryModel());

        adyenBusinessProcessService = new AdyenBusinessProcessService();
        adyenBusinessProcessService.setBusinessProcessService(businessProcessServiceMock);

        adyenProcessNotificationCronJob = new AdyenProcessNotificationCronJob();
        adyenProcessNotificationCronJob.setModelService(modelServiceMock);
        adyenProcessNotificationCronJob.setAdyenBusinessProcessService(adyenBusinessProcessService);
        adyenProcessNotificationCronJob.setAdyenTransactionService(adyenTransactionServiceMock);
        adyenProcessNotificationCronJob.setOrderRepository(orderRepositoryMock);
        adyenProcessNotificationCronJob.setNotificationItemRepository(notificationItemRepositoryMock);
        adyenProcessNotificationCronJob.setPaymentTransactionRepository(paymentTransactionRepositoryMock);
    }

    @After
    public void tearDown() {
        // implement here code executed after each test
    }

    /**
     * Test CAPTURE notification handling
     * It should save the capture notification and emmit the AdyenCaptured event
     *
     * @throws Exception
     */
    @Test
    public void testCaptureNotification() throws Exception {
        PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
        paymentTransactionModel.setEntries(new ArrayList<PaymentTransactionEntryModel>());

        OrderModel orderModel = createDummyOrderModel();

        paymentTransactionModel.setOrder(orderModel);

        when(paymentTransactionRepositoryMock.getTransactionModel(Mockito.any(String.class)))
                .thenReturn(paymentTransactionModel);

        NotificationItemModel notificationItemModel = new NotificationItemModel();
        notificationItemModel.setOriginalReference("123");
        notificationItemModel.setPspReference("456");
        notificationItemModel.setEventCode(EVENT_CODE_CAPTURE);
        notificationItemModel.setSuccess(true);

        when(adyenTransactionServiceMock.createCapturedTransactionFromNotification(
                paymentTransactionModel,
                notificationItemModel
        )).thenReturn(paymentTransactionEntryModelMock);

        adyenProcessNotificationCronJob.processNotification(notificationItemModel);

        //Verify that we emmit the event of Capture to the order processes
        verify(businessProcessServiceMock).triggerEvent("order_process_code_AdyenCaptured");

        //Verify that the capture transaction is saved
        verify(modelServiceMock).save(paymentTransactionEntryModelMock);
    }

    /**
     * Test AUTHORISATION notification handling
     * It should save the capture notification and emmit the AdyenCaptured event
     *
     * @throws Exception
     */
    @Test
    public void testAuthorisationNotification() throws Exception {
        String pspReference = "123";
        String merchantReference = "001";

        OrderModel orderModel = createDummyOrderModel();

        NotificationItemModel notificationItemModel = new NotificationItemModel();
        notificationItemModel.setPspReference(pspReference);
        notificationItemModel.setEventCode(EVENT_CODE_AUTHORISATION);
        notificationItemModel.setMerchantReference(merchantReference);
        notificationItemModel.setSuccess(true);

        when(paymentTransactionRepositoryMock.getTransactionModel(Mockito.any(String.class)))
                .thenReturn(null);

        when(orderRepositoryMock.getOrderModel(Mockito.any(String.class)))
                .thenReturn(orderModel);

        adyenProcessNotificationCronJob.processNotification(notificationItemModel);

        //Verify that we emmit the event of Capture to the order processes
        verify(businessProcessServiceMock).triggerEvent("order_process_code_AdyenAuthorized");

        //Verify that the authorizeOrderModel is called
        verify(adyenTransactionServiceMock).authorizeOrderModel(orderModel, merchantReference, pspReference);
    }

    /**
     * Test failed authorisation
     */
    @Test
    public void testFailedAuthorisationNotification() {
        String pspReference = "123";
        String merchantReference = "001";

        OrderModel orderModel = createDummyOrderModel();

        NotificationItemModel notificationItemModel = new NotificationItemModel();
        notificationItemModel.setPspReference(pspReference);
        notificationItemModel.setEventCode(EVENT_CODE_AUTHORISATION);
        notificationItemModel.setMerchantReference(merchantReference);
        notificationItemModel.setSuccess(false);

        when(paymentTransactionRepositoryMock.getTransactionModel(Mockito.any(String.class)))
                .thenReturn(null);

        when(orderRepositoryMock.getOrderModel(Mockito.any(String.class)))
                .thenReturn(orderModel);

        adyenProcessNotificationCronJob.processNotification(notificationItemModel);

        //Verify that we emmit the event of Capture to the order processes
        verify(businessProcessServiceMock).triggerEvent("order_process_code_AdyenAuthorized");

        //Verify that the authorizeOrderModel is called
        verify(adyenTransactionServiceMock).storeFailedAuthorizationFromNotification(notificationItemModel, orderModel);
    }

    /**
     * Test Duplicate AUTHORISATION event handling
     */
    @Test
    public void testDuplicateAuthorisationNotification() {
        NotificationItemModel notificationItemModel = new NotificationItemModel();
        notificationItemModel.setPspReference("123");
        notificationItemModel.setEventCode(EVENT_CODE_AUTHORISATION);
        notificationItemModel.setSuccess(true);

        when(paymentTransactionRepositoryMock.getTransactionModel(Mockito.any(String.class)))
                .thenReturn(new PaymentTransactionModel());

        adyenProcessNotificationCronJob.processNotification(notificationItemModel);

        //Verify that we emmit the event of Capture to the order processes
        verify(orderRepositoryMock, Mockito.never()).getOrderModel(Mockito.any(String.class));
    }

    /**
     * Test successful Refund notification
     *
     * @throws Exception
     */
    @Test
    public void testRefundNotification() throws Exception {
        PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
        paymentTransactionModel.setEntries(new ArrayList<PaymentTransactionEntryModel>());

        OrderModel orderModel = createDummyOrderModel();

        paymentTransactionModel.setOrder(orderModel);

        when(paymentTransactionRepositoryMock.getTransactionModel(Mockito.any(String.class)))
                .thenReturn(paymentTransactionModel);

        NotificationItemModel notificationItemModel = new NotificationItemModel();
        notificationItemModel.setOriginalReference("123");
        notificationItemModel.setPspReference("456");
        notificationItemModel.setEventCode(EVENT_CODE_REFUND);
        notificationItemModel.setSuccess(true);

        when(adyenTransactionServiceMock.createRefundedTransactionFromNotification(
                paymentTransactionModel,
                notificationItemModel
        )).thenReturn(paymentTransactionEntryModelMock);

        adyenProcessNotificationCronJob.processNotification(notificationItemModel);

        //Verify that we emmit the event of Capture to the order processes
        verify(businessProcessServiceMock).triggerEvent("return_process_code_AdyenRefunded");

        //Verify that the capture transaction is saved
        verify(modelServiceMock).save(paymentTransactionEntryModelMock);
    }


    private OrderModel createDummyOrderModel() {
        Collection<OrderProcessModel> orderProcessModels = new ArrayList<OrderProcessModel>();
        OrderProcessModel orderProcessModel = new OrderProcessModel();
        orderProcessModel.setCode("order_process_code");
        orderProcessModels.add(orderProcessModel);

        //Create necessary instances for refunds
        ReturnProcessModel returnProcess = new ReturnProcessModel();
        returnProcess.setCode("return_process_code");

        Collection<ReturnProcessModel> returnProcesses = new ArrayList<>();
        returnProcesses.add(returnProcess);

        ReturnRequestModel returnRequest = new ReturnRequestModel();
        returnRequest.setReturnProcess(returnProcesses);

        List<ReturnRequestModel> returnRequests = new ArrayList<>();
        returnRequests.add(returnRequest);

        OrderModel orderModel = new OrderModel();
        orderModel.setOrderProcess(orderProcessModels);
        orderModel.setReturnRequests(returnRequests);

        return orderModel;
    }
}
