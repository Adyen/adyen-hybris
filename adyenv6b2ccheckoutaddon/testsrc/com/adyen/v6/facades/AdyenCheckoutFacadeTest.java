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
package com.adyen.v6.facades;

import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.CheckoutPaymentsAction;
import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.v6.constants.Adyenv6coreConstants;
import com.adyen.v6.converters.PaymentsDetailsResponseConverter;
import com.adyen.v6.converters.PaymentsResponseConverter;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.impl.DefaultAdyenCheckoutFacade;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenBusinessProcessService;
import com.adyen.v6.service.AdyenOrderService;
import com.adyen.v6.service.AdyenTransactionService;
import com.adyen.v6.service.DefaultAdyenPaymentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Collections;
import java.util.Map;

import static com.adyen.constants.ApiConstants.Redirect.Data.MD;
import static com.adyen.v6.constants.Adyenv6coreConstants.*;
import static com.adyen.v6.facades.impl.DefaultAdyenCheckoutFacade.SESSION_LOCKED_CART;
import static com.adyen.v6.model.RequestInfo.ACCEPT_HEADER;
import static com.adyen.v6.model.RequestInfo.USER_AGENT_HEADER;
import static de.hybris.platform.order.impl.DefaultCartService.SESSION_CART_PARAMETER_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenCheckoutFacadeTest {
    public static final String CODE = "code";

    @Spy
    @InjectMocks
    private DefaultAdyenCheckoutFacade adyenCheckoutFacade;

    @Mock
    private BaseStoreService baseStoreServiceMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private OrderFacade orderFacadeMock;
    @Mock
    private CheckoutFacade checkoutFacadeMock;
    @Mock
    private DefaultAdyenPaymentService adyenPaymentServiceMock;
    @Mock
    private AdyenTransactionService adyenTransactionServiceMock;
    @Mock
    private OrderRepository orderRepositoryMock;
    @Mock
    private AdyenOrderService adyenOrderServiceMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private AdyenPaymentServiceFactory adyenPaymentServiceFactoryMock;
    @Mock
    private CommonI18NService commonI18NServiceMock;
    @Mock
    private KeyGenerator keyGeneratorMock;
    @Mock
    private PaymentsDetailsResponseConverter paymentsDetailsResponseConverterMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private AdyenBusinessProcessService adyenBusinessProcessServiceMock;
    @Mock
    private Converter<OrderModel, OrderData> orderConverterMock;
    @Mock
    private CartFactory cartFactoryMock;
    @Mock
    private CalculationService calculationServiceMock;

    @Mock
    private OrderModel orderModelMock;
    private CartModel cartModelMock;

    private PaymentResult paymentResultMock;
    private PaymentsResponse paymentsResponseMock;
    private PaymentsDetailsResponse paymentsDetailsResponseMock;

    private PaymentsResponse paymentsResponse;

    @Before
    public void setUp() throws SignatureException, InvalidCartException, CalculationException {
        BaseStoreModel baseStoreModelMock = mock(BaseStoreModel.class);
        cartModelMock = mock(CartModel.class);
        OrderData orderDataMock = mock(OrderData.class);
        paymentResultMock = mock(PaymentResult.class);
        paymentsResponseMock = mock(PaymentsResponse.class);
        paymentsDetailsResponseMock = mock(PaymentsDetailsResponse.class);
        CartData cartDataMock = mock(CartData.class);

        doNothing().when(calculationServiceMock).calculate(cartModelMock);

        doReturn(orderDataMock).when(orderConverterMock).convert(orderModelMock);
        when(baseStoreModelMock.getAdyenMerchantAccount()).thenReturn("merchantAccount");
        when(baseStoreServiceMock.getCurrentBaseStore()).thenReturn(baseStoreModelMock);

        when(cartModelMock.getCode()).thenReturn(CODE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartFactoryMock.createCart()).thenReturn(cartModelMock);

        when(orderDataMock.getCode()).thenReturn(CODE);
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);

        when(cartDataMock.getCode()).thenReturn(CODE);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);

        when(paymentResultMock.getPspReference()).thenReturn("pspRef");
        when(paymentResultMock.getMd()).thenReturn("md");

        when(paymentsResponseMock.getPspReference()).thenReturn("pspRef");
        paymentsResponse = new PaymentsResponse();
        paymentsResponse.setPspReference("pspRef");
        CheckoutPaymentsAction action = new CheckoutPaymentsAction();
        action.setData(Collections.singletonMap(MD, "md"));
        action.setPaymentData("This is test payment data");
        paymentsResponse.setAction(action);

        when(adyenPaymentServiceFactoryMock.createFromBaseStore(baseStoreModelMock)).thenReturn(adyenPaymentServiceMock);

        LanguageModel languageModel = new LanguageModel();
        languageModel.setIsocode("en");

        when(commonI18NServiceMock.getCurrentLanguage()).thenReturn(languageModel);
        when(keyGeneratorMock.generate()).thenReturn(new Object());
        adyenCheckoutFacade.setPaymentsResponseConverter(new PaymentsResponseConverter());
        adyenCheckoutFacade.setPaymentsDetailsResponseConverter(paymentsDetailsResponseConverterMock);
        adyenCheckoutFacade.setOrderConverter(orderConverterMock);
    }

    @Test
    public void testRestoreSessionCart() throws InvalidCartException {
        when(sessionServiceMock.getAttribute(SESSION_LOCKED_CART)).thenReturn(null);
        try {
            adyenCheckoutFacade.restoreSessionCart();
            fail("Expecting exception");
        } catch (InvalidCartException ignored) {
        }

        when(sessionServiceMock.getAttribute(SESSION_LOCKED_CART)).thenReturn(cartModelMock);
        adyenCheckoutFacade.restoreSessionCart();

        verify(cartServiceMock).setSessionCart(cartModelMock);
        verify(sessionServiceMock).removeAttribute(SESSION_LOCKED_CART);
    }

    @Test
    public void testLockSessionCart() {
        when(sessionServiceMock.getAttribute(SESSION_LOCKED_CART)).thenReturn(null);

        adyenCheckoutFacade.lockSessionCart();

        verify(sessionServiceMock).setAttribute(SESSION_LOCKED_CART, cartModelMock);
        verify(sessionServiceMock).removeAttribute(SESSION_CART_PARAMETER_NAME);
    }

    @Test
    public void testAuthorizeCardPayment() throws Exception {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        CartData cartDataMock = mock(CartData.class);
        OrderModel orderModelMock = mock(OrderModel.class);

        //When payment is authorized
        paymentsResponse.setResultCode(PaymentsResponse.ResultCodeEnum.AUTHORISED);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(null);
        when(adyenPaymentServiceMock.authorisePayment(any(CartData.class), any(RequestInfo.class), any(CustomerModel.class))).thenReturn(paymentsResponse);
        when(orderRepositoryMock.getOrderModel(CODE)).thenReturn(orderModelMock);
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(PAYMENT_METHOD_CC);
        when(requestMock.getHeader(USER_AGENT_HEADER)).thenReturn("userAgent");
        when(requestMock.getHeader(ACCEPT_HEADER)).thenReturn("acceptHeader");
        when(requestMock.getRemoteAddr()).thenReturn("addr");
        when(requestMock.getRequestURI()).thenReturn("uri");
        when(requestMock.getRequestURL()).thenReturn(new StringBuffer("url"));

        adyenCheckoutFacade.authorisePayment(requestMock, cartDataMock);

        verifyAuthorized(orderModelMock);

        //When payment is 3D secure
        paymentsResponse.setResultCode(PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER);

        try {
            adyenCheckoutFacade.authorisePayment(requestMock, cartDataMock);
            fail("Expecting exception");
        } catch (AdyenNonAuthorizedPaymentException e) {
            //throw exception with paymentResult details
            assertEquals(paymentsResponse, e.getPaymentsResponse());
        }

        //When payment is refused
        paymentsResponse.setResultCode(PaymentsResponse.ResultCodeEnum.REFUSED);

        try {
            adyenCheckoutFacade.authorisePayment(requestMock, cartDataMock);
            fail("Expecting exception");
        } catch (AdyenNonAuthorizedPaymentException e) {
            //throw exception with paymentResult details
            assertEquals(paymentsResponse, e.getPaymentsResponse());
        }
    }

    @Test
    public void testHandle3DResponse() throws Exception {
        Map detailsMap = mock(Map.class);
        PaymentInfoModel paymentInfoModelMock = mock(PaymentInfoModel.class);

        //When payment is authorized
        when(paymentResultMock.isAuthorised()).thenReturn(true);
        when(sessionServiceMock.getAttribute(SESSION_LOCKED_CART)).thenReturn(cartModelMock);
        when(adyenPaymentServiceMock.authorise3DSPayment(detailsMap)).thenReturn(paymentsDetailsResponseMock);
        when(orderRepositoryMock.getOrderModel(CODE)).thenReturn(orderModelMock);
        when(paymentsResponseMock.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.AUTHORISED);
        when(paymentsDetailsResponseMock.getMerchantReference()).thenReturn(CODE);
        when(paymentsDetailsResponseConverterMock.convert(paymentsDetailsResponseMock)).thenReturn(paymentsResponseMock);
        when(paymentsDetailsResponseMock.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.AUTHORISED);
        when(cartModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(paymentInfoModelMock.getAdyenPaymentMethod()).thenReturn(PAYMENT_METHOD_ONECLICK);
        doNothing().when(adyenBusinessProcessServiceMock).triggerOrderProcessEvent(orderModelMock, Adyenv6coreConstants.PROCESS_EVENT_ADYEN_PAYMENT_RESULT);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);

        adyenCheckoutFacade.handle3DSResponse(detailsMap);

        verify(adyenPaymentServiceMock).authorise3DSPayment(detailsMap);

        //When is not authorized
        when(paymentsDetailsResponseMock.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.REFUSED);

        try {
            adyenCheckoutFacade.handle3DSResponse(detailsMap);
            fail("Expecting exception");
        } catch (AdyenNonAuthorizedPaymentException e) {
            //throw exception with getPaymentsResponse details
            assertEquals(paymentsDetailsResponseMock, e.getPaymentsDetailsResponse());
        }
    }

    private CartData createCartData() {
        CartData cartData = new CartData();

        PriceData priceData = new PriceData();
        priceData.setValue(new BigDecimal("12.34"));
        priceData.setCurrencyIso("EUR");

        AddressData deliveryAddress = new AddressData();
        deliveryAddress.setCountry(new CountryData());

        cartData.setTotalPrice(priceData);
        cartData.setCode(CODE);
        cartData.setDeliveryAddress(deliveryAddress);

        return cartData;
    }

    private void verifyAuthorized(OrderModel orderModelMock) throws InvalidCartException {
        //authorized transactions should be stored
        verify(adyenTransactionServiceMock).authorizeOrderModel(cartModelMock, CODE, "pspRef");
        //order should be created
        verify(checkoutFacadeMock).placeOrder();
        //update of order metadata should happen
        verify(adyenOrderServiceMock).updateOrderFromPaymentsResponse(eq(orderModelMock), isA(PaymentsResponse.class));
    }
}
