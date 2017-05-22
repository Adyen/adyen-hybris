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
    private String selectedReference;
    //Save card
    private boolean rememberTheseDetails;

    //HPP
    private String issuerId;

    // openinvoice fields
    private String dob;
    private String socialSecurityNumber;
    private String dfValue;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AdyenPaymentForm {\n");
        sb.append("    paymentMethod: ").append(Util.toIndentedString(paymentMethod)).append("\n");
        sb.append("    cseToken: ").append(Util.toIndentedString(cseToken)).append("\n");
        sb.append("    issuerId: ").append(Util.toIndentedString(issuerId)).append("\n");
        sb.append("    rememberTheseDetails: ").append(Util.toIndentedString(rememberTheseDetails)).append("\n");
        sb.append("    selectedReference: ").append(Util.toIndentedString(selectedReference)).append("\n");
        sb.append("    dateOfBirth: ").append(Util.toIndentedString(dob)).append("\n");
        sb.append("    socialSecurityNumber: ").append(Util.toIndentedString(socialSecurityNumber)).append("\n");
        sb.append("    dfValue: ").append(Util.toIndentedString(dfValue)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
