package com.adyen.v6.facades.impl;

import com.adyen.v6.facades.AdyenExpressCheckoutFacade;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

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


    private Converter<AddressData, AddressModel> addressReverseConverter;


    public void expressPDPCheckout(AddressData addressData, String productCode) throws DuplicateUidException {
        CustomerModel user = createGuestCustomer(addressData.getEmail());
        CartModel cart = createCartForExpressCheckout(user);

        AddressModel addressModel = addressReverseConverter.convert(addressData);
        validateParameterNotNull(addressModel, "Empty address");

        DeliveryModeModel deliveryMode = deliveryModeService.getDeliveryModeForCode(DELIVERY_MODE_CODE);
        validateParameterNotNull(deliveryMode, "Delivery mode for Adyen express checkout not configured");

        addressModel.setOwner(user);
        addressModel.setBillingAddress(true);
        addressModel.setShippingAddress(true);

        modelService.save(addressModel);
        cart.setDeliveryAddress(addressModel);
        cart.setPaymentAddress(addressModel);
        cart.setDeliveryMode(deliveryMode);

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
            try {
                checkoutFacade.placeOrder();
            } catch (InvalidCartException e) {
                LOG.error("Invalid cart exception", e);
            }
            cartService.setSessionCart(sessionCart);
        } else {
            LOG.error("Checkout attempt on empty cart");
        }
    }

    public void expressCartCheckout(AddressData addressData) throws DuplicateUidException {
        CustomerModel user = createGuestCustomer(addressData.getEmail());
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

        cart.setDeliveryAddress(addressModel);
        cart.setPaymentAddress(addressModel);
        cart.setDeliveryMode(deliveryMode);

        modelService.save(cart);

        if (cartHasEntries(cart)) {
            try {
                checkoutFacade.placeOrder();
            } catch (InvalidCartException e) {
                LOG.error("Invalid cart exception", e);
            }
        } else {
            LOG.error("Checkout attempt on empty cart");
        }
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
}
