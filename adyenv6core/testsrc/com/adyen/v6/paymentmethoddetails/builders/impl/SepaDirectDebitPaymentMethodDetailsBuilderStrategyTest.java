package com.adyen.v6.paymentmethoddetails.builders.impl;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.SepaDirectDebitDetails;
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
public class SepaDirectDebitPaymentMethodDetailsBuilderStrategyTest {
    protected static final String ADYEN_SEPA_OWNER_NAME = "adyenSepaOwnerName";
    protected static final String ADYEN_SEPA_IBAN_NUMBER = "adyenSepaIbanNumber";

    @InjectMocks
    private SepaDirectDebitPaymentMethodDetailsBuilderStrategy testObj;

    @Mock
    private CartData cartDataMock;

    @Before
    public void setUp() throws Exception {
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(SepaDirectDebitDetails.SEPADIRECTDEBIT);
        when(cartDataMock.getAdyenSepaOwnerName()).thenReturn(ADYEN_SEPA_OWNER_NAME);
        when(cartDataMock.getAdyenSepaIbanNumber()).thenReturn(ADYEN_SEPA_IBAN_NUMBER);
    }

    @Test
    public void isApplicable_returnTrue_whenIsSepaDirectDebitPaymentMethod() {
        final boolean result = testObj.isApplicable(cartDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void isApplicable_returnFalse_whenIsNotSepaDirectDebitPaymentMethod() {
        when(cartDataMock.getAdyenPaymentMethod()).thenReturn(SepaDirectDebitDetails.SEPADIRECTDEBIT_AMAZONPAY);

        final boolean result = testObj.isApplicable(cartDataMock);

        assertThat(result).isFalse();
    }

    @Test
    public void buildPaymentMethodDetails_returnSepaDiredtDebitDetailsCorrectlyFilled() {
        final PaymentMethodDetails result = testObj.buildPaymentMethodDetails(cartDataMock);

        assertThat(result).isInstanceOfAny(SepaDirectDebitDetails.class);
        assertThat(((SepaDirectDebitDetails) result).getIban()).isEqualTo(ADYEN_SEPA_IBAN_NUMBER);
        assertThat(((SepaDirectDebitDetails) result).getOwnerName()).isEqualTo(ADYEN_SEPA_OWNER_NAME);
    }
}