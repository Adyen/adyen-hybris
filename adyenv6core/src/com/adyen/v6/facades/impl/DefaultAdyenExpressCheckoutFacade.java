package com.adyen.v6.facades.impl;

import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.model.checkout.details.ApplePayDetails;
import com.adyen.v6.constants.Adyenv6coreConstants;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.facades.AdyenExpressCheckoutFacade;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.order.*;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class DefaultAdyenExpressCheckoutFacade implements AdyenExpressCheckoutFacade {
    private static final Logger LOG = Logger.getLogger(DefaultAdyenExpressCheckoutFacade.class);
    private static final String USER_NAME = "ApplePayExpressGuest";
    private static final String DELIVERY_MODE_CODE = "adyen-express-checkout";
    private CartFactory cartFactory;
    private CartService cartService;
    private ProductService productService;
    private ModelService modelService;
    private CustomerFacade customerFacade;
    private CommonI18NService commonI18NService;
    private CustomerAccountService customerAccountService;
    private CheckoutFacade checkoutFacade;
    private CommerceCartService commerceCartService;
    private DeliveryModeService deliveryModeService;
    private ZoneDeliveryModeService zoneDeliveryModeService;
    private AdyenCheckoutFacade adyenCheckoutFacade;
    private UserService userService;
    private Converter<AddressData, AddressModel> addressReverseConverter;
    private Converter<CartModel, CartData> cartConverter;


    public PaymentsResponse expressPDPCheckout(AddressData addressData, String productCode, String merchantId, String merchantName,
                                               String applePayToken, HttpServletRequest request) throws Exception {
        validateParameterNotNull(addressData, "Empty address");
        if (StringUtils.isEmpty(addressData.getEmail())) {
            throw new IllegalArgumentException("Empty email address");
        }

        CustomerModel user = createGuestCustomer(addressData.getEmail());
        userService.setCurrentUser(user);

        CartModel cart = createCartForExpressCheckout(user);

        AddressModel addressModel = addressReverseConverter.convert(addressData);
        validateParameterNotNull(addressModel, "Empty address");

        DeliveryModeModel deliveryMode = deliveryModeService.getDeliveryModeForCode(DELIVERY_MODE_CODE);
        validateParameterNotNull(deliveryMode, "Delivery mode for Adyen express checkout not configured");

        addressModel.setOwner(user);
        addressModel.setBillingAddress(true);
        addressModel.setShippingAddress(true);

        modelService.save(addressModel);
        cart.setDeliveryMode(deliveryMode);

        cart.setDeliveryAddress(addressModel);
        cart.setPaymentAddress(addressModel);

        PaymentInfoModel paymentInfo = createPaymentInfoForCart(user, addressModel, cart,
                Adyenv6coreConstants.PAYMENT_METHOD_APPLEPAY, merchantId, merchantName);
        cart.setPaymentInfo(paymentInfo);

        ProductModel product = productService.getProductForCode(productCode);

        if (product != null) {
            cartService.addNewEntry(cart, product, 1L, product.getUnit());
        }
        modelService.save(cart);

        if (cartHasEntries(cart)) {
            CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
            commerceCartParameter.setCart(cart);
            commerceCartService.calculateCart(commerceCartParameter);

            CartModel sessionCart = cartService.getSessionCart();
            cartService.setSessionCart(cart);

            CartData cartData = cartConverter.convert(cart);

            ApplePayDetails applePayDetails = new ApplePayDetails();
            applePayDetails.setApplePayToken(applePayToken);

            PaymentsResponse paymentsResponse = adyenCheckoutFacade.componentPayment(request, cartData, applePayDetails);

            cartService.setSessionCart(sessionCart);

            return paymentsResponse;
        } else {
            throw new InvalidCartException("Checkout attempt on empty cart");
        }
    }

    public PaymentsResponse expressCartCheckout(AddressData addressData, String merchantId, String merchantName,
                                                String applePayToken, HttpServletRequest request) throws Exception {
        CustomerModel user = createGuestCustomer(addressData.getEmail());
        userService.setCurrentUser(user);
        cartService.changeCurrentCartUser(user);

        CartModel cart = cartService.getSessionCart();
        AddressModel addressModel = addressReverseConverter.convert(addressData);
        validateParameterNotNull(addressModel, "Empty address");

        DeliveryModeModel deliveryMode = deliveryModeService.getDeliveryModeForCode(DELIVERY_MODE_CODE);
        validateParameterNotNull(deliveryMode, "Delivery mode for Adyen express checkout not configured");

        addressModel.setBillingAddress(true);
        addressModel.setShippingAddress(true);
        addressModel.setOwner(user);
        modelService.save(addressModel);

        cart.setDeliveryMode(deliveryMode);

        cart.setDeliveryAddress(addressModel);
        cart.setPaymentAddress(addressModel);

        PaymentInfoModel paymentInfo = createPaymentInfoForCart(user, addressModel, cart,
                Adyenv6coreConstants.PAYMENT_METHOD_APPLEPAY, merchantId, merchantName);
        cart.setPaymentInfo(paymentInfo);

        modelService.save(cart);

        if (cartHasEntries(cart)) {
            CartData cartData = cartConverter.convert(cart);

            ApplePayDetails applePayDetails = new ApplePayDetails();
            applePayDetails.setApplePayToken(applePayToken);

            return adyenCheckoutFacade.componentPayment(request, cartData, applePayDetails);
        } else {
            throw new InvalidCartException("Checkout attempt on empty cart");
        }
    }

    public Optional<ZoneDeliveryModeValueModel> getExpressDeliveryModePrice() {
        ZoneDeliveryModeModel deliveryMode = (ZoneDeliveryModeModel) deliveryModeService.getDeliveryModeForCode(DELIVERY_MODE_CODE);
        CurrencyModel currentCurrency = commonI18NService.getCurrentCurrency();

        return deliveryMode.getValues().stream().filter(valueModel -> valueModel.getCurrency().equals(currentCurrency)).findFirst();
    }

    private CustomerModel createGuestCustomer(String emailAddress) throws DuplicateUidException {
        Assert.isTrue(EmailValidator.getInstance().isValid(emailAddress), "Invalid email address");

        return createGuestUserForAnonymousCheckout(emailAddress, USER_NAME);
    }

    private CustomerModel createGuestUserForAnonymousCheckout(final String email, final String name) throws DuplicateUidException {
        validateParameterNotNullStandardMessage("email", email);
        final CustomerModel guestCustomer = modelService.create(CustomerModel.class);
        final String guid = customerFacade.generateGUID();

        //takes care of localizing the name based on the site language
        guestCustomer.setUid(guid + "|" + email);
        guestCustomer.setName(name);
        guestCustomer.setType(CustomerType.valueOf(CustomerType.GUEST.getCode()));
        guestCustomer.setSessionLanguage(commonI18NService.getCurrentLanguage());
        guestCustomer.setSessionCurrency(commonI18NService.getCurrentCurrency());

        customerAccountService.registerGuestForAnonymousCheckout(guestCustomer, guid);

        return guestCustomer;
    }

    private CartModel createCartForExpressCheckout(CustomerModel guestUser) {
        CartModel cart = cartFactory.createCart();
        cart.setUser(guestUser);
        modelService.save(cart);
        return cart;
    }

    private PaymentInfoModel createPaymentInfoForCart(CustomerModel customerModel, AddressModel addressModel, CartModel cartModel, String paymentMethod, String merchantId, String merchantName) {
        final PaymentInfoModel paymentInfo = modelService.create(PaymentInfoModel.class);
        paymentInfo.setUser(customerModel);
        paymentInfo.setCode(generateCcPaymentInfoCode(cartModel));
        paymentInfo.setBillingAddress(addressModel);
        paymentInfo.setAdyenPaymentMethod(paymentMethod);

        paymentInfo.setAdyenApplePayMerchantName(merchantName);
        paymentInfo.setAdyenApplePayMerchantIdentifier(merchantId);

        modelService.save(paymentInfo);

        return paymentInfo;
    }

    protected String generateCcPaymentInfoCode(final CartModel cartModel) {
        return cartModel.getCode() + "_" + UUID.randomUUID();
    }

    private boolean cartHasEntries(CartModel cartModel) {
        return cartModel != null && !CollectionUtils.isEmpty(cartModel.getEntries());
    }

    public void setCartFactory(CartFactory cartFactory) {
        this.cartFactory = cartFactory;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setAddressReverseConverter(Converter<AddressData, AddressModel> addressReverseConverter) {
        this.addressReverseConverter = addressReverseConverter;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public void setCustomerFacade(CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public void setCustomerAccountService(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }

    public void setCheckoutFacade(CheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public void setCommerceCartService(CommerceCartService commerceCartService) {
        this.commerceCartService = commerceCartService;
    }

    public void setDeliveryModeService(DeliveryModeService deliveryModeService) {
        this.deliveryModeService = deliveryModeService;
    }

    public void setZoneDeliveryModeService(ZoneDeliveryModeService zoneDeliveryModeService) {
        this.zoneDeliveryModeService = zoneDeliveryModeService;
    }

    public void setAdyenCheckoutFacade(AdyenCheckoutFacade adyenCheckoutFacade) {
        this.adyenCheckoutFacade = adyenCheckoutFacade;
    }

    public void setCartConverter(Converter<CartModel, CartData> cartConverter) {
        this.cartConverter = cartConverter;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
