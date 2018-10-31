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

import java.io.IOException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import com.adyen.Util.HMACValidator;
import com.adyen.Util.Util;
import com.adyen.constants.HPPConstants;
import com.adyen.model.Amount;
import com.adyen.model.Card;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.PaymentMethod;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.recurring.Recurring;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.converters.PaymentsResponseConverter;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
import com.adyen.v6.forms.AdyenPaymentForm;
import com.adyen.v6.forms.validation.AdyenPaymentFormValidator;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenOrderService;
import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.AdyenTransactionService;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import static com.adyen.constants.ApiConstants.Redirect.Data.MD;
import static com.adyen.constants.BrandCodes.PAYPAL_ECS;
import static com.adyen.constants.HPPConstants.Fields.BRAND_CODE;
import static com.adyen.constants.HPPConstants.Fields.COUNTRY_CODE;
import static com.adyen.constants.HPPConstants.Fields.CURRENCY_CODE;
import static com.adyen.constants.HPPConstants.Fields.ISSUER_ID;
import static com.adyen.constants.HPPConstants.Fields.MERCHANT_ACCOUNT;
import static com.adyen.constants.HPPConstants.Fields.MERCHANT_REFERENCE;
import static com.adyen.constants.HPPConstants.Fields.MERCHANT_SIG;
import static com.adyen.constants.HPPConstants.Fields.PAYMENT_AMOUNT;
import static com.adyen.constants.HPPConstants.Fields.RES_URL;
import static com.adyen.constants.HPPConstants.Fields.SESSION_VALIDITY;
import static com.adyen.constants.HPPConstants.Fields.SHIP_BEFORE_DATE;
import static com.adyen.constants.HPPConstants.Fields.SKIN_CODE;
import static com.adyen.constants.HPPConstants.Response.SHOPPER_LOCALE;
import static com.adyen.v6.constants.Adyenv6coreConstants.OPENINVOICE_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER;
import static com.adyen.v6.constants.Adyenv6coreConstants.OPENINVOICE_METHODS_API;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_BOLETO;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_IDEAL;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;
import static com.adyen.v6.constants.Adyenv6coreConstants.RATEPAY;
import static de.hybris.platform.order.impl.DefaultCartService.SESSION_CART_PARAMETER_NAME;

/**
 * Adyen Checkout Facade for initiating payments using CC or APM
 */
public class DefaultAdyenCheckoutFacade implements AdyenCheckoutFacade {
    private BaseStoreService baseStoreService;
    private SessionService sessionService;
    private CartService cartService;
    private OrderFacade orderFacade;
    private CheckoutFacade checkoutFacade;
    private AdyenTransactionService adyenTransactionService;
    private OrderRepository orderRepository;
    private AdyenOrderService adyenOrderService;
    private CheckoutCustomerStrategy checkoutCustomerStrategy;
    private HMACValidator hmacValidator;
    private AdyenPaymentServiceFactory adyenPaymentServiceFactory;
    private ModelService modelService;
    private CommonI18NService commonI18NService;
    private KeyGenerator keyGenerator;
    private PaymentsResponseConverter paymentsResponseConverter;
    private FlexibleSearchService flexibleSearchService;
    private Converter<AddressData, AddressModel> addressReverseConverter;

    @Resource(name = "i18NFacade")
    private I18NFacade i18NFacade;

    public static final Logger LOGGER = Logger.getLogger(DefaultAdyenCheckoutFacade.class);

    public static final String SESSION_LOCKED_CART = "adyen_cart";
    public static final String SESSION_MD = "adyen_md";
    public static final String SESSION_CSE_TOKEN = "adyen_cse_token";
    public static final String SESSION_SF_CARD_NUMBER = "encryptedCardNumber";
    public static final String SESSION_SF_EXPIRY_MONTH = "encryptedExpiryMonth";
    public static final String SESSION_SF_EXPIRY_YEAR = "encryptedExpiryYear";
    public static final String SESSION_SF_SECURITY_CODE = "encryptedSecurityCode";
    public static final String THREE_D_MD = "MD";
    public static final String THREE_D_PARES = "PaRes";
    public static final String SESSION_PAYMENT_DATA = "adyen_payment_data";
    public static final String MODEL_SELECTED_PAYMENT_METHOD = "selectedPaymentMethod";
    public static final String MODEL_PAYMENT_METHODS = "paymentMethods";
    public static final String MODEL_ALLOWED_CARDS = "allowedCards";
    public static final String MODEL_REMEMBER_DETAILS = "showRememberTheseDetails";
    public static final String MODEL_STORED_CARDS = "storedCards";
    public static final String MODEL_DF_URL = "dfUrl";
    public static final String MODEL_ORIGIN_KEY = "originKey";
    public static final String MODEL_CHECKOUT_SHOPPER_HOST = "checkoutShopperHost";
    public static final String DF_VALUE = "dfValue";
    public static final String MODEL_OPEN_INVOICE_METHODS = "openInvoiceMethods";
    public static final String MODEL_SHOW_SOCIAL_SECURITY_NUMBER = "showSocialSecurityNumber";
    public static final String MODEL_SHOW_BOLETO = "showBoleto";
    public static final String CHECKOUT_SHOPPER_HOST_TEST = "checkoutshopper-test.adyen.com";
    public static final String CHECKOUT_SHOPPER_HOST_LIVE = "checkoutshopper-live.adyen.com";
    public static final String MODEL_IDEAL_ISSUER_LIST = "iDealissuerList";

