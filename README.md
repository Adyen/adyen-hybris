# Adyen Hybris v6 plugin

This plugin supports Hybris versions 6.0 to 6.2

## Installation

1. Add the Adyen extension to the config/localextensions.xml file
```<extension dir="${HYBRIS_BIN_DIR}/custom/adyenv6b2ccheckoutaddon"/>```

2. Add your Adyen credentials to the config/local.properties file
```
adyen.ws.username=YOUR_SYSTEM_USER_USERNAME
adyen.ws.password=YOUR_SYSTEM_USER_PASSWORD

adyen.merchantaccount=YOUR_MERCHANT_ACCOUNT

adyen.notification.username=YOUR_NOTIFICATION_USERNAME
adyen.notification.password=YOUR_NOTIFICATION_PASSWORD

adyen.cse.id=YOUR_CSE_ID (not token)

adyen.skin.code=YOUR_SKIN_CODE
adyen.skin.hmac=YOUR_HMAC_KEY

adyen.capture.immediate=true|false
```

```
csrf.allowed.url.patterns=/[^/]+(/[^?]*)+(sop-response)$,/[^/]+(/[^?]*)+(merchant_callback)$,/[^/]+(/[^?]*)+(hop-response)$,/[^/]+(/[^?]*)+(adyen-response)$

```

3. Build
```
cd bin/platform
. ./setantenv.sh
ant addoninstall -Daddonnames="adyenv6b2ccheckoutaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"
ant clean all
```

