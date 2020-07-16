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

import com.adyen.Util.HMACValidator;
import com.adyen.Util.Util;
import com.adyen.constants.HPPConstants;
import com.adyen.model.Amount;
import com.adyen.model.Card;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.CheckoutPaymentsAction;
import com.adyen.model.checkout.PaymentMethod;
import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.checkout.StoredPaymentMethod;
import com.adyen.model.nexo.ErrorConditionType;
import com.adyen.model.nexo.ResultType;
import com.adyen.model.recurring.Recurring;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.converters.PaymentsResponseConverter;
import com.adyen.v6.converters.PosPaymentResponseConverter;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
import com.adyen.v6.forms.AddressForm;
import com.adyen.v6.forms.AdyenPaymentForm;
import com.adyen.v6.forms.validation.AdyenPaymentFormValidator;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenOrderService;
import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.AdyenTransactionService;
import com.adyen.v6.util.TerminalAPIUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.i18n.comparators.CountryComparator;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.converters.populator.AddressPopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.adyen.constants.ApiConstants.Redirect.Data.MD;
import static com.adyen.constants.ApiConstants.ThreeDS2Property.CHALLENGE_RESULT;
import static com.adyen.constants.ApiConstants.ThreeDS2Property.FINGERPRINT_RESULT;
import static com.adyen.constants.ApiConstants.ThreeDS2Property.THREEDS2_CHALLENGE_TOKEN;
import static com.adyen.constants.ApiConstants.ThreeDS2Property.THREEDS2_FINGERPRINT_TOKEN;
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
import static com.adyen.v6.constants.Adyenv6coreConstants.ISSUER_PAYMENT_METHODS;
import static com.adyen.v6.constants.Adyenv6coreConstants.KLARNA;
import static com.adyen.v6.constants.Adyenv6coreConstants.OPENINVOICE_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER;
import static com.adyen.v6.constants.Adyenv6coreConstants.OPENINVOICE_METHODS_API;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_BOLETO;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_MULTIBANCO;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_SEPA_DIRECTDEBIT;
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
    private PosPaymentResponseConverter posPaymentResponseConverter;
    private Converter<CountryModel, CountryData> countryConverter;
    private Converter<OrderModel, OrderData> orderConverter;
    private CartFactory cartFactory;
    private CalculationService calculationService;
    private AddressPopulator addressPopulator;

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
    public static final String SESSION_CARD_BRAND = "cardBrand";
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
    public static final String MODEL_SHOW_POS = "showPos";
    public static final String MODEL_SHOW_COMBO_CARD = "showComboCard";
    public static final String CHECKOUT_SHOPPER_HOST_TEST = "checkoutshopper-test.adyen.com";
    public static final String CHECKOUT_SHOPPER_HOST_LIVE = "checkoutshopper-live.adyen.com";
    public static final String MODEL_ISSUER_LISTS = "issuerLists";
    public static final String MODEL_CONNECTED_TERMINAL_LIST = "connectedTerminalList";
    public static final String MODEL_ENVIRONMENT_MODE = "environmentMode";
    public static final String MODEL_AMOUNT = "amount";
    public static final String MODEL_IMMEDIATE_CAPTURE = "immediateCapture";
    public static final String MODEL_PAYPAL_MERCHANT_ID = "paypalMerchantId";



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
    public String getOriginKey(HttpServletRequest request) throws IOException, ApiException {
        return getAdyenPaymentService().getOriginKey(getBaseURL(request));
    }

    public String getBaseURL(HttpServletRequest request) {
        String currentRequestURL = request.getRequestURL().toString();
        int requestUrlLength = currentRequestURL.length();
        int requestUriLength = request.getRequestURI().length();
        return currentRequestURL.substring(0, requestUrlLength - requestUriLength);
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
    public String getEnvironmentMode() {
        if (baseStoreService.getCurrentBaseStore().getAdyenTestMode()) {
            return "test";
        }
        return "live";
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
        getSessionService().removeAttribute(SESSION_PAYMENT_DATA);
        getSessionService().removeAttribute(THREEDS2_FINGERPRINT_TOKEN);
        getSessionService().removeAttribute(THREEDS2_CHALLENGE_TOKEN);
        getSessionService().removeAttribute(PAYMENT_METHOD);

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

        if (PaymentsResponse.ResultCodeEnum.PRESENTTOSHOPPER == paymentsResponse.getResultCode()) {
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
    public PaymentsResponse handleRedirectPayload(HashMap<String, String> details) {
        try {
            PaymentsResponse response;
            String paymentMethod = getSessionService().getAttribute(PAYMENT_METHOD);

            if (paymentMethod != null && paymentMethod.startsWith(KLARNA)) {
                response = getAdyenPaymentService().getPaymentDetailsFromPayload(details, getSessionService().getAttribute(SESSION_PAYMENT_DATA));
            } else {
                response = getAdyenPaymentService().getPaymentDetailsFromPayload(details);
            }

            String orderCode = response.getMerchantReference();
            OrderModel orderModel = retrieveOrder(orderCode);

            if (PaymentsResponse.ResultCodeEnum.RECEIVED == response.getResultCode() || PaymentsResponse.ResultCodeEnum.AUTHORISED == response.getResultCode()) {
                updateOrderPaymentStatusAndInfo(orderModel, OrderStatus.PAYMENT_AUTHORIZED, response);
            } else {
                updateOrderPaymentStatusAndInfo(orderModel, OrderStatus.CANCELLED, response);
                restoreCartFromOrder(orderCode);
            }

            return response;
        } catch (Exception e) {
            LOGGER.warn(e);
        }

        throw new IllegalArgumentException("Invalid payload");
    }

    private void updateOrderPaymentStatusAndInfo(OrderModel orderModel, OrderStatus newStatus, PaymentsResponse paymentsResponse) {
        //update status
        orderModel.setStatus(newStatus);
        orderModel.setStatusInfo(paymentsResponse.getPspReference() + " - " + paymentsResponse.getResultCode().getValue());
        getModelService().save(orderModel);

        //update payment info
        getAdyenTransactionService().createPaymentTransactionFromResultCode(orderModel,
                orderModel.getCode(),
                paymentsResponse.getPspReference(),
                paymentsResponse.getResultCode());

        getAdyenOrderService().updateOrderFromPaymentsResponse(orderModel, paymentsResponse);
    }

    @Override
    public OrderData authorisePayment(final HttpServletRequest request, final CartData cartData) throws Exception {
        CustomerModel customer = null;
        if (! getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            customer = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        }

        updateCartWithSessionData(cartData);
        String adyenPaymentMethod = cartData.getAdyenPaymentMethod();

        if (adyenPaymentMethod.equals(PAYPAL_ECS) || adyenPaymentMethod.startsWith(RATEPAY)) {

            PaymentResult paymentResult = getAdyenPaymentService().authorise(cartData, request, customer);
            if (PaymentResult.ResultCodeEnum.AUTHORISED == paymentResult.getResultCode()) {
                return createAuthorizedOrder(paymentResult);
            }
            if (PaymentResult.ResultCodeEnum.RECEIVED == paymentResult.getResultCode()) {
                return createOrderFromPaymentResult(paymentResult);
            }
            throw new AdyenNonAuthorizedPaymentException(paymentResult);
        }

        RequestInfo requestInfo = new RequestInfo(request);
        requestInfo.setShopperLocale(getShopperLocale());

        PaymentsResponse paymentsResponse = getAdyenPaymentService().authorisePayment(cartData, requestInfo, customer);
        PaymentsResponse.ResultCodeEnum resultCode = paymentsResponse.getResultCode();
        if (PaymentsResponse.ResultCodeEnum.AUTHORISED == resultCode) {
            return createAuthorizedOrder(paymentsResponse);
        }
        if (PaymentsResponse.ResultCodeEnum.RECEIVED == resultCode) {
            return createOrderFromPaymentsResponse(paymentsResponse);
        }
        if (PaymentsResponse.ResultCodeEnum.PRESENTTOSHOPPER == resultCode) {
            return createOrderFromPaymentsResponse(paymentsResponse);
        }
        if (PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER == resultCode) {
            placePendingOrder(resultCode);
            if (PAYMENT_METHOD_CC.equals(adyenPaymentMethod) || adyenPaymentMethod.indexOf(PAYMENT_METHOD_ONECLICK) == 0) {
                getSessionService().setAttribute(SESSION_MD, paymentsResponse.getRedirect().getData().get(MD));
                getSessionService().setAttribute(SESSION_PAYMENT_DATA, paymentsResponse.getPaymentData());
            }
            if (adyenPaymentMethod.startsWith(KLARNA)) {
                getSessionService().setAttribute(PAYMENT_METHOD, adyenPaymentMethod);
                getSessionService().setAttribute(SESSION_PAYMENT_DATA, paymentsResponse.getPaymentData());
            }
        }
        if (PaymentsResponse.ResultCodeEnum.IDENTIFYSHOPPER == resultCode) {
            placePendingOrder(resultCode);
            if (PAYMENT_METHOD_CC.equals(adyenPaymentMethod) || adyenPaymentMethod.indexOf(PAYMENT_METHOD_ONECLICK) == 0) {
                getSessionService().setAttribute(THREEDS2_FINGERPRINT_TOKEN, paymentsResponse.getAuthentication().get(THREEDS2_FINGERPRINT_TOKEN));
                getSessionService().setAttribute(SESSION_PAYMENT_DATA, paymentsResponse.getPaymentData());
            }
        }
        if (PaymentsResponse.ResultCodeEnum.CHALLENGESHOPPER == resultCode) {
            placePendingOrder(resultCode);
            if (PAYMENT_METHOD_CC.equals(adyenPaymentMethod) || adyenPaymentMethod.indexOf(PAYMENT_METHOD_ONECLICK) == 0) {
                getSessionService().setAttribute(THREEDS2_CHALLENGE_TOKEN, paymentsResponse.getAuthentication().get(THREEDS2_CHALLENGE_TOKEN));
                getSessionService().setAttribute(SESSION_PAYMENT_DATA, paymentsResponse.getPaymentData());
            }
        }

        throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
    }

    @Override
    public PaymentsResponse componentPayment(final HttpServletRequest request, final CartData cartData, final PaymentMethodDetails paymentMethodDetails) throws Exception {
        CustomerModel customer = null;
        if (! getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            customer = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        }

        updateCartWithSessionData(cartData);

        RequestInfo requestInfo = new RequestInfo(request);
        requestInfo.setShopperLocale(getShopperLocale());

        PaymentsResponse paymentsResponse = getAdyenPaymentService().componentPayment(cartData, paymentMethodDetails, requestInfo, customer);
        if(PaymentsResponse.ResultCodeEnum.PENDING != paymentsResponse.getResultCode()) {
            //TODO: Check about other status
            throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
        }

        //Lock the cart to prevent changes while the payment is pending
        lockSessionCart();

        return paymentsResponse;
    }

    @Override
    public PaymentsResponse componentDetails(final HttpServletRequest request, final Map<String, String> details, final String paymentData) throws Exception {

        PaymentsResponse response = getAdyenPaymentService().getPaymentDetailsFromPayload(details, paymentData);

        restoreSessionCart();
        CartData cartData = getCheckoutFacade().getCheckoutCart();
        if (!cartData.getCode().equals(response.getMerchantReference())) {
            throw new InvalidCartException("Merchant reference doesn't match cart's code");
        }

        //Lock cart again to be handled on results call
        lockSessionCart();

        return response;
    }

    private void updateCartWithSessionData(CartData cartData) {
        cartData.setAdyenCseToken(getSessionService().getAttribute(SESSION_CSE_TOKEN));
        cartData.setAdyenEncryptedCardNumber(getSessionService().getAttribute(SESSION_SF_CARD_NUMBER));
        cartData.setAdyenEncryptedExpiryMonth(getSessionService().getAttribute(SESSION_SF_EXPIRY_MONTH));
        cartData.setAdyenEncryptedExpiryYear(getSessionService().getAttribute(SESSION_SF_EXPIRY_YEAR));
        cartData.setAdyenEncryptedSecurityCode(getSessionService().getAttribute(SESSION_SF_SECURITY_CODE));
        cartData.setAdyenCardBrand(getSessionService().getAttribute(SESSION_CARD_BRAND));

        getSessionService().removeAttribute(SESSION_CSE_TOKEN);
        getSessionService().removeAttribute(SESSION_SF_CARD_NUMBER);
        getSessionService().removeAttribute(SESSION_SF_EXPIRY_MONTH);
        getSessionService().removeAttribute(SESSION_SF_EXPIRY_YEAR);
        getSessionService().removeAttribute(SESSION_SF_SECURITY_CODE);
        getSessionService().removeAttribute(SESSION_CARD_BRAND);
        getSessionService().removeAttribute(PAYMENT_METHOD);
        getSessionService().removeAttribute(SESSION_PAYMENT_DATA);
    }

    @Override
    public OrderData handle3DResponse(final HttpServletRequest request) throws Exception {
        String paRes = request.getParameter(THREE_D_PARES);
        String md = request.getParameter(THREE_D_MD);

        String sessionMd = getSessionService().getAttribute(SESSION_MD);
        String sessionPaymentData = getSessionService().getAttribute(SESSION_PAYMENT_DATA);

        //Check if MD matches in order to avoid authorizing wrong order
        if (sessionMd != null && ! sessionMd.equals(md)) {
            throw new SignatureException("MD does not match!");
        }

        PaymentsResponse paymentsResponse = getAdyenPaymentService().authorise3DPayment(sessionPaymentData, paRes, md);

        String orderCode = paymentsResponse.getMerchantReference();
        OrderModel orderModel = retrieveOrder(orderCode);

        if (PaymentsResponse.ResultCodeEnum.AUTHORISED == paymentsResponse.getResultCode()) {
            updateOrderPaymentStatusAndInfo(orderModel, OrderStatus.PAYMENT_AUTHORIZED, paymentsResponse);

            OrderData orderData = getOrderConverter().convert(orderModel);
            return fillOrderDataWithPaymentInfo(orderData, paymentsResponse);
        }

        updateOrderPaymentStatusAndInfo(orderModel, OrderStatus.CANCELLED, paymentsResponse);
        restoreCartFromOrder(orderCode);

        throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
    }

    @Override
    public OrderData handle3DS2Response(final HttpServletRequest request) throws Exception {

        String fingerprintResult = request.getParameter(FINGERPRINT_RESULT);
        String challengeResult = request.getParameter(CHALLENGE_RESULT);
        String paymentData = getSessionService().getAttribute(SESSION_PAYMENT_DATA);

        String type = "";
        String token = "";

        if (challengeResult != null && ! challengeResult.isEmpty()) {
            type = "challenge";
            token = challengeResult;
        } else if (fingerprintResult != null && ! fingerprintResult.isEmpty()) {
            type = "fingerprint";
            token = fingerprintResult;

        }

        try {
            PaymentsResponse paymentsResponse = getAdyenPaymentService().authorise3DS2Payment(paymentData, token, type);
            if (paymentsResponse.getResultCode() != PaymentsResponse.ResultCodeEnum.IDENTIFYSHOPPER && paymentsResponse.getResultCode() != PaymentsResponse.ResultCodeEnum.CHALLENGESHOPPER) {
                restoreSessionCart();
            }
            if (PaymentsResponse.ResultCodeEnum.AUTHORISED == paymentsResponse.getResultCode()) {

                return createAuthorizedOrder(paymentsResponse);
            }
            throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
        } catch (ApiException e) {
            if (type.equals("challenge")) {
                LOGGER.debug("Restoring cart because ApiException occurred after challengeResult ");
                restoreSessionCart();
            }
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
        getAdyenOrderService().updateOrderFromPaymentsResponse(orderModel, paymentsResponse);

        return fillOrderDataWithPaymentInfo(orderData, paymentsResponse);
    }

    private OrderData fillOrderDataWithPaymentInfo(OrderData orderData, PaymentsResponse paymentsResponse) {
        orderData.setAdyenBoletoUrl(paymentsResponse.getBoletoUrl());
        orderData.setAdyenBoletoData(paymentsResponse.getBoletoData());
        orderData.setAdyenBoletoBarCodeReference(paymentsResponse.getBoletoBarCodeReference());
        orderData.setAdyenBoletoExpirationDate(paymentsResponse.getBoletoExpirationDate());
        orderData.setAdyenBoletoDueDate(paymentsResponse.getBoletoDueDate());

        CheckoutPaymentsAction action = paymentsResponse.getAction();
        if (action != null && PAYMENT_METHOD_MULTIBANCO.equals(action.getPaymentMethodType())) {
            orderData.setAdyenMultibancoEntity(action.getEntity());
            orderData.setAdyenMultibancoAmount(BigDecimal.valueOf(action.getInitialAmount().getValue()));
            orderData.setAdyenMultibancoDeadline(action.getExpiresAt());
            orderData.setAdyenMultibancoReference(action.getReference());
        }

        if (paymentsResponse.getAdditionalData() != null) {
            orderData.setAdyenPosReceipt(paymentsResponse.getAdditionalData().get("pos.receipt"));
        }

        return orderData;
    }

    private OrderData placePendingOrder(PaymentsResponse.ResultCodeEnum resultCode) throws InvalidCartException {
        OrderData orderData = getCheckoutFacade().placeOrder();

        OrderModel orderModel = orderRepository.getOrderModel(orderData.getCode());
        orderModel.setStatus(OrderStatus.PAYMENT_PENDING);
        orderModel.setStatusInfo(resultCode.getValue());
        getModelService().save(orderModel);

        return orderData;
    }

    /**
     * Create order
     */
    private OrderData createOrderFromPaymentResult(final PaymentResult paymentResult) throws InvalidCartException {
        PaymentsResponse paymentsResponse = paymentsResponseConverter.convert(paymentResult);
        return createOrderFromPaymentsResponse(paymentsResponse);
    }

    @Override
    public void initializeCheckoutData(Model model) {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        AdyenPaymentService adyenPaymentService = getAdyenPaymentService();
        List<PaymentMethod> alternativePaymentMethods;
        List<String> connectedTerminalList = null;
        List<StoredPaymentMethod> storedPaymentMethodList = null;
        Map<String, String> issuerLists = new HashMap<>();
        BaseStoreModel baseStore;
        CustomerModel customerModel = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        PaymentMethodsResponse response = new PaymentMethodsResponse();
        CartModel cartModel = cartService.getSessionCart();

        try {
            if (showPos()) {
                connectedTerminalList = adyenPaymentService.getConnectedTerminals().getUniqueTerminalIds();
            }

            response = adyenPaymentService.getPaymentMethodsResponse(cartData.getTotalPrice().getValue(),
                                                                     cartData.getTotalPrice().getCurrencyIso(),
                                                                     cartData.getDeliveryAddress().getCountry().getIsocode(),
                                                                     getShopperLocale(),
                                                                     customerModel.getCustomerID());
        } catch (ApiException | IOException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }


        alternativePaymentMethods = response.getPaymentMethods();

        List<PaymentMethod> issuerPaymentMethods = alternativePaymentMethods.stream()
                                                                            .filter(paymentMethod -> ! paymentMethod.getType().isEmpty() && ISSUER_PAYMENT_METHODS.contains(paymentMethod.getType()))
                                                                            .collect(Collectors.toList());
        if (! CollectionUtils.isEmpty(issuerPaymentMethods)) {
            Gson gson = new Gson();
            for (PaymentMethod paymentMethod : issuerPaymentMethods) {
                issuerLists.put(paymentMethod.getType(), gson.toJson(paymentMethod.getDetails()));
            }
        }

        Optional<PaymentMethod> sepaDirectDebit = alternativePaymentMethods.stream().
                                                                            filter(paymentMethod -> ! paymentMethod.getType().isEmpty() &&
                                                                                    PAYMENT_METHOD_SEPA_DIRECTDEBIT.contains(paymentMethod.getType())).findFirst()
                ;
        if(sepaDirectDebit.isPresent())
        {
            model.addAttribute(PAYMENT_METHOD_SEPA_DIRECTDEBIT, true);
        }

        //Exclude cards, boleto, bcmc and bcmc_mobile_QR and iDeal
        alternativePaymentMethods = alternativePaymentMethods.stream()
                                                             .filter(paymentMethod -> ! paymentMethod.getType().isEmpty() && ! isHiddenPaymentMethod(paymentMethod))
                                                             .collect(Collectors.toList());

        baseStore = baseStoreService.getCurrentBaseStore();
        if (showRememberDetails()) {
            //Include stored cards
            storedPaymentMethodList = response.getStoredPaymentMethods();
            Set<String> recurringDetailReferences = new HashSet<>();
            if (storedPaymentMethodList != null) {
                recurringDetailReferences = storedPaymentMethodList.stream().map(StoredPaymentMethod::getId).collect(Collectors.toSet());
            }
            cartModel.setAdyenStoredCards(recurringDetailReferences);

        }

        Amount amount = Util.createAmount(cartData.getTotalPrice().getValue(), cartData.getTotalPrice().getCurrencyIso());

        // current selected PaymentMethod
        model.addAttribute(MODEL_SELECTED_PAYMENT_METHOD, cartData.getAdyenPaymentMethod());

        //Set HPP payment methods
        model.addAttribute(MODEL_PAYMENT_METHODS, alternativePaymentMethods);

        //Set allowed Credit Cards
        model.addAttribute(MODEL_ALLOWED_CARDS, baseStore.getAdyenAllowedCards());

        model.addAttribute(MODEL_REMEMBER_DETAILS, showRememberDetails());
        model.addAttribute(MODEL_STORED_CARDS, storedPaymentMethodList);
        model.addAttribute(MODEL_DF_URL, adyenPaymentService.getDeviceFingerprintUrl());
        model.addAttribute(MODEL_CHECKOUT_SHOPPER_HOST, getCheckoutShopperHost());
        model.addAttribute(MODEL_ENVIRONMENT_MODE, getEnvironmentMode());
        model.addAttribute(SHOPPER_LOCALE, getShopperLocale());

        // OpenInvoice Methods
        model.addAttribute(MODEL_OPEN_INVOICE_METHODS, OPENINVOICE_METHODS_API);

        // retrieve shipping Country to define if social security number needs to be shown or date of birth field for openinvoice methods
        model.addAttribute(MODEL_SHOW_SOCIAL_SECURITY_NUMBER, showSocialSecurityNumber());

        //Include Boleto banks
        model.addAttribute(MODEL_SHOW_BOLETO, showBoleto());

        //Enable combo card flag
        model.addAttribute(MODEL_SHOW_COMBO_CARD, showComboCard());

        //Include POS Enable configuration
        model.addAttribute(MODEL_SHOW_POS, showPos());
        //Include connnected terminal List for POS
        model.addAttribute(MODEL_CONNECTED_TERMINAL_LIST, connectedTerminalList);
        //Include Issuer Lists
        model.addAttribute(MODEL_ISSUER_LISTS, issuerLists);

        //Include information for components
        model.addAttribute(MODEL_AMOUNT, amount);
        model.addAttribute(MODEL_IMMEDIATE_CAPTURE, isImmediateCapture());
        model.addAttribute(MODEL_PAYPAL_MERCHANT_ID, baseStore.getAdyenPaypalMerchantId());

        modelService.save(cartModel);
    }

    private boolean isHiddenPaymentMethod(PaymentMethod paymentMethod) {
        String paymentMethodType = paymentMethod.getType();

        if (paymentMethodType == null || paymentMethodType.isEmpty() ||
                paymentMethodType.equals("scheme") ||
                paymentMethodType.equals("bcmc") ||
                paymentMethodType.equals("bcmc_mobile_QR") ||
                (paymentMethodType.contains("wechatpay")
                        && ! paymentMethodType.equals("wechatpayWeb")) ||
                paymentMethodType.startsWith(PAYMENT_METHOD_BOLETO) ||
                paymentMethodType.contains(PAYMENT_METHOD_SEPA_DIRECTDEBIT) ||
                ISSUER_PAYMENT_METHODS.contains(paymentMethodType)) {
            return true;
        }
        return false;
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
    public boolean showComboCard() {
        CartData cartData = getCheckoutFacade().getCheckoutCart();
        String currency = cartData.getTotalPrice().getCurrencyIso();
        return "BRL".equals(currency);
    }

    @Override
    public boolean showPos() {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        //Check base store settings for POS Enabled or not.
        if (baseStore.getAdyenPosEnabled() == null || ! baseStore.getAdyenPosEnabled()) {
            return false;
        }
        return true;
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
        CartData cart = getCheckoutFacade().getCheckoutCart();
        final AddressData deliveryAddress = cart.getDeliveryAddress();
        String countryCode = deliveryAddress.getCountry().getIsocode();
        if (RATEPAY.equals(cart.getAdyenPaymentMethod()) && OPENINVOICE_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER.contains(countryCode)) {
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

        if (adyenPaymentForm.getUseAdyenDeliveryAddress() == true) {
            // Clone DeliveryAdress to BillingAddress
            final AddressModel clonedAddress = modelService.clone(cartModel.getDeliveryAddress());
            clonedAddress.setBillingAddress(true);
            clonedAddress.setOwner(paymentInfo);
            paymentInfo.setBillingAddress(clonedAddress);
        } else {
            AddressModel billingAddress = convertToAddressModel(adyenPaymentForm.getBillingAddress());
            paymentInfo.setBillingAddress(billingAddress);
            billingAddress.setOwner(paymentInfo);
            paymentInfo.setBillingAddress(billingAddress);
        }

        paymentInfo.setAdyenPaymentMethod(adyenPaymentForm.getPaymentMethod());
        paymentInfo.setAdyenIssuerId(adyenPaymentForm.getIssuerId());

        paymentInfo.setAdyenRememberTheseDetails(adyenPaymentForm.getRememberTheseDetails());
        paymentInfo.setAdyenSelectedReference(adyenPaymentForm.getSelectedReference());

        // openinvoice fields
        paymentInfo.setAdyenDob(adyenPaymentForm.getDob());

        paymentInfo.setAdyenSocialSecurityNumber(adyenPaymentForm.getSocialSecurityNumber());

        paymentInfo.setAdyenSepaOwnerName(adyenPaymentForm.getSepaOwnerName());
        paymentInfo.setAdyenSepaIbanNumber(adyenPaymentForm.getSepaIbanNumber());

        // Boleto fields
        paymentInfo.setAdyenFirstName(adyenPaymentForm.getFirstName());
        paymentInfo.setAdyenLastName(adyenPaymentForm.getLastName());

        paymentInfo.setAdyenCardHolder(adyenPaymentForm.getCardHolder());

        //required for 3DS2
        paymentInfo.setAdyenBrowserInfo(adyenPaymentForm.getBrowserInfo());

        //pos field(s)
        paymentInfo.setAdyenTerminalId(adyenPaymentForm.getTerminalId());

        //combo card fields
        paymentInfo.setCardType(adyenPaymentForm.getCardType());
        paymentInfo.setCardBrand(adyenPaymentForm.getCardBrand());

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
        paymentInfo.setAdyenSepaOwnerName(paymentDetails.getAdyenSepaOwnerName());
        paymentInfo.setAdyenSepaIbanNumber(paymentDetails.getAdyenSepaIbanNumber());
        paymentInfo.setAdyenFirstName(paymentDetails.getAdyenFirstName());
        paymentInfo.setAdyenLastName(paymentDetails.getAdyenLastName());
        paymentInfo.setOwner(cartModel.getOwner());
        paymentInfo.setAdyenTerminalId(paymentDetails.getTerminalId());
        paymentInfo.setAdyenInstallments(paymentDetails.getInstallments());
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
        if (! StringUtils.isEmpty(adyenPaymentForm.getCardBrand())) {
            getSessionService().setAttribute(SESSION_CARD_BRAND, adyenPaymentForm.getCardBrand());
        }

        //Update CartModel
        cartModel.setAdyenDfValue(adyenPaymentForm.getDfValue());

        //Create payment info
        PaymentInfoModel paymentInfo = createPaymentInfo(cartModel, adyenPaymentForm);
        cartModel.setPaymentInfo(paymentInfo);
        modelService.save(cartModel);
    }

    @Override
    public List<CountryData> getBillingCountries() {
        final List<CountryData> countries = getCountryConverter().convertAll(getCommonI18NService().getAllCountries());
        Collections.sort(countries, CountryComparator.INSTANCE);
        return countries;
    }

    public AddressModel convertToAddressModel(final AddressForm addressForm) {
        final AddressData addressData = new AddressData();
        final CountryData countryData = getI18NFacade().getCountryForIsocode(addressForm.getCountryIsoCode());
        addressData.setTitleCode(addressForm.getTitleCode());
        addressData.setFirstName(addressForm.getFirstName());
        addressData.setLastName(addressForm.getLastName());
        addressData.setLine1(addressForm.getLine1());
        addressData.setLine2(addressForm.getLine2());
        addressData.setTown(addressForm.getTownCity());
        addressData.setPostalCode(addressForm.getPostcode());
        addressData.setBillingAddress(true);
        addressData.setCountry(countryData);
        addressData.setPhone(addressForm.getPhoneNumber());

        if (addressForm.getRegionIso() != null && ! org.apache.commons.lang.StringUtils.isEmpty(addressForm.getRegionIso()))
        {
            final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIsoCode(), addressForm.getRegionIso());
            addressData.setRegion(regionData);
        }
        final AddressModel billingAddress = getModelService().create(AddressModel.class);
        getAddressReverseConverter().convert(addressData, billingAddress);

        return billingAddress;
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

    @Override
    public String getShopperLocale() {
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

    /**
     * Initiate POS Payment using Adyen Terminal API
     */
    @Override
    public OrderData initiatePosPayment(HttpServletRequest request, CartData cartData) throws Exception {
        CustomerModel customer = null;
        if (! getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            customer = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        }
        //This will be used to check status later

        String serviceId = request.getAttribute("originalServiceId").toString();
        TerminalAPIResponse terminalApiResponse = getAdyenPaymentService().sendSyncPosPaymentRequest(cartData, customer, serviceId);
        ResultType resultType = TerminalAPIUtil.getPaymentResultFromStatusOrPaymentResponse(terminalApiResponse);

        if (ResultType.SUCCESS == resultType) {
            PaymentsResponse paymentsResponse = getPosPaymentResponseConverter().convert(terminalApiResponse.getSaleToPOIResponse());
            String posReceipt = TerminalAPIUtil.getReceiptFromPaymentResponse(terminalApiResponse);
            if (StringUtils.isNotEmpty(posReceipt)) {
                paymentsResponse.putAdditionalDataItem("pos.receipt", posReceipt);
            }
            return createAuthorizedOrder(paymentsResponse);
        }
        throw new AdyenNonAuthorizedPaymentException(terminalApiResponse);
    }

    /**
     * Check POS Payment Status using Adyen Terminal API
     */
    @Override
    public OrderData checkPosPaymentStatus(HttpServletRequest request, CartData cartData) throws Exception {

        String originalServiceId = request.getAttribute("originalServiceId").toString();
        TerminalAPIResponse terminalApiResponse = getAdyenPaymentService().sendSyncPosStatusRequest(cartData, originalServiceId);
        ResultType statusResult = TerminalAPIUtil.getStatusResultFromStatusResponse(terminalApiResponse);

        if (statusResult != null) {
            if (statusResult == ResultType.SUCCESS) {
                //this will be success even if payment is failed. because this belongs to status call not payment call
                ResultType paymentResult = TerminalAPIUtil.getPaymentResultFromStatusOrPaymentResponse(terminalApiResponse);
                if (paymentResult == ResultType.SUCCESS) {
                    PaymentsResponse paymentsResponse = getPosPaymentResponseConverter().convert(terminalApiResponse.getSaleToPOIResponse());
                    String posReceipt = TerminalAPIUtil.getReceiptFromStatusResponse(terminalApiResponse);
                    if (StringUtils.isNotEmpty(posReceipt)) {
                        paymentsResponse.putAdditionalDataItem("pos.receipt", posReceipt);
                    }
                    return createAuthorizedOrder(paymentsResponse);
                } else {
                    throw new AdyenNonAuthorizedPaymentException(terminalApiResponse);
                }
            } else {
                ErrorConditionType errorCondition = TerminalAPIUtil.getErrorConditionForStatusFromStatusResponse(terminalApiResponse);
                //If transaction is still in progress, keep retrying in 5 seconds.
                if (errorCondition == ErrorConditionType.IN_PROGRESS) {
                    TimeUnit.SECONDS.sleep(5);
                    if (isPosTimedOut(request)) {
                        throw new AdyenNonAuthorizedPaymentException(terminalApiResponse);
                    } else {
                        return checkPosPaymentStatus(request, cartData);
                    }
                } else {
                    throw new AdyenNonAuthorizedPaymentException(terminalApiResponse);
                }
            }
        }

        //probably returned SaleToPOIRequest, that means terminal unreachable, return the response as error
        throw new AdyenNonAuthorizedPaymentException(terminalApiResponse);
    }

    private boolean isPosTimedOut(HttpServletRequest request) {
        long currentTime = System.currentTimeMillis();
        long processStartTime = (long) request.getAttribute("paymentStartTime");
        int totalTimeout = ((int) request.getAttribute("totalTimeout")) * 1000;
        long timeDiff = currentTime - processStartTime;
        if (timeDiff >= totalTimeout) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isImmediateCapture() {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        if (baseStore.getAdyenImmediateCapture() == null) {
            return true;
        }
        return baseStore.getAdyenImmediateCapture();
    }

    @Override
    public OrderData handleComponentResult(String resultJson) throws Exception {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        PaymentsResponse paymentsResponse = gson.fromJson(resultJson, new TypeToken<PaymentsResponse>() {
        }.getType());

        restoreSessionCart();
        if (PaymentsResponse.ResultCodeEnum.AUTHORISED == paymentsResponse.getResultCode()) {
            //TODO: Check for PENDING status.
            return createAuthorizedOrder(paymentsResponse);
        }

        throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
    }


    private OrderModel retrieveOrder(String orderCode) throws InvalidCartException {
        OrderModel orderModel = getOrderRepository().getOrderModel(orderCode);
        if (orderModel == null) {
            //TODO change exception
            throw new InvalidCartException("Order does not exist!");
        }

        getSessionService().removeAttribute(SESSION_LOCKED_CART);
        getSessionService().removeAttribute(SESSION_PAYMENT_DATA);
        getSessionService().removeAttribute(THREEDS2_FINGERPRINT_TOKEN);
        getSessionService().removeAttribute(THREEDS2_CHALLENGE_TOKEN);
        getSessionService().removeAttribute(PAYMENT_METHOD);

        return orderModel;
    }

    private void restoreCartFromOrder(String orderCode) throws CalculationException {
        OrderModel orderModel = getOrderRepository().getOrderModel(orderCode);
        if (orderModel == null) {
            LOGGER.error("Could not restore cart to session, order not found!");
            return;
        }

        //Create new cart and set in session
        CartModel cartModel = getCartFactory().createCart();
        cartModel.setStore(orderModel.getStore());
        getCartService().setSessionCart(cartModel);
        //Populate user and cart entries
        getCartService().changeCurrentCartUser(orderModel.getUser());
        orderModel.getEntries().forEach(entryModel ->
                getCartService().addNewEntry(cartModel, entryModel.getProduct(), entryModel.getQuantity(), entryModel.getUnit()));
        getModelService().save(cartModel);

        //Populate delivery address and mode
        AddressData deliveryAddressData = new AddressData();
        getAddressPopulator().populate(orderModel.getDeliveryAddress().getOriginal(), deliveryAddressData);
        getCheckoutFacade().setDeliveryAddress(deliveryAddressData);
        getCheckoutFacade().setDeliveryMode(orderModel.getDeliveryMode().getCode());

        getCalculationService().calculate(cartModel);

        getSessionService().removeAttribute(SESSION_LOCKED_CART);
        getSessionService().removeAttribute(SESSION_PAYMENT_DATA);
        getSessionService().removeAttribute(THREEDS2_FINGERPRINT_TOKEN);
        getSessionService().removeAttribute(THREEDS2_CHALLENGE_TOKEN);
        getSessionService().removeAttribute(PAYMENT_METHOD);
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

    public PosPaymentResponseConverter getPosPaymentResponseConverter() {
        return posPaymentResponseConverter;
    }

    public void setPosPaymentResponseConverter(PosPaymentResponseConverter posPaymentResponseConverter) {
        this.posPaymentResponseConverter = posPaymentResponseConverter;
    }

    protected Converter<CountryModel, CountryData> getCountryConverter() {
        return countryConverter;
    }

    @Required
    public void setCountryConverter(final Converter<CountryModel, CountryData> countryConverter) {
        this.countryConverter = countryConverter;
    }

    public Converter<OrderModel, OrderData> getOrderConverter() {
        return orderConverter;
    }

    public void setOrderConverter(Converter<OrderModel, OrderData> orderConverter) {
        this.orderConverter = orderConverter;
    }

    public CartFactory getCartFactory() {
        return cartFactory;
    }

    public void setCartFactory(CartFactory cartFactory) {
        this.cartFactory = cartFactory;
    }

    public CalculationService getCalculationService() {
        return calculationService;
    }

    public void setCalculationService(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    public AddressPopulator getAddressPopulator() {
        return addressPopulator;
    }

    public void setAddressPopulator(AddressPopulator addressPopulator) {
        this.addressPopulator = addressPopulator;
    }
}
