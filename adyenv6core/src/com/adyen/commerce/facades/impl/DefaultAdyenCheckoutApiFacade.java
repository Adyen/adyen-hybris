package com.adyen.commerce.facades.impl;

import com.adyen.commerce.facades.AdyenCheckoutApiFacade;
import com.adyen.model.checkout.AfterpayDetails;
import com.adyen.model.checkout.ApplePayDetails;
import com.adyen.model.checkout.BrowserInfo;
import com.adyen.model.checkout.CardDetails;
import com.adyen.model.checkout.PaymentDetails;
import com.adyen.model.checkout.PaymentRequest;
import com.adyen.model.checkout.PaymentResponse;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.impl.DefaultAdyenCheckoutFacade;
import com.adyen.v6.forms.AddressForm;
import com.adyen.v6.model.RequestInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;

public class DefaultAdyenCheckoutApiFacade extends DefaultAdyenCheckoutFacade implements AdyenCheckoutApiFacade {

    public static final String EXCEPTION_DURING_PROCESSING_BROWSER_INFO = "Exception during processing BrowserInfo: ";

    public void preHandlePlaceOrder(PaymentRequest paymentRequest, String adyenPaymentMethod,
                                    AddressForm billingAddress, Boolean useAdyenDeliveryAddress, Errors errors) {
        //Validate form
        CartModel cartModel = getCartService().getSessionCart();

        PaymentInfoModel paymentInfo = createPaymentInfo(cartModel, adyenPaymentMethod);

        saveBillingAddress(billingAddress, paymentInfo, cartModel, useAdyenDeliveryAddress);

        //required for 3DS2
        saveBrowserInfoOnPaymentInfo(paymentRequest.getBrowserInfo(), paymentInfo);

        if (paymentRequest.getPaymentMethod().getActualInstance() instanceof CardDetails cardDetails) {
            paymentInfo.setCardType(cardDetails.getType().getValue());
            if (CardDetails.TypeEnum.CARD.equals(cardDetails.getType()) || CardDetails.TypeEnum.SCHEME.equals(cardDetails.getType()) || CardDetails.TypeEnum.BCMC.equals(cardDetails.getType())) {
                paymentInfo.setAdyenCardHolder(cardDetails.getHolderName());
                paymentInfo.setCardBrand(cardDetails.getBrand());
                paymentInfo.setAdyenSelectedReference(cardDetails.getStoredPaymentMethodId());
                paymentInfo.setAdyenRememberTheseDetails(paymentRequest.getStorePaymentMethod());
                paymentInfo.setAdyenSelectedReference(cardDetails.getStoredPaymentMethodId());
                paymentInfo.setAdyenRememberTheseDetails(paymentRequest.getEnableOneClick());
                paymentInfo.setAdyenSelectedReference(cardDetails.getStoredPaymentMethodId());
            } else if (CardDetails.TypeEnum.GIFTCARD.equals(cardDetails.getType())) {
                // Gift card
                paymentInfo.setAdyenGiftCardBrand(cardDetails.getBrand());
            }
        } else if (paymentRequest.getPaymentMethod().getActualInstance() instanceof PaymentDetails paymentDetails) {
            paymentInfo.setAdyenIssuerId(paymentDetails.getType().getValue());

        } else if (paymentRequest.getPaymentMethod().getActualInstance() instanceof AfterpayDetails afterpayDetails) {
            paymentInfo.setAdyenTelephone(cartModel.getDeliveryAddress().getPhone1());

        } else if(paymentRequest.getPaymentMethod().getActualInstance() instanceof ApplePayDetails applePayDetails){
            paymentInfo.setAdyenApplePayMerchantName(cartModel.getAdyenApplePayMerchantName());
            paymentInfo.setAdyenApplePayMerchantIdentifier(cartModel.getAdyenApplePayMerchantIdentifier());
        }


        getTransactionTemplate().execute(transactionStatus -> {
            //Create payment info
            getModelService().save(paymentInfo);
            cartModel.setPaymentInfo(paymentInfo);
            getModelService().save(cartModel);
            return null;
        });
    }

    private static void saveBrowserInfoOnPaymentInfo(BrowserInfo browserInfo, PaymentInfoModel paymentInfo) {
        if (browserInfo != null) {
            paymentInfo.setAdyenBrowserInfo(getBrowserInfoJson(browserInfo));
        }
    }

    private static String getBrowserInfoJson(BrowserInfo browserInfo) {
        try {
            return browserInfo.toJson();
        } catch (JsonProcessingException e) {
            LOGGER.error(EXCEPTION_DURING_PROCESSING_BROWSER_INFO, e);
            return EXCEPTION_DURING_PROCESSING_BROWSER_INFO + e.getMessage();
        }
    }


    @Override
    public OrderData placeOrderWithPayment(final HttpServletRequest request, final CartData cartData, PaymentRequest paymentRequest) throws Exception {
        //updateCartWithSessionData(cartData);

        RequestInfo requestInfo = new RequestInfo(request);
        requestInfo.setShopperLocale(getShopperLocale());

        PaymentResponse paymentResponse = getAdyenPaymentService().componentPayment(cartData, paymentRequest, requestInfo, getCheckoutCustomerStrategy().getCurrentUserForCheckout());
        if (PaymentResponse.ResultCodeEnum.PENDING == paymentResponse.getResultCode() || PaymentResponse.ResultCodeEnum.REDIRECTSHOPPER == paymentResponse.getResultCode()) {
            LOGGER.info("Placing pending order");
            placePendingOrder(paymentResponse.getResultCode());
            throw new AdyenNonAuthorizedPaymentException(paymentResponse);
        }
        if (PaymentResponse.ResultCodeEnum.AUTHORISED == paymentResponse.getResultCode()) {
            LOGGER.info("Creating authorized order");
            return createAuthorizedOrder(paymentResponse);
        }

        throw new AdyenNonAuthorizedPaymentException(paymentResponse);
    }


    public void saveBillingAddress(AddressForm billingAddress, PaymentInfoModel paymentInfo, CartModel cartModel, Boolean useAdyenDeliveryAddress) {
        if (!getCheckoutCustomerStrategy().isAnonymousCheckout() && billingAddress != null
                && billingAddress.isSaveInAddressBook()) {
            AddressData addressData = convertToAddressData(billingAddress);
            addressData.setVisibleInAddressBook(true);
            addressData.setShippingAddress(true);
            getUserFacade().addAddress(addressData);
        }
        if (useAdyenDeliveryAddress == true) {
            // Clone DeliveryAdress to BillingAddress
            final AddressModel clonedAddress = getModelService().clone(cartModel.getDeliveryAddress());
            clonedAddress.setBillingAddress(true);
            clonedAddress.setOwner(paymentInfo);
            paymentInfo.setBillingAddress(clonedAddress);
        } else {
            AddressModel billingAddressModel = convertToAddressModel(billingAddress);
            billingAddressModel.setOwner(paymentInfo);
            paymentInfo.setBillingAddress(billingAddressModel);
        }
    }

    public PaymentInfoModel createPaymentInfo(final CartModel cartModel, String adyenPaymentMethod) {
        final PaymentInfoModel paymentInfo = getModelService().create(PaymentInfoModel.class);
        paymentInfo.setUser(cartModel.getUser());
        paymentInfo.setSaved(false);
        paymentInfo.setCode(generateCcPaymentInfoCode(cartModel));
        paymentInfo.setAdyenPaymentMethod(adyenPaymentMethod);

        getModelService().save(paymentInfo);

        return paymentInfo;
    }

}