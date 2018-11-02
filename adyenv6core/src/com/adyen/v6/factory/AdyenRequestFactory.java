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
package com.adyen.v6.factory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.adyen.Util.Util;
import com.adyen.enums.VatCategory;
import com.adyen.model.AbstractPaymentRequest;
import com.adyen.model.Address;
import com.adyen.model.Amount;
import com.adyen.model.Name;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentRequest3d;
import com.adyen.model.additionalData.InvoiceLine;
import com.adyen.model.checkout.DefaultPaymentMethodDetails;
import com.adyen.model.checkout.LineItem;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.modification.CancelOrRefundRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.RefundRequest;
import com.adyen.model.recurring.DisableRequest;
import com.adyen.model.recurring.Recurring;
import com.adyen.model.recurring.RecurringDetailsRequest;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.model.RequestInfo;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.util.TaxValue;
import static com.adyen.constants.BrandCodes.PAYPAL_ECS;
import static com.adyen.v6.constants.Adyenv6coreConstants.KLARNA;
import static com.adyen.v6.constants.Adyenv6coreConstants.OPENINVOICE_METHODS_API;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_BOLETO;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_BOLETO_SANTANDER;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_IDEAL;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;

public class AdyenRequestFactory {
    private static final Logger LOG = Logger.getLogger(AdyenRequestFactory.class);

    public PaymentRequest3d create3DAuthorizationRequest(final String merchantAccount, final HttpServletRequest request, final String md, final String paRes) {
        return createBasePaymentRequest(new PaymentRequest3d(), request, merchantAccount).set3DRequestData(md, paRes);
    }

    @Deprecated
    public PaymentRequest createAuthorizationRequest(final String merchantAccount,
                                                     final CartData cartData,
                                                     final HttpServletRequest request,
                                                     final CustomerModel customerModel,
                                                     final RecurringContractMode recurringContractMode) {
        String amount = String.valueOf(cartData.getTotalPrice().getValue());
        String currency = cartData.getTotalPrice().getCurrencyIso();
        String reference = cartData.getCode();

        PaymentRequest paymentRequest = createBasePaymentRequest(new PaymentRequest(), request, merchantAccount).reference(reference).setAmountData(amount, currency);

        // set shopper details
        if (customerModel != null) {
            paymentRequest.setShopperReference(customerModel.getCustomerID());
            paymentRequest.setShopperEmail(customerModel.getContactEmail());
        }

        // set recurring contract
        if (customerModel != null && PAYMENT_METHOD_CC.equals(cartData.getAdyenPaymentMethod())) {
            Recurring recurring = getRecurringContractType(recurringContractMode, cartData.getAdyenRememberTheseDetails());
            paymentRequest.setRecurring(recurring);
        }

        // if address details are provided added it into the request
        if (cartData.getDeliveryAddress() != null) {
            Address deliveryAddress = setAddressData(cartData.getDeliveryAddress());
            paymentRequest.setDeliveryAddress(deliveryAddress);
        }

        if (cartData.getPaymentInfo().getBillingAddress() != null) {
            // set PhoneNumber if it is provided
            if (cartData.getPaymentInfo().getBillingAddress().getPhone() != null && ! cartData.getPaymentInfo().getBillingAddress().getPhone().isEmpty()) {
                paymentRequest.setTelephoneNumber(cartData.getPaymentInfo().getBillingAddress().getPhone());
            }

            Address billingAddress = setAddressData(cartData.getPaymentInfo().getBillingAddress());
            paymentRequest.setBillingAddress(billingAddress);
        }

        // OpenInvoice add required additional data
        if (OPENINVOICE_METHODS_API.contains(cartData.getAdyenPaymentMethod())) {
            paymentRequest.selectedBrand(cartData.getAdyenPaymentMethod());
            setOpenInvoiceData(paymentRequest, cartData, customerModel);

            paymentRequest.setShopperName(getShopperNameFromAddress(cartData.getDeliveryAddress()));
        }

        //Set Boleto parameters
        if (cartData.getAdyenPaymentMethod().indexOf(PAYMENT_METHOD_BOLETO) == 0) {
            setBoletoData(paymentRequest, cartData);
        }

        //Set Paypal Express Checkout Shortcut parameters
        if (PAYPAL_ECS.equals(cartData.getAdyenPaymentMethod())) {
            setPaypalEcsData(paymentRequest, cartData);
        }

        return paymentRequest;
    }

