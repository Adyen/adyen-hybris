package com.adyen.v6.forms;

public class CSEPaymentForm {

    private String cseToken;
    private boolean useDeliveryAddress;

    public String getCseToken() {
        return cseToken;
    }

    public void setCseToken(String cseToken) {
        this.cseToken = cseToken;
    }

    public boolean isUseDeliveryAddress() {
        return useDeliveryAddress;
    }

    public void setUseDeliveryAddress(boolean useDeliveryAddress) {
        this.useDeliveryAddress = useDeliveryAddress;
    }
}
