package com.adyen.v6.factory;

import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.recurring.RecurringDetailsRequest;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.paymentmethoddetails.executors.AdyenPaymentMethodDetailsBuilderExecutor;
import com.adyen.v6.utils.SubscriptionsUtils;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;


public class SubscriptionPaymentRequestFactory extends AdyenRequestFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionPaymentRequestFactory.class);

    @Resource(name = "cartFacade")
    private CartFacade cartFacade;

    public SubscriptionPaymentRequestFactory(ConfigurationService configurationService, AdyenPaymentMethodDetailsBuilderExecutor adyenPaymentMethodDetailsBuilderExecutor) {
        super(configurationService, adyenPaymentMethodDetailsBuilderExecutor);
    }


    @Override
    public PaymentsRequest createPaymentsRequest(String merchantAccount, CartData cartData, PaymentMethodDetails paymentMethodDetails, RequestInfo requestInfo, CustomerModel customerModel) {
        return super.createPaymentsRequest(merchantAccount, cartData, paymentMethodDetails, requestInfo, customerModel);
    }
    @Override
    public PaymentsRequest createPaymentsRequest(String merchantAccount, CartData cartData, RequestInfo requestInfo, CustomerModel customerModel, RecurringContractMode recurringContractMode, Boolean guestUserTokenizationEnabled) {
        PaymentsRequest paymentsRequest = super.createPaymentsRequest(merchantAccount, cartData, requestInfo, customerModel, recurringContractMode, guestUserTokenizationEnabled);
        paymentsRequest.setShopperInteraction(PaymentsRequest.ShopperInteractionEnum.ECOMMERCE);
        if(BooleanUtils.isTrue(SubscriptionsUtils.containsSubscription(getCartFacade().getSessionCart()))){
            paymentsRequest.setRecurringProcessingModel(PaymentsRequest.RecurringProcessingModelEnum.SUBSCRIPTION);
        }else{
            paymentsRequest.setRecurringProcessingModel(PaymentsRequest.RecurringProcessingModelEnum.CARDONFILE);
        }

        return paymentsRequest;
    }


    @Override
    public RecurringDetailsRequest createListRecurringDetailsRequest(String merchantAccount, String customerId) {
        if(SubscriptionsUtils.containsSubscription(getCartFacade().getSessionCart())){
            return new RecurringDetailsRequest().merchantAccount(merchantAccount).shopperReference(customerId).selectRecurringContract();
        }else{
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
