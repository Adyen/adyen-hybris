package com.adyen.v6.facade;

import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.nexo.ErrorConditionType;
import com.adyen.model.nexo.PaymentResponse;
import com.adyen.model.nexo.RepeatedMessageResponse;
import com.adyen.model.nexo.RepeatedResponseMessageBody;
import com.adyen.model.nexo.Response;
import com.adyen.model.nexo.ResultType;
import com.adyen.model.nexo.SaleToPOIResponse;
import com.adyen.model.nexo.TransactionStatusResponse;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.v6.converters.PosPaymentResponseConverter;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.DefaultAdyenCheckoutFacade;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenOrderService;
import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.AdyenTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenCheckoutFacadeTest {

    private static final String SERVICE_ID = "serviceId";

    @InjectMocks
    DefaultAdyenCheckoutFacade adyenCheckoutFacade = new DefaultAdyenCheckoutFacade();

    @Mock
    AdyenPaymentServiceFactory adyenPaymentServiceFactory;
    @Mock
    AdyenPaymentService adyenPaymentService;
    @Mock
    BaseStoreService baseStoreService;
    @Mock
    CheckoutCustomerStrategy checkoutCustomerStrategy;
    @Mock
    PosPaymentResponseConverter posPaymentResponseConverter;
    @Mock
    CartService cartService;
    @Mock
    AdyenTransactionService adyenTransactionService;
    @Mock
    CheckoutFacade checkoutFacade;
    @Mock
    OrderRepository orderRepository;
    @Mock
    AdyenOrderService adyenOrderService;

    @Mock
    HttpServletRequest request;
    @Mock
    CartData cartData;
    @Mock
    PaymentsResponse paymentsResponse;
    @Mock
    OrderData orderData;

    @Mock
    TerminalAPIResponse terminalApiResponse;
    @Mock
    SaleToPOIResponse saleToPoiResponse;
    //Payment Response
    @Mock
    PaymentResponse paymentResponse;
    @Mock
    Response response;
    //Status response
    @Mock
    Response statusResponse;
    @Mock
    TransactionStatusResponse transactionStatusResponse;
    @Mock
    RepeatedMessageResponse repeatedMessageResponse;
    @Mock
    RepeatedResponseMessageBody repeatedResponseMessageBody;

    @Before
    public void setUp() {
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(true);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(new CustomerModel());
        when(request.getAttribute("originalServiceId")).thenReturn(SERVICE_ID);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(new BaseStoreModel());
        when(adyenPaymentServiceFactory.createFromBaseStore(any())).thenReturn(adyenPaymentService);
    }

    @Test
    public void testInitiatePosPaymentSuccess() throws Exception {
        when(adyenPaymentService.sendSyncPosPaymentRequest(eq(cartData), any(), eq(SERVICE_ID))).thenReturn(terminalApiResponse);
        when(posPaymentResponseConverter.convert(saleToPoiResponse)).thenReturn(paymentsResponse);
        //TerminalAPIUtil.getPaymentResult
        when(terminalApiResponse.getSaleToPOIResponse()).thenReturn(saleToPoiResponse);
        when(saleToPoiResponse.getPaymentResponse()).thenReturn(paymentResponse);
        when(paymentResponse.getResponse()).thenReturn(response);
        when(response.getResult()).thenReturn(ResultType.SUCCESS);
        //createAuthorizedOrder
        when(cartService.getSessionCart()).thenReturn(new CartModel());
        when(adyenTransactionService.authorizeOrderModel(any(), any(), any())).thenReturn(new PaymentTransactionModel());
        //createOrderFromPaymentsResponse
        when(checkoutFacade.placeOrder()).thenReturn(orderData);
        when(orderRepository.getOrderModel(any())).thenReturn(new OrderModel());
        when(paymentsResponse.getAdditionalData()).thenReturn(new HashMap<>());
        //updateOrder
        doNothing().when(adyenOrderService).updateOrderFromPaymentsResponse(isA(OrderModel.class), isA(PaymentsResponse.class));

        OrderData orderDataResult = adyenCheckoutFacade.initiatePosPayment(request, cartData);
        assertEquals(orderData, orderDataResult);
        verify(adyenPaymentService, times(1)).sendSyncPosPaymentRequest(eq(cartData), any(), eq(SERVICE_ID));
        verify(posPaymentResponseConverter, times(1)).convert(saleToPoiResponse);
        verify(adyenTransactionService, times(1)).authorizeOrderModel(any(), any(), any());
        verify(checkoutFacade, times(1)).placeOrder();
    }

    @Test
    public void testInitiatePosPaymentFailure() throws Exception {
        when(adyenPaymentService.sendSyncPosPaymentRequest(eq(cartData), any(), eq(SERVICE_ID))).thenReturn(terminalApiResponse);
        when(posPaymentResponseConverter.convert(saleToPoiResponse)).thenReturn(paymentsResponse);
        //TerminalAPIUtil.getPaymentResult
        when(terminalApiResponse.getSaleToPOIResponse()).thenReturn(saleToPoiResponse);
        when(saleToPoiResponse.getPaymentResponse()).thenReturn(paymentResponse);
        when(paymentResponse.getResponse()).thenReturn(response);
        when(response.getResult()).thenReturn(ResultType.FAILURE);

        try {
            adyenCheckoutFacade.initiatePosPayment(request, cartData);
            fail("Expected AdyenNonAuthorizedPaymentException");
        } catch (AdyenNonAuthorizedPaymentException e) {
            assertEquals(terminalApiResponse, e.getTerminalApiResponse());
        }
    }

    @Test
    public void testInitiatePosPaymentBadRequest() throws Exception {
        when(adyenPaymentService.sendSyncPosPaymentRequest(eq(cartData), any(), eq(SERVICE_ID))).thenReturn(terminalApiResponse);
        when(posPaymentResponseConverter.convert(saleToPoiResponse)).thenReturn(paymentsResponse);
        //TerminalAPIUtil.getPaymentResult
        when(terminalApiResponse.getSaleToPOIResponse()).thenReturn(null);

        try {
            adyenCheckoutFacade.initiatePosPayment(request, cartData);
            fail("Expected AdyenNonAuthorizedPaymentException");
        } catch (AdyenNonAuthorizedPaymentException e) {
            assertEquals(terminalApiResponse, e.getTerminalApiResponse());
        }
    }

    @Test
    public void testCheckPosPaymentStatusSuccessAndPaymentSuccess() throws Exception {
        when(adyenPaymentService.sendSyncPosStatusRequest(cartData, SERVICE_ID)).thenReturn(terminalApiResponse);
        when(posPaymentResponseConverter.convert(saleToPoiResponse)).thenReturn(paymentsResponse);
        //TerminalAPIUtil.getStatusResult
        when(terminalApiResponse.getSaleToPOIResponse()).thenReturn(saleToPoiResponse);
        when(saleToPoiResponse.getTransactionStatusResponse()).thenReturn(transactionStatusResponse);
        when(transactionStatusResponse.getResponse()).thenReturn(statusResponse);
        when(statusResponse.getResult()).thenReturn(ResultType.SUCCESS);
        //TerminalAPIUtil.getPaymentResult
        when(saleToPoiResponse.getPaymentResponse()).thenReturn(null);
        when(transactionStatusResponse.getRepeatedMessageResponse()).thenReturn(repeatedMessageResponse);
        when(repeatedMessageResponse.getRepeatedResponseMessageBody()).thenReturn(repeatedResponseMessageBody);
        when(repeatedResponseMessageBody.getPaymentResponse()).thenReturn(paymentResponse);
        when(paymentResponse.getResponse()).thenReturn(response);
        when(response.getResult()).thenReturn(ResultType.SUCCESS);
        //TODO Receipt
        when(paymentResponse.getPaymentReceipt()).thenReturn(null);
        //createAuthorizedOrder
        when(cartService.getSessionCart()).thenReturn(new CartModel());
        when(adyenTransactionService.authorizeOrderModel(any(), any(), any())).thenReturn(new PaymentTransactionModel());
        //createOrderFromPaymentsResponse
        when(checkoutFacade.placeOrder()).thenReturn(orderData);
        when(orderRepository.getOrderModel(any())).thenReturn(new OrderModel());
        when(paymentsResponse.getAdditionalData()).thenReturn(new HashMap<>());
        //updateOrder
        doNothing().when(adyenOrderService).updateOrderFromPaymentsResponse(isA(OrderModel.class), isA(PaymentsResponse.class));

        OrderData orderDataResult = adyenCheckoutFacade.checkPosPaymentStatus(request, cartData);
        assertEquals(orderData, orderDataResult);
        verify(adyenPaymentService, times(1)).sendSyncPosStatusRequest(cartData, SERVICE_ID);
        verify(posPaymentResponseConverter, times(1)).convert(saleToPoiResponse);
        verify(adyenTransactionService, times(1)).authorizeOrderModel(any(), any(), any());
        verify(checkoutFacade, times(1)).placeOrder();
    }

    @Test
    public void testCheckPosPaymentStatusSuccessButPaymentFailure() throws Exception {
        when(adyenPaymentService.sendSyncPosStatusRequest(cartData, SERVICE_ID)).thenReturn(terminalApiResponse);
        when(posPaymentResponseConverter.convert(saleToPoiResponse)).thenReturn(paymentsResponse);
        //TerminalAPIUtil.getStatusResult
        when(terminalApiResponse.getSaleToPOIResponse()).thenReturn(saleToPoiResponse);
        when(saleToPoiResponse.getTransactionStatusResponse()).thenReturn(transactionStatusResponse);
        when(transactionStatusResponse.getResponse()).thenReturn(statusResponse);
        when(statusResponse.getResult()).thenReturn(ResultType.SUCCESS);
        //TerminalAPIUtil.getPaymentResult
        when(saleToPoiResponse.getPaymentResponse()).thenReturn(null);
        when(transactionStatusResponse.getRepeatedMessageResponse()).thenReturn(repeatedMessageResponse);
        when(repeatedMessageResponse.getRepeatedResponseMessageBody()).thenReturn(repeatedResponseMessageBody);
        when(repeatedResponseMessageBody.getPaymentResponse()).thenReturn(paymentResponse);
        when(paymentResponse.getResponse()).thenReturn(response);
        when(response.getResult()).thenReturn(ResultType.FAILURE);

        try {
            adyenCheckoutFacade.checkPosPaymentStatus(request, cartData);
            fail("Expected AdyenNonAuthorizedPaymentException");
        } catch (AdyenNonAuthorizedPaymentException e) {
            assertEquals(terminalApiResponse, e.getTerminalApiResponse());
        }
    }

    @Test
    public void testCheckPosPaymentStatusTimeout() throws Exception {
        when(adyenPaymentService.sendSyncPosStatusRequest(cartData, SERVICE_ID)).thenReturn(terminalApiResponse);
        when(posPaymentResponseConverter.convert(saleToPoiResponse)).thenReturn(paymentsResponse);
        //TerminalAPIUtil.getStatusResult
        when(terminalApiResponse.getSaleToPOIResponse()).thenReturn(saleToPoiResponse);
        when(saleToPoiResponse.getTransactionStatusResponse()).thenReturn(transactionStatusResponse);
        when(transactionStatusResponse.getResponse()).thenReturn(statusResponse);
        when(statusResponse.getResult()).thenReturn(ResultType.FAILURE);
        //TerminalAPIUtil.getErrorConditionForStatus
        when(statusResponse.getErrorCondition()).thenReturn(ErrorConditionType.IN_PROGRESS);
        //isPosTimedOut (will timeout after 5 seconds)
        long processStartTime = System.currentTimeMillis();
        when(request.getAttribute("paymentStartTime")).thenReturn(processStartTime);
        when(request.getAttribute("totalTimeout")).thenReturn(5);

        try {
            adyenCheckoutFacade.checkPosPaymentStatus(request, cartData);
            fail("Expected AdyenNonAuthorizedPaymentException");
        } catch (AdyenNonAuthorizedPaymentException e) {
            assertEquals(terminalApiResponse, e.getTerminalApiResponse());
            verify(adyenPaymentService, atLeast(2)).sendSyncPosStatusRequest(cartData, SERVICE_ID);
        }
    }

    @Test
    public void testCheckPosPaymentStatusError() throws Exception {
        when(adyenPaymentService.sendSyncPosStatusRequest(cartData, SERVICE_ID)).thenReturn(terminalApiResponse);
        when(posPaymentResponseConverter.convert(saleToPoiResponse)).thenReturn(paymentsResponse);
        //TerminalAPIUtil.getStatusResult
        when(terminalApiResponse.getSaleToPOIResponse()).thenReturn(saleToPoiResponse);
        when(saleToPoiResponse.getTransactionStatusResponse()).thenReturn(transactionStatusResponse);
        when(transactionStatusResponse.getResponse()).thenReturn(statusResponse);
        when(statusResponse.getResult()).thenReturn(ResultType.FAILURE);
        //TerminalAPIUtil.getErrorConditionForStatus
        when(statusResponse.getErrorCondition()).thenReturn(ErrorConditionType.CANCEL);

        try {
            adyenCheckoutFacade.checkPosPaymentStatus(request, cartData);
            fail("Expected AdyenNonAuthorizedPaymentException");
        } catch (AdyenNonAuthorizedPaymentException e) {
            assertEquals(terminalApiResponse, e.getTerminalApiResponse());
        }
    }
}
