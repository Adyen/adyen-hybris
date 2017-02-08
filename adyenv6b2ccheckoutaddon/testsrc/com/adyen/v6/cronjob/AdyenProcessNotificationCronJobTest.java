package com.adyen.v6.cronjob;

import com.adyen.v6.model.NotificationItemModel;
import com.adyen.v6.service.AdyenTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
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
    private FlexibleSearchService flexibleSearchServiceMock;

    @Mock
    private BusinessProcessService businessProcessServiceMock;

    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;

    private AdyenProcessNotificationCronJob adyenProcessNotificationCronJob;

    @Before
    public void setUp() {
        when(modelServiceMock.create(PaymentTransactionEntryModel.class))
                .thenReturn(new PaymentTransactionEntryModel());

        adyenProcessNotificationCronJob = new AdyenProcessNotificationCronJob();
        adyenProcessNotificationCronJob.setModelService(modelServiceMock);
        adyenProcessNotificationCronJob.setFlexibleSearchService(flexibleSearchServiceMock);
        adyenProcessNotificationCronJob.setBusinessProcessService(businessProcessServiceMock);
        adyenProcessNotificationCronJob.setAdyenTransactionService(adyenTransactionServiceMock);
    }

    @After
    public void tearDown() {
        // implement here code executed after each test
    }

    /**
     * Test capture notification handling
     * It should save the capture notification and emmit the AdyenCaptured event
     *
     * @throws Exception
     */
    @Test
    public void testCaptureNotification() throws Exception {
        String pspReference = "123";

        PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
        paymentTransactionModel.setEntries(new ArrayList<PaymentTransactionEntryModel>());

        OrderModel orderModel = createDummyOrderModel();

        paymentTransactionModel.setOrder(orderModel);

        when(flexibleSearchServiceMock
                .searchUnique(Mockito.any(FlexibleSearchQuery.class)))
                .thenReturn(paymentTransactionModel);

        NotificationItemModel notificationItemModel = new NotificationItemModel();
        notificationItemModel.setPspReference(pspReference);
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
