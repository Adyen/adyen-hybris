package com.adyen.v6.factory;

import com.adyen.Util.Util;
import com.adyen.model.*;
import com.adyen.model.hpp.DirectoryLookupRequest;
import com.adyen.model.modification.CancelRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.RefundRequest;
import com.adyen.model.recurring.Recurring;
import com.adyen.model.recurring.RecurringDetailsRequest;
import com.adyen.v6.enums.RecurringContractMode;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.CustomerModel;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Currency;

public class AdyenRequestFactory {
    public PaymentRequest3d create3DAuthorizationRequest(final String merchantAccount,
                                                         final HttpServletRequest request,
                                                         final String md,
                                                         final String paRes) {
        return createBasePaymentRequest(
                new PaymentRequest3d(),
                request,
                merchantAccount
        ).set3DRequestData(md, paRes);
    }

    public PaymentRequest createAuthorizationRequest(final String merchantAccount,
                                                     final CartData cartData,
                                                     final HttpServletRequest request,
                                                     final CustomerModel customerModel,
                                                     final RecurringContractMode recurringContractMode) {
        String amount = String.valueOf(cartData.getTotalPrice().getValue());
        String currency = cartData.getTotalPrice().getCurrencyIso();
        String reference = cartData.getCode();
        String cseToken = cartData.getAdyenCseToken();
        String selectedAlias = cartData.getAdyenSelectedAlias();

        PaymentRequest paymentRequest = createBasePaymentRequest(new PaymentRequest(), request, merchantAccount)
                .reference(reference)
                .setAmountData(
                        amount,
                        currency
                )
                .setCSEToken(cseToken);

        // if user is logged in
        if (customerModel != null) {
            paymentRequest.setShopperReference(customerModel.getCustomerID());
            paymentRequest.setShopperEmail(customerModel.getContactEmail());

            Recurring recurring = getRecurringContractType(recurringContractMode, cartData.getAdyenRememberTheseDetails());
            paymentRequest.setRecurring(recurring);
        }

        // if address details are provided added it into the request
        if (cartData.getDeliveryAddress() != null) {
            Address deliveryAddress = fillInAddressData(cartData.getDeliveryAddress());
            paymentRequest.setDeliveryAddress(deliveryAddress);
        }

        if (cartData.getPaymentInfo().getBillingAddress() != null) {
            Address billingAddress = fillInAddressData(cartData.getPaymentInfo().getBillingAddress());
            paymentRequest.setBillingAddress(billingAddress);
        }

        //OneClick
        if (selectedAlias != null && !selectedAlias.isEmpty()) {
            paymentRequest.setSelectedRecurringDetailReference(selectedAlias);
            paymentRequest.setShopperInteraction(AbstractPaymentRequest.ShopperInteractionEnum.ECOMMERCE);

            //set oneclick
            Recurring recurring = getRecurringContractType(RecurringContractMode.ONECLICK);
            paymentRequest.setRecurring(recurring);
        }

        return paymentRequest;
    }

    public CaptureRequest createCaptureRequest(final String merchantAccount,
                                               final BigDecimal amount,
                                               final Currency currency,
                                               final String authReference,
                                               final String merchantReference) {
        return new CaptureRequest()
                .fillAmount(String.valueOf(amount), currency.getCurrencyCode())
                .merchantAccount(merchantAccount)
                .originalReference(authReference)
                .reference(merchantReference);
    }

    public CancelRequest createCancelRequest(final String merchantAccount,
                                             final String authReference,
                                             final String merchantReference) {
        return new CancelRequest()
                .merchantAccount(merchantAccount)
                .originalReference(authReference)
                .reference(merchantReference);
    }

    public RefundRequest createRefundRequest(final String merchantAccount,
                                             final BigDecimal amount,
                                             final Currency currency,
                                             final String authReference,
                                             final String merchantReference) {
        return new RefundRequest()
                .fillAmount(String.valueOf(amount), currency.getCurrencyCode())
                .merchantAccount(merchantAccount)
                .originalReference(authReference)
                .reference(merchantReference);
    }

    public DirectoryLookupRequest createListPaymentMethodsRequest(
            final BigDecimal amount,
            final String currency,
            final String countryCode
    ) {
        Amount amountData = Util.createAmount(amount, currency);

        return new DirectoryLookupRequest()
                .setCountryCode(countryCode)
                .setMerchantReference("GetPaymentMethods")
                .setPaymentAmount(String.valueOf(amountData.getValue()))
                .setCurrencyCode(amountData.getCurrency());
    }

    public RecurringDetailsRequest createListRecurringDetailsRequest(final String merchantAccount,
                                                                     final CustomerModel customerModel) {
        return new RecurringDetailsRequest()
                .merchantAccount(merchantAccount)
                .shopperReference(customerModel.getCustomerID())
                .selectOneClickContract();
    }

    private <T extends AbstractPaymentRequest> T createBasePaymentRequest(
            T abstractPaymentRequest,
            final HttpServletRequest request,
            final String merchantAccount) {
        String userAgent = request.getHeader("User-Agent");
        String acceptHeader = request.getHeader("Accept");
        String shopperIP = request.getRemoteAddr();

        abstractPaymentRequest
                .merchantAccount(merchantAccount)
                .setBrowserInfoData(
                        userAgent,
                        acceptHeader
                )
                .shopperIP(shopperIP);

        return abstractPaymentRequest;
    }

    /**
     * Set Address Data into API
     *
     * @param addressData
     * @return
     */
    private Address fillInAddressData(AddressData addressData) {

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

        if (addressData.getRegion() != null && !addressData.getRegion().getIsocode().isEmpty()) {
            address.setStateOrProvince(addressData.getRegion().getIsocode());
        }

        return address;
    }


    /**
     * Return Recurring object from RecurringContractMode
     *
     * @param recurringContractMode
     * @return
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
     *
     * @param recurringContractMode
     * @param enableOneClick
     * @return
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
        } else {
            Recurring.ContractEnum contractEnum = recurringContract.getContract();
            /**
             * If save card is not checked do the folllowing changes:
             * NONE => NONE
             * ONECLICK => NONE
             * ONECLICK,RECURRING => RECURRING
             * RECURRING => RECURRING
             */
            if (Recurring.ContractEnum.ONECLICK_RECURRING.equals(contractEnum)
                    || Recurring.ContractEnum.RECURRING.equals(contractEnum)) {
                return recurringContract.contract(Recurring.ContractEnum.RECURRING);
            }
        }

        return null;
    }
}
