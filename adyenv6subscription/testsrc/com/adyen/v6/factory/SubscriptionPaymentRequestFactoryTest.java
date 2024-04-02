package com.adyen.v6.factory;

import com.adyen.model.checkout.PaymentRequest;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.model.RequestInfo;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubscriptionPaymentRequestFactoryTest {


    @Mock
    private ConfigurationService configurationService;

    @Mock
    private CartFacade cartFacade;

    @InjectMocks
    private SubscriptionPaymentRequestFactory subscriptionPaymentRequestFactory;

    @Before
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePaymentsRequest() {
        // Mocking necessary objects and methods
        String merchantAccount = "merchantAccount";
        CartData cartData = mock(CartData.class);
        RequestInfo requestInfo = mock(RequestInfo.class);
        CustomerModel customerModel = mock(CustomerModel.class);
        RecurringContractMode recurringContractMode = mock(RecurringContractMode.class);
        Boolean guestUserTokenizationEnabled = Boolean.TRUE;

        when(cartData.getSubscriptionOrder()).thenReturn(Boolean.FALSE);

        // Call the method to be tested
        PaymentRequest paymentsRequest = subscriptionPaymentRequestFactory.createPaymentsRequest(merchantAccount,
                cartData, requestInfo, customerModel, recurringContractMode, guestUserTokenizationEnabled);

        // Verifications and assertions
        assertNotNull(paymentsRequest);
        assertEquals(PaymentRequest.ShopperInteractionEnum.CONTAUTH, paymentsRequest.getShopperInteraction());
    }
}
