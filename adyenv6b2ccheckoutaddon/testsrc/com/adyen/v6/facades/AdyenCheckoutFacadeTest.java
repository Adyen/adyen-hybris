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
import com.adyen.v6.converters.PaymentsResponseConverter;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.impl.DefaultAdyenCheckoutFacade;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.repository.OrderRepository;
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
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Collections;
import java.util.Map;

import static com.adyen.constants.ApiConstants.Redirect.Data.MD;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;
import static com.adyen.v6.facades.impl.DefaultAdyenCheckoutFacade.SESSION_LOCKED_CART;
import static de.hybris.platform.order.impl.DefaultCartService.SESSION_CART_PARAMETER_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenCheckoutFacadeTest {
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

    @InjectMocks
    private DefaultAdyenCheckoutFacade adyenCheckoutFacade;

    private CartModel cartModelMock;

    private PaymentResult paymentResultMock;
    private PaymentsResponse paymentsResponseMock;
    private PaymentsDetailsResponse paymentsDetailsResponseMock;

    private PaymentsResponse paymentsResponse;

    @Before
    public void setUp() throws SignatureException, InvalidCartException {
        BaseStoreModel baseStoreModelMock = mock(BaseStoreModel.class);
        cartModelMock = mock(CartModel.class);
        OrderData orderDataMock = mock(OrderData.class);
        paymentResultMock = mock(PaymentResult.class);
        paymentsResponseMock = mock(PaymentsResponse.class);
        paymentsDetailsResponseMock = mock(PaymentsDetailsResponse.class);
        CartData cartDataMock = mock(CartData.class);

        when(baseStoreModelMock.getAdyenMerchantAccount()).thenReturn("merchantAccount");
        when(baseStoreServiceMock.getCurrentBaseStore()).thenReturn(baseStoreModelMock);

        when(cartModelMock.getCode()).thenReturn("code");
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);

        when(orderDataMock.getCode()).thenReturn("code");
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);

        when(cartDataMock.getCode()).thenReturn("code");
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
        when(orderRepositoryMock.getOrderModel("code")).thenReturn(orderModelMock);
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(PAYMENT_METHOD_CC);

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

        //Lock the cart
        verify(sessionServiceMock).setAttribute(SESSION_LOCKED_CART, cartModelMock);
        verify(sessionServiceMock).removeAttribute(SESSION_CART_PARAMETER_NAME);


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
        OrderModel orderModelMock = mock(OrderModel.class);
        PaymentInfoModel paymentInfoModelMock = mock(PaymentInfoModel.class);

        //When payment is authorized
        when(paymentResultMock.isAuthorised()).thenReturn(true);
        when(sessionServiceMock.getAttribute(SESSION_LOCKED_CART)).thenReturn(cartModelMock);
        when(adyenPaymentServiceMock.authorise3DSPayment(anyMap())).thenReturn(paymentsDetailsResponseMock);
        when(orderRepositoryMock.getOrderModel("code")).thenReturn(orderModelMock);

        //When payment is authorized
        when(paymentsResponseMock.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.AUTHORISED);

        when(cartModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(paymentInfoModelMock.getAdyenPaymentMethod()).thenReturn(PAYMENT_METHOD_ONECLICK);

        adyenCheckoutFacade.handle3DSResponse(detailsMap);

        //the order should be created
        verifyAuthorized(orderModelMock);

        //When is not authorized
        when(paymentsResponseMock.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.REFUSED);

        try {
            adyenCheckoutFacade.handle3DSResponse(detailsMap);
            fail("Expecting exception");
        } catch (AdyenNonAuthorizedPaymentException e) {
            //throw exception with getPaymentsResponse details
            assertEquals(paymentsResponseMock, e.getPaymentsResponse());
        }

        try {
            adyenCheckoutFacade.handle3DSResponse(detailsMap);
            //throw SignatureException
            fail("Expecting exception");
        } catch (SignatureException ignored) {
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
        cartData.setCode("code");
        cartData.setDeliveryAddress(deliveryAddress);

        return cartData;
    }

    private void verifyAuthorized(OrderModel orderModelMock) throws InvalidCartException {
        //authorized transactions should be stored
        verify(adyenTransactionServiceMock).authorizeOrderModel(cartModelMock, "code", "pspRef");
        //order should be created
        verify(checkoutFacadeMock).placeOrder();
        //update of order metadata should happen
        verify(adyenOrderServiceMock).updateOrderFromPaymentsResponse(eq(orderModelMock), isA(PaymentsResponse.class));
    }
}
