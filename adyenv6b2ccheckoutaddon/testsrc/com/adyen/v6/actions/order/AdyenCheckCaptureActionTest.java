package com.adyen.v6.actions.order;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.PAYMENT_PROVIDER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Tests AdyenCheckCaptureAction
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenCheckCaptureActionTest {
    @Mock
    private OrderProcessModel orderProcessModelMock;

    @Mock
    private OrderModel orderModelMock;

    @Mock
    private ModelService modelServiceMock;

    AdyenCheckCaptureAction adyenCheckCaptureAction;

    @Before
    public void setUp() {
        // implement here code executed before each test
        when(orderProcessModelMock.getCode()).thenReturn("1234");
        when(orderProcessModelMock.getOrder()).thenReturn(orderModelMock);
        adyenCheckCaptureAction = new AdyenCheckCaptureAction();
        adyenCheckCaptureAction.setModelService(modelServiceMock);
    }

    @After
    public void tearDown() {
        // implement here code executed after each test
    }

    private PaymentTransactionEntryModel createAuthorizedEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.AUTHORIZATION);
        entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());

        return entry;
    }

    private PaymentTransactionEntryModel createCaptureReceivedEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.CAPTURE);
        entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.REVIEW_NEEDED.name());

        return entry;
    }

    private PaymentTransactionEntryModel createCaptureSuccessEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.CAPTURE);
        entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());

        return entry;
    }

    private PaymentTransactionEntryModel createCaptureRejectedEntry() {
        PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
        entry.setType(PaymentTransactionType.CAPTURE);
        entry.setTransactionStatus(TransactionStatus.REJECTED.name());
        entry.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE.name());

        return entry;
    }

    /**
     * No authorizations found - consider payment captured
     *
     * @throws Exception
     */
    @Test
    public void testNoAuthorizations() throws Exception {
        List<PaymentTransactionModel> transactions = new ArrayList<>();
        when(orderModelMock.getPaymentTransactions()).thenReturn(transactions);

        assertEquals(
                AdyenCheckCaptureAction.Transition.OK.toString(),
                adyenCheckCaptureAction.execute(orderProcessModelMock)
        );

        PaymentTransactionModel adyenTransaction = new PaymentTransactionModel();
        adyenTransaction.setPaymentProvider(PAYMENT_PROVIDER);

        adyenTransaction.setEntries(new ArrayList<>());

        adyenTransaction.getEntries().add(createAuthorizedEntry());
        adyenTransaction.getEntries().add(createCaptureReceivedEntry());

        transactions.add(adyenTransaction);

        assertEquals(
                AdyenCheckCaptureAction.Transition.WAIT.toString(),
                adyenCheckCaptureAction.execute(orderProcessModelMock)
        );

        adyenTransaction.getEntries().add(createCaptureSuccessEntry());

        assertEquals(
                AdyenCheckCaptureAction.Transition.OK.toString(),
                adyenCheckCaptureAction.execute(orderProcessModelMock)
        );
    }

    /**
     * Failed capture scenario
     *
     * @throws Exception
     */
    @Test
    public void testCaptureRejected() throws Exception {
        PaymentTransactionModel adyenTransaction = new PaymentTransactionModel();
        adyenTransaction.setPaymentProvider(PAYMENT_PROVIDER);
        List<PaymentTransactionEntryModel> transactionEntries = new ArrayList<>();

        transactionEntries.add(createAuthorizedEntry());
        transactionEntries.add(createCaptureReceivedEntry());
        transactionEntries.add(createCaptureRejectedEntry());

        adyenTransaction.setEntries(transactionEntries);

        List<PaymentTransactionModel> transactions = new ArrayList<>();
        transactions.add(adyenTransaction);
        when(orderModelMock.getPaymentTransactions()).thenReturn(transactions);

        String result = adyenCheckCaptureAction.execute(orderProcessModelMock);

        assertEquals(AdyenCheckCaptureAction.Transition.NOK.toString(), result);
    }

    /**
     * Test multiple transactions (Adyen and non-Adyen)
     *
     * @throws Exception
     */
    @Test
    public void testMultiTransactions() throws Exception {
        List<PaymentTransactionModel> transactions = new ArrayList<>();

        //Add Adyen Capture Pending transaction
        PaymentTransactionModel adyenTransaction = new PaymentTransactionModel();
        adyenTransaction.setPaymentProvider(PAYMENT_PROVIDER);
        adyenTransaction.setEntries(new ArrayList<>());

        adyenTransaction.getEntries().add(createAuthorizedEntry());
        adyenTransaction.getEntries().add(createCaptureReceivedEntry());

        transactions.add(adyenTransaction);

        when(orderModelMock.getPaymentTransactions()).thenReturn(transactions);

        assertEquals(
                AdyenCheckCaptureAction.Transition.WAIT.toString(),
                adyenCheckCaptureAction.execute(orderProcessModelMock)
        );

        //Add non-Adyen transaction
        PaymentTransactionModel otherTransaction = new PaymentTransactionModel();
        otherTransaction.setEntries(new ArrayList<>());

        otherTransaction.getEntries().add(createAuthorizedEntry());
        otherTransaction.getEntries().add(createCaptureSuccessEntry());

        transactions.add(otherTransaction);

        assertEquals(
                AdyenCheckCaptureAction.Transition.WAIT.toString(),
                adyenCheckCaptureAction.execute(orderProcessModelMock)
        );

        //Add Adyen captured transaction
        PaymentTransactionModel adyenTransaction2 = new PaymentTransactionModel();
        adyenTransaction2.setPaymentProvider(PAYMENT_PROVIDER);
        adyenTransaction2.setEntries(new ArrayList<>());

        adyenTransaction2.getEntries().add(createAuthorizedEntry());
        adyenTransaction2.getEntries().add(createCaptureReceivedEntry());
        adyenTransaction2.getEntries().add(createCaptureSuccessEntry());

        transactions.add(adyenTransaction2);

        assertEquals(
                AdyenCheckCaptureAction.Transition.WAIT.toString(),
                adyenCheckCaptureAction.execute(orderProcessModelMock)
        );

        //Make all Adyen entries captured
        adyenTransaction.getEntries().add(createCaptureSuccessEntry());

        assertEquals(
                AdyenCheckCaptureAction.Transition.OK.toString(),
                adyenCheckCaptureAction.execute(orderProcessModelMock)
        );
    }
}
