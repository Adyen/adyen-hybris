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
package com.adyen.v6.facades.impl;

import com.adyen.model.Amount;
import com.adyen.model.Card;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.*;
import com.adyen.model.checkout.CheckoutPaymentsAction.CheckoutActionType;
import com.adyen.model.nexo.ErrorConditionType;
import com.adyen.model.nexo.ResultType;
import com.adyen.model.recurring.Recurring;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.exception.ApiException;
import com.adyen.util.DateUtil;
import com.adyen.util.Util;
import com.adyen.v6.constants.Adyenv6coreConstants;
import com.adyen.v6.controllers.dtos.PaymentResultDTO;
import com.adyen.v6.converters.PaymentsDetailsResponseConverter;
import com.adyen.v6.converters.PaymentsResponseConverter;
import com.adyen.v6.converters.PosPaymentResponseConverter;
import com.adyen.v6.enums.AdyenCardTypeEnum;
import com.adyen.v6.enums.AdyenRegions;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
import com.adyen.v6.forms.AddressForm;
import com.adyen.v6.forms.AdyenPaymentForm;
import com.adyen.v6.forms.validation.AdyenPaymentFormValidator;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenBusinessProcessService;
import com.adyen.v6.service.AdyenOrderService;
import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.AdyenTransactionService;
import com.adyen.v6.util.TerminalAPIUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.i18n.comparators.CountryComparator;
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
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
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
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.adyen.constants.ApiConstants.ThreeDS2Property.THREEDS2_CHALLENGE_TOKEN;
import static com.adyen.constants.ApiConstants.ThreeDS2Property.THREEDS2_FINGERPRINT_TOKEN;
import static com.adyen.constants.HPPConstants.Response.SHOPPER_LOCALE;
import static com.adyen.v6.constants.Adyenv6coreConstants.*;
import static de.hybris.platform.order.impl.DefaultCartService.SESSION_CART_PARAMETER_NAME;

/**
 * Adyen Checkout Facade for initiating payments using CC or APM
 */
public class DefaultAdyenCheckoutFacade implements AdyenCheckoutFacade {

    public static final String DETAILS = "details";
    private static final String LOCALE = "locale";
    private static final String SESSION_DATA = "sessionData";
    private static final String REGION = "region";
    private static final String US_LOCALE = "en_US";
    private static final String GB_LOCALE = "en_GB";
    private static final String DE_LOCALE = "de_DE";
    private static final String FR_LOCALE = "fr_FR";
    private static final String IT_LOCALE = "it_IT";
    private static final String ES_LOCALE = "es_ES";
    private static final String US = "US";

    private BaseStoreService baseStoreService;
    private SessionService sessionService;
    private CartService cartService;
    private OrderFacade orderFacade;
    private CheckoutFacade checkoutFacade;
    private AdyenTransactionService adyenTransactionService;
    private OrderRepository orderRepository;
    private AdyenOrderService adyenOrderService;
    private CheckoutCustomerStrategy checkoutCustomerStrategy;
    private AdyenPaymentServiceFactory adyenPaymentServiceFactory;
    private ModelService modelService;
    private CommonI18NService commonI18NService;
    private KeyGenerator keyGenerator;
    private PaymentsResponseConverter paymentsResponseConverter;
    private PaymentsDetailsResponseConverter paymentsDetailsResponseConverter;
    private FlexibleSearchService flexibleSearchService;
    private Converter<AddressData, AddressModel> addressReverseConverter;
    private PosPaymentResponseConverter posPaymentResponseConverter;
    private Converter<CountryModel, CountryData> countryConverter;
    private Converter<OrderModel, OrderData> orderConverter;
    private CartFactory cartFactory;
    private CalculationService calculationService;
    private Populator<AddressModel, AddressData> addressPopulator;
    private AdyenBusinessProcessService adyenBusinessProcessService;


    @Resource(name = "i18NFacade")
    private I18NFacade i18NFacade;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    public static final Logger LOGGER = Logger.getLogger(DefaultAdyenCheckoutFacade.class);