    protected static final Set<String> HPP_RESPONSE_PARAMETERS = new HashSet<>(Arrays.asList(HPPConstants.Response.MERCHANT_REFERENCE,
                                                                                             HPPConstants.Response.SKIN_CODE,
                                                                                             HPPConstants.Response.SHOPPER_LOCALE,
                                                                                             HPPConstants.Response.PAYMENT_METHOD,
                                                                                             HPPConstants.Response.AUTH_RESULT,
                                                                                             HPPConstants.Response.PSP_REFERENCE,
                                                                                             HPPConstants.Response.MERCHANT_RETURN_DATA));

    public DefaultAdyenCheckoutFacade() {
        hmacValidator = new HMACValidator();
    }

    @Override
    public void validateHPPResponse(SortedMap<String, String> hppResponseData, String merchantSig) throws SignatureException {
        BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();

        String hmacKey = baseStore.getAdyenSkinHMAC();
        if (StringUtils.isEmpty(hmacKey)) {
            LOGGER.error("Empty HMAC Key");
            throw new SignatureException("Empty HMAC Key");
        }
        String dataToSign = getHmacValidator().getDataToSign(hppResponseData);
        String calculatedMerchantSig = getHmacValidator().calculateHMAC(dataToSign, hmacKey);
        LOGGER.debug("Calculated signature: " + calculatedMerchantSig + " from data: " + dataToSign);
        if (StringUtils.isEmpty(calculatedMerchantSig) || ! calculatedMerchantSig.equals(merchantSig)) {
            LOGGER.error("Signature does not match!");
            throw new SignatureException("Signatures doesn't match");
        }
    }

    @Override
    public void validateHPPResponse(final HttpServletRequest request) throws SignatureException {
        SortedMap<String, String> hppResponseData = getQueryParameters(request);

        LOGGER.debug("Received HPP response: " + hppResponseData);

        String merchantSig = request.getParameter(HPPConstants.Response.MERCHANT_SIG);
        if (StringUtils.isEmpty(merchantSig)) {
            LOGGER.error("MerchantSig was not provided");
            throw new SignatureException("MerchantSig was not provided");
        }

        validateHPPResponse(hppResponseData, merchantSig);
    }

    @Override
    public String getOriginKey() {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        return baseStore.getAdyenOriginKey();
    }

    @Override
    public String getCheckoutShopperHost() {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        if (baseStore.getAdyenTestMode()) {
            return CHECKOUT_SHOPPER_HOST_TEST;
        }

        return CHECKOUT_SHOPPER_HOST_LIVE;
    }

    @Override
    public String getHppUrl() {
        return getAdyenPaymentService().getHppEndpoint() + "/details.shtml";
    }

    @Override
    public void lockSessionCart() {
        getSessionService().setAttribute(SESSION_LOCKED_CART, cartService.getSessionCart());
        getSessionService().removeAttribute(SESSION_CART_PARAMETER_NAME);

        //Refresh session for registered users
        if (! getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            getCartService().getSessionCart();
        }
    }

    @Override
    public CartModel restoreSessionCart() throws InvalidCartException {
        CartModel cartModel = getSessionService().getAttribute(SESSION_LOCKED_CART);
        if (cartModel == null) {
            throw new InvalidCartException("Cart does not exist!");
        }

        getCartService().setSessionCart(cartModel);
        getSessionService().removeAttribute(SESSION_LOCKED_CART);

        return cartModel;
    }

    @Override
    public OrderData handleHPPResponse(final HttpServletRequest request) throws SignatureException {
        validateHPPResponse(request);

        String merchantReference = request.getParameter(HPPConstants.Response.MERCHANT_REFERENCE);
        String authResult = request.getParameter(HPPConstants.Response.AUTH_RESULT);

        OrderData orderData = null;
        //Restore the cart or find the created order
        try {
            restoreSessionCart();

            CartData cartData = getCheckoutFacade().getCheckoutCart();
            if (! cartData.getCode().equals(merchantReference)) {
                throw new InvalidCartException("Merchant reference doesn't match cart's code");
            }

            if (HPPConstants.Response.AUTH_RESULT_AUTHORISED.equals(authResult) || HPPConstants.Response.AUTH_RESULT_PENDING.equals(authResult)) {
                orderData = getCheckoutFacade().placeOrder();
            }
        } catch (InvalidCartException e) {
            LOGGER.warn("InvalidCartException", e);
            //Cart does not exist, retrieve order
            orderData = getOrderFacade().getOrderDetailsForCode(merchantReference);
        }

        return orderData;
    }

