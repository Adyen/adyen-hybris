# Adyen Hybris v6 plugin

This plugin supports Hybris versions 6.0 to 6.2

## Installation

1. Add the Adyen extensions to the config/localextensions.xml file
```
<extension dir="${HYBRIS_BIN_DIR}/custom/adyenv6b2ccheckoutaddon"/>
<extension dir="${HYBRIS_BIN_DIR}/custom/adyenv6backoffice"/>
```

2. Add your Adyen credentials to the BaseStore via Hybirs backoffice

adyenUsername -> System User Username

adyenPassword -> System User Password

adyenNotificationUsername -> Server Communication HTTP Basic username

adyenNotificationPassword -> Server Communication HTTP Basic password

adyenMerchantAccount -> Merchant account name

adyenCSEID -> Client Side Encryption ID (not key!)

adyenSkinCode -> HPP skin code

adyenSkinHMAC -> HPP skin HMAC key

adyenImmediateCapture -> Immediate capture flow (true) - manual capture flow (false)


3. Build
```
cd bin/platform
. ./setantenv.sh
ant addoninstall -Daddonnames="adyenv6b2ccheckoutaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"
ant clean all
```

