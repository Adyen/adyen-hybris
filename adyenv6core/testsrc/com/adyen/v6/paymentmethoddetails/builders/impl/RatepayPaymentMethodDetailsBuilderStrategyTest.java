package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.CardDetails;
import com.adyen.model.checkout.details.SepaDirectDebitDetails;
import com.adyen.v6.constants.Adyenv6coreConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RatepayPaymentMethodDetailsBuilderStrategyTest {

    @InjectMocks
    private RatepayPaymentMethodDetailsBuilderStrategy testObj;

    @Mock
    private CartData cartDataMock;

    @Before
    public void setUp() throws Exception {
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(Adyenv6coreConstants.RATEPAY);
    }

    @Test
    public void isApplicable_returnTrue_whenIsSepaDirectDebitPaymentMethod() {
        final boolean result = testObj.isApplicable(cartDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void isApplicable_returnFalse_whenIsNotSepaDirectDebitPaymentMethod() {
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(Adyenv6coreConstants.PAYBRIGHT);
        final boolean result = testObj.isApplicable(cartDataMock);

        assertThat(result).isFalse();
    }

    @Test
    public void buildPaymentMethodDetails_returnSepaDiredtDebitDetailsCorrectlyFilled() {
        final PaymentMethodDetails result = testObj.buildPaymentMethodDetails(cartDataMock);

        assertThat(result).isInstanceOfAny(CardDetails.class);
        assertThat(result.getType()).isEqualTo(Adyenv6coreConstants.RATEPAY);
    }
}