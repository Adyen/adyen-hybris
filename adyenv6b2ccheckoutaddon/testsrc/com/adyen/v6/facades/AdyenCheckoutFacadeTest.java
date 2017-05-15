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

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.adyen.Util.HMACValidator;
import com.adyen.constants.HPPConstants;
import com.adyen.model.PaymentResult;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
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
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import static com.adyen.constants.HPPConstants.Fields.CURRENCY_CODE;
import static com.adyen.constants.HPPConstants.Fields.PAYMENT_AMOUNT;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.SESSION_LOCKED_CART;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.SESSION_MD;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.THREE_D_MD;
import static com.adyen.v6.facades.DefaultAdyenCheckoutFacade.THREE_D_PARES;
import static de.hybris.platform.order.impl.DefaultCartService.SESSION_CART_PARAMETER_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
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
    private HMACValidator hmacValidatorMock;

    @Mock
    private AdyenPaymentServiceFactory adyenPaymentServiceFactoryMock;

    @InjectMocks
    private DefaultAdyenCheckoutFacade adyenCheckoutFacade;

    private CartModel cartModelMock;

    private PaymentResult paymentResultMock;

    @Before
    public void setUp() throws SignatureException, InvalidCartException {
        BaseStoreModel baseStoreModelMock = mock(BaseStoreModel.class);
        cartModelMock = mock(CartModel.class);
        OrderData orderDataMock = mock(OrderData.class);
        paymentResultMock = mock(PaymentResult.class);

        when(baseStoreModelMock.getAdyenSkinHMAC()).thenReturn("hmacKey");
        when(baseStoreModelMock.getAdyenMerchantAccount()).thenReturn("merchantAccount");
        when(baseStoreModelMock.getAdyenSkinCode()).thenReturn("skinCode");
        when(baseStoreServiceMock.getCurrentBaseStore()).thenReturn(baseStoreModelMock);

        when(hmacValidatorMock.calculateHMAC(null, "hmacKey")).thenReturn("merchantSig");

        when(cartModelMock.getCode()).thenReturn("code");
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);

        when(orderDataMock.getCode()).thenReturn("code");
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);

        when(paymentResultMock.getPspReference()).thenReturn("pspRef");
        when(paymentResultMock.getMd()).thenReturn("md");

        when(adyenPaymentServiceFactoryMock.createFromBaseStore(baseStoreModelMock)).thenReturn(adyenPaymentServiceMock);
    }

    @Test
    public void testRestoreSessionCart() throws InvalidCartException {
        when(sessionServiceMock.getAttribute(SESSION_LOCKED_CART)).thenReturn(null);
        try {
            adyenCheckoutFacade.restoreSessionCart();
            fail("Expecting exception");
        } catch (InvalidCartException e) {
        }

        when(sessionServiceMock.getAttribute(SESSION_LOCKED_CART)).thenReturn(cartModelMock);
        adyenCheckoutFacade.restoreSessionCart();

        verify(cartServiceMock).setSessionCart(cartModelMock);
    }

    @Test
    public void testValidateHPPResponse() throws NoSuchAlgorithmException, SignatureException {
        SortedMap<String, String> hppResponseData = new TreeMap<String, String>();

        adyenCheckoutFacade.validateHPPResponse(hppResponseData, "merchantSig");

        try {
            adyenCheckoutFacade.validateHPPResponse(hppResponseData, "wrongMerchantSig");

            fail("Expected exception!");
        } catch (SignatureException e) {
        }
    }

    @Test
    public void testHandleHPPResponse() throws InvalidCartException, SignatureException {
        //Case no Cart in session
        when(sessionServiceMock.getAttribute(SESSION_LOCKED_CART)).thenReturn(null);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getParameter(HPPConstants.Response.AUTH_RESULT)).thenReturn(HPPConstants.Response.AUTH_RESULT_AUTHORISED);
        when(requestMock.getParameter(HPPConstants.Response.MERCHANT_REFERENCE)).thenReturn("merchantReference");
        when(requestMock.getParameter(HPPConstants.Response.PAYMENT_METHOD)).thenReturn("paymentMethod");
        when(requestMock.getParameter(HPPConstants.Response.PSP_REFERENCE)).thenReturn("pspReference");
        when(requestMock.getParameter(HPPConstants.Response.SHOPPER_LOCALE)).thenReturn("shopperLocale");
        when(requestMock.getParameter(HPPConstants.Response.SKIN_CODE)).thenReturn("skinCode");
        when(requestMock.getParameter(HPPConstants.Response.MERCHANT_SIG)).thenReturn("merchantSig");

        OrderData existingOrderDataMock = mock(OrderData.class);
        when(orderFacadeMock.getOrderDetailsForCode("merchantReference")).thenReturn(existingOrderDataMock);

        OrderData newOrderDataMock = mock(OrderData.class);

        OrderData orderData = adyenCheckoutFacade.handleHPPResponse(requestMock);
        assertEquals(existingOrderDataMock, orderData);

        //Case no order yet created
        when(sessionServiceMock.getAttribute(SESSION_LOCKED_CART)).thenReturn(cartModelMock);
        when(checkoutFacadeMock.placeOrder()).thenReturn(newOrderDataMock);

        orderData = adyenCheckoutFacade.handleHPPResponse(requestMock);
        assertEquals(newOrderDataMock, orderData);

        //Case cancelled
        when(requestMock.getParameter(HPPConstants.Response.AUTH_RESULT)).thenReturn(HPPConstants.Response.AUTH_RESULT_CANCELLED);

        orderData = adyenCheckoutFacade.handleHPPResponse(requestMock);
        assertEquals(null, orderData);
    }

    @Test
    public void testAuthorizeCardPayment() throws Exception {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        CartData cartDataMock = mock(CartData.class);
        OrderModel orderModelMock = mock(OrderModel.class);

        //When payment is authorized
        when(paymentResultMock.isAuthorised()).thenReturn(true);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(null);
        when(adyenPaymentServiceMock.authorise(cartDataMock, requestMock, null)).thenReturn(paymentResultMock);
        when(orderRepositoryMock.getOrderModel("code")).thenReturn(orderModelMock);

        adyenCheckoutFacade.authoriseCardPayment(requestMock, cartDataMock);

        verifyAuthorized(orderModelMock);


        //When payment is 3D secure
        when(paymentResultMock.isAuthorised()).thenReturn(false);
        when(paymentResultMock.isRedirectShopper()).thenReturn(true);

        try {
            adyenCheckoutFacade.authoriseCardPayment(requestMock, cartDataMock);
            fail("Expecting exception");
        } catch (AdyenNonAuthorizedPaymentException e) {
            //throw exception with paymentResult details
            assertEquals(paymentResultMock, e.getPaymentResult());
        }

        //store MD to session
        verify(sessionServiceMock).setAttribute(SESSION_MD, "md");
        //Lock the cart
        verify(sessionServiceMock).setAttribute(SESSION_LOCKED_CART, cartModelMock);
        verify(sessionServiceMock).removeAttribute(SESSION_CART_PARAMETER_NAME);


        //When payment is refused
        when(paymentResultMock.isRedirectShopper()).thenReturn(false);

        try {
            adyenCheckoutFacade.authoriseCardPayment(requestMock, cartDataMock);
            fail("Expecting exception");
        } catch (AdyenNonAuthorizedPaymentException e) {
            //throw exception with paymentResult details
            assertEquals(paymentResultMock, e.getPaymentResult());
        }
    }

    @Test
    public void testHandle3DResponse() throws Exception {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        OrderModel orderModelMock = mock(OrderModel.class);

        when(requestMock.getParameter(THREE_D_PARES)).thenReturn("PaRes");
        when(requestMock.getParameter(THREE_D_MD)).thenReturn("md");

        //When payment is authorized
        when(paymentResultMock.isAuthorised()).thenReturn(true);
        when(sessionServiceMock.getAttribute(SESSION_MD)).thenReturn("md");
        when(sessionServiceMock.getAttribute(SESSION_LOCKED_CART)).thenReturn(cartModelMock);
        when(adyenPaymentServiceMock.authorise3D(requestMock, "PaRes", "md")).thenReturn(paymentResultMock);
        when(orderRepositoryMock.getOrderModel("code")).thenReturn(orderModelMock);

        adyenCheckoutFacade.handle3DResponse(requestMock);

        //the order should be created
        verifyAuthorized(orderModelMock);


        //When is not authorized
        when(paymentResultMock.isAuthorised()).thenReturn(false);

        try {
            adyenCheckoutFacade.handle3DResponse(requestMock);
            fail("Expecting exception");
        } catch (AdyenNonAuthorizedPaymentException e) {
            //throw exception with paymentResult details
            assertEquals(paymentResultMock, e.getPaymentResult());
        }


        //When the MD is different
        when(sessionServiceMock.getAttribute(SESSION_MD)).thenReturn("differentMd");

        try {
            adyenCheckoutFacade.handle3DResponse(requestMock);
            //throw SignatureException
            fail("Expecting exception");
        } catch (SignatureException e) {
        }
    }

    @Test
    public void testInitializeHostedPayment() throws SignatureException {
        CartData cartDataMock = mock(CartData.class);

        PriceData priceData = new PriceData();
        priceData.setValue(new BigDecimal("12.34"));
        priceData.setCurrencyIso("EUR");

        AddressData deliveryAddress = new AddressData();
        deliveryAddress.setCountry(new CountryData());

        when(cartDataMock.getTotalPrice()).thenReturn(priceData);
        when(cartDataMock.getCode()).thenReturn("code");
        when(cartDataMock.getDeliveryAddress()).thenReturn(deliveryAddress);

        Map<String, String> hppFormData = adyenCheckoutFacade.initializeHostedPayment(cartDataMock, "redirectUrl");

        assertEquals("1234", hppFormData.get(PAYMENT_AMOUNT));
        assertEquals("EUR", hppFormData.get(CURRENCY_CODE));
    }

    private void verifyAuthorized(OrderModel orderModelMock) throws InvalidCartException {
        //authorized transactions should be stored
        verify(adyenTransactionServiceMock).authorizeOrderModel(cartModelMock, "code", "pspRef");
        //order should be created
        verify(checkoutFacadeMock).placeOrder();
        //update of order metadata should happen
        verify(adyenOrderServiceMock).updateOrderFromPaymentResult(orderModelMock, paymentResultMock);
    }
}
