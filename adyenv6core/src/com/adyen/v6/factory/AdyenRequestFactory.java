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

import com.adyen.builders.terminal.TerminalAPIRequestBuilder;
import com.adyen.enums.VatCategory;
import com.adyen.model.Amount;
import com.adyen.model.PaymentRequest;
import com.adyen.model.*;
import com.adyen.model.additionalData.InvoiceLine;
import com.adyen.model.applicationinfo.ApplicationInfo;
import com.adyen.model.applicationinfo.CommonField;
import com.adyen.model.applicationinfo.ExternalPlatform;
import com.adyen.model.checkout.LineItem;
import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.details.CardDetails;
import com.adyen.model.modification.CancelOrRefundRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.RefundRequest;
import com.adyen.model.nexo.*;
import com.adyen.model.recurring.DisableRequest;
import com.adyen.model.recurring.Recurring;
import com.adyen.model.recurring.RecurringDetailsRequest;
import com.adyen.model.terminal.SaleToAcquirerData;
import com.adyen.model.terminal.TerminalAPIRequest;
import com.adyen.util.Util;
import com.adyen.v6.constants.Adyenv6coreConstants;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.model.RequestInfo;
import com.adyen.v6.paymentmethoddetails.executors.AdyenPaymentMethodDetailsBuilderExecutor;
import com.google.gson.Gson;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.TaxValue;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.adyen.v6.constants.Adyenv6coreConstants.*;

public class AdyenRequestFactory {
    private static final Logger LOG = Logger.getLogger(AdyenRequestFactory.class);

    private static final String PLATFORM_NAME = "Hybris";
    private static final String PLATFORM_VERSION_PROPERTY = "build.version.api";
    private static final String IS_3DS2_ALLOWED_PROPERTY = "is3DS2allowed";
    private static final String ALLOW_3DS2_PROPERTY = "allow3DS2";
    private static final String OVERWRITE_BRAND_PROPERTY = "overwriteBrand";
    private static final String DUAL_BRANDED_NOT_SELECTED_FLOW_PAYMENT_TYPE = "scheme";

    protected final ConfigurationService configurationService;
    protected final AdyenPaymentMethodDetailsBuilderExecutor adyenPaymentMethodDetailsBuilderExecutor;

    public AdyenRequestFactory(final ConfigurationService configurationService, final AdyenPaymentMethodDetailsBuilderExecutor adyenPaymentMethodDetailsBuilderExecutor) {
        this.configurationService = configurationService;
        this.adyenPaymentMethodDetailsBuilderExecutor = adyenPaymentMethodDetailsBuilderExecutor;
    }

    @Deprecated
    public PaymentRequest createAuthorizationRequest(final String merchantAccount,
                                                     final CartData cartData,
                                                     final HttpServletRequest request,
                                                     final CustomerModel customerModel,
                                                     final RecurringContractMode recurringContractMode) {

        String amount = String.valueOf(cartData.getTotalPriceWithTax().getValue());
        String currency = cartData.getTotalPriceWithTax().getCurrencyIso();
        String reference = cartData.getCode();

        PaymentRequest paymentRequest = createBasePaymentRequest(new PaymentRequest(), request, merchantAccount).reference(reference).setAmountData(amount, currency);

        // set shopper details
        if (customerModel != null) {
            paymentRequest.setShopperReference(customerModel.getCustomerID());
            paymentRequest.setShopperEmail(customerModel.getContactEmail());
        }
        // if address details are provided added it into the request
        if (cartData.getDeliveryAddress() != null) {
            Address deliveryAddress = setAddressData(cartData.getDeliveryAddress());
            paymentRequest.setDeliveryAddress(deliveryAddress);
        }

        if (cartData.getPaymentInfo().getBillingAddress() != null) {
            // set PhoneNumber if it is provided
            if (cartData.getPaymentInfo().getBillingAddress().getPhone() != null && !cartData.getPaymentInfo().getBillingAddress().getPhone().isEmpty()) {
                paymentRequest.setTelephoneNumber(cartData.getPaymentInfo().getBillingAddress().getPhone());
            }

            Address billingAddress = setAddressData(cartData.getPaymentInfo().getBillingAddress());
            paymentRequest.setBillingAddress(billingAddress);
        }

        // OpenInvoice add required additional data
        if (OPENINVOICE_METHODS_API.contains(cartData.getAdyenPaymentMethod())
                || PAYMENT_METHOD_PAYPAL.contains(cartData.getAdyenPaymentMethod())) {
            paymentRequest.selectedBrand(cartData.getAdyenPaymentMethod());
            setOpenInvoiceData(paymentRequest, cartData, customerModel);

            paymentRequest.setShopperName(getShopperNameFromAddress(cartData.getDeliveryAddress()));
        }
        return paymentRequest;
    }

