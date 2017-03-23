package com.adyen.v6.facades;

import com.adyen.Util.HMACValidator;
import com.adyen.Util.Util;
import com.adyen.constants.HPPConstants;
import com.adyen.model.Amount;
import com.adyen.model.PaymentResult;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.exceptions.AdyenNonAuthorizedPaymentException;
import com.adyen.v6.repository.OrderRepository;
import com.adyen.v6.service.AdyenOrderService;
import com.adyen.v6.service.AdyenPaymentService;
import com.adyen.v6.service.AdyenTransactionService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.adyen.constants.HPPConstants.Fields.*;
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
    private AdyenPaymentService adyenPaymentService;
    private AdyenTransactionService adyenTransactionService;
    private OrderRepository orderRepository;
    private AdyenOrderService adyenOrderService;
    private CheckoutCustomerStrategy checkoutCustomerStrategy;
    private HMACValidator hmacValidator;

    public static final String SESSION_LOCKED_CART = "adyen_cart";
    public static final String SESSION_MD = "adyen_md";
    public static final String THREE_D_MD = "MD";
    public static final String THREE_D_PARES = "PaRes";
    public static final Logger LOGGER = Logger.getLogger(AdyenCheckoutFacade.class);

    public AdyenCheckoutFacade() {
        hmacValidator = new HMACValidator();
    }

    /**
     * Validates an HPP response
     *
     * @param hppResponseData
     * @param merchantSig
     * @throws SignatureException
     */
    public void validateHPPResponse(SortedMap<String, String> hppResponseData, String merchantSig) throws SignatureException {
        BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();

        String hmacKey = baseStore.getAdyenSkinHMAC();
        Assert.notNull(hmacKey);

        String dataToSign = getHmacValidator().getDataToSign(hppResponseData);
        String calculatedMerchantSig = getHmacValidator().calculateHMAC(dataToSign, hmacKey);
        LOGGER.info("Calculated signature: " + calculatedMerchantSig);
        if (!calculatedMerchantSig.equals(merchantSig)) {
            LOGGER.error("Signature does not match!");
            throw new SignatureException("Signatures doesn't match");
        }
    }

    /**
     * Retrieve the HPP base URL for the current basestore
     *
     * @return
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
     * @return
     * @throws InvalidCartException
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
     * @param request
     * @return OrderData
     * @throws SignatureException
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

        LOGGER.info("Received HPP response: " + hppResponseData);

        String merchantSig = request.getParameter(HPPConstants.Response.MERCHANT_SIG);
        String merchantReference = request.getParameter(HPPConstants.Response.MERCHANT_REFERENCE);
        String authResult = request.getParameter(HPPConstants.Response.AUTH_RESULT);

        validateHPPResponse(hppResponseData, merchantSig);

        OrderData orderData = null;

        //Restore the cart or find the created order
        try {
            restoreSessionCart();

            if (HPPConstants.Response.AUTH_RESULT_AUTHORISED.equals(authResult)
                    || HPPConstants.Response.AUTH_RESULT_PENDING.equals(authResult)) {
                orderData = getCheckoutFacade().placeOrder();
            }
        } catch (InvalidCartException e) {
            LOGGER.info(e);
            //Cart does not exist, retrieve order
            orderData = getOrderFacade().getOrderDetailsForCode(merchantReference);
        }

        return orderData;
    }

    /**
     * Authorizes a CC payment
     * In case of authorized, it places an order from cart
     *
     * @param request
     * @param cartData
     * @return
     * @throws Exception
     */
    public OrderData authoriseCardPayment(final HttpServletRequest request, final CartData cartData) throws Exception {
        Boolean guestUser = getCheckoutCustomerStrategy().isAnonymousCheckout();
        PaymentResult paymentResult = getAdyenPaymentService().authorise(
                cartData,
                request,
                getCheckoutCustomerStrategy().getCurrentUserForCheckout(),
                guestUser
        );

        LOGGER.info("authorization result: " + paymentResult);

        if (paymentResult.isAuthorised()) {
            return createAuthorizedOrder(paymentResult);
        } else if (paymentResult.isRedirectShopper()) {
            getSessionService().setAttribute(SESSION_MD, paymentResult.getMd());

            lockSessionCart();
        }

        throw new AdyenNonAuthorizedPaymentException(paymentResult);
    }

    /**
     * Handles an 3D response
     * In case of authorized, it places an order from cart
     *
     * @param request
     * @return
     * @throws Exception
     */
    public OrderData handle3DResponse(final HttpServletRequest request) throws Exception {
        String paRes = request.getParameter(THREE_D_PARES);
        String md = request.getParameter(THREE_D_MD);

        String sessionMd = getSessionService().getAttribute(SESSION_MD);

        try {
            //Check if MD matches in order to avoid authorizing wrong order
            if (sessionMd != null && !sessionMd.equals(md)) {
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
     * @param cartData
     * @param redirectUrl
     * @return
     * @throws SignatureException
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
        Amount amount = Util.createAmount(
                cartData.getTotalPrice().getValue(),
                cartData.getTotalPrice().getCurrencyIso()
        );

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
        hppFormData.put(BRAND_CODE, cartData.getAdyenBrandCode());
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
     *
     * @param paymentResult
     * @return
     * @throws CMSItemNotFoundException
     */
    private OrderData createAuthorizedOrder(final PaymentResult paymentResult) throws InvalidCartException {
        final CartModel cartModel = cartService.getSessionCart();
        final String merchantTransactionCode = cartModel.getCode();

        //First save the transactions to the CartModel < AbstractOrderModel
        getAdyenTransactionService().authorizeOrderModel(
                cartModel,
                merchantTransactionCode,
                paymentResult.getPspReference());

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

    public AdyenPaymentService getAdyenPaymentService() {
        //Set the baseStore first
        adyenPaymentService.setBaseStore(baseStoreService.getCurrentBaseStore());
        return adyenPaymentService;
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

    public void setAdyenPaymentService(AdyenPaymentService adyenPaymentService) {
        this.adyenPaymentService = adyenPaymentService;
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
}
