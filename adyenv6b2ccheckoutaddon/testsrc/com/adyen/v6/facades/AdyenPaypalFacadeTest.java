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

import java.security.SignatureException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.adyen.v6.factory.AdyenAddressDataFactory;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import static com.adyen.constants.BrandCodes.PAYPAL_ECS;
import static com.adyen.constants.HPPConstants.Response.AUTH_RESULT;
import static com.adyen.constants.HPPConstants.Response.AUTH_RESULT_ERROR;
import static com.adyen.constants.HPPConstants.Response.AUTH_RESULT_PENDING;
import static com.adyen.constants.HPPConstants.Response.MERCHANT_REFERENCE;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_BILLING_ADDRESS_CITY;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_BILLING_ADDRESS_COUNTRY;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_BILLING_ADDRESS_POSTAL_CODE;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_BILLING_ADDRESS_STATE;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_BILLING_ADDRESS_STATE_OR_PROVINCE;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_BILLING_ADDRESS_STREET;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_DELIVERY_ADDRESS_CITY;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_DELIVERY_ADDRESS_COUNTRY;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_DELIVERY_ADDRESS_POSTAL_CODE;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_DELIVERY_ADDRESS_STATE;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_DELIVERY_ADDRESS_STATE_OR_PROVINCE;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_DELIVERY_ADDRESS_STREET;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_PAYMENT_TOKEN;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_SHOPPER_FIRST_NAME;
import static com.adyen.v6.facades.AdyenPaypalFacade.PAYPAL_ECS_SHOPPER_LAST_NAME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenPaypalFacadeTest {
    @Mock
    private AdyenAddressDataFactory adyenAddressDataFactoryMock;

    @Mock
    private AdyenCheckoutFacade adyenCheckoutFacadeMock;

    @Mock
    private ModelService modelServiceMock;

    @Mock
    private CartService cartServiceMock;

    @Mock
    private CheckoutFacade checkoutFacadeMock;

    @Mock
    private UserFacade userFacadeMock;

    @Mock
    private Converter<AddressData, AddressModel> addressReverseConverterMock;

    @InjectMocks
    private DefaultAdyenPaypalFacade adyenPaypalFacade;

    @Mock
    HttpServletRequest requestMock;

    @Mock
    CartData cartDataMock;

    @Before
    public void setUp() throws SignatureException, InvalidCartException {
        when(requestMock.getParameter(MERCHANT_REFERENCE)).thenReturn("code");
        when(cartDataMock.getCode()).thenReturn("code");
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
    }

    @Test
    public void testHandlePaypalEcsResponse() throws SignatureException, InvalidCartException {
        //Success
        when(requestMock.getParameter(AUTH_RESULT)).thenReturn(AUTH_RESULT_PENDING);
        boolean success = adyenPaypalFacade.handlePaypalECSResponse(requestMock);
        assertTrue(success);

        //Cancelled
        when(requestMock.getParameter(AUTH_RESULT)).thenReturn(AUTH_RESULT_ERROR);
        success = adyenPaypalFacade.handlePaypalECSResponse(requestMock);
        assertFalse(success);
    }

    @Test(expected = SignatureException.class)
    public void testHandlePaypalEcsResponseSignatureException() throws SignatureException, InvalidCartException {
        doThrow(new SignatureException("")).when(adyenCheckoutFacadeMock).validateHPPResponse(requestMock);
        adyenPaypalFacade.handlePaypalECSResponse(requestMock);
        fail("Expected exception");
    }

    @Test(expected = InvalidCartException.class)
    public void testHandlePaypalEcsResponseRestoreCartException() throws SignatureException, InvalidCartException {
        doThrow(new InvalidCartException("")).when(adyenCheckoutFacadeMock).restoreSessionCart();
            adyenPaypalFacade.handlePaypalECSResponse(requestMock);
            fail("Expected exception");
    }

    @Test
    public void testInitializePaypalEcs() throws SignatureException, InvalidCartException {
        Map<String, String> hppFormData = adyenPaypalFacade.initializePaypalECS("redirectUrl");

        verify(cartDataMock).setAdyenPaymentMethod(PAYPAL_ECS);
    }

    @Test
    public void testUpdateCart() {
        CartModel cartModelMock = mock(CartModel.class);
        AddressData deliveryAddressMock = mock(AddressData.class);
        PaymentInfoModel paymentInfoMock = mock(PaymentInfoModel.class);
        AddressModel addressModel = new AddressModel();

        when(requestMock.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_COUNTRY)).thenReturn("country");
        when(requestMock.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_STATE)).thenReturn("state");
        when(requestMock.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_STATE_OR_PROVINCE)).thenReturn("stateOrProvince");
        when(requestMock.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_CITY)).thenReturn("city");
        when(requestMock.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_STREET)).thenReturn("street");
        when(requestMock.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_POSTAL_CODE)).thenReturn("postalCode");
        when(requestMock.getParameter(PAYPAL_ECS_BILLING_ADDRESS_COUNTRY)).thenReturn("country");
        when(requestMock.getParameter(PAYPAL_ECS_BILLING_ADDRESS_STATE)).thenReturn("state");
        when(requestMock.getParameter(PAYPAL_ECS_BILLING_ADDRESS_STATE_OR_PROVINCE)).thenReturn("stateOrProvince");
        when(requestMock.getParameter(PAYPAL_ECS_BILLING_ADDRESS_CITY)).thenReturn("city");
        when(requestMock.getParameter(PAYPAL_ECS_BILLING_ADDRESS_STREET)).thenReturn("street");
        when(requestMock.getParameter(PAYPAL_ECS_BILLING_ADDRESS_POSTAL_CODE)).thenReturn("postalCode");
        when(requestMock.getParameter(PAYPAL_ECS_SHOPPER_FIRST_NAME)).thenReturn("firstName");
        when(requestMock.getParameter(PAYPAL_ECS_SHOPPER_LAST_NAME)).thenReturn("lastName");
        when(requestMock.getParameter(PAYPAL_ECS_PAYMENT_TOKEN)).thenReturn("paymentToken");

        when(adyenAddressDataFactoryMock.createAddressData("country", "state", "stateOrProvince", "city", "street", "postalCode", "firstName", "lastName")).thenReturn(deliveryAddressMock);

        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(modelServiceMock.create(PaymentInfoModel.class)).thenReturn(paymentInfoMock);
        when(modelServiceMock.create(AddressModel.class)).thenReturn(addressModel);

        adyenPaypalFacade.updateCart(requestMock, false);

        verify(checkoutFacadeMock).setDeliveryAddress(deliveryAddressMock);
        verify(paymentInfoMock).setAdyenPaymentMethod(PAYPAL_ECS);
        verify(paymentInfoMock).setAdyenPaypalEcsToken("paymentToken");
        verify(modelServiceMock).save(paymentInfoMock);
    }
}
