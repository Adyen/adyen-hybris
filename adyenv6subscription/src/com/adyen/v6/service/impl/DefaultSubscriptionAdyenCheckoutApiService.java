package com.adyen.v6.service.impl;

import com.adyen.model.checkout.PaymentRequest;
import com.adyen.model.checkout.PaymentResponse;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.service.DefaultAdyenCheckoutApiService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.adyen.v6.utils.SubscriptionsUtils.containsSubscription;
import static com.adyen.v6.utils.SubscriptionsUtils.findRecurringProcessingModel;

public class DefaultSubscriptionAdyenCheckoutApiService extends DefaultAdyenCheckoutApiService {

    protected static final Logger LOG = LogManager.getLogger(DefaultSubscriptionAdyenCheckoutApiService.class);
    private CartFacade cartFacade;
    private BaseStoreService baseStoreService;

    public DefaultSubscriptionAdyenCheckoutApiService(final BaseStoreModel baseStore) {
        super(baseStore);
    }

    @Override
    public PaymentResponse authorisePayment(final CartData cartData, final RequestInfo requestInfo, final CustomerModel customerModel)  throws Exception {
        LOG.debug("Authorize payment");

        PaymentsApi paymentsApi = new PaymentsApi(client);

        PaymentRequest paymentsRequest = getAdyenRequestFactory().createPaymentsRequest(baseStore.getAdyenMerchantAccount(),
                cartData,
                null,
                requestInfo,
                customerModel,
                baseStore.getAdyenRecurringContractMode(),
                baseStore.getAdyenGuestUserTokenization());

        if (BooleanUtils.isTrue(containsSubscription(cartData))) {
            paymentsRequest.setShopperInteraction(PaymentRequest.ShopperInteractionEnum.CONTAUTH);
            paymentsRequest.setRecurringProcessingModel(findRecurringProcessingModel(cartData));
            paymentsRequest.setEnableRecurring(Boolean.TRUE);
        }
        LOG.debug(paymentsRequest);
        PaymentResponse paymentsResponse = paymentsApi.payments(paymentsRequest);
        LOG.debug(paymentsResponse);
        return paymentsResponse;
    }

    public CartFacade getCartFacade() {
        return cartFacade;
    }

    public void setCartFacade(final CartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }
}