    public PaymentsDetailsRequest create3DPaymentsRequest(final String paymentData, final String md, final String paRes) {

        PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        paymentsDetailsRequest.set3DRequestData(md, paRes, paymentData);
        return paymentsDetailsRequest;
    }

    public PaymentsRequest createPaymentsRequest(final String merchantAccount,
                                                 final CartData cartData,
                                                 final RequestInfo requestInfo,
                                                 final CustomerModel customerModel,
                                                 final RecurringContractMode recurringContractMode) {
        PaymentsRequest paymentsRequest = new PaymentsRequest();
        String adyenPaymentMethod = cartData.getAdyenPaymentMethod();

        if (adyenPaymentMethod == null) {
            throw new IllegalArgumentException("Payment method is null");
        }
        //Update payment request for generic information for all payment method types

        updatePaymentRequest(merchantAccount, cartData, requestInfo, customerModel, paymentsRequest);

        //For credit cards
        if (PAYMENT_METHOD_CC.equals(adyenPaymentMethod)) {
            updatePaymentRequestForCC(paymentsRequest, cartData, recurringContractMode);
        }
        //For one click
        else if (adyenPaymentMethod.indexOf(PAYMENT_METHOD_ONECLICK) == 0) {
            String selectedReference = cartData.getAdyenSelectedReference();
            if (selectedReference != null && ! selectedReference.isEmpty()) {
                paymentsRequest.addOneClickData(selectedReference, cartData.getAdyenEncryptedSecurityCode());
            }
        }
        //For alternate payment methods like iDeal, Paypal etc.
        else {
            updatePaymentRequestForAlternateMethod(paymentsRequest, cartData, customerModel);
        }
        return paymentsRequest;
    }

    private void updatePaymentRequest(final String merchantAccount, final CartData cartData, final RequestInfo requestInfo, final CustomerModel customerModel, PaymentsRequest paymentsRequest) {

        //Get details from CartData to set in PaymentRequest.
        String amount = String.valueOf(cartData.getTotalPrice().getValue());
        String currency = cartData.getTotalPrice().getCurrencyIso();
        String reference = cartData.getCode();

        AddressData billingAddress = cartData.getPaymentInfo().getBillingAddress();
        AddressData deliveryAddress = cartData.getDeliveryAddress();

        //Get details from HttpServletRequest to set in PaymentRequest.
        String userAgent = requestInfo.getUserAgent();
        String acceptHeader = requestInfo.getAcceptHeader();
        String shopperIP = requestInfo.getShopperIp();

        paymentsRequest.setAmountData(amount, currency).reference(reference).merchantAccount(merchantAccount).addBrowserInfoData(userAgent, acceptHeader).
                shopperIP(shopperIP).setCountryCode(getCountryCode(cartData));

        // set shopper details from CustomerModel.
        if (customerModel != null) {
            paymentsRequest.setShopperReference(customerModel.getCustomerID());
            paymentsRequest.setShopperEmail(customerModel.getContactEmail());
        }

        // if address details are provided, set it to the PaymentRequest
        if (deliveryAddress != null) {
            paymentsRequest.setDeliveryAddress(setAddressData(deliveryAddress));
        }

        if (billingAddress != null) {
            paymentsRequest.setBillingAddress(setAddressData(billingAddress));
            // set PhoneNumber if it is provided
            String phone = billingAddress.getPhone();
            if (phone != null && ! phone.isEmpty()) {
                paymentsRequest.setTelephoneNumber(phone);
            }
        }
    }

    private void updatePaymentRequestForCC(PaymentsRequest paymentsRequest, CartData cartData, RecurringContractMode recurringContractMode) {
        Recurring recurringContract = getRecurringContractType(recurringContractMode);
        Recurring.ContractEnum contractEnum = null;
        if (recurringContract != null) {
            contractEnum = recurringContract.getContract();
        }

        paymentsRequest.setEnableRecurring(false);
        paymentsRequest.setEnableOneClick(false);

        String encryptedCardNumber = cartData.getAdyenEncryptedCardNumber();
        String encryptedExpiryMonth = cartData.getAdyenEncryptedExpiryMonth();
        String encryptedExpiryYear = cartData.getAdyenEncryptedExpiryYear();

        if (! StringUtils.isEmpty(encryptedCardNumber) && ! StringUtils.isEmpty(encryptedExpiryMonth) && ! StringUtils.isEmpty(encryptedExpiryYear)) {

            paymentsRequest.addEncryptedCardData(encryptedCardNumber, encryptedExpiryMonth, encryptedExpiryYear, cartData.getAdyenEncryptedSecurityCode(), cartData.getAdyenCardHolder());
        }
        if (Recurring.ContractEnum.ONECLICK_RECURRING == contractEnum) {
            paymentsRequest.setEnableRecurring(true);
            paymentsRequest.setEnableOneClick(true);
        } else if (Recurring.ContractEnum.ONECLICK == contractEnum) {
            paymentsRequest.setEnableOneClick(true);
        } else if (Recurring.ContractEnum.RECURRING == contractEnum) {
            paymentsRequest.setEnableRecurring(true);
        }

        // Set storeDetails parameter when shopper selected to have his card details stored
        if (cartData.getAdyenRememberTheseDetails()) {
            DefaultPaymentMethodDetails paymentMethodDetails = (DefaultPaymentMethodDetails) paymentsRequest.getPaymentMethod();
            paymentMethodDetails.setStoreDetails(true);
        }
    }

