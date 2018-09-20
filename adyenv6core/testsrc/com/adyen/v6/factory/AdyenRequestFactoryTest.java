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
package com.adyen.v6.factory;

import com.adyen.model.PaymentRequest;
import com.adyen.model.recurring.Recurring;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.model.RequestInfo;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenRequestFactoryTest {
    private AdyenRequestFactory adyenRequestFactory = new AdyenRequestFactory();

    @Mock
    CartData cartDataMock;

    @Mock
    HttpServletRequest requestMock;

    @Mock
    CustomerModel customerModelMock;

    @Mock
    AddressData deliveryAddressMock;

    @Mock
    AddressData billingAddressMock;

    @Mock
    CCPaymentInfoData paymentInfoMock;

    @Mock
    CountryData deliveryCountryDataMock;

    @Mock
    CountryData billingCountryDataMock;

    @Before
    public void setUp() {
        PriceData priceData = new PriceData();
        priceData.setValue(new BigDecimal("12.34"));
        priceData.setCurrencyIso("EUR");

        when(cartDataMock.getTotalPrice()).thenReturn(priceData);
        when(cartDataMock.getCode()).thenReturn("code");
        when(cartDataMock.getAdyenCseToken()).thenReturn("AdyenCseToken");
        when(cartDataMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(PAYMENT_METHOD_CC);

        when(paymentInfoMock.getBillingAddress()).thenReturn(billingAddressMock);
        when(deliveryAddressMock.getTown()).thenReturn("deliveryTown");
        when(billingAddressMock.getTown()).thenReturn("billingTown");
        when(deliveryAddressMock.getCountry()).thenReturn(deliveryCountryDataMock);
        when(billingAddressMock.getCountry()).thenReturn(billingCountryDataMock);
        when(deliveryCountryDataMock.getIsocode()).thenReturn("NL");
        when(billingCountryDataMock.getIsocode()).thenReturn("GR");

        when(customerModelMock.getCustomerID()).thenReturn("uuid");
        when(customerModelMock.getContactEmail()).thenReturn("email");

        when(requestMock.getHeader("User-Agent")).thenReturn("User-Agent");
        when(requestMock.getHeader("Accept")).thenReturn("Accept");
        when(requestMock.getRemoteAddr()).thenReturn("1.2.3.4");
    }

    @Test
    public void testAuthorise() throws Exception {
        PaymentRequest paymentRequest;

        //Test anonymous
        paymentRequest = adyenRequestFactory.createAuthorizationRequest("merchantAccount", cartDataMock, new RequestInfo(requestMock), null, RecurringContractMode.NONE);

        //use delivery/billing address from cart
        assertEquals("deliveryTown", paymentRequest.getDeliveryAddress().getCity());
        assertEquals("NL", paymentRequest.getDeliveryAddress().getCountry());
        assertEquals("billingTown", paymentRequest.getBillingAddress().getCity());
        assertEquals("GR", paymentRequest.getBillingAddress().getCountry());

        assertEquals("AdyenCseToken", paymentRequest.getAdditionalData().get("card.encrypted.json"));
        assertNull(paymentRequest.getShopperReference());

        assertEquals("User-Agent", paymentRequest.getBrowserInfo().getUserAgent());
        assertEquals("Accept", paymentRequest.getBrowserInfo().getAcceptHeader());
        assertEquals("1.2.3.4", paymentRequest.getShopperIP());

        //Test recurring contract when remember-me is NOT set
        when(cartDataMock.getAdyenRememberTheseDetails()).thenReturn(false);
        testRecurringOption(null, null);
        testRecurringOption(RecurringContractMode.NONE, null);
        testRecurringOption(RecurringContractMode.ONECLICK, null);
        testRecurringOption(RecurringContractMode.RECURRING, Recurring.ContractEnum.RECURRING);
        testRecurringOption(RecurringContractMode.ONECLICK_RECURRING, Recurring.ContractEnum.RECURRING);

        //Test recurring contract when remember-me is set
        when(cartDataMock.getAdyenRememberTheseDetails()).thenReturn(true);
        testRecurringOption(null, null);
        testRecurringOption(RecurringContractMode.NONE, null);
        testRecurringOption(RecurringContractMode.ONECLICK, Recurring.ContractEnum.ONECLICK);
        testRecurringOption(RecurringContractMode.RECURRING, Recurring.ContractEnum.RECURRING);
        testRecurringOption(RecurringContractMode.ONECLICK_RECURRING, Recurring.ContractEnum.ONECLICK_RECURRING);

        //When a store card is selected, send the reference and include the recurring contract
        when(cartDataMock.getAdyenSelectedReference()).thenReturn("recurring_reference");
        when(cartDataMock.getAdyenRememberTheseDetails()).thenReturn(false);
        paymentRequest = adyenRequestFactory.createAuthorizationRequest("merchantAccount", cartDataMock, new RequestInfo(requestMock), customerModelMock, null);

        assertEquals("recurring_reference", paymentRequest.getSelectedRecurringDetailReference());
        assertEquals(Recurring.ContractEnum.ONECLICK, paymentRequest.getRecurring().getContract());
    }

    private void testRecurringOption(final RecurringContractMode recurringContractModeSetting, final Recurring.ContractEnum expectedRecurringContractMode) {

        PaymentRequest paymentRequest = adyenRequestFactory.createAuthorizationRequest("merchantAccount", cartDataMock, new RequestInfo(requestMock), customerModelMock, recurringContractModeSetting);

        if (expectedRecurringContractMode == null) {
            assertNull(paymentRequest.getRecurring());
        } else {
            assertEquals(expectedRecurringContractMode, paymentRequest.getRecurring().getContract());
        }

        //when customer is set, shopperReference and Email should be set as well
        assertEquals("uuid", paymentRequest.getShopperReference());
        assertEquals("email", paymentRequest.getShopperEmail());
    }
}
