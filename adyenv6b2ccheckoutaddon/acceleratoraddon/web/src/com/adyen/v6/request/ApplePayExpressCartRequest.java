package com.adyen.v6.request;

import de.hybris.platform.commercefacades.user.data.AddressData;

import java.io.Serializable;

public class ApplePayExpressCartRequest implements Serializable {
    private AddressData addressData;
    private String adyenApplePayMerchantName;
    private String adyenApplePayMerchantIdentifier;
    private String applePayToken;

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