    private void updatePaymentRequestForAlternateMethod(PaymentsRequest paymentsRequest, CartData cartData, CustomerModel customerModel) {
        String adyenPaymentMethod = cartData.getAdyenPaymentMethod();
        DefaultPaymentMethodDetails paymentMethod = new DefaultPaymentMethodDetails();
        paymentsRequest.setPaymentMethod(paymentMethod);
        paymentMethod.setType(adyenPaymentMethod);
        paymentsRequest.setReturnUrl(cartData.getAdyenReturnUrl());
        if (adyenPaymentMethod.equals(PAYMENT_METHOD_IDEAL)) {
            paymentMethod.setIdealIssuer(cartData.getAdyenIssuerId());
        } else if (KLARNA.contains(adyenPaymentMethod)) {
            setOpenInvoiceData(paymentsRequest, cartData, customerModel);
        }
    }

    private String getCountryCode(CartData cartData) {
        //Identify country code based on shopper's delivery address
        String countryCode = "";
        AddressData billingAddressData = cartData.getPaymentInfo().getBillingAddress();
        if (billingAddressData != null) {
            CountryData billingCountry = billingAddressData.getCountry();
            if (billingCountry != null) {
                countryCode = billingCountry.getIsocode();
            }
        } else {
            AddressData deliveryAddressData = cartData.getDeliveryAddress();
            if (deliveryAddressData != null) {
                CountryData deliveryCountry = deliveryAddressData.getCountry();
                if (deliveryCountry != null) {
                    countryCode = deliveryCountry.getIsocode();
                }
            }
        }
        return countryCode;
    }


    public CaptureRequest createCaptureRequest(final String merchantAccount, final BigDecimal amount, final Currency currency, final String authReference, final String merchantReference) {
        return new CaptureRequest().fillAmount(String.valueOf(amount), currency.getCurrencyCode()).merchantAccount(merchantAccount).originalReference(authReference).reference(merchantReference);
    }

    public CancelOrRefundRequest createCancelOrRefundRequest(final String merchantAccount, final String authReference, final String merchantReference) {
        return new CancelOrRefundRequest().merchantAccount(merchantAccount).originalReference(authReference).reference(merchantReference);
    }

    public RefundRequest createRefundRequest(final String merchantAccount, final BigDecimal amount, final Currency currency, final String authReference, final String merchantReference) {
        return new RefundRequest().fillAmount(String.valueOf(amount), currency.getCurrencyCode()).merchantAccount(merchantAccount).originalReference(authReference).reference(merchantReference);
    }

    public RecurringDetailsRequest createListRecurringDetailsRequest(final String merchantAccount, final String customerId) {
        return new RecurringDetailsRequest().merchantAccount(merchantAccount).shopperReference(customerId).selectOneClickContract();
    }

    /**
     * Creates a request to disable a recurring contract
     */
    public DisableRequest createDisableRequest(final String merchantAccount, final String customerId, final String recurringReference) {
        return new DisableRequest().merchantAccount(merchantAccount).shopperReference(customerId).recurringDetailReference(recurringReference);
    }

    private <T extends AbstractPaymentRequest> T createBasePaymentRequest(T abstractPaymentRequest, HttpServletRequest request, final String merchantAccount) {
        String userAgent = request.getHeader("User-Agent");
        String acceptHeader = request.getHeader("Accept");
        String shopperIP = request.getRemoteAddr();
        abstractPaymentRequest.merchantAccount(merchantAccount).setBrowserInfoData(userAgent, acceptHeader).shopperIP(shopperIP);

        return abstractPaymentRequest;
    }

