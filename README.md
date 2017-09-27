# Adyen Hybris v6 plugin

This plugin supports Hybris versions 6.0 to 6.3

## Installation

### 1. Copy extension files to bin/custom directory

### 2. Add the Adyen extensions to the config/localextensions.xml file

Required for the checkout:
```
<extension dir="${HYBRIS_BIN_DIR}/custom/adyen-hybris/adyenv6core"/>
<extension dir="${HYBRIS_BIN_DIR}/custom/adyen-hybris/adyenv6b2ccheckoutaddon"/>
<extension dir="${HYBRIS_BIN_DIR}/custom/adyen-hybris/adyenv6backoffice"/>
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

Supported via Adyen [Hosted Payment Pages](https://docs.adyen.com/developers/products-and-subscriptions/hosted-payment-pages).