    public PaymentsDetailsRequest create3DSPaymentsRequest(final Map<String, String> details) {
        PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        paymentsDetailsRequest.setDetails(details);
        return paymentsDetailsRequest;
    }

    public PaymentsRequest createPaymentsRequest(final String merchantAccount,
                                                 final CartData cartData,
                                                 final RequestInfo requestInfo,
                                                 final CustomerModel customerModel,
                                                 final RecurringContractMode recurringContractMode,
                                                 final Boolean guestUserTokenizationEnabled) {
        final String adyenPaymentMethod = cartData.getAdyenPaymentMethod();
        final Boolean is3DS2allowed = is3DS2Allowed();
        final PaymentsRequest paymentsRequest = new PaymentsRequest();

        if (adyenPaymentMethod == null) {
            throw new IllegalArgumentException("Payment method is null");
        }
        //Update payment request for generic information for all payment method types
        setCommonInfoOnPaymentRequest(merchantAccount, cartData, requestInfo, customerModel, paymentsRequest);
        updateApplicationInfoEcom(paymentsRequest.getApplicationInfo());

        paymentsRequest.setReturnUrl(cartData.getAdyenReturnUrl());
        paymentsRequest.setRedirectFromIssuerMethod(RequestMethod.POST.toString());
        paymentsRequest.setRedirectToIssuerMethod(RequestMethod.POST.toString());

        //For credit cards
        if (PAYMENT_METHOD_CC.equals(adyenPaymentMethod) || PAYMENT_METHOD_BCMC.equals(adyenPaymentMethod)) {
            if (CARD_TYPE_DEBIT.equals(cartData.getAdyenCardType())) {
                updatePaymentRequestForDC(paymentsRequest, cartData, recurringContractMode);
            } else {
                updatePaymentRequestForCC(paymentsRequest, cartData, recurringContractMode);
            }
            if (is3DS2allowed) {
                enhanceForThreeDS2(paymentsRequest, cartData);
            }
            if (customerModel.getType() == CustomerType.GUEST && guestUserTokenizationEnabled) {
                paymentsRequest.setEnableOneClick(false);
            }
        }
        //For one click
        else if (adyenPaymentMethod.indexOf(PAYMENT_METHOD_ONECLICK) == 0) {
            Optional.ofNullable(cartData.getAdyenSelectedReference())
                    .filter(StringUtils::isNotEmpty)
                    .map(selectedReference -> getCardDetails(cartData, selectedReference))
                    .ifPresent(paymentsRequest::setPaymentMethod);

            if (is3DS2allowed) {
                enhanceForThreeDS2(paymentsRequest, cartData);
            }
        }
        //For Pix APM
        else if (PAYMENT_METHOD_PIX.equals(cartData.getAdyenPaymentMethod())) {
            setPixData(paymentsRequest, cartData);
        }
        //Set Boleto parameters
        else if (cartData.getAdyenPaymentMethod().indexOf(PAYMENT_METHOD_BOLETO) == 0) {
            setBoletoData(paymentsRequest, cartData);
        }
        //For alternate payment methods like iDeal, Paypal etc.
        else {
            updatePaymentRequestForAlternateMethod(paymentsRequest, cartData);
        }

        return paymentsRequest;
    }

    protected CardDetails getCardDetails(CartData cartData, String selectedReference) {
        final CardDetails paymentMethodDetails = new CardDetails();
        paymentMethodDetails.encryptedSecurityCode(cartData.getAdyenEncryptedSecurityCode());
        paymentMethodDetails.recurringDetailReference(selectedReference);
        Optional.ofNullable(cartData.getAdyenCardBrand()).ifPresent(paymentMethodDetails::setType);
        return paymentMethodDetails;
    }