    /**
     * Set Address Data into API
     */
    private Address setAddressData(AddressData addressData) {

        Address address = new Address();

        // set defaults because all fields are required into the API
        address.setCity("NA");
        address.setCountry("NA");
        address.setHouseNumberOrName("NA");
        address.setPostalCode("NA");
        address.setStateOrProvince("NA");
        address.setStreet("NA");

        // set the actual values if they are available
        if (addressData.getTown() != null && ! addressData.getTown().isEmpty()) {
            address.setCity(addressData.getTown());
        }

        if (addressData.getCountry() != null && ! addressData.getCountry().getIsocode().isEmpty()) {
            address.setCountry(addressData.getCountry().getIsocode());
        }

        if (addressData.getLine1() != null && ! addressData.getLine1().isEmpty()) {
            address.setStreet(addressData.getLine1());
        }

        if (addressData.getLine2() != null && ! addressData.getLine2().isEmpty()) {
            address.setHouseNumberOrName(addressData.getLine2());
        }

        if (addressData.getPostalCode() != null && ! address.getPostalCode().isEmpty()) {
            address.setPostalCode(addressData.getPostalCode());
        }

        if (addressData.getRegion() != null && ! addressData.getRegion().getIsocode().isEmpty()) {
            address.setStateOrProvince(addressData.getRegion().getIsocode());
        }

        return address;
    }


    /**
     * Return Recurring object from RecurringContractMode
     */
    private Recurring getRecurringContractType(RecurringContractMode recurringContractMode) {
        Recurring recurringContract = new Recurring();

        //If recurring contract is disabled, return null
        if (recurringContractMode == null || RecurringContractMode.NONE.equals(recurringContractMode)) {
            return null;
        }

        String recurringMode = recurringContractMode.getCode();
        Recurring.ContractEnum contractEnum = Recurring.ContractEnum.valueOf(recurringMode);

        recurringContract.contract(contractEnum);

        return recurringContract;
    }

    /**
     * Return the recurringContract. If the user did not want to save the card don't send it as ONECLICK
     */
    private Recurring getRecurringContractType(RecurringContractMode recurringContractMode, final Boolean enableOneClick) {
        Recurring recurringContract = getRecurringContractType(recurringContractMode);

        //If recurring contract is disabled, return null
        if (recurringContract == null) {
            return null;
        }

        // if user want to save his card use the configured recurring contract type
        if (enableOneClick != null && enableOneClick) {
            return recurringContract;
        }

        Recurring.ContractEnum contractEnum = recurringContract.getContract();
        /*
         * If save card is not checked do the folllowing changes:
         * NONE => NONE
         * ONECLICK => NONE
         * ONECLICK,RECURRING => RECURRING
         * RECURRING => RECURRING
         */
        if (Recurring.ContractEnum.ONECLICK_RECURRING.equals(contractEnum) || Recurring.ContractEnum.RECURRING.equals(contractEnum)) {
            return recurringContract.contract(Recurring.ContractEnum.RECURRING);
        }

        return null;
    }

    /**
     * Get shopper name and gender
     */
    private Name getShopperNameFromAddress(AddressData addressData) {
        Name shopperName = new Name();

        shopperName.setFirstName(addressData.getFirstName());
        shopperName.setLastName(addressData.getLastName());
        shopperName.setGender(Name.GenderEnum.UNKNOWN);

        if (addressData.getTitleCode() != null && ! addressData.getTitleCode().isEmpty()) {
            if (addressData.getTitleCode().equals("mrs") || addressData.getTitleCode().equals("miss") || addressData.getTitleCode().equals("ms")) {
                shopperName.setGender(Name.GenderEnum.FEMALE);
            } else {
                shopperName.setGender(Name.GenderEnum.MALE);
            }
        }

        return shopperName;
    }


