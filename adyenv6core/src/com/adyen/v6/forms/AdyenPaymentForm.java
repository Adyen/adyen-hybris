/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.forms;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.adyen.Util.Util;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;

/**
 * Form for select payment method page
 */
public class AdyenPaymentForm {
    private static final Logger LOG = Logger.getLogger(AdyenPaymentForm.class);

    @NotNull
    private String paymentMethod;

    //CSE
    private String cseToken;

    //Secured Fields
    private String encryptedCardNumber;
    private String encryptedExpiryMonth;
    private String encryptedExpiryYear;
    private String encryptedSecurityCode;
    private String cardBrand;
    private String cardHolder;

    private String selectedReference;
    private int installments;

    //Save card
    private boolean rememberTheseDetails;

    //HPP
    private String issuerId;

    // openinvoice fields
    private String dob;
    private String dfValue;

    //Boleto
    private String firstName;
    private String lastName;

    // used in openinvoice and boleto
    private String socialSecurityNumber;

    //3DS 2.0
    private String browserInfo;

    public String getBrowserInfo() {
        return browserInfo;
    }

    public void setBrowserInfo(String browserInfo) {
        this.browserInfo = browserInfo;
    }

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDob() {
        Date dateOfBirth = null;
        if (dob != null) {
            try {
                // make sure the input format is yyyy-MM-dd
                if (dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    dateOfBirth = format.parse(dob);
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }
        return dateOfBirth;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public boolean isCC() {
        return PAYMENT_METHOD_CC.equals(paymentMethod);

    }

    public boolean isOneClick() {
        return PAYMENT_METHOD_ONECLICK.indexOf(paymentMethod) == 0;
    }

    public String getDfValue() {
        return dfValue;
    }

    public void setDfValue(String dfValue) {
        this.dfValue = dfValue;
    }

    public String getEncryptedCardNumber() {
        return encryptedCardNumber;
    }

    public void setEncryptedCardNumber(String encryptedCardNumber) {
        this.encryptedCardNumber = encryptedCardNumber;
    }

    public String getEncryptedExpiryMonth() {
        return encryptedExpiryMonth;
    }

    public void setEncryptedExpiryMonth(String encryptedExpiryMonth) {
        this.encryptedExpiryMonth = encryptedExpiryMonth;
    }

    public String getEncryptedExpiryYear() {
        return encryptedExpiryYear;
    }

    public void setEncryptedExpiryYear(String encryptedExpiryYear) {
        this.encryptedExpiryYear = encryptedExpiryYear;
    }

    public String getEncryptedSecurityCode() {
        return encryptedSecurityCode;
    }

    public void setEncryptedSecurityCode(String encryptedSecurityCode) {
        this.encryptedSecurityCode = encryptedSecurityCode;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public int getInstallments() {
        return installments;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AdyenPaymentForm {\n");
        sb.append("    paymentMethod: ").append(Util.toIndentedString(paymentMethod)).append("\n");
        sb.append("    cseToken isEmpty?: ").append(StringUtils.isEmpty(cseToken)).append("\n");
        sb.append("    encryptedCardNumber isEmpty?: ").append(StringUtils.isEmpty(encryptedCardNumber)).append("\n");
        sb.append("    encryptedExpiryMonth isEmpty?: ").append(StringUtils.isEmpty(encryptedExpiryMonth)).append("\n");
        sb.append("    encryptedExpiryYear isEmpty?: ").append(StringUtils.isEmpty(encryptedExpiryYear)).append("\n");
        sb.append("    encryptedSecurityCode isEmpty?: ").append(StringUtils.isEmpty(encryptedSecurityCode)).append("\n");
        sb.append("    cardHolder: ").append(Util.toIndentedString(cardHolder)).append("\n");
        sb.append("    installments: ").append(Util.toIndentedString(installments)).append("\n");
        sb.append("    issuerId: ").append(Util.toIndentedString(issuerId)).append("\n");
        sb.append("    rememberTheseDetails: ").append(Util.toIndentedString(rememberTheseDetails)).append("\n");
        sb.append("    selectedReference: ").append(Util.toIndentedString(selectedReference)).append("\n");
        sb.append("    dateOfBirth: ").append(Util.toIndentedString(dob)).append("\n");
        sb.append("    socialSecurityNumber: ").append(Util.toIndentedString(socialSecurityNumber)).append("\n");
        sb.append("    firstName: ").append(Util.toIndentedString(firstName)).append("\n");
        sb.append("    lastName: ").append(Util.toIndentedString(lastName)).append("\n");
        sb.append("    dfValue: ").append(Util.toIndentedString(dfValue)).append("\n");
        sb.append("    cardBrand: ").append(Util.toIndentedString(cardBrand)).append("\n");
        sb.append("    browserInfo: ").append(Util.toIndentedString(browserInfo)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
