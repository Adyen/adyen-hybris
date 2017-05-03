package com.adyen.v6.forms;

import javax.validation.constraints.NotNull;
import com.adyen.Util.Util;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;

/**
 * Form for select payment method page
 */
public class AdyenPaymentForm {
    @NotNull
    private String paymentMethod;

    //CSE
    private String cseToken;
    private String selectedReference;
    //Save card
    private boolean rememberTheseDetails;

    //HPP
    private String issuerId;


    public String getCseToken() {
        return cseToken;
    }

    public void setCseToken(String cseToken) {
        this.cseToken = cseToken;
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

    public boolean getRememberTheseDetails() {
        return this.rememberTheseDetails;
    }

    public void setRememberTheseDetails(boolean rememberTheseDetails) {
        this.rememberTheseDetails = rememberTheseDetails;
    }

    public boolean isRememberTheseDetails() {
        return rememberTheseDetails;
    }

    public String getSelectedReference() {
        return selectedReference;
    }

    public void setSelectedReference(String selectedReference) {
        this.selectedReference = selectedReference;
    }

    public boolean isCC() {
        return PAYMENT_METHOD_CC.equals(paymentMethod);

    }

    public boolean isOneClick() {
        return PAYMENT_METHOD_ONECLICK.indexOf(paymentMethod) == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AdyenPaymentForm {\n");

        sb.append("    paymentMethod: ").append(Util.toIndentedString(paymentMethod)).append("\n");
        sb.append("    cseToken: ").append(Util.toIndentedString(cseToken)).append("\n");
        sb.append("    issuerId: ").append(Util.toIndentedString(issuerId)).append("\n");
        sb.append("    rememberTheseDetails: ").append(Util.toIndentedString(rememberTheseDetails)).append("\n");
        sb.append("    selectedReference: ").append(Util.toIndentedString(selectedReference)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
