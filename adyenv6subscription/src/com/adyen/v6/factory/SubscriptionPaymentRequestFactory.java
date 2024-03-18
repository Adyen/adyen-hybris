package com.adyen.v6.factory;

import com.adyen.model.checkout.CheckoutPaymentMethod;
import com.adyen.model.checkout.PaymentRequest;
import com.adyen.model.recurring.RecurringDetailsRequest;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.utils.SubscriptionsUtils;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SubscriptionPaymentRequestFactory extends AdyenRequestFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionPaymentRequestFactory.class);

    private CartFacade cartFacade;

    public SubscriptionPaymentRequestFactory(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Override
    public PaymentRequest createPaymentsRequest(String merchantAccount, CartData cartData, CheckoutPaymentMethod checkoutPaymentMethod, RequestInfo requestInfo,
                                                CustomerModel customerModel, RecurringContractMode recurringContractMode,
                                                Boolean guestUserTokenizationEnabled) {

        LOG.info("Creating PaymentsRequest for merchant account: {}", merchantAccount);

        if (BooleanUtils.isTrue(cartData.getSubscriptionOrder())) {
            return createRecurringPaymentsRequest(merchantAccount, cartData, requestInfo, customerModel,
                    recurringContractMode, guestUserTokenizationEnabled);
        }

        return createRegularPaymentsRequest(merchantAccount, cartData, requestInfo, customerModel,
                recurringContractMode, guestUserTokenizationEnabled);
    }

    private PaymentRequest createRegularPaymentsRequest(String merchantAccount, CartData cartData, RequestInfo requestInfo,
                                                         CustomerModel customerModel, RecurringContractMode recurringContractMode,
                                                         Boolean guestUserTokenizationEnabled) {
        LOG.info("Creating regular PaymentsRequest...");
        PaymentRequest paymentsRequest = super.createPaymentsRequest(merchantAccount, cartData,null, requestInfo,
                customerModel, recurringContractMode, guestUserTokenizationEnabled);
        paymentsRequest.setShopperInteraction(PaymentRequest.ShopperInteractionEnum.CONTAUTH);

        PaymentRequest.RecurringProcessingModelEnum recurringProcessingModel = BooleanUtils.isTrue(
                SubscriptionsUtils.containsSubscription(getCartFacade().getSessionCart()))
                ? PaymentRequest.RecurringProcessingModelEnum.SUBSCRIPTION
                : PaymentRequest.RecurringProcessingModelEnum.CARDONFILE;

        paymentsRequest.setRecurringProcessingModel(recurringProcessingModel);
        return paymentsRequest;
    }

    @Override
    public RecurringDetailsRequest createListRecurringDetailsRequest(String merchantAccount, String customerId) {
        if (SubscriptionsUtils.containsSubscription(getCartFacade().getSessionCart())) {
            return new RecurringDetailsRequest().merchantAccount(merchantAccount).shopperReference(customerId).selectRecurringContract();
        } else {
            return new RecurringDetailsRequest().merchantAccount(merchantAccount).shopperReference(customerId).selectOneClickContract();
        }
    }



    protected CartFacade getCartFacade() {
        return cartFacade;
    }

    public void setCartFacade(CartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }
}
