package com.adyen.commerce.facades;

import com.adyen.model.checkout.PaymentDetailsRequest;
import com.adyen.model.checkout.PaymentRequest;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.forms.AddressForm;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;

import javax.servlet.http.HttpServletRequest;

public interface AdyenCheckoutApiFacade extends AdyenCheckoutFacade {

    void preHandlePlaceOrder(PaymentRequest paymentRequest, String adyenPaymentMethod, AddressForm billingAddress, Boolean useAdyenDeliveryAddress);

    OrderData placeOrderWithPayment(final HttpServletRequest request, final CartData cartData, PaymentRequest paymentRequest) throws Exception;

    OrderData placeOrderWithAdditionalDetails(PaymentDetailsRequest detailsRequest) throws Exception;
}
