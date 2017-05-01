package com.adyen.v6.forms;

import com.adyen.Util.Util;

import javax.validation.constraints.NotNull;

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
    private String selectedAlias;

    //Save card
    private boolean rememberTheseDetails;

    //HPP
    private String issuerId;

    //Boleto
    private String boletoSocialSecurityNumber;
    private String boletoFirstName;
    private String boletoLastName;
    private String boletoBank;

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

    public String getSelectedAlias() {
        return selectedAlias;
    }

    public void setSelectedAlias(String selectedAlias) {
        this.selectedAlias = selectedAlias;
    }

    public boolean isRememberTheseDetails() {
        return rememberTheseDetails;
    }

    public String getBoletoSocialSecurityNumber() {
        return boletoSocialSecurityNumber;
    }

    public void setBoletoSocialSecurityNumber(String boletoSocialSecurityNumber) {
        this.boletoSocialSecurityNumber = boletoSocialSecurityNumber;
    }

    public String getBoletoFirstName() {
        return boletoFirstName;
    }

    public void setBoletoFirstName(String boletoFirstName) {
        this.boletoFirstName = boletoFirstName;
    }

    public String getBoletoLastName() {
        return boletoLastName;
    }

    public void setBoletoLastName(String boletoLastName) {
        this.boletoLastName = boletoLastName;
    }

    public String getBoletoBank() {
        return boletoBank;
    }

    public void setBoletoBank(String boletoBank) {
        this.boletoBank = boletoBank;
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
        sb.append("    selectedAlias: ").append(Util.toIndentedString(selectedAlias)).append("\n");
        sb.append("    boletoBank: ").append(Util.toIndentedString(boletoBank)).append("\n");
        sb.append("    boletoSocialSecurityNumber: ").append(Util.toIndentedString(boletoSocialSecurityNumber)).append("\n");
        sb.append("    boletoFirstName: ").append(Util.toIndentedString(boletoFirstName)).append("\n");
        sb.append("    boletoLastName: ").append(Util.toIndentedString(boletoLastName)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