    @Override
    public OrderData authorisePayment(final CartData cartData) throws Exception {
        CustomerModel customer = null;
        if (! getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            customer = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        }

        PaymentsResponse paymentsResponse = getAdyenPaymentService().authorisePayment(cartData, RequestInfo.empty(), customer);

        //In case of Authorized: create order and authorize it
        if (PaymentsResponse.ResultCodeEnum.AUTHORISED == paymentsResponse.getResultCode()) {
            return createAuthorizedOrder(paymentsResponse);
        }

        //In case of Received: create order
        if (PaymentsResponse.ResultCodeEnum.RECEIVED == paymentsResponse.getResultCode()) {
            return createOrderFromPaymentsResponse(paymentsResponse);
        }

        throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
    }

    @Override
    public PaymentDetailsWsDTO addPaymentDetails(PaymentDetailsWsDTO paymentDetails) {
        CartModel cartModel = cartService.getSessionCart();

        final AddressModel billingAddress = createBillingAddress(paymentDetails);

        PaymentInfoModel paymentInfo = createPaymentInfo(cartModel, paymentDetails);
        paymentInfo.setBillingAddress(billingAddress);
        billingAddress.setOwner(paymentInfo);

        modelService.save(paymentInfo);

        cartModel.setPaymentInfo(paymentInfo);
        modelService.save(cartModel);

        return paymentDetails;
    }

    private AddressModel createBillingAddress(PaymentDetailsWsDTO paymentDetails) {
        String titleCode = paymentDetails.getBillingAddress().getTitleCode();
        final AddressModel billingAddress = getModelService().create(AddressModel.class);
        if (StringUtils.isNotBlank(titleCode)) {
            final TitleModel title = new TitleModel();
            title.setCode(titleCode);
            billingAddress.setTitle(getFlexibleSearchService().getModelByExample(title));
        }
        billingAddress.setFirstname(paymentDetails.getBillingAddress().getFirstName());
        billingAddress.setLastname(paymentDetails.getBillingAddress().getLastName());
        billingAddress.setLine1(paymentDetails.getBillingAddress().getLine1());
        billingAddress.setLine2(paymentDetails.getBillingAddress().getLine2());
        billingAddress.setTown(paymentDetails.getBillingAddress().getTown());
        billingAddress.setPostalcode(paymentDetails.getBillingAddress().getPostalCode());
        billingAddress.setCountry(getCommonI18NService().getCountry(paymentDetails.getBillingAddress().getCountry().getIsocode()));

        final AddressData addressData = new AddressData();
        addressData.setTitleCode(paymentDetails.getBillingAddress().getTitleCode());
        addressData.setFirstName(billingAddress.getFirstname());
        addressData.setLastName(billingAddress.getLastname());
        addressData.setLine1(billingAddress.getLine1());
        addressData.setLine2(billingAddress.getLine2());
        addressData.setTown(billingAddress.getTown());
        addressData.setPostalCode(billingAddress.getPostalcode());
        addressData.setBillingAddress(true);

        if (paymentDetails.getBillingAddress().getCountry() != null) {
            final CountryData countryData = getI18NFacade().getCountryForIsocode(paymentDetails.getBillingAddress().getCountry().getIsocode());
            addressData.setCountry(countryData);
        }
        if (paymentDetails.getBillingAddress().getRegion().getIsocode() != null) {
            final RegionData regionData = getI18NFacade().getRegion(paymentDetails.getBillingAddress().getCountry().getIsocode(), paymentDetails.getBillingAddress().getRegion().getIsocode());
            addressData.setRegion(regionData);
        }

        getAddressReverseConverter().convert(addressData, billingAddress);

        return billingAddress;
    }

    @Override
    public PaymentsResponse handleRedirectPayload(final String payload) {
        try {
            PaymentsResponse response = getAdyenPaymentService().getPaymentDetailsFromPayload(payload);
            restoreSessionCart();
            CartData cartData = getCheckoutFacade().getCheckoutCart();
            if (! cartData.getCode().equals(response.getMerchantReference())) {
                throw new InvalidCartException("Merchant reference doesn't match cart's code");
            }

            if (PaymentsResponse.ResultCodeEnum.RECEIVED == response.getAuthResponse() || PaymentsResponse.ResultCodeEnum.AUTHORISED == response.getAuthResponse()) {
                getCheckoutFacade().placeOrder();
            }

            return response;
        } catch (Exception e) {
            LOGGER.warn(e);
        }

        throw new IllegalArgumentException("Invalid payload");
    }