    public static final String SESSION_LOCKED_CART = "adyen_cart";
    public static final String SESSION_PENDING_ORDER_CODE = "adyen_pending_order_code";
    public static final String SESSION_CSE_TOKEN = "adyen_cse_token";
    public static final String SESSION_SF_CARD_NUMBER = "encryptedCardNumber";
    public static final String SESSION_SF_EXPIRY_MONTH = "encryptedExpiryMonth";
    public static final String SESSION_SF_EXPIRY_YEAR = "encryptedExpiryYear";
    public static final String SESSION_SF_SECURITY_CODE = "encryptedSecurityCode";
    public static final String SESSION_CARD_BRAND = "cardBrand";
    public static final String MODEL_SELECTED_PAYMENT_METHOD = "selectedPaymentMethod";
    public static final String MODEL_PAYMENT_METHODS = "paymentMethods";
    public static final String MODEL_CREDIT_CARD_LABEL = "creditCardLabel";
    public static final String MODEL_ALLOWED_CARDS = "allowedCards";
    public static final String MODEL_REMEMBER_DETAILS = "showRememberTheseDetails";
    public static final String MODEL_STORED_CARDS = "storedCards";
    public static final String MODEL_DF_URL = "dfUrl";
    public static final String MODEL_CLIENT_KEY = "clientKey";
    public static final String MODEL_MERCHANT_ACCOUNT = "merchantAccount";
    public static final String MODEL_CHECKOUT_SHOPPER_HOST = "checkoutShopperHost";
    public static final String DF_VALUE = "dfValue";
    public static final String MODEL_OPEN_INVOICE_METHODS = "openInvoiceMethods";
    public static final String MODEL_SHOW_SOCIAL_SECURITY_NUMBER = "showSocialSecurityNumber";
    public static final String MODEL_SHOW_BOLETO = "showBoleto";
    public static final String MODEL_SHOW_POS = "showPos";
    public static final String MODEL_SHOW_COMBO_CARD = "showComboCard";
    public static final String CHECKOUT_SHOPPER_HOST_TEST = "checkoutshopper-test.adyen.com";
    public static final String CHECKOUT_SHOPPER_HOST_LIVE = "checkoutshopper-live.adyen.com";
    public static final String CHECKOUT_SHOPPER_HOST_LIVE_IN = "checkoutshopper-live-in.adyen.com";
    public static final String MODEL_ISSUER_LISTS = "issuerLists";
    public static final String MODEL_CONNECTED_TERMINAL_LIST = "connectedTerminalList";
    public static final String MODEL_ENVIRONMENT_MODE = "environmentMode";
    public static final String MODEL_AMOUNT = "amount";
    public static final String MODEL_IMMEDIATE_CAPTURE = "immediateCapture";
    public static final String MODEL_PAYPAL_MERCHANT_ID = "paypalMerchantId";
    public static final String MODEL_COUNTRY_CODE = "countryCode";
    public static final String MODEL_APPLEPAY_MERCHANT_IDENTIFIER = "applePayMerchantIdentifier";
    public static final String MODEL_APPLEPAY_MERCHANT_NAME = "applePayMerchantName";
    public static final String MODEL_AMAZONPAY_CONFIGURATION = "amazonPayConfiguration";
    public static final String MODEL_DELIVERY_ADDRESS = "deliveryAddress";
    public static final String ECOMMERCE_SHOPPER_INTERACTION = "Ecommerce";
    public static final String MODEL_CARD_HOLDER_NAME_REQUIRED = "cardHolderNameRequired";
    public static final String IS_CARD_HOLDER_NAME_REQUIRED_PROPERTY = "isCardHolderNameRequired";

    public DefaultAdyenCheckoutFacade() {
    }

    @Override
    public String getCheckoutShopperHost() {
        final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        if (Boolean.TRUE.equals(baseStore.getAdyenTestMode())) {
            return CHECKOUT_SHOPPER_HOST_TEST;
        }

        if (AdyenRegions.IN.equals(baseStore.getAdyenRegion())) {
            return CHECKOUT_SHOPPER_HOST_LIVE_IN;
        }
        return CHECKOUT_SHOPPER_HOST_LIVE;
    }


    @Override
    public String getEnvironmentMode() {
        if (Boolean.TRUE.equals(baseStoreService.getCurrentBaseStore().getAdyenTestMode())) {
            return "test";
        }
        if (AdyenRegions.IN.equals(baseStoreService.getCurrentBaseStore().getAdyenRegion())) {
            return "live-in";
        }
        return "live";
    }

    @Override
    public String getClientKey() {
        return baseStoreService.getCurrentBaseStore().getAdyenClientKey();
    }

