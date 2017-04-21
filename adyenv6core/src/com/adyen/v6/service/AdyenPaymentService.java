package com.adyen.v6.service;

import com.adyen.Client;
import com.adyen.Config;
import com.adyen.Util.Util;
import com.adyen.enums.VatCategory;
import com.adyen.httpclient.HTTPClientException;
import com.adyen.model.*;
import com.adyen.model.additionalData.InvoiceLine;
import com.adyen.model.hpp.DirectoryLookupRequest;
import com.adyen.model.hpp.PaymentMethod;
import com.adyen.model.modification.CancelRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.model.modification.RefundRequest;
import com.adyen.model.recurring.Recurring;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.model.recurring.RecurringDetailsRequest;
import com.adyen.model.recurring.RecurringDetailsResult;
import com.adyen.service.HostedPaymentPages;
import com.adyen.service.Modification;
import com.adyen.service.Payment;
import com.adyen.service.exception.ApiException;
import com.adyen.v6.enums.RecurringContractMode;
import com.adyen.v6.factory.AdyenRequestFactory;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.TaxService;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.adyen.Client.HPP_LIVE;
import static com.adyen.Client.HPP_TEST;

//TODO: implement an interface
public class AdyenPaymentService extends DefaultPaymentServiceImpl {
    private BaseStoreModel baseStore;
    private AdyenRequestFactory adyenRequestFactory;

    private static final Logger LOG = Logger.getLogger(AdyenPaymentService.class);

    @Resource(name = "cartService")
    private CartService cartService;

    @Resource(name = "taxService")
    private TaxService taxService;

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

