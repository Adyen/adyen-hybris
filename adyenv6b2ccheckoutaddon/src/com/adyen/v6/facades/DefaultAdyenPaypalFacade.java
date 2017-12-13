/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.facades;

import java.security.SignatureException;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import com.adyen.v6.factory.AdyenAddressDataFactory;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import static com.adyen.constants.BrandCodes.PAYPAL_ECS;
import static com.adyen.constants.HPPConstants.Response.AUTH_RESULT;
import static com.adyen.constants.HPPConstants.Response.AUTH_RESULT_AUTHORISED;
import static com.adyen.constants.HPPConstants.Response.AUTH_RESULT_PENDING;
import static com.adyen.constants.HPPConstants.Response.MERCHANT_REFERENCE;

public class DefaultAdyenPaypalFacade implements AdyenPaypalFacade {
    private AdyenAddressDataFactory adyenAddressDataFactory;
    private AdyenCheckoutFacade adyenCheckoutFacade;
    private ModelService modelService;
    private CartService cartService;
    private CheckoutFacade checkoutFacade;
    private UserFacade userFacade;
    private Converter<AddressData, AddressModel> addressReverseConverter;

    private static final Logger LOG = Logger.getLogger(DefaultAdyenPaypalFacade.class);

    public PaymentInfoModel createPaymentInfo(CartModel cartModel, final HttpServletRequest request) {
        final PaymentInfoModel paymentInfo = modelService.create(PaymentInfoModel.class);
        paymentInfo.setUser(cartModel.getUser());
        paymentInfo.setSaved(false);
        paymentInfo.setCode(cartModel.getCode() + "_" + UUID.randomUUID());

        //Set billing address
        AddressData billingAddressData = adyenAddressDataFactory.createAddressData(request.getParameter(PAYPAL_ECS_BILLING_ADDRESS_COUNTRY),
                                                                                   request.getParameter(PAYPAL_ECS_BILLING_ADDRESS_STATE),
                                                                                   request.getParameter(PAYPAL_ECS_BILLING_ADDRESS_STATE_OR_PROVINCE),
                                                                                   request.getParameter(PAYPAL_ECS_BILLING_ADDRESS_CITY),
                                                                                   request.getParameter(PAYPAL_ECS_BILLING_ADDRESS_STREET),
                                                                                   request.getParameter(PAYPAL_ECS_BILLING_ADDRESS_POSTAL_CODE),
                                                                                   request.getParameter(PAYPAL_ECS_SHOPPER_FIRST_NAME),
                                                                                   request.getParameter(PAYPAL_ECS_SHOPPER_LAST_NAME));

        AddressModel billingAddressModel = getModelService().create(AddressModel.class);
        getAddressReverseConverter().convert(billingAddressData, billingAddressModel);

        billingAddressModel.setBillingAddress(true);
        billingAddressModel.setOwner(paymentInfo);
        paymentInfo.setBillingAddress(billingAddressModel);

        //Add PP ECS data to the payment info
        paymentInfo.setAdyenPaymentMethod(PAYPAL_ECS);
        paymentInfo.setAdyenPaypalPayerId(request.getParameter(PAYPAL_ECS_PAYMENT_PAYER_ID));
        paymentInfo.setAdyenPaypalEcsToken(request.getParameter(PAYPAL_ECS_PAYMENT_TOKEN));

        return paymentInfo;
    }

    @Override
    public boolean handlePaypalECSResponse(final HttpServletRequest request) throws SignatureException, InvalidCartException {
        //Validate merchant signature
        adyenCheckoutFacade.validateHPPResponse(request);

        adyenCheckoutFacade.restoreSessionCart();

        //Validate merchant reference
        String merchantReference = request.getParameter(MERCHANT_REFERENCE);
        CartData cartData = getCheckoutFacade().getCheckoutCart();
        if (! cartData.getCode().equals(merchantReference)) {
            throw new InvalidCartException("Merchant reference doesn't match cart's code");
        }

        //Return true for Authorised or Pending result
        String authResult = request.getParameter(AUTH_RESULT);
        return AUTH_RESULT_PENDING.equals(authResult) || AUTH_RESULT_AUTHORISED.equals(authResult);
    }

    @Override
    public void updateCart(final HttpServletRequest request, final boolean updateExistingDeliveryAddress) {
        CartData cartData = checkoutFacade.getCheckoutCart();
        if (cartData == null) {
            return;
        }

        //Create a delivery address from PP response
        if (updateExistingDeliveryAddress || cartData.getDeliveryAddress() == null) {
            final AddressData deliveryAddress = adyenAddressDataFactory.createAddressData(request.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_COUNTRY),
                                                                                          request.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_STATE),
                                                                                          request.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_STATE_OR_PROVINCE),
                                                                                          request.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_CITY),
                                                                                          request.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_STREET),
                                                                                          request.getParameter(PAYPAL_ECS_DELIVERY_ADDRESS_POSTAL_CODE),
                                                                                          request.getParameter(PAYPAL_ECS_SHOPPER_FIRST_NAME),
                                                                                          request.getParameter(PAYPAL_ECS_SHOPPER_LAST_NAME));

            deliveryAddress.setShippingAddress(true);
            userFacade.addAddress(deliveryAddress);
            checkoutFacade.setDeliveryAddress(deliveryAddress);

            LOG.debug("setting delivery address for " + cartService.getSessionCart().getCode());
        }

        //Generating payment info
        CartModel cartModel = getCartService().getSessionCart();
        PaymentInfoModel paymentInfo = createPaymentInfo(cartModel, request);
        modelService.save(paymentInfo);
        cartModel.setPaymentInfo(paymentInfo);

        cartService.saveOrder(cartService.getSessionCart());
    }

    @Override
    public Map<String, String> initializePaypalECS(final String redirectUrl) throws SignatureException, InvalidCartException {
        CartData cartData = checkoutFacade.getCheckoutCart();

        cartData.setAdyenPaymentMethod(PAYPAL_ECS);
        cartData.setAdyenIssuerId(null);

        return adyenCheckoutFacade.initializeHostedPayment(cartData, redirectUrl);
    }

    public AdyenAddressDataFactory getAdyenAddressDataFactory() {
        return adyenAddressDataFactory;
    }

    public void setAdyenAddressDataFactory(AdyenAddressDataFactory adyenAddressDataFactory) {
        this.adyenAddressDataFactory = adyenAddressDataFactory;
    }

    public AdyenCheckoutFacade getAdyenCheckoutFacade() {
        return adyenCheckoutFacade;
    }

    public void setAdyenCheckoutFacade(AdyenCheckoutFacade adyenCheckoutFacade) {
        this.adyenCheckoutFacade = adyenCheckoutFacade;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public CheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(CheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public Converter<AddressData, AddressModel> getAddressReverseConverter() {
        return addressReverseConverter;
    }

    public void setAddressReverseConverter(Converter<AddressData, AddressModel> addressReverseConverter) {
        this.addressReverseConverter = addressReverseConverter;
    }
}