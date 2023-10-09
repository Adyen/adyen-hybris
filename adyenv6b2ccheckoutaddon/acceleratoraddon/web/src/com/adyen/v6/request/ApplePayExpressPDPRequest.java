package com.adyen.v6.request;

import de.hybris.platform.commercefacades.user.data.AddressData;

import java.io.Serializable;

public class ApplePayExpressPDPRequest implements Serializable {
    private String productCode;
    private AddressData addressData;

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
}
