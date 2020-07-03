package com.adyen.v6.facade;

import com.adyen.model.checkout.InputDetail;
import com.adyen.model.checkout.Item;
import com.adyen.model.checkout.PaymentMethod;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.nexo.DocumentQualifierType;
import com.adyen.model.nexo.ErrorConditionType;
import com.adyen.model.nexo.OutputContent;
import com.adyen.model.nexo.OutputText;
import com.adyen.model.nexo.PaymentReceipt;
import com.adyen.model.nexo.PaymentResponse;
import com.adyen.model.nexo.RepeatedMessageResponse;
import com.adyen.model.nexo.RepeatedResponseMessageBody;
import com.adyen.model.nexo.Response;
import com.adyen.model.nexo.ResultType;
import com.adyen.model.nexo.SaleToPOIResponse;
import com.adyen.model.nexo.TransactionStatusResponse;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.v6.constants.Adyenv6coreConstants;
import com.adyen.v6.converters.PosPaymentResponseConverter;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
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
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_EPS;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.MODEL_ISSUER_LISTS;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.MODEL_SELECTED_PAYMENT_METHOD;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.SESSION_MD;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.SESSION_PAYMENT_DATA;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.THREE_D_MD;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.THREE_D_PARES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenCheckoutFacadeTest {

    private static final String MD = "md";
    private static final String MERCHANT_REFERENCE = "merchantReference";
    private static final String PA_RES = "paRes";
    private static final String PAYMENT_DATA = "paymentData";
    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String SERVICE_ID = "serviceId";
    private static final String URL = "url";

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
    CommonI18NService commonI18NService;
    @Mock
    ModelService modelService;
    @Mock
    SessionService sessionService;
    @Mock
    Converter<CountryModel, CountryData> countryConverter;
    @Mock
    Converter<OrderModel, OrderData> orderConverter;
    @Mock
    CartFactory cartFactory;

    @Mock
    HttpServletRequest request;
    @Mock
    CartData cartData;
    @Mock
    CartModel cartModel;
    @Mock
    PaymentsResponse paymentsResponse;
    @Mock
    OrderData orderData;
    @Mock
    OrderModel orderModel;
    @Mock
    BaseStoreModel baseStore;
    @Mock
    PriceData priceData;
    @Mock
    AddressData addressData;
    @Mock
    CountryData countryData;

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
    //Receipts
    @Mock
    PaymentReceipt receipt;
    @Mock
    OutputContent outputContent;
    @Mock
    OutputText textWithNameValue;
    @Mock
    OutputText textJustName;
    @Mock
    OutputText textJustValue;

    @Before
    public void setUp() {
        when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(true);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(new CustomerModel());
        when(request.getAttribute("originalServiceId")).thenReturn(SERVICE_ID);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
        when(adyenPaymentServiceFactory.createFromBaseStore(any())).thenReturn(adyenPaymentService);
    }

    @Test
    public void testInitializeEpsCheckoutData() throws Exception {
        when(checkoutFacade.getCheckoutCart()).thenReturn(cartData);
        when(baseStore.getAdyenPosEnabled()).thenReturn(false);
        when(cartData.getTotalPrice()).thenReturn(priceData);
        when(priceData.getValue()).thenReturn(BigDecimal.TEN);
        when(priceData.getCurrencyIso()).thenReturn("EUR");
        when(cartData.getDeliveryAddress()).thenReturn(addressData);
        when(addressData.getCountry()).thenReturn(countryData);
        when(countryData.getIsocode()).thenReturn("NL");
        when(commonI18NService.getCurrentLanguage()).thenReturn(null);
        when(adyenPaymentService.getPaymentMethodsResponse(any(), any(), any(), any(), any())).thenReturn(createEpsPaymentMethodsResponse());
        when(baseStore.getAdyenRecurringContractMode()).thenReturn(RecurringContractMode.NONE);
        when(cartService.getSessionCart()).thenReturn(new CartModel());
        when(cartData.getAdyenPaymentMethod()).thenReturn(PAYMENT_METHOD_EPS);

        Model model = new ExtendedModelMap();
        adyenCheckoutFacade.initializeCheckoutData(model);

        verify(modelService).save(any());
        assertTrue(model.containsAttribute(MODEL_SELECTED_PAYMENT_METHOD));
        assertEquals(PAYMENT_METHOD_EPS, model.asMap().get(MODEL_SELECTED_PAYMENT_METHOD));
        assertTrue(model.containsAttribute(MODEL_ISSUER_LISTS));
        assertTrue(((Map<String, String>) model.asMap().get(MODEL_ISSUER_LISTS)).containsKey(PAYMENT_METHOD_EPS));
    }

    private PaymentMethodsResponse createEpsPaymentMethodsResponse() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(PAYMENT_METHOD_EPS);
        InputDetail detail = new InputDetail();
        detail.setKey("issuer");
        detail.setType("select");
        Item item = new Item();
        item.setId(UUID.randomUUID().toString());
        item.setName("FakeIssuer");
        detail.addItemsItem(item);
        paymentMethod.addDetailsItem(detail);

        PaymentMethodsResponse response = new PaymentMethodsResponse();
        response.setPaymentMethods(Collections.singletonList(paymentMethod));

        return response;
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

        setSuccessfulPaymentStubs();

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

        setSuccessfulPaymentStubs();

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
        //isPosTimedOut (will timeout after 10 seconds)
        long processStartTime = System.currentTimeMillis();
        when(request.getAttribute("paymentStartTime")).thenReturn(processStartTime);
        when(request.getAttribute("totalTimeout")).thenReturn(10);

        AdyenCheckoutFacade adyenCheckoutFacadeSpy = spy(adyenCheckoutFacade);
        try {
            adyenCheckoutFacadeSpy.checkPosPaymentStatus(request, cartData);
            fail("Expected AdyenNonAuthorizedPaymentException");
        } catch (AdyenNonAuthorizedPaymentException e) {
            assertEquals(terminalApiResponse, e.getTerminalApiResponse());
            verify(adyenCheckoutFacadeSpy, atLeast(2)).checkPosPaymentStatus(request, cartData);
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


    private void setSuccessfulPaymentStubs() throws InvalidCartException {
        //Receipt
        when(paymentResponse.getPaymentReceipt()).thenReturn(Collections.singletonList(receipt));
        when(receipt.getDocumentQualifier()).thenReturn(DocumentQualifierType.CUSTOMER_RECEIPT);
        when(receipt.getOutputContent()).thenReturn(outputContent);
        when(outputContent.getOutputText()).thenReturn(Arrays.asList(textWithNameValue, textJustName, textJustValue));
        when(textWithNameValue.getText()).thenReturn("name=some_name&value=some_value");
        when(textJustName.getText()).thenReturn("name=some_name");
        when(textJustValue.getText()).thenReturn("value=some_value");
        //createAuthorizedOrder
        when(cartService.getSessionCart()).thenReturn(new CartModel());
        when(adyenTransactionService.authorizeOrderModel(any(), any(), any())).thenReturn(new PaymentTransactionModel());
        //createOrderFromPaymentsResponse
        when(checkoutFacade.placeOrder()).thenReturn(orderData);
        when(orderRepository.getOrderModel(any())).thenReturn(new OrderModel());
        //updateOrder
        doNothing().when(adyenOrderService).updateOrderFromPaymentsResponse(any(), any());
    }

    @Test
    public void testAuthorisePaymentRedirect() throws Exception {
        doNothing().when(sessionService).removeAttribute(any());
        when(cartData.getAdyenPaymentMethod()).thenReturn(PAYMENT_METHOD);
        when(commonI18NService.getCurrentLanguage()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer(URL));
        when(request.getRequestURI()).thenReturn(URL);
        when(adyenPaymentService.authorisePayment(any(), any(), any())).thenReturn(paymentsResponse);
        when(paymentsResponse.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER);
        when(checkoutFacade.placeOrder()).thenReturn(orderData);
        when(orderRepository.getOrderModel(any())).thenReturn(orderModel);
        doNothing().when(modelService).save(any());

        try {
            adyenCheckoutFacade.authorisePayment(request, cartData);
            fail("Expected AdyenNonAuthorizedPaymentException");
        } catch (AdyenNonAuthorizedPaymentException e) {
            verify(adyenPaymentService, times(1)).authorisePayment(eq(cartData), any(), any());
            verify(checkoutFacade, times(1)).placeOrder();
            assertNotNull(e.getPaymentsResponse());
            assertEquals(PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER, e.getPaymentsResponse().getResultCode());
        }
    }

    @Test
    public void testHandle3DResponseAuthorised() throws Exception {
        when(request.getParameter(THREE_D_PARES)).thenReturn(PA_RES);
        when(request.getParameter(THREE_D_MD)).thenReturn(MD);
        when(sessionService.getAttribute(SESSION_MD)).thenReturn(MD);
        when(sessionService.getAttribute(SESSION_PAYMENT_DATA)).thenReturn(PAYMENT_DATA);
        when(adyenPaymentService.authorise3DPayment(any(), any(), any())).thenReturn(paymentsResponse);
        when(paymentsResponse.getMerchantReference()).thenReturn(MERCHANT_REFERENCE);
        when(orderRepository.getOrderModel(any())).thenReturn(orderModel);
        doNothing().when(sessionService).removeAttribute(any());
        when(adyenTransactionService.createPaymentTransactionFromResultCode(any(), any(), any(), any())).thenReturn(new PaymentTransactionModel());
        doNothing().when(adyenOrderService).updateOrderFromPaymentsResponse(any(), any());
        when(paymentsResponse.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.AUTHORISED);
        doNothing().when(modelService).save(any());
        when(orderConverter.convert(any())).thenReturn(orderData);

        OrderData orderDataResult = adyenCheckoutFacade.handle3DResponse(request);
        assertEquals(orderData, orderDataResult);
        verify(adyenPaymentService, times(1)).authorise3DPayment(PAYMENT_DATA, PA_RES, MD);
        verify(orderRepository, times(1)).getOrderModel(MERCHANT_REFERENCE);
        verify(orderConverter, times(1)).convert(orderModel);
    }

    @Test
    public void testHandle3DResponseError() throws Exception {
        when(request.getParameter(THREE_D_PARES)).thenReturn(PA_RES);
        when(request.getParameter(THREE_D_MD)).thenReturn(MD);
        when(sessionService.getAttribute(SESSION_MD)).thenReturn(MD);
        when(sessionService.getAttribute(SESSION_PAYMENT_DATA)).thenReturn(PAYMENT_DATA);
        when(adyenPaymentService.authorise3DPayment(any(), any(), any())).thenReturn(paymentsResponse);
        when(paymentsResponse.getMerchantReference()).thenReturn(MERCHANT_REFERENCE);
        when(orderRepository.getOrderModel(any())).thenReturn(orderModel);
        doNothing().when(sessionService).removeAttribute(any());
        when(adyenTransactionService.createPaymentTransactionFromResultCode(any(), any(), any(), any())).thenReturn(new PaymentTransactionModel());
        doNothing().when(adyenOrderService).updateOrderFromPaymentsResponse(any(), any());
        when(paymentsResponse.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.REFUSED);
        doNothing().when(modelService).save(any());
        when(cartFactory.createCart()).thenReturn(cartModel);
        doNothing().when(cartService).setSessionCart(any());

        try {
            adyenCheckoutFacade.handle3DResponse(request);
            fail("Expected AdyenNonAuthorizedPaymentException");
        } catch (AdyenNonAuthorizedPaymentException e) {
            verify(adyenPaymentService, times(1)).authorise3DPayment(PAYMENT_DATA, PA_RES, MD);
            verify(orderRepository, times(2)).getOrderModel(MERCHANT_REFERENCE);
            verify(cartFactory, times(1)).createCart();
            verify(cartService, times(1)).setSessionCart(cartModel);
        }
    }

    @Test
    public void testHandle3DResponseWrongSignature() throws Exception {
        when(request.getParameter(THREE_D_PARES)).thenReturn(PA_RES);
        when(request.getParameter(THREE_D_MD)).thenReturn(MD);
        when(sessionService.getAttribute(SESSION_MD)).thenReturn("different_" + MD);
        when(sessionService.getAttribute(SESSION_PAYMENT_DATA)).thenReturn(PAYMENT_DATA);

        try {
            adyenCheckoutFacade.handle3DResponse(request);
            fail("Expected SignatureException");
        } catch (SignatureException e) {
            assertEquals("MD does not match!", e.getMessage());
        }
    }

    @Test
    public void testHandleRedirectPayloadAuthorised() throws Exception {
        when(sessionService.getAttribute(Adyenv6coreConstants.PAYMENT_METHOD)).thenReturn(PAYMENT_METHOD);
        when(adyenPaymentService.getPaymentDetailsFromPayload(any())).thenReturn(paymentsResponse);
        when(paymentsResponse.getMerchantReference()).thenReturn(MERCHANT_REFERENCE);
        when(orderRepository.getOrderModel(any())).thenReturn(orderModel);
        doNothing().when(sessionService).removeAttribute(any());
        when(paymentsResponse.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.AUTHORISED);
        when(adyenTransactionService.createPaymentTransactionFromResultCode(any(), any(), any(), any())).thenReturn(new PaymentTransactionModel());
        doNothing().when(adyenOrderService).updateOrderFromPaymentsResponse(any(), any());

        HashMap<String, String> details = new HashMap<>();
        PaymentsResponse paymentsResponseReturned = adyenCheckoutFacade.handleRedirectPayload(details);
        assertEquals(paymentsResponseReturned, paymentsResponse);
        assertEquals(PaymentsResponse.ResultCodeEnum.AUTHORISED, paymentsResponseReturned.getResultCode());
        verify(adyenPaymentService, times(1)).getPaymentDetailsFromPayload(details);
        verify(orderRepository, times(1)).getOrderModel(MERCHANT_REFERENCE);
    }

    @Test
    public void testHandleRedirectPayloadNotAuthorised() throws Exception {
        when(sessionService.getAttribute(Adyenv6coreConstants.PAYMENT_METHOD)).thenReturn(PAYMENT_METHOD);
        when(adyenPaymentService.getPaymentDetailsFromPayload(any())).thenReturn(paymentsResponse);
        when(paymentsResponse.getMerchantReference()).thenReturn(MERCHANT_REFERENCE);
        when(orderRepository.getOrderModel(any())).thenReturn(orderModel);
        doNothing().when(sessionService).removeAttribute(any());
        when(paymentsResponse.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.REFUSED);
        doNothing().when(modelService).save(any());
        when(cartFactory.createCart()).thenReturn(cartModel);
        doNothing().when(cartService).setSessionCart(any());
        when(adyenTransactionService.createPaymentTransactionFromResultCode(any(), any(), any(), any())).thenReturn(new PaymentTransactionModel());
        doNothing().when(adyenOrderService).updateOrderFromPaymentsResponse(any(), any());

        HashMap<String, String> details = new HashMap<>();
        PaymentsResponse paymentsResponseReturned = adyenCheckoutFacade.handleRedirectPayload(details);
        assertEquals(paymentsResponseReturned, paymentsResponse);
        assertEquals(PaymentsResponse.ResultCodeEnum.REFUSED, paymentsResponseReturned.getResultCode());
        verify(adyenPaymentService, times(1)).getPaymentDetailsFromPayload(details);
        verify(orderRepository, times(2)).getOrderModel(MERCHANT_REFERENCE);
    }
}