    public PaymentsRequest createPaymentsRequest(final String merchantAccount,
                                                 final CartData cartData,
                                                 final PaymentMethodDetails paymentMethodDetails,
                                                 final RequestInfo requestInfo,
                                                 final CustomerModel customerModel) {
        final PaymentsRequest paymentsRequest = new PaymentsRequest();
        setCommonInfoOnPaymentRequest(merchantAccount, cartData, requestInfo, customerModel, paymentsRequest);
        updateApplicationInfoEcom(paymentsRequest.getApplicationInfo());
        paymentsRequest.setPaymentMethod(paymentMethodDetails);
        paymentsRequest.setReturnUrl(cartData.getAdyenReturnUrl());

        return paymentsRequest;
    }

    protected PaymentsRequest enhanceForThreeDS2(final PaymentsRequest paymentsRequest, final CartData cartData) {
        final BrowserInfo browserInfo = Optional.ofNullable(new Gson().fromJson(cartData.getAdyenBrowserInfo(), BrowserInfo.class))
                .orElse(new BrowserInfo())
                .acceptHeader(paymentsRequest.getBrowserInfo().getAcceptHeader())
                .userAgent(paymentsRequest.getBrowserInfo().getUserAgent());

        paymentsRequest.setAdditionalData(Optional.ofNullable(paymentsRequest.getAdditionalData()).orElse(new HashMap<>()));
        paymentsRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);
        paymentsRequest.setBrowserInfo(browserInfo);

