# Adyen Hybris v6 plugin

This plugin supports Hybris versions 6.0 to 6.3

## Installation

### 1. Copy extension files to bin/custom directory ###

### 2. Add the Adyen extensions to the config/localextensions.xml file ###

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

### 3. Add your Adyen credentials to the BaseStore via Hybris backoffice ###

adyenUsername -> System User Username

adyenPassword -> System User Password

adyenNotificationUsername -> Server Communication HTTP Basic username

adyenNotificationPassword -> Server Communication HTTP Basic password

adyenMerchantAccount -> Merchant account name

adyenCSEID -> Client Side Encryption ID (not key!)

adyenSkinCode -> HPP skin code (leave empty if you don't want to use HPP)

adyenSkinHMAC -> HPP skin HMAC key

adyenImmediateCapture -> Immediate capture flow (true) - manual capture flow (false)

adyenHppTest -> HPP test mode (set to false for live mode)

adyenAPIEndpoint -> Adyen API endpoint. Set to https://pal-test.adyen.com for Test or https://pal-live.adyen.com for Live.

adyenAllowedCards -> Allowed Credit Card variants

### 4. Modify local.properties ###

Modify config/local.properties file: 
append ,/[^/]+(/[^?]*)+(adyen-response)$,/adyen(/[^?]*)+$ to the value of csrf.allowed.url.patterns

### 5. Build ###
```
cd bin/platform
. ./setantenv.sh
ant addoninstall -Daddonnames="adyenv6b2ccheckoutaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"
ant clean all
```
