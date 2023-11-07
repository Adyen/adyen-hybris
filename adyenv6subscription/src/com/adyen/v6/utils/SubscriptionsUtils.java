package com.adyen.v6.utils;
import com.adyen.model.checkout.CreateCheckoutSessionRequest;
import com.adyen.model.checkout.PaymentsRequest;
import de.hybris.platform.commercefacades.order.data.CartData;
public class SubscriptionsUtils {
    public static boolean containsSubscription(CartData cartData){
        return cartData.getEntries().stream().anyMatch(orderEntryData -> orderEntryData.getProduct().getSubscriptionTerm()!=null);
    }

    public static PaymentsRequest.RecurringProcessingModelEnum findRecurringProcessingModel(CartData cartData){
        if(cartData.getEntries().stream()
                .filter(orderEntryData->orderEntryData.getProduct().getSubscriptionTerm()!=null)
                .allMatch(orderEntryData -> orderEntryData.getProduct().getSubscriptionTerm().getBillingPlan()!=null)) {
            return PaymentsRequest.RecurringProcessingModelEnum.SUBSCRIPTION;
        }

        return PaymentsRequest.RecurringProcessingModelEnum.UNSCHEDULEDCARDONFILE;
    }
}