    /**
     * Set the required fields for using the OpenInvoice API
     *
     * To deprecate when RatePay is natively implemented
     */
    public void setOpenInvoiceData(PaymentRequest paymentRequest, CartData cartData, final CustomerModel customerModel) {
        // set date of birth
        if (cartData.getAdyenDob() != null) {
            paymentRequest.setDateOfBirth(cartData.getAdyenDob());
        }

        if (cartData.getAdyenSocialSecurityNumber() != null && ! cartData.getAdyenSocialSecurityNumber().isEmpty()) {
            paymentRequest.setSocialSecurityNumber(cartData.getAdyenSocialSecurityNumber());
        }

        if (cartData.getAdyenDfValue() != null && ! cartData.getAdyenDfValue().isEmpty()) {
            paymentRequest.setDeviceFingerprint(cartData.getAdyenDfValue());
        }

        // set the invoice lines
        List<InvoiceLine> invoiceLines = new ArrayList();
        String currency = cartData.getTotalPrice().getCurrencyIso();

        for (OrderEntryData entry : cartData.getEntries()) {

            // Use totalPrice because the basePrice does include tax as well if you have configured this to be calculated in the price
            BigDecimal pricePerItem = entry.getTotalPrice().getValue().divide(new BigDecimal(entry.getQuantity()));


            String description = "NA";
            if (entry.getProduct().getName() != null && ! entry.getProduct().getName().equals("")) {
                description = entry.getProduct().getName();
            }

            // Tax of total price (included quantity)
            Double tax = entry.getTaxValues().stream().map(taxValue -> taxValue.getAppliedValue()).reduce(0.0, (x, y) -> x = x + y);


            // Calculate Tax per quantitiy
            if (tax > 0) {
                tax = tax / entry.getQuantity().intValue();
            }

            // Calculate price without tax
            Amount itemAmountWithoutTax = Util.createAmount(pricePerItem.subtract(new BigDecimal(tax)), currency);
            Double percentage = entry.getTaxValues().stream().map(taxValue -> taxValue.getValue()).reduce(0.0, (x, y) -> x = x + y) * 100;

            InvoiceLine invoiceLine = new InvoiceLine();
            invoiceLine.setCurrencyCode(currency);
            invoiceLine.setDescription(description);

            /*
             * The price for one item in the invoice line, represented in minor units.
             * The due amount for the item, VAT excluded.
             */
            invoiceLine.setItemAmount(itemAmountWithoutTax.getValue());

            // The VAT due for one item in the invoice line, represented in minor units.
            invoiceLine.setItemVATAmount(Util.createAmount(BigDecimal.valueOf(tax), currency).getValue());

            // The VAT percentage for one item in the invoice line, represented in minor units.
            invoiceLine.setItemVatPercentage(percentage.longValue());

            // The country-specific VAT category a product falls under.  Allowed values: (High,Low,None)
            invoiceLine.setVatCategory(VatCategory.NONE);

            // An unique id for this item. Required for RatePay if the description of each item is not unique.
            if (! entry.getProduct().getCode().isEmpty()) {
                invoiceLine.setItemId(entry.getProduct().getCode());
            }

            invoiceLine.setNumberOfItems(entry.getQuantity().intValue());

            if (entry.getProduct() != null && ! entry.getProduct().getCode().isEmpty()) {
                invoiceLine.setItemId(entry.getProduct().getCode());
            }

            if (entry.getProduct() != null && ! entry.getProduct().getCode().isEmpty()) {
                invoiceLine.setItemId(entry.getProduct().getCode());
            }

            LOG.debug("InvoiceLine Product:" + invoiceLine.toString());
            invoiceLines.add(invoiceLine);

        }

        // Add delivery costs
        if (cartData.getDeliveryCost() != null) {

            InvoiceLine invoiceLine = new InvoiceLine();
            invoiceLine.setCurrencyCode(currency);
            invoiceLine.setDescription("Delivery Costs");
            Amount deliveryAmount = Util.createAmount(cartData.getDeliveryCost().getValue().toString(), currency);
            invoiceLine.setItemAmount(deliveryAmount.getValue());
            invoiceLine.setItemVATAmount(new Long("0"));
            invoiceLine.setItemVatPercentage(new Long("0"));
            invoiceLine.setVatCategory(VatCategory.NONE);
            invoiceLine.setNumberOfItems(1);
            LOG.debug("InvoiceLine DeliveryCosts:" + invoiceLine.toString());
            invoiceLines.add(invoiceLine);
        }

        paymentRequest.setInvoiceLines(invoiceLines);
    }


