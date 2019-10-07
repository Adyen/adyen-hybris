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

import java.math.BigDecimal;

import com.adyen.model.nexo.AmountsReq;
import com.adyen.model.nexo.MessageHeader;
import com.adyen.model.nexo.SaleData;
import com.adyen.model.terminal.TerminalAPIRequest;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.adyen.model.PaymentRequest;
import com.adyen.model.checkout.DefaultPaymentMethodDetails;
import com.adyen.model.checkout.PaymentsRequest;
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
import de.hybris.platform.servicelayer.config.ConfigurationService;

import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenRequestFactoryTest {
    private AdyenRequestFactory adyenRequestFactory;

    @Mock
    CartData cartDataMock;

    @Mock
    javax.servlet.http.HttpServletRequest requestMock;

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

    @Mock
    ConfigurationService configurationServiceMock;

    @Before
    public void setUp() {
        adyenRequestFactory = new AdyenRequestFactory();
        PriceData priceData = new PriceData();
        priceData.setValue(new BigDecimal("12.34"));
        priceData.setCurrencyIso("EUR");

        when(cartDataMock.getTotalPrice()).thenReturn(priceData);
        when(cartDataMock.getCode()).thenReturn("code");
        when(cartDataMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(PAYMENT_METHOD_CC);
        when(cartDataMock.getStore()).thenReturn("StoreName");
        when(cartDataMock.getAdyenTerminalId()).thenReturn("V400m-123456789");

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
        when(requestMock.getRequestURL()).thenReturn(new StringBuffer("https://localhost:9002/electronics/en/checkout/multi/adyen/summary/placeOrder"));
        when(requestMock.getRequestURI()).thenReturn("/electronics/en/checkout/multi/adyen/summary/placeOrder");

        Configuration configurationMock = mock(BaseConfiguration.class);
        when(configurationMock.getString(any(String.class))).thenReturn("dummy");
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);

        adyenRequestFactory.setConfigurationService(configurationServiceMock);
    }

    @Test
    public void testAuthorise() throws Exception {
        PaymentsRequest paymentsRequest;

        //Test anonymous
        paymentsRequest = adyenRequestFactory.createPaymentsRequest("merchantAccount", cartDataMock, new RequestInfo(requestMock), null, RecurringContractMode.NONE);

        //use delivery/billing address from cart
        assertEquals("deliveryTown", paymentsRequest.getDeliveryAddress().getCity());
        assertEquals("NL", paymentsRequest.getDeliveryAddress().getCountry());
        assertEquals("billingTown", paymentsRequest.getBillingAddress().getCity());
        assertEquals("GR", paymentsRequest.getBillingAddress().getCountry());

        assertNull(paymentsRequest.getShopperReference());

        assertEquals("User-Agent", paymentsRequest.getBrowserInfo().getUserAgent());
        assertEquals("Accept", paymentsRequest.getBrowserInfo().getAcceptHeader());
        assertEquals("1.2.3.4", paymentsRequest.getShopperIP());

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
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(PAYMENT_METHOD_ONECLICK);
        when(cartDataMock.getAdyenSelectedReference()).thenReturn("recurring_reference");
        when(cartDataMock.getAdyenRememberTheseDetails()).thenReturn(false);
        paymentsRequest = adyenRequestFactory.createPaymentsRequest("merchantAccount", cartDataMock, new RequestInfo(requestMock), customerModelMock, null);

        DefaultPaymentMethodDetails paymentMethodDetails = (DefaultPaymentMethodDetails) paymentsRequest.getPaymentMethod();
        assertEquals("recurring_reference", paymentMethodDetails.getRecurringDetailReference());
    }

    private void testRecurringOption(final RecurringContractMode recurringContractModeSetting, final Recurring.ContractEnum expectedRecurringContractMode) {

        PaymentRequest paymentRequest = adyenRequestFactory.createAuthorizationRequest("merchantAccount", cartDataMock, requestMock, customerModelMock, recurringContractModeSetting);

        if (expectedRecurringContractMode == null) {
            assertNull(paymentRequest.getRecurring());
        } else {
            assertEquals(expectedRecurringContractMode, paymentRequest.getRecurring().getContract());
        }

        //when customer is set, shopperReference and Email should be set as well
        assertEquals("uuid", paymentRequest.getShopperReference());
        assertEquals("email", paymentRequest.getShopperEmail());
    }

    @Test
    public void testTerminalApiRequestAnonymous() throws Exception {
        TerminalAPIRequest terminalApiRequest = adyenRequestFactory.createTerminalAPIRequest(cartDataMock, null, null, null);

        validateTerminalApiRequest(terminalApiRequest);
    }

    @Test
    public void testTerminalApiRequestWithRecurring() throws Exception {
        TerminalAPIRequest terminalApiRequest = adyenRequestFactory.createTerminalAPIRequest(cartDataMock, customerModelMock, RecurringContractMode.ONECLICK_RECURRING, null);

        validateTerminalApiRequest(terminalApiRequest);

        assertNotNull(terminalApiRequest.getSaleToPOIRequest().getPaymentRequest().getSaleData().getSaleToAcquirerData());

        String saleToAcquirerData = terminalApiRequest.getSaleToPOIRequest().getPaymentRequest().getSaleData().getSaleToAcquirerData();
        assertTrue(saleToAcquirerData.contains("recurringContract=ONECLICK,RECURRING"));
        assertTrue(saleToAcquirerData.contains("shopperEmail=email"));
        assertTrue(saleToAcquirerData.contains("shopperReference=uuid"));
    }

    private void validateTerminalApiRequest(TerminalAPIRequest terminalApiRequest) {
        assertNotNull(terminalApiRequest);
        assertNotNull(terminalApiRequest.getSaleToPOIRequest());
        assertNotNull(terminalApiRequest.getSaleToPOIRequest().getMessageHeader());

        MessageHeader messageHeader = terminalApiRequest.getSaleToPOIRequest().getMessageHeader();
        assertEquals("StoreName", messageHeader.getSaleID());
        assertEquals("V400m-123456789", messageHeader.getPOIID());

        assertNotNull(terminalApiRequest.getSaleToPOIRequest().getPaymentRequest());
        assertNotNull(terminalApiRequest.getSaleToPOIRequest().getPaymentRequest().getSaleData());

        SaleData saleData = terminalApiRequest.getSaleToPOIRequest().getPaymentRequest().getSaleData();
        assertNotNull(saleData.getSaleTransactionID());
        assertEquals("code", saleData.getSaleTransactionID().getTransactionID());

        assertNotNull(terminalApiRequest.getSaleToPOIRequest().getPaymentRequest().getPaymentTransaction());
        assertNotNull(terminalApiRequest.getSaleToPOIRequest().getPaymentRequest().getPaymentTransaction().getAmountsReq());

        AmountsReq amountsReq = terminalApiRequest.getSaleToPOIRequest().getPaymentRequest().getPaymentTransaction().getAmountsReq();
        assertEquals("EUR", amountsReq.getCurrency());
        assertEquals("12.34", amountsReq.getRequestedAmount().toString());
    }
}
