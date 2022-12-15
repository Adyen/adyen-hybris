package com.adyen.v6.facades.impl;

public class SessionRequest {
    private String merchantAccount;
    private String returnUrl;
    private String reference;
    private String countryCode;
    private Amount amount;

    public SessionRequest(String merchantAccount, String returnUrl, String reference, String countryCode, Amount amount) {
        this.merchantAccount = merchantAccount;
        this.returnUrl = returnUrl;
        this.reference = reference;
        this.countryCode = countryCode;
        this.amount = amount;
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

    public String getReference() {
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

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
}