    /*
     * Set the required fields for using the OpenInvoice API
     */
    public void setOpenInvoiceData(PaymentsRequest paymentsRequest, CartData cartData, final CustomerModel customerModel) {
        paymentsRequest.setShopperName(getShopperNameFromAddress(cartData.getDeliveryAddress()));

        // set date of birth
        if (cartData.getAdyenDob() != null) {
            paymentsRequest.setDateOfBirth(cartData.getAdyenDob());
        }

        if (cartData.getAdyenSocialSecurityNumber() != null && ! cartData.getAdyenSocialSecurityNumber().isEmpty()) {
            paymentsRequest.setSocialSecurityNumber(cartData.getAdyenSocialSecurityNumber());
        }

        if (cartData.getAdyenDfValue() != null && ! cartData.getAdyenDfValue().isEmpty()) {
            paymentsRequest.setDeviceFingerprint(cartData.getAdyenDfValue());
        }

        // set the invoice lines
        List<LineItem> invoiceLines = new ArrayList<>();
        String currency = cartData.getTotalPrice().getCurrencyIso();

        for (OrderEntryData entry : cartData.getEntries()) {
            if(entry.getQuantity() == 0L) {
                // skip zero quantities
                continue;
            }
            // Use totalPrice because the basePrice does include tax as well if you have configured this to be calculated in the price
            BigDecimal pricePerItem = entry.getTotalPrice().getValue().divide(new BigDecimal(entry.getQuantity()));

            String description = "NA";
            if (entry.getProduct().getName() != null && ! entry.getProduct().getName().isEmpty()) {
                description = entry.getProduct().getName();
            }

            // Tax of total price (included quantity)
            Double tax = entry.getTaxValues().stream().map(TaxValue::getAppliedValue).reduce(0.0, (x, y) -> x + y);

            // Calculate Tax per quantitiy
            if (tax > 0) {
                tax = tax / entry.getQuantity().intValue();
            }

            // Calculate price without tax
            Amount itemAmountWithoutTax = Util.createAmount(pricePerItem.subtract(new BigDecimal(tax)), currency);
            Double percentage = entry.getTaxValues().stream().map(TaxValue::getValue).reduce(0.0, (x, y) -> x + y) * 100;

            LineItem invoiceLine = new LineItem();
            invoiceLine.setDescription(description);

            /*
             * The price for one item in the invoice line, represented in minor units.
             * The due amount for the item, VAT excluded.
             */
            invoiceLine.setAmountExcludingTax(itemAmountWithoutTax.getValue());

            // The VAT due for one item in the invoice line, represented in minor units.
            invoiceLine.setTaxAmount(Util.createAmount(BigDecimal.valueOf(tax), currency).getValue());

            // The VAT percentage for one item in the invoice line, represented in minor units.
            invoiceLine.setTaxPercentage(percentage.longValue());

            // The country-specific VAT category a product falls under.  Allowed values: (High,Low,None)
            invoiceLine.setTaxCategory(LineItem.TaxCategoryEnum.NONE);

            invoiceLine.setQuantity(entry.getQuantity());

            if (entry.getProduct() != null && ! entry.getProduct().getCode().isEmpty()) {
                invoiceLine.setId(entry.getProduct().getCode());
            }

            LOG.debug("InvoiceLine Product:" + invoiceLine.toString());
            invoiceLines.add(invoiceLine);
        }

        // Add delivery costs
        if (cartData.getDeliveryCost() != null) {
            LineItem invoiceLine = new LineItem();
            invoiceLine.setDescription("Delivery Costs");
            Amount deliveryAmount = Util.createAmount(cartData.getDeliveryCost().getValue().toString(), currency);
            invoiceLine.setAmountExcludingTax(deliveryAmount.getValue());
            invoiceLine.setTaxAmount(new Long("0"));
            invoiceLine.setTaxPercentage(new Long("0"));
            invoiceLine.setTaxCategory(LineItem.TaxCategoryEnum.NONE);
            invoiceLine.setQuantity(1L);
            LOG.debug("InvoiceLine DeliveryCosts:" + invoiceLine.toString());
            invoiceLines.add(invoiceLine);
        }

        paymentsRequest.setLineItems(invoiceLines);
    }

    /**
     * Set Boleto payment request data
     */
    private void setBoletoData(PaymentRequest paymentRequest, CartData cartData) {
        paymentRequest.selectedBrand(PAYMENT_METHOD_BOLETO_SANTANDER);
        paymentRequest.setSocialSecurityNumber(cartData.getAdyenSocialSecurityNumber());

        Name shopperName = new Name();
        shopperName.setFirstName(cartData.getAdyenFirstName());
        shopperName.setLastName(cartData.getAdyenLastName());
        paymentRequest.setShopperName(shopperName);
    }

    /**
     * Set Paypal ECS request data
     */
    private void setPaypalEcsData(PaymentRequest paymentRequest, CartData cartData) {
        paymentRequest.selectedBrand(PAYPAL_ECS);
        paymentRequest.setPaymentToken(cartData.getAdyenPaymentToken());
    }
}