    @Override
    public OrderData authorisePayment(final HttpServletRequest request, final CartData cartData) throws Exception {
        CustomerModel customer = null;
        if (! getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            customer = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        }

        updateCartWithSessionData(cartData);
        String adyenPaymentMethod = cartData.getAdyenPaymentMethod();

        if(adyenPaymentMethod.equals(PAYMENT_METHOD_BOLETO) || adyenPaymentMethod.equals(PAYPAL_ECS) || adyenPaymentMethod.startsWith(RATEPAY)) {

            PaymentResult paymentResult = getAdyenPaymentService().authorise(cartData, request, customer);
            if (PaymentResult.ResultCodeEnum.AUTHORISED == paymentResult.getResultCode()) {
                return createAuthorizedOrder(paymentResult);
            }
            if (PaymentResult.ResultCodeEnum.RECEIVED == paymentResult.getResultCode()) {
                return createOrderFromPaymentResult(paymentResult);
            }
            throw new AdyenNonAuthorizedPaymentException(paymentResult);
        }

        PaymentsResponse paymentsResponse = getAdyenPaymentService().authorisePayment(cartData, new RequestInfo(request), customer);
        if (PaymentsResponse.ResultCodeEnum.AUTHORISED == paymentsResponse.getResultCode()) {
            return createAuthorizedOrder(paymentsResponse);
        }
        if (PaymentsResponse.ResultCodeEnum.RECEIVED == paymentsResponse.getResultCode()) {
            return createOrderFromPaymentsResponse(paymentsResponse);
        }
        if(PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER == paymentsResponse.getResultCode()) {
            if (PAYMENT_METHOD_CC.equals(adyenPaymentMethod) || adyenPaymentMethod.indexOf(PAYMENT_METHOD_ONECLICK) == 0) {
                getSessionService().setAttribute(SESSION_MD, paymentsResponse.getRedirect().getData().get(MD));
                getSessionService().setAttribute(SESSION_PAYMENT_DATA, paymentsResponse.getPaymentData());
            }
            lockSessionCart();
        }
        throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
    }

    private void updateCartWithSessionData(CartData cartData) {
        cartData.setAdyenCseToken(getSessionService().getAttribute(SESSION_CSE_TOKEN));
        cartData.setAdyenEncryptedCardNumber(getSessionService().getAttribute(SESSION_SF_CARD_NUMBER));
        cartData.setAdyenEncryptedExpiryMonth(getSessionService().getAttribute(SESSION_SF_EXPIRY_MONTH));
        cartData.setAdyenEncryptedExpiryYear(getSessionService().getAttribute(SESSION_SF_EXPIRY_YEAR));
        cartData.setAdyenEncryptedSecurityCode(getSessionService().getAttribute(SESSION_SF_SECURITY_CODE));

        getSessionService().removeAttribute(SESSION_CSE_TOKEN);
        getSessionService().removeAttribute(SESSION_SF_CARD_NUMBER);
        getSessionService().removeAttribute(SESSION_SF_EXPIRY_MONTH);
        getSessionService().removeAttribute(SESSION_SF_EXPIRY_YEAR);
        getSessionService().removeAttribute(SESSION_SF_SECURITY_CODE);
    }

    @Override
    public OrderData handle3DResponse(final HttpServletRequest request) throws Exception {
        String paRes = request.getParameter(THREE_D_PARES);
        String md = request.getParameter(THREE_D_MD);

        String sessionMd = getSessionService().getAttribute(SESSION_MD);
        String sessionPaymentData = getSessionService().getAttribute(SESSION_PAYMENT_DATA);

        try {
            //Check if MD matches in order to avoid authorizing wrong order
            if (sessionMd != null && !sessionMd.equals(md)) {
                throw new SignatureException("MD does not match!");
            }

            restoreSessionCart();
            PaymentsResponse paymentsResponse = getAdyenPaymentService().authorise3DPayment(sessionPaymentData, paRes, md);
                if (PaymentsResponse.ResultCodeEnum.AUTHORISED == paymentsResponse.getResultCode()) {
                    return createAuthorizedOrder(paymentsResponse);
                }
                throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
        } catch (ApiException e) {
            throw e;
        }
    }

