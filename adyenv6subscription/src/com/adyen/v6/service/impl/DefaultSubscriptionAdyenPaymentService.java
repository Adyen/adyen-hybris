package com.adyen.v6.service.impl;

import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import com.adyen.util.Util;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.service.DefaultAdyenPaymentService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.adyen.v6.utils.SubscriptionsUtils.containsSubscription;
import static com.adyen.v6.utils.SubscriptionsUtils.findRecurringProcessingModel;

public class DefaultSubscriptionAdyenPaymentService extends DefaultAdyenPaymentService {

    protected static final Logger LOG = LogManager.getLogger(DefaultSubscriptionAdyenPaymentService.class);
    private CartFacade cartFacade;
    private BaseStoreService baseStoreService;

    public DefaultSubscriptionAdyenPaymentService(final BaseStoreModel baseStore) {
        super(baseStore);
    }

    @Override
    public PaymentMethodsResponse getPaymentMethodsResponse(final BigDecimal amount,
                                                            final String currency,
                                                            final String countryCode,
                                                            final String shopperLocale,
                                                            final String shopperReference) throws IOException, ApiException {

        LOG.debug("Get payment methods response");

        Checkout checkout = new Checkout(getClient());
        PaymentMethodsRequest request = new PaymentMethodsRequest();
        request.merchantAccount(getClient().getConfig().getMerchantAccount()).amount(Util.createAmount(amount, currency)).countryCode(countryCode);

        if (!StringUtils.isEmpty(shopperLocale)) {
            request.setShopperLocale(shopperLocale);
        }

        if (!StringUtils.isEmpty(shopperReference)) {
            request.setShopperReference(shopperReference);
        }

        if (BooleanUtils.isTrue(containsSubscription(getCartFacade().getSessionCart()))) {
            final List<String> allowedPaymentMethods =  new ArrayList<>(getBaseStoreService().getCurrentBaseStore().getSubscriptionAllowedPaymentMethods());
            request.setAllowedPaymentMethods(allowedPaymentMethods);
        }

        LOG.debug(request);
        final PaymentMethodsResponse response = checkout.paymentMethods(request);
        LOG.debug(response);

        return response;
    }

    @Override
    public PaymentsResponse authorisePayment(final CartData cartData, final RequestInfo requestInfo, final CustomerModel customerModel) throws Exception {
        LOG.debug("Authorize payment");

        Checkout checkout = new Checkout(getClient());

        PaymentsRequest paymentsRequest = getAdyenRequestFactory().createPaymentsRequest(getClient().getConfig().getMerchantAccount(),
                cartData,
                requestInfo,
                customerModel,
                getBaseStore().getAdyenRecurringContractMode(),
                getBaseStore().getAdyenGuestUserTokenization());

        if (BooleanUtils.isTrue(containsSubscription(cartData))) {
            paymentsRequest.setShopperInteraction(PaymentsRequest.ShopperInteractionEnum.CONTAUTH);
            paymentsRequest.setRecurringProcessingModel(findRecurringProcessingModel(cartData));
            paymentsRequest.setEnableRecurring(Boolean.TRUE);
        }
        LOG.debug(paymentsRequest);
        PaymentsResponse paymentsResponse = checkout.payments(paymentsRequest);
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
