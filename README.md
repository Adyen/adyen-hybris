# Adyen SAP Commerce (Hybris) v6 plugin

This plugin supports SAP Commerce (Hybris) versions 6.x

## Integration

The SAP Commerce integrates Adyen Checkout for all card payments and local/redirect payment methods.
Boleto, PayPal ECS and RatePay are routed over the old integration. When available in the new Checkout they will be migrated to the new flow.

## Requirements
SAP Commerce (Hybris) version 6.x

## Installation

### 1. Copy extension files to bin/custom directory

### 2. Add the Adyen extensions to the config/localextensions.xml file

Required for the checkout:
```
<extension dir="${HYBRIS_BIN_DIR}/custom/adyen-hybris/adyenv6core"/>
<extension dir="${HYBRIS_BIN_DIR}/custom/adyen-hybris/adyenv6b2ccheckoutaddon"/>
<extension dir="${HYBRIS_BIN_DIR}/custom/adyen-hybris/adyenv6backoffice"/>
```
Required for the notifications:
```
<extension dir="${HYBRIS_BIN_DIR}/custom/adyen-hybris/adyenv6notification"/>
```

Additionally, required when using yacceleratorordermanagement (b2c_acc_oms recipe):
```
<extension dir="${HYBRIS_BIN_DIR}/custom/adyen-hybris/adyenv6ordermanagement"/>
```

Additionally, required when using yacceleratorfulfilment (b2c_acc recipe):
```
<extension dir="${HYBRIS_BIN_DIR}/custom/adyen-hybris/adyenv6fulfilmentprocess"/>
```

### 3. Modify local.properties 

Modify config/local.properties file: 
append ,/[^/]+(/[^?]*)+(adyen-response)$,/adyen(/[^?]*)+$ to the value of csrf.allowed.url.patterns

### 4. Build
```
cd bin/platform
. ./setantenv.sh
ant addoninstall -Daddonnames="adyenv6b2ccheckoutaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"
ant clean all
```

### 5. Add your Adyen credentials to the BaseStore via Hybris backoffice

For more detailed instructions you can visit the [documentation page](https://docs.adyen.com/developers/plug-ins-and-partners/hybris)
Please make sure your merchant has Variant true in API and responses section so that you get paymentMethod back in response.


## Supported payment methods

### Credit Cards

Credit Card payments are supported using [Client Side Encryption](https://docs.adyen.com/support/payment-glossary/client-side-encryption-cse).

### Klarna

Klarna is supported via Adyen API.
Requires shopper data listed in: https://developers.klarna.com/en/se/kpm/test-credentials

### Boleto

[Boleto](https://docs.adyen.com/developers/payment-methods/boleto-bancario) is supported via Adyen API.

### Paypal Express Checkout Shortcut

Requires both Adyen API and HPP credentials.

The plugin offers:
 - a facade (AdyenPaypalFacade) that takes care of the communication from and to Adyen regarding Paypal ECS
 - a default controller that given a valid Cart, can initiate such payment "/en/adyen/paypal-ecs/initialize"

More details can be found here: https://docs.adyen.com/developers/payment-methods/paypal/express-checkout-shortcut

### Other alternative payment methods

Supported via Adyen [Hosted Payment Pages](https://docs.adyen.com/classic-integration/hosted-payment-pages/).


## Usage with OCC

The plugin supports the following OCC v2 compatible methods via [com.adyen.v6.facades.AdyenCheckoutFacade](adyenv6core/src/com/adyen/v6/facades/AdyenCheckoutFacade.java):

1. PaymentDetailsListWsDTO getPaymentDetails(String userId) throws IOException, ApiException;

```
    OCC controller: UsersController.getPaymentInfos
    Endpoint: GET /{userId}/paymentdetails
```

This method that will return the stored cards associated to the shopping cart user via Adyen API.

2. PaymentDetailsWsDTO addPaymentDetails(PaymentDetailsWsDTO paymentDetails, DataMapper dataMapper);

```
    OCC controller: CartsController.addPaymentDetails
    Endpoint: POST /{cartId}/paymentdetails
```

This method that will receive the payment method selection and rest of payment details and store them in the Cart.

For Credit Card payments - it expects encrypted card holder data obtained from your frontend implementation using Secured Fields

For Stored Cards payments - selected Adyen recurringReference of the card and encrypted cvc

For Boleto payments - social security number
    

3. OrderData authorisePayment(CartData cartData) throws Exception;

```
    OCC controller: OrdersController.placeOrder
    Endpoint: POST /users/{userId}/orders
```

This method will place the payment request using the previously stored payment method selection data. Upon successful response from Adyen API, it will register payment response in cart/order level.

It returns an instance of OrderWSDTO obtained from OrderData of the placed order.
For Boleto, it will contain the pdf url, the base64 encoded data, expiration date and due date
https://docs.adyen.com/developers/payment-methods/boleto-bancario/boleto-payment-request

 ## 3DS2 configuration
 By default 3DS2 is disabled. If you want to enable 3DS2 in your system, please set following property in local.properties file, build your environment and restart the server.
```
is3DS2allowed = true

```

 ## Documentation
 https://docs.adyen.com/developers/plugins/hybris
 
 ## Support
 You can create issues on our Magento Repository. In case of specific problems with your account, please contact
 support@adyen.com.
 
 ## License
 MIT license. For more information, see the LICENSE file.