    @Override
    public Map<String, String> initializeHostedPayment(final CartData cartData, final String redirectUrl) throws SignatureException, InvalidCartException {
        final String sessionValidity = Util.calculateSessionValidity();
        final SortedMap<String, String> hppFormData = new TreeMap<>();

        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        String merchantAccount = baseStore.getAdyenMerchantAccount();
        String skinCode = baseStore.getAdyenSkinCode();
        String hmacKey = baseStore.getAdyenSkinHMAC();

        Assert.notNull(merchantAccount);
        Assert.notNull(skinCode);
        Assert.notNull(hmacKey);

        Amount amount = Util.createAmount(cartData.getTotalPrice().getValue(), cartData.getTotalPrice().getCurrencyIso());

        //Identify country code based on shopper's delivery address
        String countryCode = "";
        AddressData deliveryAddress = cartData.getDeliveryAddress();
        if (deliveryAddress != null) {
            CountryData deliveryCountry = deliveryAddress.getCountry();
            if (deliveryCountry != null) {
                countryCode = deliveryCountry.getIsocode();
            }
        }

        CartModel cartModel = regenerateCartCode();
        String merchantReference = cartModel.getCode();

        hppFormData.put(PAYMENT_AMOUNT, String.valueOf(amount.getValue()));
        hppFormData.put(CURRENCY_CODE, cartData.getTotalPrice().getCurrencyIso());
        hppFormData.put(SHIP_BEFORE_DATE, sessionValidity);
        hppFormData.put(MERCHANT_REFERENCE, merchantReference);
        hppFormData.put(SKIN_CODE, skinCode);
        hppFormData.put(MERCHANT_ACCOUNT, merchantAccount);
        hppFormData.put(SESSION_VALIDITY, sessionValidity);
        hppFormData.put(BRAND_CODE, cartData.getAdyenPaymentMethod());
        hppFormData.put(ISSUER_ID, cartData.getAdyenIssuerId());
        hppFormData.put(COUNTRY_CODE, countryCode);
        hppFormData.put(RES_URL, redirectUrl);
        hppFormData.put(DF_VALUE, cartData.getAdyenDfValue());

        if (! StringUtils.isEmpty(getShopperLocale())) {
            hppFormData.put(SHOPPER_LOCALE, getShopperLocale());
        }

        String dataToSign = getHmacValidator().getDataToSign(hppFormData);
        String merchantSig = getHmacValidator().calculateHMAC(dataToSign, hmacKey);

        hppFormData.put(MERCHANT_SIG, merchantSig);

        //Lock the cart
        lockSessionCart();

        return hppFormData;
    }

    private CartModel regenerateCartCode() {
        final CartModel cartModel = cartService.getSessionCart();
        cartModel.setCode(String.valueOf(keyGenerator.generate()));
        cartService.saveOrder(cartModel);
        return cartModel;
    }

    /**
     * Create order and authorized TX
     */
    private OrderData createAuthorizedOrder(final PaymentsResponse paymentsResponse) throws InvalidCartException {
        final CartModel cartModel = cartService.getSessionCart();
        final String merchantTransactionCode = cartModel.getCode();

        //First save the transactions to the CartModel < AbstractOrderModel
        getAdyenTransactionService().authorizeOrderModel(cartModel, merchantTransactionCode, paymentsResponse.getPspReference());

        return createOrderFromPaymentsResponse(paymentsResponse);
    }

    /**
     * Create order and authorized TX
     */
    private OrderData createAuthorizedOrder(final PaymentResult paymentResult) throws InvalidCartException {
        PaymentsResponse paymentsResponse = paymentsResponseConverter.convert(paymentResult);
        return createAuthorizedOrder(paymentsResponse);
    }

    /**
     * Create order
     */
    private OrderData createOrderFromPaymentsResponse(final PaymentsResponse paymentsResponse) throws InvalidCartException {
        LOGGER.debug("Create order from paymentsResponse: " + paymentsResponse.getPspReference());

        OrderData orderData = getCheckoutFacade().placeOrder();
        OrderModel orderModel = orderRepository.getOrderModel(orderData.getCode());
        updateOrder(orderModel, paymentsResponse);

        orderData.setAdyenBoletoUrl(paymentsResponse.getBoletoUrl());
        orderData.setAdyenBoletoData(paymentsResponse.getBoletoData());
        orderData.setAdyenBoletoExpirationDate(paymentsResponse.getBoletoExpirationDate());
        orderData.setAdyenBoletoDueDate(paymentsResponse.getBoletoDueDate());

        return orderData;
    }

    /**
     * Create order
     */
    private OrderData createOrderFromPaymentResult(final PaymentResult paymentResult) throws InvalidCartException {
        PaymentsResponse paymentsResponse = paymentsResponseConverter.convert(paymentResult);
        return createOrderFromPaymentsResponse(paymentsResponse);
    }

