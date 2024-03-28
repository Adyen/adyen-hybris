package com.adyen.v6.hooks;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.SubscriptionModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.*;

public class AdyenSubscriptionCommercePlaceOrderMethodHookTest {

    @Mock
    private ModelService modelService;

    @InjectMocks
    private AdyenSubscriptionCommercePlaceOrderMethodHook adyenSubscriptionCommercePlaceOrderMethodHook;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAfterPlaceOrder() {
        // Create mock objects
        CommerceCheckoutParameter parameter = mock(CommerceCheckoutParameter.class);
        CommerceOrderResult result = mock(CommerceOrderResult.class);
        OrderModel order = mock(OrderModel.class);
        AbstractOrderEntryModel orderEntry = mock(AbstractOrderEntryModel.class);

        // Setup mock behavior
        when(result.getOrder()).thenReturn(order);
        when(order.getChildren()).thenReturn(Collections.singleton(order));
        when(order.getEntries()).thenReturn(Collections.singletonList(orderEntry));
        when(orderEntry.getChildEntries()).thenReturn(Collections.emptyList());
        when(orderEntry.getEntryGroupNumbers()).thenReturn(Collections.singleton(Integer.valueOf(0)));
        when(orderEntry.getOrder()).thenReturn(order);

        // Call the method to be tested
        adyenSubscriptionCommercePlaceOrderMethodHook.afterPlaceOrder(parameter, result);

        // Verify interactions
        verify(modelService, never()).save(any(SubscriptionModel.class));  // No subscription should be created due to empty child entries and entry group numbers
    }

    @Test
    public void testCreateSubscriptionFromOrder() {
        // Create mock objects
        OrderModel order = mock(OrderModel.class);
        SubscriptionModel subscription = mock(SubscriptionModel.class);
        UserModel userModel = mock(UserModel.class);
        when(userModel.getUid()).thenReturn("userId");
        when(order.getUser()).thenReturn(userModel);
        // Setup mock behavior
        when(modelService.create(SubscriptionModel.class)).thenReturn(subscription);
        when(order.getCode()).thenReturn("orderCode");
        when(order.getDate()).thenReturn(new Date());

        // Call the method to be tested
        //adyenSubscriptionCommercePlaceOrderMethodHook.createSubscriptionFromOrder(order);

        // Verify interactions
        verify(subscription).setOrderNumber("orderCode");
        verify(subscription).setPlacedOn(any(Date.class));
        verify(subscription).setNextChargeDate(any(Date.class));
        verify(subscription).setSubscriptionStatus(anyString());
        verify(subscription).setSubscriptionOrder(order);
        verify(subscription).setCustomerId("userId");
        verify(modelService).save(subscription);
    }

}
