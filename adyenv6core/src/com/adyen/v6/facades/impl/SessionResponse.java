package com.adyen.v6.facades.impl;

public class SessionResponse {

    private String countryCode;
    private String expiresAt;
    private String merchantAccount;
    private String returnUrl;
    private String sessionData;
    private String id;
    private String reference;
    private Amount amount;

    public SessionResponse(){

    }
    public SessionResponse(String countryCode, String expiresAt, String merchantAccount, String returnUrl, String sessionData, String id, Amount amount) {
        this.countryCode = countryCode;
        this.expiresAt = expiresAt;
        this.merchantAccount = merchantAccount;
        this.returnUrl = returnUrl;
        this.sessionData = sessionData;
        this.id = id;
        this.amount = amount;
    }

    public String getReference(){
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getMerchantAccount() {
        return merchantAccount;
    }

    public void setMerchantAccount(String merchantAccount) {
        this.merchantAccount = merchantAccount;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getSessionData() {
        return sessionData;
    }

    public void setSessionData(String sessionData) {
        this.sessionData = sessionData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
}