    private void updateOrder(final OrderModel orderModel, final PaymentsResponse paymentsResponse) {
        try {
            adyenOrderService.updateOrderFromPaymentsResponse(orderModel, paymentsResponse);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void initializeCheckoutData(Model model) {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        AdyenPaymentService adyenPaymentService = getAdyenPaymentService();

        //Set APMs from Adyen HPP Directory Lookup
        List<PaymentMethod> alternativePaymentMethods = new ArrayList<>();
        String iDealissuerList=null;

        try {
            alternativePaymentMethods = adyenPaymentService.getPaymentMethods(cartData.getTotalPrice().getValue(),
                                                                              cartData.getTotalPrice().getCurrencyIso(),
                                                                              cartData.getDeliveryAddress().getCountry().getIsocode(),
                                                                              getShopperLocale(),
                                                                              null);
            PaymentMethod idealPaymentMethod= alternativePaymentMethods.stream()
                    .filter(paymentMethod -> ! paymentMethod.getType().isEmpty()
                            &&  PAYMENT_METHOD_IDEAL.equals(paymentMethod.getType())).findFirst().orElse(null);

            if(idealPaymentMethod!=null) {
                Gson gson = new Gson();
                iDealissuerList = gson.toJson(idealPaymentMethod.getDetails().get(0).getItems());
            }

            //Exclude cards, boleto and iDeal
            alternativePaymentMethods = alternativePaymentMethods.stream()
                                                                 .filter(paymentMethod -> ! paymentMethod.getType().isEmpty()
                                                                         && ! "scheme".equals(paymentMethod.getType())
                                                                         && ! PAYMENT_METHOD_IDEAL.equals(paymentMethod.getType())
                                                                         && paymentMethod.getType().indexOf(PAYMENT_METHOD_BOLETO) != 0)
                                                                 .collect(Collectors.toList());
        } catch (ApiException | IOException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        //Set allowed cards from BaseStore configuration
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        List<RecurringDetail> storedCards = new ArrayList<>();
        boolean showRememberTheseDetails = showRememberDetails();
        if (showRememberTheseDetails) {
            //Include stored cards
            CustomerModel customerModel = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
            try {
                storedCards = adyenPaymentService.getStoredCards(customerModel.getCustomerID());
            } catch (ApiException e) {
                LOGGER.error("API Exception", e);
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
        }

        // current selected PaymentMethod
        model.addAttribute(MODEL_SELECTED_PAYMENT_METHOD, cartData.getAdyenPaymentMethod());

        //Set HPP payment methods
        model.addAttribute(MODEL_PAYMENT_METHODS, alternativePaymentMethods);

        //Set allowed Credit Cards
        model.addAttribute(MODEL_ALLOWED_CARDS, baseStore.getAdyenAllowedCards());

        model.addAttribute(MODEL_REMEMBER_DETAILS, showRememberTheseDetails);
        model.addAttribute(MODEL_STORED_CARDS, storedCards);
        model.addAttribute(MODEL_ORIGIN_KEY, getOriginKey());
        model.addAttribute(MODEL_DF_URL, adyenPaymentService.getDeviceFingerprintUrl());
        model.addAttribute(MODEL_CHECKOUT_SHOPPER_HOST, getCheckoutShopperHost());
        model.addAttribute(SHOPPER_LOCALE, getShopperLocale());

        Set<String> recurringDetailReferences = new HashSet<>();
        if (storedCards != null) {
            recurringDetailReferences = storedCards.stream().map(RecurringDetail::getRecurringDetailReference).collect(Collectors.toSet());
        }

        //Set stored cards to model
        CartModel cartModel = cartService.getSessionCart();
        cartModel.setAdyenStoredCards(recurringDetailReferences);

        // OpenInvoice Methods
        model.addAttribute(MODEL_OPEN_INVOICE_METHODS, OPENINVOICE_METHODS_API);

        // retrieve shipping Country to define if social security number needs to be shown or date of birth field for openinvoice methods
        model.addAttribute(MODEL_SHOW_SOCIAL_SECURITY_NUMBER, showSocialSecurityNumber());

        //Include Boleto banks
        model.addAttribute(MODEL_SHOW_BOLETO, showBoleto());

        //Include Issuer List for iDEAL
        model.addAttribute(MODEL_IDEAL_ISSUER_LIST, iDealissuerList);
        modelService.save(cartModel);
    }

    @Override
    public boolean showBoleto() {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        //Check base store settings
        if (baseStore.getAdyenBoleto() == null || ! baseStore.getAdyenBoleto()) {
            return false;
        }

        CartData cartData = getCheckoutFacade().getCheckoutCart();
        String currency = cartData.getTotalPrice().getCurrencyIso();
        String country = cartData.getDeliveryAddress().getCountry().getIsocode();

        //Show only on Brasil with BRL
        return "BRL".equals(currency) && "BR".equals(country);
    }

    @Override
    public boolean showRememberDetails() {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        /*
         * The show remember me checkout should only be shown as the
         * user is logged in and the recurirng mode is set to ONECLICK or ONECLICK,RECURRING
         */
        RecurringContractMode recurringContractMode = baseStore.getAdyenRecurringContractMode();
        if (! getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            if (Recurring.ContractEnum.ONECLICK_RECURRING.name().equals(recurringContractMode.getCode()) || Recurring.ContractEnum.ONECLICK.name().equals(recurringContractMode.getCode())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean showSocialSecurityNumber() {
        Boolean showSocialSecurityNumber = false;
        final AddressData addressData = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
        String countryCode = addressData.getCountry().getIsocode();
        if (OPENINVOICE_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER.contains(countryCode)) {
            showSocialSecurityNumber = true;
        }
        return showSocialSecurityNumber;
    }

    @Override
    public PaymentInfoModel createPaymentInfo(final CartModel cartModel, AdyenPaymentForm adyenPaymentForm) {
        final PaymentInfoModel paymentInfo = modelService.create(PaymentInfoModel.class);
        paymentInfo.setUser(cartModel.getUser());
        paymentInfo.setSaved(false);
        paymentInfo.setCode(generateCcPaymentInfoCode(cartModel));

        // Clone DeliveryAdress to BillingAddress
        final AddressModel clonedAddress = modelService.clone(cartModel.getDeliveryAddress());
        clonedAddress.setBillingAddress(true);
        clonedAddress.setOwner(paymentInfo);
        paymentInfo.setBillingAddress(clonedAddress);

        paymentInfo.setAdyenPaymentMethod(adyenPaymentForm.getPaymentMethod());
        paymentInfo.setAdyenIssuerId(adyenPaymentForm.getIssuerId());

        paymentInfo.setAdyenRememberTheseDetails(adyenPaymentForm.getRememberTheseDetails());
        paymentInfo.setAdyenSelectedReference(adyenPaymentForm.getSelectedReference());

        // openinvoice fields
        paymentInfo.setAdyenDob(adyenPaymentForm.getDob());

        paymentInfo.setAdyenSocialSecurityNumber(adyenPaymentForm.getSocialSecurityNumber());

        // Boleto fields
        paymentInfo.setAdyenFirstName(adyenPaymentForm.getFirstName());
        paymentInfo.setAdyenLastName(adyenPaymentForm.getLastName());

        paymentInfo.setAdyenCardHolder(adyenPaymentForm.getCardHolder());

        modelService.save(paymentInfo);

        return paymentInfo;
    }

    public PaymentInfoModel createPaymentInfo(final CartModel cartModel, PaymentDetailsWsDTO paymentDetails) {
        final PaymentInfoModel paymentInfo = modelService.create(PaymentInfoModel.class);
        paymentInfo.setUser(cartModel.getUser());
        paymentInfo.setSaved(false);
        paymentInfo.setCode(generateCcPaymentInfoCode(cartModel));

        paymentInfo.setAdyenIssuerId(paymentDetails.getIssueNumber());

        paymentInfo.setAdyenCardHolder(paymentDetails.getAccountHolderName());
        paymentInfo.setEncryptedCardNumber(paymentDetails.getEncryptedCardNumber());
        paymentInfo.setEncryptedExpiryMonth(paymentDetails.getEncryptedExpiryMonth());
        paymentInfo.setEncryptedExpiryYear(paymentDetails.getEncryptedExpiryYear());
        paymentInfo.setEncryptedSecurityCode(paymentDetails.getEncryptedSecurityCode());
        paymentInfo.setAdyenRememberTheseDetails(paymentDetails.getSaveCardData());
        paymentInfo.setAdyenPaymentMethod(paymentDetails.getAdyenPaymentMethod());
        paymentInfo.setAdyenSelectedReference(paymentDetails.getAdyenSelectedReference());
        paymentInfo.setAdyenSocialSecurityNumber(paymentDetails.getAdyenSocialSecurityNumber());
        paymentInfo.setAdyenFirstName(paymentDetails.getAdyenFirstName());
        paymentInfo.setAdyenLastName(paymentDetails.getAdyenLastName());
        paymentInfo.setOwner(cartModel.getOwner());

        return paymentInfo;
    }

    @Override
    public void handlePaymentForm(AdyenPaymentForm adyenPaymentForm, BindingResult bindingResult) {
        //Validate form
        CartModel cartModel = cartService.getSessionCart();
        boolean showRememberDetails = showRememberDetails();
        boolean showSocialSecurityNumber = showSocialSecurityNumber();

        AdyenPaymentFormValidator adyenPaymentFormValidator = new AdyenPaymentFormValidator(cartModel.getAdyenStoredCards(), showRememberDetails, showSocialSecurityNumber);
        adyenPaymentFormValidator.validate(adyenPaymentForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return;
        }

        //Put encrypted data to session
        if (! StringUtils.isEmpty(adyenPaymentForm.getCseToken())) {
            getSessionService().setAttribute(SESSION_CSE_TOKEN, adyenPaymentForm.getCseToken());
        }
        if (! StringUtils.isEmpty(adyenPaymentForm.getEncryptedCardNumber())) {
            getSessionService().setAttribute(SESSION_SF_CARD_NUMBER, adyenPaymentForm.getEncryptedCardNumber());
        }
        if (! StringUtils.isEmpty(adyenPaymentForm.getEncryptedExpiryMonth())) {
            getSessionService().setAttribute(SESSION_SF_EXPIRY_MONTH, adyenPaymentForm.getEncryptedExpiryMonth());
        }
        if (! StringUtils.isEmpty(adyenPaymentForm.getEncryptedExpiryYear())) {
            getSessionService().setAttribute(SESSION_SF_EXPIRY_YEAR, adyenPaymentForm.getEncryptedExpiryYear());
        }
        if (! StringUtils.isEmpty(adyenPaymentForm.getEncryptedSecurityCode())) {
            getSessionService().setAttribute(SESSION_SF_SECURITY_CODE, adyenPaymentForm.getEncryptedSecurityCode());
        }

        //Update CartModel
        cartModel.setAdyenDfValue(adyenPaymentForm.getDfValue());

        //Create payment info
        PaymentInfoModel paymentInfo = createPaymentInfo(cartModel, adyenPaymentForm);
        cartModel.setPaymentInfo(paymentInfo);
        modelService.save(cartModel);
    }

    @Override
    public PaymentDetailsListWsDTO getPaymentDetails(String userId) throws IOException, ApiException {
        CustomerModel customer = getCheckoutCustomerStrategy().getCurrentUserForCheckout();

        List<RecurringDetail> recurringDetails = getAdyenPaymentService().getStoredCards(customer.getCustomerID());

        PaymentDetailsListWsDTO paymentDetailsListWsDTO = new PaymentDetailsListWsDTO();
        paymentDetailsListWsDTO.setPayments(toPaymentDetails(recurringDetails));

        return paymentDetailsListWsDTO;
    }

    private List<PaymentDetailsWsDTO> toPaymentDetails(List<RecurringDetail> recurringDetails) {
        return recurringDetails.stream().map(r -> toPaymentDetail(r)).collect(Collectors.toList());
    }

    private PaymentDetailsWsDTO toPaymentDetail(RecurringDetail recurringDetail) {
        PaymentDetailsWsDTO paymentDetailsWsDTO = new PaymentDetailsWsDTO();

        Card card = recurringDetail.getCard();

        if (card == null) {
            throw new RuntimeException("Card information not found");
        }

        paymentDetailsWsDTO.setAccountHolderName(card.getHolderName());
        paymentDetailsWsDTO.setCardNumber("**** **** **** " + card.getNumber());
        paymentDetailsWsDTO.setExpiryMonth(card.getExpiryMonth());
        paymentDetailsWsDTO.setExpiryYear(card.getExpiryYear());
        paymentDetailsWsDTO.setSubscriptionId(recurringDetail.getRecurringDetailReference());

        return paymentDetailsWsDTO;
    }

    private String getShopperLocale() {
        if (commonI18NService.getCurrentLanguage() != null) {
            return commonI18NService.getCurrentLanguage().getIsocode();
        }

        return null;
    }

    /**
     * Helper function for retrieving only GET parameters (of querystring)
     *
     * @param request HttpServletRequest request object
     * @return Sorted map with parameters
     */
    private static SortedMap<String, String> getQueryParameters(HttpServletRequest request) {
        SortedMap<String, String> queryParameters = new TreeMap<>();
        String queryString = request.getQueryString();

        if (StringUtils.isEmpty(queryString)) {
            return queryParameters;
        }

        String[] parameters = queryString.split("&");

        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            String key = keyValuePair[0];
            String value = request.getParameter(key);

            // Add only HPP parameters for signature calculation
            if (HPP_RESPONSE_PARAMETERS.contains(key) || key.startsWith("additionalData.")) {
                queryParameters.put(key, value);
            }
        }

        return queryParameters;
    }

    protected String generateCcPaymentInfoCode(final CartModel cartModel) {
        return cartModel.getCode() + "_" + UUID.randomUUID();
    }

    public AdyenPaymentService getAdyenPaymentService() {
        return adyenPaymentServiceFactory.createFromBaseStore(baseStoreService.getCurrentBaseStore());
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public OrderFacade getOrderFacade() {
        return orderFacade;
    }

    public void setOrderFacade(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }

    public CheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(CheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public AdyenTransactionService getAdyenTransactionService() {
        return adyenTransactionService;
    }

    public void setAdyenTransactionService(AdyenTransactionService adyenTransactionService) {
        this.adyenTransactionService = adyenTransactionService;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public AdyenOrderService getAdyenOrderService() {
        return adyenOrderService;
    }

    public void setAdyenOrderService(AdyenOrderService adyenOrderService) {
        this.adyenOrderService = adyenOrderService;
    }

    public CheckoutCustomerStrategy getCheckoutCustomerStrategy() {
        return checkoutCustomerStrategy;
    }

    public void setCheckoutCustomerStrategy(CheckoutCustomerStrategy checkoutCustomerStrategy) {
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
    }

    public HMACValidator getHmacValidator() {
        return hmacValidator;
    }

    public void setHmacValidator(HMACValidator hmacValidator) {
        this.hmacValidator = hmacValidator;
    }

    public AdyenPaymentServiceFactory getAdyenPaymentServiceFactory() {
        return adyenPaymentServiceFactory;
    }

    public void setAdyenPaymentServiceFactory(AdyenPaymentServiceFactory adyenPaymentServiceFactory) {
        this.adyenPaymentServiceFactory = adyenPaymentServiceFactory;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public PaymentsResponseConverter getPaymentsResponseConverter() {
        return paymentsResponseConverter;
    }

    public void setPaymentsResponseConverter(PaymentsResponseConverter paymentsResponseConverter) {
        this.paymentsResponseConverter = paymentsResponseConverter;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public Converter<AddressData, AddressModel> getAddressReverseConverter() {
        return addressReverseConverter;
    }

    public void setAddressReverseConverter(Converter<AddressData, AddressModel> addressReverseConverter) {
        this.addressReverseConverter = addressReverseConverter;
    }

    public I18NFacade getI18NFacade() {
        return i18NFacade;
    }

    public void setI18NFacade(I18NFacade i18NFacade) {
        this.i18NFacade = i18NFacade;
    }
}