    @Override
    public void lockSessionCart() {
        getSessionService().setAttribute(SESSION_LOCKED_CART, cartService.getSessionCart());
        getSessionService().removeAttribute(SESSION_CART_PARAMETER_NAME);

        //Refresh session for registered users
        if (!getCheckoutCustomerStrategy().isAnonymousCheckout()) {
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
        getSessionService().removeAttribute(THREEDS2_FINGERPRINT_TOKEN);
        getSessionService().removeAttribute(THREEDS2_CHALLENGE_TOKEN);
        getSessionService().removeAttribute(PAYMENT_METHOD);

        return cartModel;
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
    public PaymentsDetailsResponse handleRedirectPayload(HashMap<String, String> details) throws Exception {
        PaymentsDetailsResponse response;
        try {
            response = getAdyenPaymentService().getPaymentDetailsFromPayload(details);
        } catch (Exception e) {
            LOGGER.debug(e instanceof ApiException ? e.toString() : e.getMessage());
            restoreCartFromOrderCodeInSession();
            throw new AdyenNonAuthorizedPaymentException(e.getMessage());
        }

        String orderCode = response.getMerchantReference();
        OrderModel orderModel = retrievePendingOrder(orderCode);
        PaymentsResponse paymentsResponse = getPaymentsDetailsResponseConverter().convert(response);
        updateOrderPaymentStatusAndInfo(orderModel, paymentsResponse);

        if (PaymentsResponse.ResultCodeEnum.AUTHORISED != response.getResultCode()
                && PaymentsResponse.ResultCodeEnum.RECEIVED != response.getResultCode()) {
            restoreCartFromOrder(orderCode);
        }

        return response;
    }

    private void updateOrderPaymentStatusAndInfo(OrderModel orderModel, PaymentsResponse paymentsResponse) {
        PaymentsResponse.ResultCodeEnum resultCode = paymentsResponse.getResultCode();

        if (PaymentsResponse.ResultCodeEnum.RECEIVED != resultCode) {
            //payment authorisation is finished, update payment info
            getAdyenTransactionService().createPaymentTransactionFromResultCode(orderModel,
                    orderModel.getCode(),
                    paymentsResponse.getPspReference(),
                    paymentsResponse.getResultCode());
        }

        if (PaymentsResponse.ResultCodeEnum.AUTHORISED == resultCode || PaymentsResponse.ResultCodeEnum.RECEIVED == resultCode) {
            //remove PAYMENT_PENDING status, will be processed by order management
            orderModel.setStatus(null);
            orderModel.setStatusInfo(null);
        } else {
            //payment was not authorised, cancel pending order
            orderModel.setStatus(OrderStatus.CANCELLED);
            orderModel.setStatusInfo(paymentsResponse.getPspReference() + " - " + paymentsResponse.getResultCode().getValue());
        }
        getModelService().save(orderModel);
        getAdyenBusinessProcessService().triggerOrderProcessEvent(orderModel, Adyenv6coreConstants.PROCESS_EVENT_ADYEN_PAYMENT_RESULT);

        getAdyenOrderService().updateOrderFromPaymentsResponse(orderModel, paymentsResponse);
    }

    @Override
    public OrderData authorisePayment(final HttpServletRequest request, final CartData cartData) throws Exception {
        CheckoutCustomerStrategy checkoutCustomerStrategy = getCheckoutCustomerStrategy();

        CustomerModel customer = checkoutCustomerStrategy.getCurrentUserForCheckout();

        updateCartWithSessionData(cartData);
        String adyenPaymentMethod = cartData.getAdyenPaymentMethod();

        RequestInfo requestInfo = new RequestInfo(request);
        requestInfo.setShopperLocale(getShopperLocale());

        PaymentsResponse paymentsResponse = getAdyenPaymentService().authorisePayment(cartData, requestInfo, customer);
        PaymentsResponse.ResultCodeEnum resultCode = paymentsResponse.getResultCode();
        CheckoutPaymentsAction action = paymentsResponse.getAction();
        if (PaymentsResponse.ResultCodeEnum.AUTHORISED == resultCode || PaymentsResponse.ResultCodeEnum.PENDING == resultCode) {
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
            if (adyenPaymentMethod.startsWith(PAYMENT_METHOD_KLARNA)) {
                getSessionService().setAttribute(PAYMENT_METHOD, adyenPaymentMethod);
            }
        } else if (action != null && CheckoutActionType.THREEDS2.equals(action.getType())) {
            placePendingOrder(resultCode);
        }

        throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
    }

    private boolean isGuestUserTokenizationEnabled() {
        Boolean guestCheckoutFlag = baseStoreService.getCurrentBaseStore().getAdyenGuestUserTokenization();
        if (guestCheckoutFlag == null) {
            return false;
        } else {
            return guestCheckoutFlag;
        }
    }

    @Override
    public OrderData handleResultcomponentPayment(final PaymentResultDTO paymentResultDTO) throws Exception {
        if (PaymentsResponse.ResultCodeEnum.PENDING.getValue().equals(paymentResultDTO.getResultCode()) ||
                PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER.getValue().equals(paymentResultDTO.getResultCode())) {
            return placePendingOrder(PaymentsResponse.ResultCodeEnum.fromValue(paymentResultDTO.getResultCode()));
        }
        if (PaymentsResponse.ResultCodeEnum.AUTHORISED.getValue().equals(paymentResultDTO.getResultCode())) {
            return placeAuthorisedOrder(PaymentsResponse.ResultCodeEnum.AUTHORISED);
        }
        return null;
    }

    @Override
    public PaymentsResponse componentPayment(final HttpServletRequest request, final CartData cartData, final PaymentMethodDetails paymentMethodDetails) throws Exception {
        updateCartWithSessionData(cartData);

        RequestInfo requestInfo = new RequestInfo(request);
        requestInfo.setShopperLocale(getShopperLocale());

        PaymentsResponse paymentsResponse = getAdyenPaymentService().componentPayment(cartData, paymentMethodDetails, requestInfo, getCheckoutCustomerStrategy().getCurrentUserForCheckout());
        if (PaymentsResponse.ResultCodeEnum.PENDING == paymentsResponse.getResultCode() || PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER == paymentsResponse.getResultCode()) {
            placePendingOrder(paymentsResponse.getResultCode());
            return paymentsResponse;
        }
        if (PaymentsResponse.ResultCodeEnum.AUTHORISED == paymentsResponse.getResultCode()) {
            createAuthorizedOrder(paymentsResponse);
            return paymentsResponse;
        }

        throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
    }

    @Override
    public PaymentsDetailsResponse componentDetails(final HttpServletRequest request, final Map<String, String> details, final String paymentData) throws Exception {
        PaymentsDetailsResponse response = getAdyenPaymentService().getPaymentDetailsFromPayload(details, paymentData);
        PaymentsResponse paymentsResponse = getPaymentsDetailsResponseConverter().convert(response);

        String orderCode = response.getMerchantReference();
        OrderModel orderModel = retrievePendingOrder(orderCode);
        updateOrderPaymentStatusAndInfo(orderModel, paymentsResponse);

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
    }

    @Override
    public OrderData handle3DSResponse(final Map<String, String> details) throws Exception {
        PaymentsDetailsResponse paymentsDetailsResponse;
        try {
            paymentsDetailsResponse = getAdyenPaymentService().authorise3DSPayment(details);
        } catch (Exception e) {
            LOGGER.debug(e instanceof ApiException ? e.toString() : e.getMessage());
            restoreCartFromOrderCodeInSession();
            throw new AdyenNonAuthorizedPaymentException(e.getMessage());
        }

        String orderCode = paymentsDetailsResponse.getMerchantReference();
        OrderModel orderModel = retrievePendingOrder(orderCode);
        updateOrderPaymentStatusAndInfo(orderModel, getPaymentsDetailsResponseConverter().convert(paymentsDetailsResponse));

        PaymentsResponse.ResultCodeEnum resultCode = paymentsDetailsResponse.getResultCode();

        if (PaymentsResponse.ResultCodeEnum.AUTHORISED == resultCode) {
            return getOrderConverter().convert(orderModel);
        }

        restoreCartFromOrder(orderCode);
        throw new AdyenNonAuthorizedPaymentException(paymentsDetailsResponse);
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

        CheckoutPaymentsAction action = paymentsResponse.getAction();
        if (action != null) {
            if (PAYMENT_METHOD_MULTIBANCO.equals(action.getPaymentMethodType())) {
                orderData.setAdyenMultibancoEntity(action.getEntity());
                orderData.setAdyenMultibancoAmount(BigDecimal.valueOf(action.getInitialAmount().getValue()));
                orderData.setAdyenMultibancoDeadline(action.getExpiresAt());
                orderData.setAdyenMultibancoReference(action.getReference());
            } else if (PAYMENT_METHOD_BOLETO.equals(action.getPaymentMethodType()) || PAYMENT_METHOD_BOLETO_SANTANDER.equals(action.getPaymentMethodType())) {
                orderData.setAdyenBoletoUrl(action.getDownloadUrl());
                orderData.setAdyenBoletoBarCodeReference(action.getReference());
                orderData.setAdyenBoletoExpirationDate(DateUtil.parseYmdDate(action.getExpiresAt()));
            }
        }

        if (paymentsResponse.getAdditionalData() != null) {
            orderData.setAdyenPosReceipt(paymentsResponse.getAdditionalData().get("pos.receipt"));
        }

        return orderData;
    }

    private OrderData placePendingOrder(PaymentsResponse.ResultCodeEnum resultCode) throws InvalidCartException {
        CartModel cartModel = getCartService().getSessionCart();
        cartModel.setStatus(OrderStatus.PAYMENT_PENDING);
        cartModel.setStatusInfo(resultCode.getValue());
        getModelService().save(cartModel);

        OrderData orderData = getCheckoutFacade().placeOrder();

        getSessionService().setAttribute(SESSION_PENDING_ORDER_CODE, orderData.getCode());

        //Set new cart in session to avoid bugs (like going "back" on browser)
        CartModel newCartModel = getCartFactory().createCart();
        getCartService().setSessionCart(newCartModel);

        return orderData;
    }

    private OrderData placeAuthorisedOrder(PaymentsResponse.ResultCodeEnum resultCode) throws InvalidCartException {
        CartModel cartModel = getCartService().getSessionCart();
        cartModel.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
        cartModel.setStatusInfo(resultCode.getValue());
        getModelService().save(cartModel);

        OrderData orderData = getCheckoutFacade().placeOrder();

        getSessionService().setAttribute(SESSION_PENDING_ORDER_CODE, orderData.getCode());

        //Set new cart in session to avoid bugs (like going "back" on browser)
        CartModel newCartModel = getCartFactory().createCart();
        getCartService().setSessionCart(newCartModel);

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
    public void initializeCheckoutData(Model model) throws ApiException {
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

            response = adyenPaymentService.getPaymentMethodsResponse(cartData.getTotalPriceWithTax().getValue(),
                    cartData.getTotalPriceWithTax().getCurrencyIso(),
                    cartData.getDeliveryAddress().getCountry().getIsocode(),
                    getShopperLocale(),
                    customerModel.getCustomerID());
        } catch (ApiException | IOException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }

        alternativePaymentMethods = response.getPaymentMethods();

        final List<PaymentMethod> issuerPaymentMethods = alternativePaymentMethods.stream()
                .filter(paymentMethod -> !paymentMethod.getType().isEmpty() && ISSUER_PAYMENT_METHODS.contains(paymentMethod.getType()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(issuerPaymentMethods)) {
            Gson gson = new Gson();
            for (PaymentMethod paymentMethod : issuerPaymentMethods) {
                issuerLists.put(paymentMethod.getType(), gson.toJson(paymentMethod.getIssuers()));
            }
        }

        Optional<PaymentMethod> sepaDirectDebit = alternativePaymentMethods.stream().
                filter(paymentMethod -> !paymentMethod.getType().isEmpty() &&
                        PAYMENT_METHOD_SEPA_DIRECTDEBIT.contains(paymentMethod.getType())).findFirst();

        if (sepaDirectDebit.isPresent()) {
            model.addAttribute(PAYMENT_METHOD_SEPA_DIRECTDEBIT, true);
        }

        //apple pay
        Optional<PaymentMethod> applePayMethod = alternativePaymentMethods.stream()
                .filter(paymentMethod -> !paymentMethod.getType().isEmpty()
                        && PAYMENT_METHOD_APPLEPAY.contains(paymentMethod.getType()))
                .findFirst();
        if (applePayMethod.isPresent()) {
            Map<String, String> applePayConfiguration = applePayMethod.get().getConfiguration();
            if (!CollectionUtils.isEmpty(applePayConfiguration)) {
                cartModel.setAdyenApplePayMerchantName(applePayConfiguration.get("merchantName"));
                cartModel.setAdyenApplePayMerchantIdentifier(applePayConfiguration.get("merchantId"));
            }
        }

        //amazon pay
        Optional<PaymentMethod> amazonPayMethod = alternativePaymentMethods.stream()
                .filter(paymentMethod -> !paymentMethod.getType().isEmpty()
                        && PAYMENT_METHOD_AMAZONPAY.contains(paymentMethod.getType()))
                .findFirst();
        if (amazonPayMethod.isPresent()) {
            Map<String, String> amazonPayConfiguration = amazonPayMethod.get().getConfiguration();
            if (!CollectionUtils.isEmpty(amazonPayConfiguration)) {
                cartModel.setAdyenAmazonPayConfiguration(amazonPayConfiguration);
            }
        }

        baseStore = baseStoreService.getCurrentBaseStore();

        //Verify allowedCards
        String creditCardLabel = null;
        Set<AdyenCardTypeEnum> allowedCards = null;
        PaymentMethod cardsPaymentMethod = alternativePaymentMethods.stream()
                .filter(paymentMethod -> PAYMENT_METHOD_SCHEME.equals(paymentMethod.getType()))
                .findAny().orElse(null);

        if (cardsPaymentMethod != null) {
            creditCardLabel = cardsPaymentMethod.getName();
            allowedCards = baseStore.getAdyenAllowedCards();

            List<String> cardBrands = cardsPaymentMethod.getBrands();
            allowedCards = allowedCards.stream()
                    .filter(adyenCardTypeEnum -> cardBrands.contains(adyenCardTypeEnum.getCode()))
                    .collect(Collectors.toSet());
        }

        //Exclude cards, boleto and iDeal
        alternativePaymentMethods = alternativePaymentMethods.stream()
                .filter(paymentMethod -> !paymentMethod.getType().isEmpty() && !isHiddenPaymentMethod(paymentMethod))
                .collect(Collectors.toList());

        if (showRememberDetails()) {
            //Include stored one-click cards
            storedPaymentMethodList = getStoredOneClickPaymentMethods(response);
            Set<String> recurringDetailReferences = new HashSet<>();
            if (storedPaymentMethodList != null) {
                recurringDetailReferences = storedPaymentMethodList.stream().map(StoredPaymentMethod::getId).collect(Collectors.toSet());
            }
            cartModel.setAdyenStoredCards(recurringDetailReferences);
        }

        Amount amount = Util.createAmount(cartData.getTotalPriceWithTax().getValue(), cartData.getTotalPriceWithTax().getCurrencyIso());

        model.addAttribute(SESSION_DATA, getAdyenSessionData());

        // current selected PaymentMethod
        model.addAttribute(MODEL_SELECTED_PAYMENT_METHOD, cartData.getAdyenPaymentMethod());

        //Set payment methods
        model.addAttribute(MODEL_PAYMENT_METHODS, alternativePaymentMethods);

        //Set allowed Credit Cards
        model.addAttribute(MODEL_CREDIT_CARD_LABEL, creditCardLabel);
        model.addAttribute(MODEL_ALLOWED_CARDS, allowedCards);

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
        model.addAttribute(MODEL_CLIENT_KEY, baseStore.getAdyenClientKey());
        model.addAttribute(MODEL_AMOUNT, amount);
        model.addAttribute(MODEL_IMMEDIATE_CAPTURE, isImmediateCapture());
        model.addAttribute(MODEL_PAYPAL_MERCHANT_ID, baseStore.getAdyenPaypalMerchantId());
        model.addAttribute(MODEL_COUNTRY_CODE, cartData.getDeliveryAddress().getCountry().getIsocode());
        model.addAttribute(MODEL_CARD_HOLDER_NAME_REQUIRED, getHolderNameRequired());

        modelService.save(cartModel);
    }

    private CreateCheckoutSessionResponse getAdyenSessionData() throws ApiException {
        try {
            final CartData cartData = getCheckoutFacade().getCheckoutCart();
            return getAdyenPaymentService().getPaymentSessionData(cartData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void initializeSummaryData(Model model) throws ApiException {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        final AdyenPaymentService adyenPaymentService = getAdyenPaymentService();
        final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        final Amount amount = Util.createAmount(cartData.getTotalPriceWithTax().getValue(), cartData.getTotalPriceWithTax().getCurrencyIso());
        final Gson gson = new Gson();
        final String shopperLocale = getShopperLocale();
        final String countryCode = Objects.nonNull(cartData.getDeliveryAddress()) &&
                Objects.nonNull(cartData.getDeliveryAddress().getCountry()) ?
                cartData.getDeliveryAddress().getCountry().getIsocode() : null;

        model.addAttribute(MODEL_SELECTED_PAYMENT_METHOD, cartData.getAdyenPaymentMethod());
        model.addAttribute(MODEL_DF_URL, adyenPaymentService.getDeviceFingerprintUrl());
        model.addAttribute(MODEL_CHECKOUT_SHOPPER_HOST, getCheckoutShopperHost());
        model.addAttribute(MODEL_ENVIRONMENT_MODE, getEnvironmentMode());
        model.addAttribute(SHOPPER_LOCALE, shopperLocale);

        //Include information for components
        model.addAttribute(MODEL_CLIENT_KEY, baseStore.getAdyenClientKey());
        model.addAttribute(MODEL_MERCHANT_ACCOUNT, baseStore.getAdyenMerchantAccount());
        model.addAttribute(MODEL_AMOUNT, amount);
        model.addAttribute(MODEL_IMMEDIATE_CAPTURE, isImmediateCapture());
        model.addAttribute(MODEL_PAYPAL_MERCHANT_ID, baseStore.getAdyenPaypalMerchantId());
        model.addAttribute(MODEL_APPLEPAY_MERCHANT_IDENTIFIER, cartData.getAdyenApplePayMerchantIdentifier());
        model.addAttribute(MODEL_APPLEPAY_MERCHANT_NAME, cartData.getAdyenApplePayMerchantName());
        model.addAttribute(MODEL_AMAZONPAY_CONFIGURATION, gson.toJson(cartData.getAdyenAmazonPayConfiguration()));
        model.addAttribute(MODEL_COUNTRY_CODE, countryCode);
        model.addAttribute(MODEL_DELIVERY_ADDRESS, gson.toJson(cartData.getDeliveryAddress()));
        model.addAttribute(SESSION_DATA, getAdyenSessionData());
        model.addAttribute(LOCALE, gson.toJson(setLocale(cartData.getAdyenAmazonPayConfiguration(), shopperLocale)));
    }

    private String setLocale(final Map<String, String> map, final String shopperLocale) {
        if (Objects.nonNull(map) && !map.get(REGION).isBlank() && map.get(REGION).equals(US)) {
            return US_LOCALE;
        } else {
            switch (shopperLocale) {
                case "de":
                    return DE_LOCALE;
                case "fr":
                    return FR_LOCALE;
                case "it":
                    return IT_LOCALE;
                case "es":
                    return ES_LOCALE;
                default:
                    return GB_LOCALE;
            }
        }
    }

    private boolean isHiddenPaymentMethod(PaymentMethod paymentMethod) {
        String paymentMethodType = paymentMethod.getType();

        if (paymentMethodType == null || paymentMethodType.isEmpty() ||
                paymentMethodType.equals("scheme") ||
                (paymentMethodType.contains("wechatpay")
                        && !paymentMethodType.equals("wechatpayWeb")) ||
                paymentMethodType.startsWith(PAYMENT_METHOD_BOLETO) ||
                paymentMethodType.contains(PAYMENT_METHOD_SEPA_DIRECTDEBIT) ||
                (ISSUER_PAYMENT_METHODS.contains(paymentMethodType) &&
                        !paymentMethodType.equals(PAYMENT_METHOD_ONLINEBANKING_IN) &&
                        !paymentMethodType.equals(PAYMENT_METHOD_ONLINEBANKING_PL))) {
            return true;
        }
        return false;
    }

    private List<StoredPaymentMethod> getStoredOneClickPaymentMethods(PaymentMethodsResponse response) {
        List<StoredPaymentMethod> storedPaymentMethodList = null;
        if (response.getStoredPaymentMethods() != null) {
            storedPaymentMethodList = response.getStoredPaymentMethods().stream()
                    .filter(storedPaymentMethod -> storedPaymentMethod.getSupportedShopperInteractions() != null
                            && storedPaymentMethod.getSupportedShopperInteractions().contains(ECOMMERCE_SHOPPER_INTERACTION))
                    .collect(Collectors.toList());
        }

        return storedPaymentMethodList;
    }

    @Override
    public boolean showBoleto() {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        //Check base store settings
        if (baseStore.getAdyenBoleto() == null || !baseStore.getAdyenBoleto()) {
            return false;
        }

        CartData cartData = getCheckoutFacade().getCheckoutCart();
        String currency = cartData.getTotalPriceWithTax().getCurrencyIso();
        String country = cartData.getDeliveryAddress().getCountry().getIsocode();

        //Show only on Brasil with BRL
        return "BRL".equals(currency) && "BR".equals(country);
    }

    @Override
    public boolean showComboCard() {
        CartData cartData = getCheckoutFacade().getCheckoutCart();
        String currency = cartData.getTotalPriceWithTax().getCurrencyIso();
        return "BRL".equals(currency);
    }

    @Override
    public boolean showPos() {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        //Check base store settings for POS Enabled or not.
        if (baseStore.getAdyenPosEnabled() == null || !baseStore.getAdyenPosEnabled()) {
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
        if (!getCheckoutCustomerStrategy().isAnonymousCheckout()) {
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
        if (PAYMENT_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER.contains(cart.getAdyenPaymentMethod()) && OPENINVOICE_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER.contains(countryCode)) {
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
        paymentInfo.setAdyenUPIVirtualAddress(adyenPaymentForm.getUpiVirtualAddress());
        paymentInfo.setAdyenRememberTheseDetails(adyenPaymentForm.getRememberTheseDetails());
        paymentInfo.setAdyenSelectedReference(adyenPaymentForm.getSelectedReference());

        // openinvoice fields
        paymentInfo.setAdyenDob(adyenPaymentForm.getDob());

        paymentInfo.setAdyenSocialSecurityNumber(adyenPaymentForm.getSocialSecurityNumber());

        paymentInfo.setAdyenSepaOwnerName(adyenPaymentForm.getSepaOwnerName());
        paymentInfo.setAdyenSepaIbanNumber(adyenPaymentForm.getSepaIbanNumber());

        // AfterPay fields
        paymentInfo.setAdyenTelephone(cartModel.getDeliveryAddress().getPhone1());
        paymentInfo.setAdyenShopperEmail(adyenPaymentForm.getShopperEmail());
        paymentInfo.setAdyenShopperGender(adyenPaymentForm.getGender());

        // Boleto fields
        paymentInfo.setAdyenFirstName(adyenPaymentForm.getFirstName());
        paymentInfo.setAdyenLastName(adyenPaymentForm.getLastName());

        paymentInfo.setAdyenCardHolder(adyenPaymentForm.getCardHolder());

        //required for 3DS2
        paymentInfo.setAdyenBrowserInfo(adyenPaymentForm.getBrowserInfo());

        //pos field(s)
        paymentInfo.setAdyenTerminalId(adyenPaymentForm.getTerminalId());

        //apple pay
        paymentInfo.setAdyenApplePayMerchantName(cartModel.getAdyenApplePayMerchantName());
        paymentInfo.setAdyenApplePayMerchantIdentifier(cartModel.getAdyenApplePayMerchantIdentifier());

        //combo card fields
        paymentInfo.setCardType(adyenPaymentForm.getCardType());
        paymentInfo.setCardBrand(adyenPaymentForm.getCardBrand());

        // Gift card
        paymentInfo.setAdyenGiftCardBrand(adyenPaymentForm.getGiftCardBrand());

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
        boolean holderNameRequired = getHolderNameRequired();

        AdyenPaymentFormValidator adyenPaymentFormValidator = new AdyenPaymentFormValidator(cartModel.getAdyenStoredCards(), showRememberDetails, showSocialSecurityNumber, holderNameRequired);
        if (PAYBRIGHT.equals(adyenPaymentForm.getPaymentMethod())) {
            adyenPaymentFormValidator.setTelephoneNumberRequired(true);
        }

        adyenPaymentFormValidator.validate(adyenPaymentForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return;
        }

        //Put encrypted data to session
        if (!StringUtils.isEmpty(adyenPaymentForm.getCseToken())) {
            getSessionService().setAttribute(SESSION_CSE_TOKEN, adyenPaymentForm.getCseToken());
        }
        if (!StringUtils.isEmpty(adyenPaymentForm.getEncryptedCardNumber())) {
            getSessionService().setAttribute(SESSION_SF_CARD_NUMBER, adyenPaymentForm.getEncryptedCardNumber());
        }
        if (!StringUtils.isEmpty(adyenPaymentForm.getEncryptedExpiryMonth())) {
            getSessionService().setAttribute(SESSION_SF_EXPIRY_MONTH, adyenPaymentForm.getEncryptedExpiryMonth());
        }
        if (!StringUtils.isEmpty(adyenPaymentForm.getEncryptedExpiryYear())) {
            getSessionService().setAttribute(SESSION_SF_EXPIRY_YEAR, adyenPaymentForm.getEncryptedExpiryYear());
        }
        if (!StringUtils.isEmpty(adyenPaymentForm.getEncryptedSecurityCode())) {
            getSessionService().setAttribute(SESSION_SF_SECURITY_CODE, adyenPaymentForm.getEncryptedSecurityCode());
        }
        if (!StringUtils.isEmpty(adyenPaymentForm.getCardBrand())) {
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

        if (addressForm.getRegionIso() != null && !org.apache.commons.lang.StringUtils.isEmpty(addressForm.getRegionIso())) {
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
        if (!getCheckoutCustomerStrategy().isAnonymousCheckout()) {
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

        String orderCode = paymentsResponse.getMerchantReference();

        if (PaymentsResponse.ResultCodeEnum.AUTHORISED == paymentsResponse.getResultCode()
                || PaymentsResponse.ResultCodeEnum.RECEIVED == paymentsResponse.getResultCode()) {
            OrderModel orderModel = retrievePendingOrder(orderCode);
            return getOrderConverter().convert(orderModel);
        }

        if (PaymentsResponse.ResultCodeEnum.REDIRECTSHOPPER != paymentsResponse.getResultCode()) {
            restoreCartFromOrder(orderCode);
        }
        throw new AdyenNonAuthorizedPaymentException(paymentsResponse);
    }

    private OrderModel retrievePendingOrder(String orderCode) throws InvalidCartException {
        if (orderCode == null || orderCode.isEmpty()) {
            throw new InvalidCartException("Could not retrieve pending order: missing orderCode!");
        }

        OrderModel orderModel = getOrderRepository().getOrderModel(orderCode);
        if (orderModel == null) {
            throw new InvalidCartException("Order '" + orderCode + "' does not exist!");
        }

        getSessionService().removeAttribute(SESSION_PENDING_ORDER_CODE);
        getSessionService().removeAttribute(THREEDS2_FINGERPRINT_TOKEN);
        getSessionService().removeAttribute(THREEDS2_CHALLENGE_TOKEN);
        getSessionService().removeAttribute(PAYMENT_METHOD);

        return orderModel;
    }

    private void restoreCartFromOrder(String orderCode) throws CalculationException, InvalidCartException {
        OrderModel orderModel = getOrderRepository().getOrderModel(orderCode);
        if (orderModel == null) {
            LOGGER.error("Could not restore cart to session, order with code '" + orderCode + "' not found!");
            return;
        }

        // Get cart from session
        CartModel cartModel;
        if (getCartService().hasSessionCart()) {
            cartModel = getCartService().getSessionCart();
        }
        // Or create new cart if no cart in session
        else {
            cartModel = getCartFactory().createCart();
            getCartService().setSessionCart(cartModel);
        }

        Boolean isAnonymousCheckout = getCheckoutCustomerStrategy().isAnonymousCheckout();

        if (!isAnonymousCheckout && hasUserContextChanged(orderModel, cartModel)) {
            throw new InvalidCartException("Cart from order '" + orderCode + "' not restored to session, since user or store in session changed.");
        }

        //Populate cart entries
        for (AbstractOrderEntryModel entryModel : orderModel.getEntries()) {
            getCartService().addNewEntry(cartModel, entryModel.getProduct(), entryModel.getQuantity(), entryModel.getUnit());
        }
        getModelService().save(cartModel);

        if (!isAnonymousCheckout) {
            //Populate delivery address and mode
            AddressData deliveryAddressData = new AddressData();
            getAddressPopulator().populate(orderModel.getDeliveryAddress().getOriginal(), deliveryAddressData);
            getCheckoutFacade().setDeliveryAddress(deliveryAddressData);
            getCheckoutFacade().setDeliveryMode(orderModel.getDeliveryMode().getCode());
        }

        getCalculationService().calculate(cartModel);
    }

    private boolean hasUserContextChanged(OrderModel orderModel, CartModel cartModel) {
        return !orderModel.getUser().equals(cartModel.getUser())
                || !orderModel.getStore().equals(cartModel.getStore());
    }

    @Override
    public void restoreCartFromOrderCodeInSession() throws InvalidCartException, CalculationException {
        String orderCode = getSessionService().getAttribute(SESSION_PENDING_ORDER_CODE);
        if (orderCode == null) {
            LOGGER.debug("OrderCode not in session, no cart will be restored");
            return;
        }

        OrderModel orderModel = retrievePendingOrder(orderCode);

        orderModel.setStatus(OrderStatus.PROCESSING_ERROR);
        orderModel.setStatusInfo("AdyenException");
        getModelService().save(orderModel);
        getAdyenBusinessProcessService().triggerOrderProcessEvent(orderModel, Adyenv6coreConstants.PROCESS_EVENT_ADYEN_PAYMENT_RESULT);

        getSessionService().removeAttribute(SESSION_PENDING_ORDER_CODE);
        getSessionService().removeAttribute(THREEDS2_FINGERPRINT_TOKEN);
        getSessionService().removeAttribute(THREEDS2_CHALLENGE_TOKEN);
        getSessionService().removeAttribute(PAYMENT_METHOD);

        restoreCartFromOrder(orderCode);
    }

    private boolean getHolderNameRequired() {
        boolean holderNameRequired = true;
        Configuration configuration = this.configurationService.getConfiguration();
        if (configuration != null && configuration.containsKey(IS_CARD_HOLDER_NAME_REQUIRED_PROPERTY)) {
            holderNameRequired = configuration.getBoolean(IS_CARD_HOLDER_NAME_REQUIRED_PROPERTY);
        }
        return holderNameRequired;
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

    public Populator<AddressModel, AddressData> getAddressPopulator() {
        return addressPopulator;
    }

    public void setAddressPopulator(Populator<AddressModel, AddressData> addressPopulator) {
        this.addressPopulator = addressPopulator;
    }

    public AdyenBusinessProcessService getAdyenBusinessProcessService() {
        return adyenBusinessProcessService;
    }

    public void setAdyenBusinessProcessService(AdyenBusinessProcessService adyenBusinessProcessService) {
        this.adyenBusinessProcessService = adyenBusinessProcessService;
    }

    public PaymentsDetailsResponseConverter getPaymentsDetailsResponseConverter() {
        return paymentsDetailsResponseConverter;
    }

    public void setPaymentsDetailsResponseConverter(PaymentsDetailsResponseConverter paymentsDetailsResponseConverter) {
        this.paymentsDetailsResponseConverter = paymentsDetailsResponseConverter;
    }
}