        return paymentsRequest;
    }

    private void updateApplicationInfoEcom(final ApplicationInfo applicationInfo) {
        final CommonField version = new CommonField().name(PLUGIN_NAME).version(PLUGIN_VERSION);

        applicationInfo.setExternalPlatform((ExternalPlatform) new ExternalPlatform()
                .name(PLATFORM_NAME)
                .version(getPlatformVersion()));
        applicationInfo.setMerchantApplication(version);
        applicationInfo.setAdyenPaymentSource(version);

    }

    protected void setCommonInfoOnPaymentRequest(final String merchantAccount, final CartData cartData,
                                                 final RequestInfo requestInfo, final CustomerModel customerModel,
                                                 final PaymentsRequest paymentsRequest) {

        //Get details from CartData to set in PaymentRequest.
        final String amount = String.valueOf(cartData.getTotalPriceWithTax().getValue());
        final String currency = cartData.getTotalPriceWithTax().getCurrencyIso();
        final String reference = cartData.getCode();
        final AddressData billingAddress = cartData.getPaymentInfo() != null ? cartData.getPaymentInfo().getBillingAddress() : null;
        final AddressData deliveryAddress = cartData.getDeliveryAddress();

        //Get details from HttpServletRequest to set in PaymentRequest.
        final String userAgent = requestInfo.getUserAgent();
        final String acceptHeader = requestInfo.getAcceptHeader();
        final String shopperIP = requestInfo.getShopperIp();
        final String origin = requestInfo.getOrigin();
        final String shopperLocale = requestInfo.getShopperLocale();

        paymentsRequest
                .amount(Util.createAmount(amount, currency))
                .reference(reference)
                .merchantAccount(merchantAccount)
                .browserInfo(new BrowserInfo().userAgent(userAgent).acceptHeader(acceptHeader))
                .shopperIP(shopperIP)
                .origin(origin)
                .shopperLocale(shopperLocale)
                .shopperReference(customerModel.getCustomerID())
                .shopperEmail(customerModel.getContactEmail())
                .deliveryAddress(setAddressData(deliveryAddress))
                .billingAddress(setAddressData(billingAddress))
                .telephoneNumber(billingAddress.getPhone())
                .setCountryCode(getCountryCode(cartData));
    }

    protected void updatePaymentRequestForCC(final PaymentsRequest paymentsRequest, final CartData cartData, final RecurringContractMode recurringContractMode) {
        final Recurring recurringContract = getRecurringContractType(recurringContractMode);
        final Recurring.ContractEnum contract = recurringContract.getContract();
        final String encryptedCardNumber = cartData.getAdyenEncryptedCardNumber();
        final String encryptedExpiryMonth = cartData.getAdyenEncryptedExpiryMonth();
        final String encryptedExpiryYear = cartData.getAdyenEncryptedExpiryYear();

        if (Recurring.ContractEnum.ONECLICK_RECURRING.equals(contract)) {
            paymentsRequest.setEnableRecurring(true);
            if(Boolean.TRUE.equals(cartData.getAdyenRememberTheseDetails())) {
                paymentsRequest.setEnableOneClick(true);
            }
        } else if (Recurring.ContractEnum.ONECLICK.equals(contract) && Boolean.TRUE.equals(cartData.getAdyenRememberTheseDetails()) ) {
            paymentsRequest.setEnableOneClick(true);
        } else if (Recurring.ContractEnum.RECURRING.equals(contract)) {
            paymentsRequest.setEnableRecurring(true);
        }

        if (StringUtils.isNotEmpty(encryptedCardNumber) && StringUtils.isNotEmpty(encryptedExpiryMonth) && StringUtils.isNotEmpty(encryptedExpiryYear)) {
            paymentsRequest.setPaymentMethod(new CardDetails()
                    .encryptedCardNumber(encryptedCardNumber)
                    .encryptedExpiryMonth(encryptedExpiryMonth)
                    .encryptedExpiryYear(encryptedExpiryYear)
                    .encryptedSecurityCode(cartData.getAdyenEncryptedSecurityCode())
                    .holderName(cartData.getAdyenCardHolder()));
        }

        // For Dual branded card set card brand as payment method type
        if (StringUtils.isNotEmpty(cartData.getAdyenCardBrand())) {
            paymentsRequest.getPaymentMethod().setType(DUAL_BRANDED_NOT_SELECTED_FLOW_PAYMENT_TYPE);
        }
        if (cartData.getAdyenInstallments() != null) {
            Installments installmentObj = new Installments();
            installmentObj.setValue(cartData.getAdyenInstallments());
            paymentsRequest.setInstallments(installmentObj);
        }
    }

    protected void updatePaymentRequestForDC(final PaymentsRequest paymentsRequest, final CartData cartData, final RecurringContractMode recurringContractMode) {

        final Recurring recurringContract = getRecurringContractType(recurringContractMode);
        final Recurring.ContractEnum contract = recurringContract.getContract();
        final String encryptedCardNumber = cartData.getAdyenEncryptedCardNumber();
        final String encryptedExpiryMonth = cartData.getAdyenEncryptedExpiryMonth();
        final String encryptedExpiryYear = cartData.getAdyenEncryptedExpiryYear();
        final String cardBrand = cartData.getAdyenCardBrand();

        if ((Recurring.ContractEnum.ONECLICK_RECURRING.equals(contract) || Recurring.ContractEnum.ONECLICK.equals(contract))
                && cartData.getAdyenRememberTheseDetails()) {
            paymentsRequest.setEnableOneClick(true);
        }

        if (StringUtils.isNotEmpty(encryptedCardNumber) && StringUtils.isNotEmpty(encryptedExpiryMonth) && StringUtils.isNotEmpty(encryptedExpiryYear)) {
            paymentsRequest.setPaymentMethod(new CardDetails()
                    .encryptedCardNumber(encryptedCardNumber)
                    .encryptedExpiryMonth(encryptedExpiryMonth)
                    .encryptedExpiryYear(encryptedExpiryYear)
                    .encryptedSecurityCode(cartData.getAdyenEncryptedSecurityCode())
                    .holderName(cartData.getAdyenCardHolder()));
        }

        paymentsRequest.putAdditionalDataItem(OVERWRITE_BRAND_PROPERTY, "true");
        paymentsRequest.getPaymentMethod().setType(cardBrand);
    }

    protected void updatePaymentRequestForAlternateMethod(final PaymentsRequest paymentsRequest, final CartData cartData) {
        final String adyenPaymentMethod = cartData.getAdyenPaymentMethod();

        paymentsRequest.setShopperName(getShopperNameFromAddress(cartData.getDeliveryAddress()));
        paymentsRequest.setPaymentMethod(adyenPaymentMethodDetailsBuilderExecutor.createPaymentMethodDetails(cartData));
        paymentsRequest.setReturnUrl(cartData.getAdyenReturnUrl());

        if (adyenPaymentMethod.startsWith(PAYMENT_METHOD_KLARNA)
                || adyenPaymentMethod.startsWith(PAYMENT_METHOD_FACILPAY_PREFIX)
                || OPENINVOICE_METHODS_API.contains(adyenPaymentMethod)
                || adyenPaymentMethod.contains(RATEPAY)) {
            setOpenInvoiceData(paymentsRequest, cartData);
        }
    }

    protected String getCountryCode(final CartData cartData) {
        //Identify country code based on shopper's delivery address
        return Optional.ofNullable(cartData.getPaymentInfo())
                .map(CCPaymentInfoData::getBillingAddress)
                .map(billingAddress -> Optional.ofNullable(billingAddress).or(() -> Optional.ofNullable(cartData.getDeliveryAddress())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(AddressData::getCountry)
                .map(CountryData::getIsocode)
                .orElse("");
    }


    public CaptureRequest createCaptureRequest(final String merchantAccount, final BigDecimal amount, final Currency currency, final String authReference, final String merchantReference) {
        CaptureRequest request = new CaptureRequest().fillAmount(String.valueOf(amount), currency.getCurrencyCode())
                .merchantAccount(merchantAccount)
                .originalReference(authReference)
                .reference(merchantReference);
        updateApplicationInfoEcom(request.getApplicationInfo());
        return request;
    }

    public CancelOrRefundRequest createCancelOrRefundRequest(final String merchantAccount, final String authReference, final String merchantReference) {
        CancelOrRefundRequest request = new CancelOrRefundRequest().merchantAccount(merchantAccount).originalReference(authReference).reference(merchantReference);
        updateApplicationInfoEcom(request.getApplicationInfo());
        return request;
    }

    public RefundRequest createRefundRequest(final String merchantAccount, final BigDecimal amount, final Currency currency, final String authReference, final String merchantReference) {
        RefundRequest request = new RefundRequest().fillAmount(String.valueOf(amount), currency.getCurrencyCode())
                .merchantAccount(merchantAccount)
                .originalReference(authReference)
                .reference(merchantReference);
        updateApplicationInfoEcom(request.getApplicationInfo());
        return request;
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


    public TerminalAPIRequest createTerminalAPIRequestForStatus(final CartData cartData, String originalServiceId) {
        TransactionStatusRequest transactionStatusRequest = new TransactionStatusRequest();
        transactionStatusRequest.setReceiptReprintFlag(true);

        MessageReference messageReference = new MessageReference();
        messageReference.setMessageCategory(MessageCategoryType.PAYMENT);
        messageReference.setSaleID(cartData.getStore());
        messageReference.setServiceID(originalServiceId);

        transactionStatusRequest.setMessageReference(messageReference);
        transactionStatusRequest.getDocumentQualifier().add(DocumentQualifierType.CASHIER_RECEIPT);
        transactionStatusRequest.getDocumentQualifier().add(DocumentQualifierType.CUSTOMER_RECEIPT);

        String serviceId = Long.toString(System.currentTimeMillis() % 10000000000L);

        TerminalAPIRequestBuilder builder = new TerminalAPIRequestBuilder(cartData.getStore(), serviceId, cartData.getAdyenTerminalId());
        builder.withTransactionStatusRequest(transactionStatusRequest);

        return builder.build();
    }

    public TerminalAPIRequest createTerminalAPIRequest(final CartData cartData, CustomerModel customer, RecurringContractMode recurringContractMode, String serviceId) throws Exception {
        com.adyen.model.nexo.PaymentRequest paymentRequest = new com.adyen.model.nexo.PaymentRequest();

        SaleData saleData = new SaleData();
        TransactionIdentification transactionIdentification = new TransactionIdentification();
        transactionIdentification.setTransactionID(cartData.getCode());
        XMLGregorianCalendar timestamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
        transactionIdentification.setTimeStamp(timestamp);
        saleData.setSaleTransactionID(transactionIdentification);

        //Set recurring contract, if exists
        if (customer != null) {
            String shopperReference = customer.getCustomerID();
            String shopperEmail = customer.getContactEmail();
            Recurring recurringContract = getRecurringContractType(recurringContractMode);
            SaleToAcquirerData saleToAcquirerData = new SaleToAcquirerData();

            if (recurringContract != null && StringUtils.isNotEmpty(shopperReference) && StringUtils.isNotEmpty(shopperEmail)) {
                saleToAcquirerData.setShopperEmail(shopperEmail);
                saleToAcquirerData.setShopperReference(shopperReference);
                saleToAcquirerData.setRecurringContract(recurringContract.getContract().toString());
            }
            updateApplicationInfoEcom(saleToAcquirerData.getApplicationInfo());
            saleData.setSaleToAcquirerData(saleToAcquirerData);
        }

        paymentRequest.setSaleData(saleData);

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        AmountsReq amountsReq = new AmountsReq();
        amountsReq.setCurrency(cartData.getTotalPriceWithTax().getCurrencyIso());
        amountsReq.setRequestedAmount(cartData.getTotalPriceWithTax().getValue());
        paymentTransaction.setAmountsReq(amountsReq);

        paymentRequest.setPaymentTransaction(paymentTransaction);

        TerminalAPIRequestBuilder builder = new TerminalAPIRequestBuilder(cartData.getStore(), serviceId, cartData.getAdyenTerminalId());
        builder.withPaymentRequest(paymentRequest);

        return builder.build();
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
        if (addressData.getTown() != null && !addressData.getTown().isEmpty()) {
            address.setCity(addressData.getTown());
        }

        if (addressData.getCountry() != null && !addressData.getCountry().getIsocode().isEmpty()) {
            address.setCountry(addressData.getCountry().getIsocode());
        }

        if (addressData.getLine1() != null && !addressData.getLine1().isEmpty()) {
            address.setStreet(addressData.getLine1());
        }

        if (addressData.getLine2() != null && !addressData.getLine2().isEmpty()) {
            address.setHouseNumberOrName(addressData.getLine2());
        }

        if (addressData.getPostalCode() != null && !address.getPostalCode().isEmpty()) {
            address.setPostalCode(addressData.getPostalCode());
        }

        //State value will be updated later for boleto in boleto specific method.
        if (addressData.getRegion() != null && StringUtils.isNotEmpty(addressData.getRegion().getIsocodeShort())) {
            address.setStateOrProvince(addressData.getRegion().getIsocodeShort());
        } else if (addressData.getRegion() != null && StringUtils.isNotEmpty(addressData.getRegion().getIsocode())) {
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

        if (addressData.getTitleCode() != null && !addressData.getTitleCode().isEmpty()) {
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
     * <p>
     * To deprecate when RatePay is natively implemented
     */
    public void setOpenInvoiceData(PaymentRequest paymentRequest, CartData cartData, final CustomerModel customerModel) {
        // set date of birth
        if (cartData.getAdyenDob() != null) {
            paymentRequest.setDateOfBirth(cartData.getAdyenDob());
        }

        if (cartData.getAdyenSocialSecurityNumber() != null && !cartData.getAdyenSocialSecurityNumber().isEmpty()) {
            paymentRequest.setSocialSecurityNumber(cartData.getAdyenSocialSecurityNumber());
        }

        if (cartData.getAdyenDfValue() != null && !cartData.getAdyenDfValue().isEmpty()) {
            paymentRequest.setDeviceFingerprint(cartData.getAdyenDfValue());
        }

        // set the invoice lines
        List<InvoiceLine> invoiceLines = new ArrayList();
        String currency = cartData.getTotalPriceWithTax().getCurrencyIso();

        for (OrderEntryData entry : cartData.getEntries()) {

            // Use totalPrice because the basePrice does include tax as well if you have configured this to be calculated in the price
            BigDecimal pricePerItem = entry.getBasePrice().getValue();


            String description = "NA";
            if (entry.getProduct().getName() != null && !entry.getProduct().getName().equals("")) {
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
            if (!entry.getProduct().getCode().isEmpty()) {
                invoiceLine.setItemId(entry.getProduct().getCode());
            }

            invoiceLine.setNumberOfItems(entry.getQuantity().intValue());

            if (entry.getProduct() != null && !entry.getProduct().getCode().isEmpty()) {
                invoiceLine.setItemId(entry.getProduct().getCode());
            }

            if (entry.getProduct() != null && !entry.getProduct().getCode().isEmpty()) {
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
    public void setOpenInvoiceData(PaymentsRequest paymentsRequest, CartData cartData) {
        paymentsRequest.setShopperName(getShopperNameFromAddress(cartData.getDeliveryAddress()));

        // set date of birth
        if (cartData.getAdyenDob() != null) {
            paymentsRequest.setDateOfBirth(cartData.getAdyenDob());
        }

        if (cartData.getAdyenSocialSecurityNumber() != null && !cartData.getAdyenSocialSecurityNumber().isEmpty()) {
            paymentsRequest.setSocialSecurityNumber(cartData.getAdyenSocialSecurityNumber());
        }

        if (cartData.getAdyenDfValue() != null && !cartData.getAdyenDfValue().isEmpty()) {
            paymentsRequest.setDeviceFingerprint(cartData.getAdyenDfValue());
        }

        if (AFTERPAY.equals(cartData.getAdyenPaymentMethod())) {
            paymentsRequest.setShopperEmail(cartData.getAdyenShopperEmail());
            paymentsRequest.setTelephoneNumber(cartData.getAdyenShopperTelephone());
            paymentsRequest.setShopperName(getAfterPayShopperName(cartData));
        } else if (PAYBRIGHT.equals(cartData.getAdyenPaymentMethod())) {
            paymentsRequest.setTelephoneNumber(cartData.getAdyenShopperTelephone());
        }

        // set the invoice lines
        List<LineItem> invoiceLines = new ArrayList<>();
        String currency = cartData.getTotalPriceWithTax().getCurrencyIso();

        for (OrderEntryData entry : cartData.getEntries()) {
            if (entry.getQuantity() == 0L) {
                // skip zero quantities
                continue;
            }

            String description = "NA";
            if (entry.getProduct().getName() != null && !entry.getProduct().getName().isEmpty()) {
                description = entry.getProduct().getName();
            }

            // Tax of total price (included quantity)
            Double tax = entry.getTaxValues().stream().map(TaxValue::getAppliedValue).reduce(0.0, Double::sum);

            // Calculate Tax per quantitiy
            if (tax > 0) {
                tax = tax / entry.getQuantity().intValue();
            }

            final Double percentage = entry.getTaxValues().stream().map(TaxValue::getValue).reduce(0.0, Double::sum) * 100;

            final LineItem invoiceLine = new LineItem();

            invoiceLine.setDescription(description);

            /*
             * The price for one item in the invoice line, represented in minor units.
             * The due amount for the item, VAT excluded.
             */
            final Amount itemAmount = Util.createAmount(entry.getBasePrice().getValue(), currency);

            if (cartData.isNet()) {
                invoiceLine.setAmountExcludingTax(itemAmount.getValue());
                invoiceLine.setTaxAmount(Util.createAmount(BigDecimal.valueOf(tax), currency).getValue());
            } else {
                invoiceLine.setAmountIncludingTax(itemAmount.getValue());
            }

            // The VAT percentage for one item in the invoice line, represented in minor units.
            invoiceLine.setTaxPercentage(percentage.longValue());

            invoiceLine.setQuantity(entry.getQuantity());

            if (entry.getProduct() != null && !entry.getProduct().getCode().isEmpty()) {
                invoiceLine.setId(entry.getProduct().getCode());
            }

            LOG.debug("InvoiceLine Product:" + invoiceLine);
            invoiceLines.add(invoiceLine);
        }

        // Add delivery costs
        if (cartData.getDeliveryCost() != null) {
            final LineItem invoiceLine = new LineItem();
            invoiceLine.setDescription("Delivery Costs");

            final Amount deliveryAmount = Util.createAmount(cartData.getDeliveryCost().getValue().toString(), currency);

            if (cartData.isNet()) {
                final Double taxAmount = cartData.getEntries().stream()
                        .map(OrderEntryData::getTaxValues)
                        .flatMap(Collection::stream)
                        .map(TaxValue::getAppliedValue)
                        .collect(Collectors.toList())
                        .stream()
                        .reduce(0.0, Double::sum);
                invoiceLine.setAmountExcludingTax(deliveryAmount.getValue());
                invoiceLine.setTaxAmount(Util.createAmount(cartData.getTotalTax().getValue().subtract(new BigDecimal(taxAmount)), currency).getValue());
            } else {
                invoiceLine.setAmountIncludingTax(deliveryAmount.getValue());
            }

            final Double percentage = cartData.getEntries().stream()
                    .findFirst()
                    .map(OrderEntryData::getTaxValues)
                    .stream()
                    .flatMap(Collection::stream)
                    .map(TaxValue::getValue)
                    .reduce(0.0, Double::sum) * 100;

            invoiceLine.setTaxPercentage(percentage.longValue());
            invoiceLine.setQuantity(1L);
            LOG.debug("InvoiceLine DeliveryCosts:" + invoiceLine);
            invoiceLines.add(invoiceLine);
        }

        paymentsRequest.setLineItems(invoiceLines);
    }

    private Name getAfterPayShopperName(final CartData cartData) {
        return new Name()
                .firstName(cartData.getAdyenFirstName())
                .lastName(cartData.getAdyenLastName())
                .gender(Name.GenderEnum.valueOf(cartData.getAdyenShopperGender()));
    }

    /**
     * Set Boleto payment request data
     */
    private void setBoletoData(final PaymentsRequest paymentsRequest, final CartData cartData) {
        paymentsRequest.setPaymentMethod(adyenPaymentMethodDetailsBuilderExecutor.createPaymentMethodDetails(cartData));
        paymentsRequest.setSocialSecurityNumber(cartData.getAdyenSocialSecurityNumber());

        final Name shopperName = new Name()
                .firstName(cartData.getAdyenFirstName())
                .lastName(cartData.getAdyenLastName());

        paymentsRequest.setShopperName(shopperName);

        if (paymentsRequest.getBillingAddress() != null) {
            String stateOrProvinceBilling = paymentsRequest.getBillingAddress().getStateOrProvince();
            if (!StringUtils.isEmpty(stateOrProvinceBilling) && stateOrProvinceBilling.length() > 2) {
                String shortStateOrProvince = stateOrProvinceBilling.substring(stateOrProvinceBilling.length() - 2);
                paymentsRequest.getBillingAddress().setStateOrProvince(shortStateOrProvince);
            }
        }
        if (paymentsRequest.getDeliveryAddress() != null) {
            String stateOrProvinceDelivery = paymentsRequest.getDeliveryAddress().getStateOrProvince();
            if (!StringUtils.isEmpty(stateOrProvinceDelivery) && stateOrProvinceDelivery.length() > 2) {
                String shortStateOrProvince = stateOrProvinceDelivery.substring(stateOrProvinceDelivery.length() - 2);
                paymentsRequest.getDeliveryAddress().setStateOrProvince(shortStateOrProvince);
            }
        }
    }


    private void setPixData(final PaymentsRequest paymentsRequest, final CartData cartData) {
        final List<LineItem> invoiceLines = cartData.getEntries().stream()
                .filter(cartEntry -> cartEntry.getQuantity() > 0)
                .map(cartEntry ->
                        new LineItem()
                                .amountIncludingTax(cartEntry.getBasePrice().getValue().longValue())
                                .id(Optional.ofNullable(cartEntry.getProduct().getName())
                                        .filter(StringUtils::isNotEmpty)
                                        .orElse("NA")
                                )
                )
                .collect(Collectors.toList());

        paymentsRequest.setSocialSecurityNumber(cartData.getAdyenSocialSecurityNumber());
        paymentsRequest.setShopperName(new Name()
                .firstName(cartData.getAdyenFirstName())
                .lastName(cartData.getAdyenLastName())
        );
        paymentsRequest.setLineItems(invoiceLines);
    }

    private String getPlatformVersion() {
        return getConfigurationService().getConfiguration().getString(PLATFORM_VERSION_PROPERTY);
    }

    private Boolean is3DS2Allowed() {
        final Configuration configuration = getConfigurationService().getConfiguration();
        if (configuration.containsKey(IS_3DS2_ALLOWED_PROPERTY)) {
            return configuration.getBoolean(IS_3DS2_ALLOWED_PROPERTY);
        }
        return false;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

}
