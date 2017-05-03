package com.adyen.v6.facades;

import java.io.IOException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import com.adyen.Util.HMACValidator;
import com.adyen.Util.Util;
import com.adyen.constants.HPPConstants;
import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.Amount;
import com.adyen.model.PaymentResult;
import com.adyen.model.hpp.PaymentMethod;
import com.adyen.model.recurring.Recurring;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.factory.AdyenPaymentServiceFactory;
import com.adyen.v6.forms.AdyenPaymentForm;
import com.adyen.v6.forms.validation.AdyenPaymentFormValidator;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenOrderService;
import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.AdyenTransactionService;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
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
import static de.hybris.platform.order.impl.DefaultCartService.SESSION_CART_PARAMETER_NAME;

/**
 * Adyen Checkout Facade for initiating payments using CC or APM
 */
public class AdyenCheckoutFacade {
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

    public static final String SESSION_LOCKED_CART = "adyen_cart";
    public static final String SESSION_MD = "adyen_md";
    public static final String THREE_D_MD = "MD";
    public static final String THREE_D_PARES = "PaRes";
    public static final Logger LOGGER = Logger.getLogger(AdyenCheckoutFacade.class);

    public static final String MODEL_PAYMENT_METHODS = "paymentMethods";
    public static final String MODEL_ALLOWED_CARDS = "allowedCards";
    public static final String MODEL_REMEMBER_DETAILS = "showRememberTheseDetails";
    public static final String MODEL_STORED_CARDS = "storedCards";
    public static final String MODEL_CSE_URL = "cseUrl";

    public AdyenCheckoutFacade() {
        hmacValidator = new HMACValidator();
    }

    /**
     * Validates an HPP response
     *
     * @param hppResponseData map with hpp data
     * @param merchantSig     merchant signature
     * @throws SignatureException in case signature doesn't match
     */
    public void validateHPPResponse(SortedMap<String, String> hppResponseData, String merchantSig) throws SignatureException {
        BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();

        String hmacKey = baseStore.getAdyenSkinHMAC();
        Assert.notNull(hmacKey);

        String dataToSign = getHmacValidator().getDataToSign(hppResponseData);
        String calculatedMerchantSig = getHmacValidator().calculateHMAC(dataToSign, hmacKey);
        LOGGER.debug("Calculated signature: " + calculatedMerchantSig);
        if (! calculatedMerchantSig.equals(merchantSig)) {
            LOGGER.error("Signature does not match!");
            throw new SignatureException("Signatures doesn't match");
        }
    }

    /**
     * Retrieve the HPP base URL for the current basestore
     *
     * @return HPP url
     */
    public String getHppUrl() {
        return getAdyenPaymentService().getConfig().getHppEndpoint() + "/details.shtml";
    }

    /**
     * Removes cart from the session so that users can't update it while being in a payment page
     */
    public void lockSessionCart() {
        getSessionService().setAttribute(SESSION_LOCKED_CART, cartService.getSessionCart());
        getSessionService().removeAttribute(SESSION_CART_PARAMETER_NAME);
        //Refresh session
        getCartService().getSessionCart();
    }

    /**
     * Restores the sessionCart that has been previously locked
     *
     * @return session cart
     * @throws InvalidCartException if cart cannot be retrieved
     */
    public CartModel restoreSessionCart() throws InvalidCartException {
        CartModel cartModel = getSessionService().getAttribute(SESSION_LOCKED_CART);
        if (cartModel == null) {
            throw new InvalidCartException("Cart does not exist!");
        }
        getCartService().setSessionCart(cartModel);

        return cartModel;
    }

