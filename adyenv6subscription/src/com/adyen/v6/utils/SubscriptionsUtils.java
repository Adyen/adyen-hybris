package com.adyen.v6.utils;
import de.hybris.platform.commercefacades.order.data.CartData;
public class SubscriptionsUtils {
    public static boolean containsSubscription(CartData cartData){
        return cartData.getEntries().stream().anyMatch(orderEntryData -> orderEntryData.getProduct().getSubscriptionTerm()!=null);
    }
}
