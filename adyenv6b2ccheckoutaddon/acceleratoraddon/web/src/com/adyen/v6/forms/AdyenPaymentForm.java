package com.adyen.v6.forms;

import com.adyen.Util.Util;

import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;

/**
 * Form for select payment method page
 */
public class AdyenPaymentForm {
    private String paymentMethod;

    //CSE
    private String cseToken;

    //HPP
    private String issuerId;
    private String brandCode;

    //Billing Address
    private boolean useDeliveryAddress;

    //Save card
    private boolean rememberTheseDetails;

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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public boolean getRememberTheseDetails() {
        return this.rememberTheseDetails;
    }

    public void setRememberTheseDetails(boolean rememberTheseDetails) {
        this.rememberTheseDetails = rememberTheseDetails;
    }

    public boolean isCSE() {
        if(PAYMENT_METHOD_CC.equals(paymentMethod)) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AdyenPaymentForm {\n");

        sb.append("    paymentMethod: ").append(Util.toIndentedString(paymentMethod)).append("\n");
        sb.append("    cseToken: ").append(Util.toIndentedString(cseToken)).append("\n");
        sb.append("    brandCode: ").append(Util.toIndentedString(brandCode)).append("\n");
        sb.append("    issuerId: ").append(Util.toIndentedString(issuerId)).append("\n");
        sb.append("    rememberTheseDetails: ").append(Util.toIndentedString(rememberTheseDetails)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