    /**
     * Handles an HPP response
     * In case of authorized, it places an order from cart
     *
     * @param request Request object containing HPP data
     * @return OrderData
     * @throws SignatureException if signature doesn't match
     */
    public OrderData handleHPPResponse(final HttpServletRequest request) throws SignatureException {
        final SortedMap<String, String> hppResponseData = new TreeMap<>();

        //Compose HPP response data map
        mapRequest(request, hppResponseData, HPPConstants.Response.AUTH_RESULT);
        mapRequest(request, hppResponseData, HPPConstants.Response.MERCHANT_REFERENCE);
        mapRequest(request, hppResponseData, HPPConstants.Response.PAYMENT_METHOD);
        mapRequest(request, hppResponseData, HPPConstants.Response.PSP_REFERENCE);
        mapRequest(request, hppResponseData, HPPConstants.Response.SHOPPER_LOCALE);
        mapRequest(request, hppResponseData, HPPConstants.Response.SKIN_CODE);

        LOGGER.debug("Received HPP response: " + hppResponseData);

        String merchantSig = request.getParameter(HPPConstants.Response.MERCHANT_SIG);
        String merchantReference = request.getParameter(HPPConstants.Response.MERCHANT_REFERENCE);
        String authResult = request.getParameter(HPPConstants.Response.AUTH_RESULT);

        validateHPPResponse(hppResponseData, merchantSig);

        OrderData orderData = null;
        //Restore the cart or find the created order
        try {
            restoreSessionCart();

            if (HPPConstants.Response.AUTH_RESULT_AUTHORISED.equals(authResult) || HPPConstants.Response.AUTH_RESULT_PENDING.equals(authResult)) {
                orderData = getCheckoutFacade().placeOrder();
            }
        } catch (InvalidCartException e) {
            LOGGER.debug(e);
            //Cart does not exist, retrieve order
            orderData = getOrderFacade().getOrderDetailsForCode(merchantReference);
        }

        return orderData;
    }

    /**
     * Authorizes a CC payment
     * In case of authorized, it places an order from cart
     *
     * @param request  HTTP Request object
     * @param cartData cartData object
     * @return OrderData
     * @throws Exception In case order failed to be created
     */
    public OrderData authoriseCardPayment(final HttpServletRequest request, final CartData cartData) throws Exception {
        CustomerModel customer = null;
        if (! getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            customer = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
        }

        PaymentResult paymentResult = getAdyenPaymentService().authorise(cartData, request, customer);

        LOGGER.debug("authorization result: " + paymentResult);

        if (paymentResult.isAuthorised()) {
            return createAuthorizedOrder(paymentResult);
        }

        if (paymentResult.isRedirectShopper()) {
            getSessionService().setAttribute(SESSION_MD, paymentResult.getMd());

            lockSessionCart();
        }

        throw new AdyenNonAuthorizedPaymentException(paymentResult);
    }

    /**
     * Handles an 3D response
     * In case of authorized, it places an order from cart
     *
     * @param request HTTP Request object
     * @return OrderData
     * @throws Exception In case order failed to be created
     */
    public OrderData handle3DResponse(final HttpServletRequest request) throws Exception {
        String paRes = request.getParameter(THREE_D_PARES);
        String md = request.getParameter(THREE_D_MD);

        String sessionMd = getSessionService().getAttribute(SESSION_MD);

        try {
            //Check if MD matches in order to avoid authorizing wrong order
            if (sessionMd != null && ! sessionMd.equals(md)) {
                throw new SignatureException("MD does not match!");
            }

            restoreSessionCart();

            PaymentResult paymentResult = getAdyenPaymentService().authorise3D(request, paRes, md);

            if (paymentResult.isAuthorised()) {
                return createAuthorizedOrder(paymentResult);
            }

            throw new AdyenNonAuthorizedPaymentException(paymentResult);
        } catch (ApiException e) {
            LOGGER.error("API Exception " + e.getError());
            throw e;
        }
    }

