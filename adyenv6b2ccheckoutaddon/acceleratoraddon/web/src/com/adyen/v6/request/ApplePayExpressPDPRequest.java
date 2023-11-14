package com.adyen.v6.request;

import de.hybris.platform.commercefacades.user.data.AddressData;

import java.io.Serializable;

public class ApplePayExpressPDPRequest implements Serializable {
    private String productCode;
    private AddressData addressData;
    private String adyenApplePayMerchantName;
    private String adyenApplePayMerchantIdentifier;
    private String applePayToken;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public AddressData getAddressData() {
        return addressData;
    }

    public void setAddressData(AddressData addressData) {
        this.addressData = addressData;
    }

    public String getAdyenApplePayMerchantName() {
        return adyenApplePayMerchantName;
    }

    public void setAdyenApplePayMerchantName(String adyenApplePayMerchantName) {
        this.adyenApplePayMerchantName = adyenApplePayMerchantName;
    }

    public String getAdyenApplePayMerchantIdentifier() {
        return adyenApplePayMerchantIdentifier;
    }

    public void setAdyenApplePayMerchantIdentifier(String adyenApplePayMerchantIdentifier) {
        this.adyenApplePayMerchantIdentifier = adyenApplePayMerchantIdentifier;
    }

    public String getApplePayToken() {
        return applePayToken;
    }

    public void setApplePayToken(String applePayToken) {
        this.applePayToken = applePayToken;
    }
}