        if (isHppTest) {
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

    /**
     * Performs authorization request
     *
     * @param cartData
     * @param request
     * @param customerModel
     * @return
     * @throws Exception
     */
    public PaymentResult authorise(final CartData cartData, final HttpServletRequest request, final CustomerModel customerModel) throws Exception {
        Client client = createClient();
        Payment payment = new Payment(client);

        PaymentRequest paymentRequest = getAdyenRequestFactory().createAuthorizationRequest(
                client.getConfig().getMerchantAccount(),
                cartData,
                request,
                customerModel,
                baseStore.getAdyenRecurringContractMode()
        );


        if (cartData.getAdyenPaymentMethod().equals("klarna") || cartData.getAdyenPaymentMethod().equals("ratepay")) {

            paymentRequest.selectedBrand(cartData.getAdyenPaymentMethod());

            // TODO quest user check
            paymentRequest.setShopperReference(customerModel.getUid());
            // If NONE is selected the merchants don't save card data
            if(baseStore.getAdyenRecurringContractMode() != RecurringContractMode.NONE) {
                paymentRequest.setRecurring(getRecurringContractType(cartData, baseStore.getAdyenRecurringContractMode()));
            }


            // if address details are provided added it into the request
            if(cartData.getDeliveryAddress() != null)  {
                LOG.info("Retrieve Delivery Address Data");
                Address deliveryAddress = fillInAddressData(cartData.getDeliveryAddress());
                paymentRequest.setDeliveryAddress(deliveryAddress);
            }

            if(cartData.getPaymentInfo().getBillingAddress() != null)
            {
                LOG.info("Retrieve Billing Address Data");
                Address billingAddress = fillInAddressData(cartData.getPaymentInfo().getBillingAddress());
                paymentRequest.setBillingAddress(billingAddress);


                paymentRequest.setShopperEmail(cartData.getPaymentInfo().getBillingAddress().getEmail());
                paymentRequest.setShopperName(fillinShopperName(cartData.getPaymentInfo().getBillingAddress()));

                paymentRequest.setTelephoneNumber(cartData.getPaymentInfo().getBillingAddress().getPhone());
            }

            this.setOpenInvoiceData(paymentRequest, cartData, customerModel);

        }

        LOG.debug(paymentRequest);
        PaymentResult paymentResult = payment.authorise(paymentRequest);
        LOG.debug(paymentResult);

        return paymentResult;
    }

//    public PaymentResult authoriseOpenInvoice(final CartData cartData, final HttpServletRequest request, final CustomerModel customerModel, final Boolean guestUser) throws Exception {
//
//        Client client = createClient();
//        Payment payment = new Payment(client);
//
//        PaymentRequest paymentRequest = getAdyenRequestFactory().createAuthorizationRequest(
//                client.getConfig().getMerchantAccount(),
//                cartData,
//                request,
//                customerModel,
//                baseStore.getAdyenRecurringContractMode()
//        );
//
//        paymentRequest.selectedBrand(cartData.getAdyenPaymentMethod());
//
//
//        // if user is logged in
//        if(!guestUser) {
//            paymentRequest.setShopperReference(customerModel.getUid());
//            // If NONE is selected the merchants don't save card data
//            if(baseStore.getAdyenRecurringContractMode() != RecurringContractMode.NONE) {
//                paymentRequest.setRecurring(getRecurringContractType(cartData, baseStore.getAdyenRecurringContractMode()));
//            }
//        }
//
//        // if address details are provided added it into the request
//        if(cartData.getDeliveryAddress() != null)  {
//            LOG.info("Retrieve Delivery Address Data");
//            Address deliveryAddress = fillInAddressData(cartData.getDeliveryAddress());
//            paymentRequest.setDeliveryAddress(deliveryAddress);
//        }
//
//        if(cartData.getPaymentInfo().getBillingAddress() != null)
//        {
//            LOG.info("Retrieve Billing Address Data");
//            Address billingAddress = fillInAddressData(cartData.getPaymentInfo().getBillingAddress());
//            paymentRequest.setBillingAddress(billingAddress);
//
//
//            paymentRequest.setShopperEmail(cartData.getPaymentInfo().getBillingAddress().getEmail());
//            paymentRequest.setShopperName(fillinShopperName(cartData.getPaymentInfo().getBillingAddress()));
//
//            paymentRequest.setTelephoneNumber(cartData.getPaymentInfo().getBillingAddress().getPhone());
//        }
//
//        this.setOpenInvoiceData(paymentRequest, cartData);
//
//        LOG.info(paymentRequest);
//
//        return payment.authorise(paymentRequest);
//    }

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
     * Set shopper info
     *
     * @param addressData
     * @return
     */
    public Name fillinShopperName(AddressData addressData)
    {
        Name shopperName = new Name();

        shopperName.setFirstName(addressData.getFirstName());
        shopperName.setLastName(addressData.getLastName());

        if( addressData.getTitleCode().equals("mrs") ||
                addressData.getTitleCode().equals("miss") ||
                addressData.getTitleCode().equals("ms"))
        {
            shopperName.setGender(Name.GenderEnum.FEMALE);
        } else {
            shopperName.setGender(Name.GenderEnum.MALE);
        }

        return shopperName;
    }

    public void setOpenInvoiceData(PaymentRequest paymentRequest, CartData cartData, final CustomerModel customerModel)
    {

        // TODO: make this dynamic


//        cartData.getPaymentInfo().getBillingAddress().getD

        for (AddressModel address : customerModel.getAddresses()) {
            Date dob = address.getDateOfBirth();
            address.getGender();

        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date dateOfBirth = format.parse("1970-07-10");
            paymentRequest.setDateOfBirth(dateOfBirth);
            LOG.info("DOB" + dateOfBirth);
        } catch(Exception e) {}


        String vatCategory = "None";
        Integer count = 1;

        //
        List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();


        String currency = cartService.getSessionCart().getCurrency().getIsocode();
        LOG.info("CURRENCY" + currency);

        for(AbstractOrderEntryModel entry : cartService.getSessionCart().getEntries()) {

            Double totalPrice = entry.getTotalPrice();
            Double vatAmount = entry.getBasePrice();

            Amount itemAmount = Util.createAmount(totalPrice.toString(), currency);

            String description = "NA";
            if(entry.getProduct().getName() != null && !entry.getProduct().getName().equals("")) {
                description  = entry.getProduct().getName();
            }

            Double tax = entry.getTaxValues().stream()
                    .map(taxValue -> taxValue.getAppliedValue())
                    .reduce(0.0, (x, y) -> x = x+y);


            Double percentage = entry.getTaxValues().stream()
                    .map(taxValue -> taxValue.getValue())
                    .reduce(0.0, (x, y) -> x = x+y) * 100;

            LOG.info("percentage" + percentage);

            InvoiceLine invoiceLine = new InvoiceLine();
            invoiceLine.setCurrencyCode(currency);
            invoiceLine.setDescription(description);
            invoiceLine.setItemAmount(itemAmount.getValue());
            invoiceLine.setItemVATAmount(tax.longValue());
            invoiceLine.setItemVatPercentage(percentage.longValue());
            invoiceLine.setVatCategory(VatCategory.NONE);
            invoiceLine.setNumberOfItems(entry.getQuantity().intValue());

            if (entry.getProduct() != null && !entry.getProduct().getCode().isEmpty()) {
                invoiceLine.setItemId(entry.getProduct().getCode());
            }

            if (entry.getProduct() != null && !entry.getProduct().getCode().isEmpty()) {
                invoiceLine.setItemId(entry.getProduct().getCode());
            }

            invoiceLines.add(invoiceLine);

        }

        // get delivery costs
        if (cartData.getDeliveryCost() != null) {

            InvoiceLine invoiceLine = new InvoiceLine();
            invoiceLine.setCurrencyCode(currency);
            invoiceLine.setDescription("Payment Fee");
            invoiceLine.setItemAmount(cartData.getDeliveryCost().getValue().longValue());
            invoiceLine.setItemVATAmount(new Long("0"));
            invoiceLine.setItemVatPercentage(new Long("0"));
            invoiceLine.setVatCategory(VatCategory.NONE);
            invoiceLine.setNumberOfItems(1);
            invoiceLines.add(invoiceLine);

        } else {
            LOG.info("No Delivery Costs");
        }




//        for(OrderEntryData entry : cartData.getEntries()) {
////            entry.getProduct();
//
//            Double tax = cartService.getSessionCart().getEntries().get(0).getTaxValues().stream()
//                    .map(taxValue -> taxValue.getAppliedValue())
//                    .reduce(0.0, (x, y) -> x = x+y);
//
//
//            cartService.getSessionCart().getEntries().get(0).getTaxValues();
//
//            for(TaxValue taxValue : cartService.getSessionCart().getEntries().get(0).getTaxValues()) {
//
//                LOG.info("TAX VALUE CODE" + taxValue.getCode());
//                LOG.info("TAX VALUE Value" + taxValue.getValue());
//                LOG.info("TAX VALUE Applied Value" + taxValue.getAppliedValue());
//
//                // grab tax from identifier
//                TaxModel taxModel = taxService.getTaxForCode(taxValue.getCode());
//                LOG.info("TAX VALUE FROM TAX" + taxModel.getValue());
//                LOG.info("TAX NAME FROM TAX" + taxModel.getName());
//            }
//
//            UserTaxGroup userTaxGroup = baseStore.getTaxGroup();
//            LOG.info("USER TAX GROUP:" + userTaxGroup.getCode());
//
//
////            cartService.getSessionCart().getEntries().get(0).getTaxValues();
//            LOG.info("TAX" + tax);
//
//            String currency = entry.getTotalPrice().getCurrencyIso();
//            BigDecimal totalPrice = entry.getTotalPrice().getValue();
//            BigDecimal vatAmount = entry.getBasePrice().getValue();
//
//            Amount itemAmount = Util.createAmount(totalPrice.toString(), currency);
//            Long vatPercentage = new Long("1900");
//
//            String lineNumber = "openinvoicedata.line" + count.toString();
//
//            LOG.info("LineNumber" + lineNumber);
//
//            String description = "NA";
//            if(entry.getProduct().getName() != null && !entry.getProduct().getName().equals("")) {
//                description  = entry.getProduct().getName();
//            }
//
//            InvoiceLine invoiceLine = new InvoiceLine();
//            invoiceLine.setCurrencyCode(currency);
//            invoiceLine.setDescription(description);
//            invoiceLine.setItemAmount(itemAmount.getValue());
//            invoiceLine.setItemVATAmount(tax.longValue());
//            invoiceLine.setItemVatPercentage(vatPercentage);
//            invoiceLine.setVatCategory(VatCategory.NONE);
//            invoiceLine.setNumberOfItems(entry.getQuantity().intValue());
//
//            if (entry.getProduct() != null && !entry.getProduct().getCode().equals("")) {
//                invoiceLine.setItemId(entry.getProduct().getCode());
//            }
//
//            invoiceLines.add(invoiceLine);
//
//            LOG.info(invoiceLine);
//            LOG.info("ITEMID:" + entry.getProduct().getCode());
//            count++;
//        }

        paymentRequest.setInvoiceLines(invoiceLines);

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
        Recurring recurringContract = new Recurring();

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

    /**
     * Performs 3D secure authorization request
     *
     * @param request
     * @param paRes
     * @param md
     * @return
     * @throws Exception
     */
    public PaymentResult authorise3D(final HttpServletRequest request,
                                     final String paRes,
                                     final String md) throws Exception {
        Client client = createClient();
        Payment payment = new Payment(client);

        PaymentRequest3d paymentRequest3d = getAdyenRequestFactory().create3DAuthorizationRequest(
                client.getConfig().getMerchantAccount(),
                request,
                md,
                paRes
        );

        LOG.debug(paymentRequest3d);
        PaymentResult paymentResult = payment.authorise3D(paymentRequest3d);
        LOG.debug(paymentResult);

        return paymentResult;
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

        CaptureRequest captureRequest = getAdyenRequestFactory().createCaptureRequest(
                client.getConfig().getMerchantAccount(),
                amount,
                currency,
                authReference,
                merchantReference
        );

        LOG.debug(captureRequest);
        ModificationResult modificationResult = modification.capture(captureRequest);
        LOG.debug(modificationResult);

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

        CancelRequest cancelRequest = getAdyenRequestFactory().createCancelRequest(
                client.getConfig().getMerchantAccount(),
                authReference,
                merchantReference
        );

        LOG.debug(cancelRequest);
        ModificationResult modificationResult = modification.cancelOrRefund(cancelRequest);
        LOG.debug(modificationResult);

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

        RefundRequest refundRequest = getAdyenRequestFactory().createRefundRequest(
                client.getConfig().getMerchantAccount(),
                amount,
                currency,
                authReference,
                merchantReference
        );

        LOG.debug(refundRequest);
        ModificationResult modificationResult = modification.refund(refundRequest);
        LOG.debug(modificationResult);

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
        Client client = createClient();

        if(client.getConfig().getSkinCode() == null || client.getConfig().getSkinCode().isEmpty()) {
            return new ArrayList<>();
        }

        HostedPaymentPages hostedPaymentPages = new HostedPaymentPages(client);

        DirectoryLookupRequest directoryLookupRequest = getAdyenRequestFactory()
                .createListPaymentMethodsRequest(amount, currency, countryCode);

        LOG.debug(directoryLookupRequest);
        List<PaymentMethod> paymentMethods = hostedPaymentPages.getPaymentMethods(directoryLookupRequest);
        LOG.debug(paymentMethods);

        return paymentMethods;
    }

    public List<RecurringDetail> getStoredCards(final CustomerModel customerModel) throws IOException, ApiException {
        if (customerModel == null) {
            return null;
        }

        Client client = createClient();
        com.adyen.service.Recurring recurring = new com.adyen.service.Recurring(client);

        RecurringDetailsRequest request = getAdyenRequestFactory().createListRecurringDetailsRequest(
                client.getConfig().getMerchantAccount(),
                customerModel
        );

        LOG.debug(request);
        RecurringDetailsResult result = recurring.listRecurringDetails(request);
        LOG.debug(result);

        //Return only cards
        List<RecurringDetail> storedCards = result.getRecurringDetails().stream()
                .filter(detail -> (detail.getCard() != null && detail.getAlias() != null))
                .collect(Collectors.toList());

        return storedCards;
    }

    /**
     * Retrive the CSE JS Url
     *
     * @return
     */
    public String getCSEUrl() {
        Config config = getConfig();

        String cseId = baseStore.getAdyenCSEID();
        Assert.notNull(cseId);

        return config.getHppEndpoint() + "/cse/js/" + cseId + ".shtml";
    }

    public AdyenRequestFactory getAdyenRequestFactory() {
        if (adyenRequestFactory == null) {
            adyenRequestFactory = new AdyenRequestFactory();
        }

        return adyenRequestFactory;
    }

    public void setAdyenRequestFactory(AdyenRequestFactory adyenRequestFactory) {
        this.adyenRequestFactory = adyenRequestFactory;
    }

    public BaseStoreModel getBaseStore() {
        return baseStore;
    }

    public void setBaseStore(BaseStoreModel baseStore) {
        this.baseStore = baseStore;
    }
}
