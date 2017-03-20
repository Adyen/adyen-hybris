package com.adyen.v6.service;

import com.adyen.Client;
import com.adyen.Config;
import com.adyen.Util.Util;
import com.adyen.enums.Environment;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.*;
import com.adyen.model.hpp.DirectoryLookupRequest;
import com.adyen.model.hpp.PaymentMethod;
import com.adyen.model.modification.CancelRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.model.modification.RefundRequest;
import com.adyen.service.HostedPaymentPages;
import com.adyen.service.Modification;
import com.adyen.service.Payment;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.store.BaseStoreModel;
import com.adyen.model.Recurring;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Currency;
import java.util.List;

import static com.adyen.Client.HPP_LIVE;
import static com.adyen.Client.HPP_TEST;

//TODO: implement an interface
public class AdyenPaymentService extends DefaultPaymentServiceImpl {
    private BaseStoreModel baseStore;

    private static final Logger LOG = Logger.getLogger(AdyenPaymentService.class);

    public Config getConfig() {
        Assert.notNull(baseStore);

        String username = baseStore.getAdyenUsername();
        String password = baseStore.getAdyenPassword();
        String merchantAccount = baseStore.getAdyenMerchantAccount();
        String skinCode = baseStore.getAdyenSkinCode();
        String hmacKey = baseStore.getAdyenSkinHMAC();
        String apiEndpoint = baseStore.getAdyenAPIEndpoint();
        boolean isHppTest = baseStore.getAdyenHppTest();

        Assert.notNull(merchantAccount);

        Config config = new Config();
        config.setUsername(username);
        config.setPassword(password);
        config.setMerchantAccount(merchantAccount);
        config.setSkinCode(skinCode);
        config.setHmacKey(hmacKey);
        config.setApplicationName("Hybris v6.0");
        config.setEndpoint(apiEndpoint);
        config.setHppEndpoint(HPP_LIVE);

        if(isHppTest) {
            config.setHppEndpoint(HPP_TEST);
        }

        return config;
    }

    /**
     * Returns a Client
     * It is not a singleton, so that it can support dynamic credential changes
     *
     * @return
     */
    private Client createClient() {
        Config config = getConfig();
        Client client = new Client(config);

        return client;
    }

    public PaymentResult authorise(final CartData cartData, final HttpServletRequest request, final UserModel user, RecurringContractMode recurringContractMode) throws Exception {
        Client client = createClient();
        Payment payment = new Payment(client);

        String amount = cartData.getTotalPrice().getValue().toString();
        String currency = cartData.getTotalPrice().getCurrencyIso();
        String reference = cartData.getCode();
        String cseToken = cartData.getAdyenCseToken();

        String merchantAccount = client.getConfig().getMerchantAccount();

        PaymentRequest paymentRequest = createBasePaymentRequest(new PaymentRequest(), request, merchantAccount)
                .reference(reference)
                .setAmountData(
                        amount,
                        currency
                )
                .setCSEToken(cseToken);

        // if user is logged in and saved his card
        if(!user.getUid().equals("")) {
            paymentRequest.setShopperReference(user.getUid());
            // If NONE is selected the merchants don't want to save card data
            if(recurringContractMode != RecurringContractMode.NONE) {
                paymentRequest.setRecurring(getRecurringContractType(cartData, recurringContractMode));
            }
        }

        // if address details are provided added it into the request
        if(cartData.getDeliveryAddress() != null)  {
            Address deliveryAddress = fillInAddressData(cartData.getDeliveryAddress());
            paymentRequest.setDeliveryAddress(deliveryAddress);
        }

        if(cartData.getPaymentInfo().getBillingAddress() != null)
        {
            Address billingAddress = fillInAddressData(cartData.getPaymentInfo().getBillingAddress());
            paymentRequest.setBillingAddress(billingAddress);
        }

        return payment.authorise(paymentRequest);
    }

    /**
     * Set Address Data into API
     *
     * @param addressData
     * @return
     */
    public Address fillInAddressData(AddressData addressData)
    {

        Address address = new Address();

        // set defaults because all fields are required into the API
        address.setCity("NA");
        address.setCountry("NA");
        address.setHouseNumberOrName("NA");
        address.setPostalCode("NA");
        address.setStateOrProvince("NA");
        address.setStreet("NA");

        // set the actual values if they are available
        if(addressData.getTown() !=  null &&  addressData.getTown() !=  "") {
            address.setCity(addressData.getTown());
        }

        if(addressData.getCountry() != null  && addressData.getCountry().getIsocode() != "") {
            address.setCountry(addressData.getCountry().getIsocode());
        }

        if(addressData.getLine2() != null && addressData.getLine2() != "") {
            address.setHouseNumberOrName(addressData.getLine2());
        }

        if(addressData.getPostalCode() != null && address.getPostalCode() != "") {
            address.setPostalCode(addressData.getPostalCode());
        }

        if(addressData.getRegion() != null && addressData.getRegion().getIsocode() != "") {
            address.setStateOrProvince(addressData.getRegion().getIsocode());
        }

        if(addressData.getLine1() != null && addressData.getLine1() != "") {
           address.setStreet(addressData.getLine1());
        }

        return address;
    }