    /**
     * Initializes an HPP payment
     * Returns map of data to be submitted to Adyen HPP
     *
     * @param cartData    Shopper's cart
     * @param redirectUrl HPP result url
     * @return HPP data
     * @throws SignatureException In case signature cannot be generated
     */
    public Map<String, String> initializeHostedPayment(final CartData cartData, final String redirectUrl) throws SignatureException {
        final String sessionValidity = Util.calculateSessionValidity();
        final SortedMap<String, String> hppFormData = new TreeMap<>();

        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        String merchantAccount = baseStore.getAdyenMerchantAccount();
        String skinCode = baseStore.getAdyenSkinCode();
        String hmacKey = baseStore.getAdyenSkinHMAC();

        Assert.notNull(merchantAccount);
        Assert.notNull(skinCode);
        Assert.notNull(hmacKey);

        //todo: with vat?
        Amount amount = Util.createAmount(cartData.getTotalPrice().getValue(), cartData.getTotalPrice().getCurrencyIso());

        String countryCode = "";
        CountryData deliveryCountry = cartData.getDeliveryAddress().getCountry();
        if (deliveryCountry != null) {
            countryCode = deliveryCountry.getIsocode();
        }

        hppFormData.put(PAYMENT_AMOUNT, String.valueOf(amount.getValue()));
        hppFormData.put(CURRENCY_CODE, cartData.getTotalPrice().getCurrencyIso());
        hppFormData.put(SHIP_BEFORE_DATE, sessionValidity);
        hppFormData.put(MERCHANT_REFERENCE, cartData.getCode());
        hppFormData.put(SKIN_CODE, skinCode);
        hppFormData.put(MERCHANT_ACCOUNT, merchantAccount);
        hppFormData.put(SESSION_VALIDITY, sessionValidity);
        hppFormData.put(BRAND_CODE, cartData.getAdyenPaymentMethod());
        hppFormData.put(ISSUER_ID, cartData.getAdyenIssuerId());
        hppFormData.put(COUNTRY_CODE, countryCode);
        hppFormData.put(RES_URL, redirectUrl);

        String dataToSign = getHmacValidator().getDataToSign(hppFormData);
        String merchantSig = getHmacValidator().calculateHMAC(dataToSign, hmacKey);

        hppFormData.put(MERCHANT_SIG, merchantSig);

        //Lock the cart
        lockSessionCart();

        return hppFormData;
    }

    private void mapRequest(final HttpServletRequest request, final Map<String, String> map, String parameterName) {
        String value = request.getParameter(parameterName);
        if (value != null) {
            map.put(parameterName, value);
        }
    }

    /**
     * Create order and authorized TX
     */
    private OrderData createAuthorizedOrder(final PaymentResult paymentResult) throws InvalidCartException {
        final CartModel cartModel = cartService.getSessionCart();
        final String merchantTransactionCode = cartModel.getCode();

        //First save the transactions to the CartModel < AbstractOrderModel
        getAdyenTransactionService().authorizeOrderModel(cartModel, merchantTransactionCode, paymentResult.getPspReference());

        OrderData orderData = getCheckoutFacade().placeOrder();
        OrderModel orderModel = orderRepository.getOrderModel(orderData.getCode());
        updateOrder(orderModel, paymentResult);

        return orderData;
    }

