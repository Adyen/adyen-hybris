package com.adyen.v6.dto;

import com.adyen.model.Amount;
import com.adyen.model.checkout.PaymentMethod;
import com.adyen.model.checkout.StoredPaymentMethod;
import com.adyen.v6.enums.AdyenCardTypeEnum;
import de.hybris.platform.commercefacades.order.data.CartData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CheckoutConfigDTO {
    private final CartData cartData;
    private final List<PaymentMethod> alternativePaymentMethods;
    private final List<String> connectedTerminalList;
    private final List<StoredPaymentMethod> storedPaymentMethodList;
    private final Map<String, String> issuerLists;
    private final PaymentMethod sepaDirectDebit;
    private final String creditCardLabel;
    private final List<AdyenCardTypeEnum> allowedCards;//lista
    private final Amount amount;
    private final String adyenClientKey;
    private final String adyenPaypalMerchantId;
    private final String deviceFingerPrintUrl;


    public CheckoutConfigDTO(CartData cartData, List<PaymentMethod> alternativePaymentMethods, List<String> connectedTerminalList, List<StoredPaymentMethod> storedPaymentMethodList, Map<String, String> issuerLists, PaymentMethod sepaDirectDebit, String creditCardLabel, List<AdyenCardTypeEnum> allowedCards, Amount amount, String adyenClientKey, String adyenPaypalMerchantId, String deviceFingerPrintUrl) {
        this.cartData = cartData;
        this.alternativePaymentMethods = alternativePaymentMethods;
        this.connectedTerminalList = connectedTerminalList;
        this.storedPaymentMethodList = storedPaymentMethodList;
        this.issuerLists = issuerLists;
        this.sepaDirectDebit = sepaDirectDebit;
        this.creditCardLabel = creditCardLabel;
        this.allowedCards = allowedCards;
        this.amount = amount;
        this.adyenClientKey = adyenClientKey;
        this.adyenPaypalMerchantId = adyenPaypalMerchantId;
        this.deviceFingerPrintUrl = deviceFingerPrintUrl;
    }

    public CartData getCartData() {
        return cartData;
    }

    public List<PaymentMethod> getAlternativePaymentMethods() {
        return alternativePaymentMethods;
    }

    public List<String> getConnectedTerminalList() {
        return connectedTerminalList;
    }

    public List<StoredPaymentMethod> getStoredPaymentMethodList() {
        return storedPaymentMethodList;
    }

    public Map<String, String> getIssuerLists() {
        return issuerLists;
    }

    public PaymentMethod getSepaDirectDebit() {
        return sepaDirectDebit;
    }

    public String getCreditCardLabel() {
        return creditCardLabel;
    }

    public List<AdyenCardTypeEnum> getAllowedCards() {
        return allowedCards;
    }

    public Amount getAmount() {
        return amount;
    }

    public String getAdyenClientKey() {
        return adyenClientKey;
    }

    public String getAdyenPaypalMerchantId() {
        return adyenPaypalMerchantId;
    }

    public String getDeviceFingerPrintUrl() {
        return deviceFingerPrintUrl;
    }
}