package com.adyen.v6.factory;

import com.adyen.model.checkout.CheckoutPaymentMethod;
import com.adyen.model.checkout.PaymentRequest;
import com.adyen.model.recurring.RecurringDetailsRequest;
import com.adyen.util.Util;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.utils.SubscriptionsUtils;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;


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

    protected PaymentRequest createRecurringPaymentsRequest(final String merchantAccount, final CartData cartData,
                                                             final RequestInfo requestInfo, final CustomerModel customerModel, final RecurringContractMode recurringContractMode,
                                                             final Boolean guestUserTokenizationEnabled)
    {

        LOG.info("Creating RecurringPaymentsRequest for merchant account: {}", merchantAccount);

        final PaymentRequest paymentsRequest = new PaymentRequest();
        final String adyenPaymentMethod = cartData.getAdyenPaymentMethod();

        if (adyenPaymentMethod == null)
        {
            throw new IllegalArgumentException("Payment method is null");
        }

        updatePaymentRequest(merchantAccount, cartData, requestInfo, customerModel, paymentsRequest);

/*        final PaymentMethodDetails paymentMethod = adyenPaymentMethodDetailsBuilderExecutor.createPaymentMethodDetails(cartData);
        if(paymentMethod instanceof CardDetails) {
            ((CardDetails) paymentMethod).setStoredPaymentMethodId(cartData.getAdyenSelectedReference());
        }
        paymentMethod.setType(Adyenv6coreConstants.PAYMENT_METHOD_SCHEME);
        paymentsRequest.setPaymentMethod(paymentMethod);*/


        updateApplicationInfoEcom(paymentsRequest.getApplicationInfo());


        paymentsRequest.setRedirectFromIssuerMethod(RequestMethod.POST.toString());
        paymentsRequest.setRedirectToIssuerMethod(RequestMethod.POST.toString());
        paymentsRequest.setShopperInteraction(PaymentsRequest.ShopperInteractionEnum.CONTAUTH);
        paymentsRequest.setRecurringProcessingModel(PaymentsRequest.RecurringProcessingModelEnum.SUBSCRIPTION);

        return paymentsRequest;
    }


    private void updatePaymentRequest(final String merchantAccount, final CartData cartData, final RequestInfo requestInfo,
                                      final CustomerModel customerModel, final PaymentsRequest paymentsRequest)
    {


        final String currency = cartData.getTotalPrice().getCurrencyIso();
        final String reference = cartData.getCode();

        final AddressData billingAddress = cartData.getPaymentInfo() != null ? cartData.getPaymentInfo().getBillingAddress() : null;
        final AddressData deliveryAddress = cartData.getDeliveryAddress();

        paymentsRequest.amount(Util.createAmount(cartData.getTotalPrice().getValue(), currency)).reference(reference).merchantAccount(merchantAccount)
                .setCountryCode(getCountryCode(cartData));
        // set shopper details from CustomerModel.
        if (customerModel != null)
        {
            paymentsRequest.setShopperReference(customerModel.getCustomerID());
            paymentsRequest.setShopperEmail(customerModel.getContactEmail());
        }

        // if address details are provided, set it to the PaymentRequest
        if (deliveryAddress != null)
        {
            paymentsRequest.setDeliveryAddress(convertToDeliveryAddress(deliveryAddress));
        }

        if (billingAddress != null)
        {
            paymentsRequest.setBillingAddress(convertToDeliveryAddress(billingAddress));
            // set PhoneNumber if it is provided
            final String phone = billingAddress.getPhone();
            if (StringUtils.isNotBlank(phone ))
            {
                paymentsRequest.setTelephoneNumber(phone);
            }
        }

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