    private void updateOrder(final OrderModel orderModel, final PaymentResult paymentResult) {
        try {
            adyenOrderService.updateOrderFromPaymentResult(orderModel, paymentResult);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * Retrieve available payment methods
     */
    public void initializeCheckoutData(Model model) {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        AdyenPaymentService adyenPaymentService = getAdyenPaymentService();

        //Set APMs from Adyen HPP Directory Lookup
        List<PaymentMethod> alternativePaymentMethods = new ArrayList<>();
        try {
            alternativePaymentMethods = adyenPaymentService.getPaymentMethods(cartData.getTotalPrice().getValue(),
                                                                              cartData.getTotalPrice().getCurrencyIso(),
                                                                              cartData.getDeliveryAddress().getCountry().getIsocode());
        } catch (HTTPClientException | SignatureException | IOException e) {
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
                LOGGER.error("API Exception " + e.getError());
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
        }

        //Set HPP payment methods
        model.addAttribute(MODEL_PAYMENT_METHODS, alternativePaymentMethods);

        //Set allowed Credit Cards
        model.addAttribute(MODEL_ALLOWED_CARDS, baseStore.getAdyenAllowedCards());

        model.addAttribute(MODEL_REMEMBER_DETAILS, showRememberTheseDetails);
        model.addAttribute(MODEL_STORED_CARDS, storedCards);

        //Set the url for CSE script
        model.addAttribute(MODEL_CSE_URL, adyenPaymentService.getCSEUrl());

        Set<String> recurringDetailReferences = storedCards.stream().map(RecurringDetail::getRecurringDetailReference).collect(Collectors.toSet());

        //Set stored cards to model
        CartModel cartModel = cartService.getSessionCart();
        cartModel.setAdyenStoredCards(recurringDetailReferences);
        modelService.save(cartModel);
    }

    public boolean showRememberDetails() {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        /*
         * The show remember me checkout should only be shown as the
         * user is logged in and the recurirng mode is set to ONECLICK or ONECLICK,RECURRING
         */
        RecurringContractMode recurringContractMode = baseStore.getAdyenRecurringContractMode();
        if (! getCheckoutCustomerStrategy().isAnonymousCheckout() ) {
            if (Recurring.ContractEnum.ONECLICK_RECURRING.name().equals(recurringContractMode.getCode())
                || Recurring.ContractEnum.ONECLICK.name().equals(recurringContractMode.getCode())){
                return true;
            }
        }

        return false;
    }

    public PaymentInfoModel createPaymentInfo(final CartModel cartModel, AdyenPaymentForm adyenPaymentForm) {
        final PaymentInfoModel paymentInfo = modelService.create(PaymentInfoModel.class);
        paymentInfo.setUser(cartModel.getUser());
        paymentInfo.setSaved(false);
        paymentInfo.setCode(generateCcPaymentInfoCode(cartModel));

        final AddressData addressData = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
        addressData.setEmail(getCheckoutCustomerStrategy().getCurrentUserForCheckout().getContactEmail());

        AddressModel addressModel = new AddressModel();

        CountryModel country = null;

        if (addressData.getCountry() != null && ! addressData.getCountry().getIsocode().isEmpty()) {

            // countryModel from service
            country = commonI18NService.getCountry(addressData.getCountry().getIsocode());
            addressModel.setCountry(country);
        }

        addressModel.setEmail(getCheckoutCustomerStrategy().getCurrentUserForCheckout().getContactEmail());

        addressModel.setStreetname(addressData.getLine1());
        addressModel.setLine2(addressData.getLine2());
        addressModel.setPostalcode(addressData.getPostalCode());
        addressModel.setTown(addressData.getTown());

        if (addressData.getRegion() != null && ! addressData.getRegion().getIsocode().isEmpty() && country != null) {
            final RegionModel regionModel = commonI18NService.getRegion(country, addressData.getRegion().getIsocode());
            addressModel.setRegion(regionModel);
        }

        addressModel.setBillingAddress(true);
        addressModel.setOwner(paymentInfo);

        paymentInfo.setBillingAddress(addressModel);

        paymentInfo.setAdyenPaymentMethod(adyenPaymentForm.getPaymentMethod());
        paymentInfo.setAdyenIssuerId(adyenPaymentForm.getIssuerId());

        paymentInfo.setAdyenRememberTheseDetails(adyenPaymentForm.getRememberTheseDetails());
        paymentInfo.setAdyenSelectedReference(adyenPaymentForm.getSelectedReference());

        modelService.save(paymentInfo);

        return paymentInfo;
    }

    public void handlePaymentForm(AdyenPaymentForm adyenPaymentForm, BindingResult bindingResult) {
        CartModel cartModel = cartService.getSessionCart();
        boolean showRememberDetails = showRememberDetails();
        AdyenPaymentFormValidator adyenPaymentFormValidator = new AdyenPaymentFormValidator(cartModel.getAdyenStoredCards(), showRememberDetails);

        adyenPaymentFormValidator.validate(adyenPaymentForm, bindingResult);

        if(bindingResult.hasErrors()) {
            return;
        }

        //Update CartModel
        cartModel.setAdyenCseToken(adyenPaymentForm.getCseToken());

        PaymentInfoModel paymentInfo = createPaymentInfo(cartModel, adyenPaymentForm);
        cartModel.setPaymentInfo(paymentInfo);
        modelService.save(cartModel);
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
}
