package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.PayPalDetails;
import com.adyen.v6.constants.Adyenv6coreConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaypalAdyenPaymentMethodDetailsBuilderStrategyTest {

    private static String personalDetails = "firstName lastName ";
    private static String contactDetails = " 666666666 test@test.com";

    @InjectMocks
    private PaypalAdyenPaymentMethodDetailsBuilderStrategy testObj;

    @Mock
    private CartData cartDataMock;
    @Mock
    private AddressData addressDataMock;

    @Before
    public void setUp() throws Exception {
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(PayPalDetails.PAYPAL);
        when(cartDataMock.getDeliveryAddress()).thenReturn(addressDataMock);
        when(cartDataMock.getAdyenDob()).thenReturn(new Date(0));
        when(addressDataMock.getFirstName()).thenReturn("firstName");
        when(addressDataMock.getLastName()).thenReturn("lastName");
        when(addressDataMock.getPhone()).thenReturn("666666666");
        when(addressDataMock.getEmail()).thenReturn("test@test.com");
    }

    @Test
    public void isApplicable_returnTrue_whenIsPaypalPaymentMethod() {
        final boolean result = testObj.isApplicable(cartDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void isApplicable_returnFalse_whenIsNotPaypalPaymentMethod() {
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(Adyenv6coreConstants.PAYBRIGHT);
        final boolean result = testObj.isApplicable(cartDataMock);

        assertThat(result).isFalse();
    }

    @Test
    public void buildPaymentMethodDetails_returnPaypalDetailsCorrectlyFilled() {
        final PaymentMethodDetails result = testObj.buildPaymentMethodDetails(cartDataMock);

        assertThat(result).isInstanceOfAny(PayPalDetails.class);
        assertThat(((PayPalDetails) result).getSubtype()).isEqualTo(PayPalDetails.SubtypeEnum.SDK);
        assertThat(((PayPalDetails) result).getPayerID()).isEqualTo(personalDetails + new Date(0) + contactDetails);
    }

}