    /**
     * Return the recurringContract. If the user did not want to save the card don't send it as ONECLICK
     *
     * @param cartData
     * @param recurringContractMode
     * @return
     */
    public Recurring getRecurringContractType(final CartData cartData, RecurringContractMode recurringContractMode)
    {
        Recurring recurringContract = new com.adyen.model.Recurring();

        String recurringMode = recurringContractMode.getCode();
        Recurring.ContractEnum contractEnum = Recurring.ContractEnum.valueOf(recurringMode);

        // if user want to save his card use the configured recurring contract type
        if(cartData.getAdyenRememberTheseDetails() != null && cartData.getAdyenRememberTheseDetails()) {
            recurringContract.contract(contractEnum);
        } else {

            /**
             * If save card is not checked do the folllowing changes:
             * ONECLICK => NONE
             * ONECLICK,RECURRING => RECURRING
             * NONE => NONE
             * RECURRING => RECURRING
             */
            if(contractEnum.equals(Recurring.ContractEnum.ONECLICK_RECURRING)) {
                recurringContract.contract(Recurring.ContractEnum.RECURRING);
            } else if(!contractEnum.equals(Recurring.ContractEnum.ONECLICK)) {
                recurringContract.contract(contractEnum);
            }
        }

        return recurringContract;
    }

    public PaymentResult authorise3D(final HttpServletRequest request,
                                     final String paRes,
                                     final String md) throws Exception {
        Client client = createClient();
        Payment payment = new Payment(client);

        PaymentRequest3d paymentRequest3d = createBasePaymentRequest(
                new PaymentRequest3d(),
                request,
                client.getConfig().getMerchantAccount()
        ).set3DRequestData(md, paRes);

        LOG.info(paymentRequest3d); //TODO: anonymize
        PaymentResult paymentResult = payment.authorise3D(paymentRequest3d);
        LOG.info(paymentResult);

        return paymentResult;
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
     * Performs Capture request
     *
     * @param amount
     * @param currency
     * @param authReference
     * @param merchantReference
     * @return
     * @throws Exception
     */
    public ModificationResult capture(final BigDecimal amount,
                                      final Currency currency,
                                      final String authReference,
                                      final String merchantReference) throws Exception {
        Client client = createClient();
        Modification modification = new Modification(client);

        final CaptureRequest captureRequest = new CaptureRequest()
                .fillAmount(amount.toString(), currency.getCurrencyCode())
                .merchantAccount(client.getConfig().getMerchantAccount())
                .originalReference(authReference)
                .reference(merchantReference);

        LOG.info(captureRequest);
        ModificationResult modificationResult = modification.capture(captureRequest);
        LOG.info(modificationResult);

        return modificationResult;
    }

    /**
     * Performs cancelOrRefund request
     *
     * @param authReference
     * @param merchantReference
     * @return
     * @throws Exception
     */
    public ModificationResult cancelOrRefund(final String authReference, final String merchantReference) throws Exception {
        Client client = createClient();
        Modification modification = new Modification(client);

        final CancelRequest cancelRequest = new CancelRequest()
                .merchantAccount(client.getConfig().getMerchantAccount())
                .originalReference(authReference)
                .reference(merchantReference);

        LOG.info(cancelRequest);
        ModificationResult modificationResult = modification.cancelOrRefund(cancelRequest);
        LOG.info(modificationResult);

        return modificationResult;
    }

    /**
     * Performs refund request
     *
     * @param amount
     * @param currency
     * @param authReference
     * @param merchantReference
     * @return
     * @throws Exception
     */
    public ModificationResult refund(final BigDecimal amount,
                                     final Currency currency,
                                     final String authReference,
                                     final String merchantReference) throws Exception {
        Client client = createClient();
        Modification modification = new Modification(client);

        final RefundRequest refundRequest = new RefundRequest()
                .fillAmount(amount.toString(), currency.getCurrencyCode())
                .merchantAccount(client.getConfig().getMerchantAccount())
                .originalReference(authReference)
                .reference(merchantReference);

        LOG.info(refundRequest);
        ModificationResult modificationResult = modification.refund(refundRequest);
        LOG.info(modificationResult);

        return modificationResult;
    }

    /**
     * Get Payment methods using HPP Directory Lookup
     *
     * @param amount
     * @param currency
     * @param countryCode
     * @return
     * @throws HTTPClientException
     * @throws SignatureException
     * @throws IOException
     */
    public List<PaymentMethod> getPaymentMethods(final BigDecimal amount,
                                                 final String currency,
                                                 final String countryCode) throws HTTPClientException, SignatureException, IOException {
        Amount amountData = Util.createAmount(amount.toString(), currency);
        DirectoryLookupRequest directoryLookupRequest = new DirectoryLookupRequest()
                .setCountryCode(countryCode)
                .setMerchantReference("GetPaymentMethods")
                .setPaymentAmount(amountData.getValue().toString())
                .setCurrencyCode(amountData.getCurrency());

        LOG.info(directoryLookupRequest);

        Client client = createClient();
        HostedPaymentPages hostedPaymentPages = new HostedPaymentPages(client);

        List<PaymentMethod> paymentMethods = hostedPaymentPages.getPaymentMethods(directoryLookupRequest);
        LOG.info(paymentMethods);

        return paymentMethods;
    }

    /**
     * Retrive the HPP base URL for the current basestore
     *
     * @return
     */
    public String getHppUrl() {
        Config config = getConfig();

        return config.getHppEndpoint() + "/details.shtml";
    }

    public BaseStoreModel getBaseStore() {
        return baseStore;
    }

    public void setBaseStore(BaseStoreModel baseStore) {
        this.baseStore = baseStore;
    }
}
