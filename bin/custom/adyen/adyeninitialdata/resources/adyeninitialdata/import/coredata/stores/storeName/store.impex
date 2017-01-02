# 
# Create the BaseStore
#

$productCatalog=__PRODUCT_CATALOG_NAME__
$classificationCatalog=__CLASSIFICATION_CATALOG_NAME__
$currencies=JPY,USD
$net=false
$storeUid=__STORE_UID__
$defaultCurrency=USD
$languages=ja,en,de,zh
$defaultLanguage=en
$unitedKingdom=GB,GG,IM,JE
$europeNotUK=AD,AL,AT,BA,BE,BG,BY,CH,CY,CZ,DE,DK,EE,ES,FI,FO,FR,GI,GL,GR,HR,HU,IE,IS,IT,LI,LT,LU,LV,MC,MD,ME,MK,MT,NL,NO,PL,PT,RO,RS,RU,SE,SI,SK,SM,TR,UA,VA
$asianCountries=CN,JP,VN,HK,KP,KR
$deliveryCountries=$unitedKingdom,$europeNotUK,$asianCountries,US
$orderProcessCode=order-process
$pickupInStoreMode=BUY_AND_COLLECT
$customerAllowedToIgnoreSuggestions=true
$paymentProvider=Mockup
$promoGrp=__DEFAULT_PROMO_GRP__
$checkoutFlowGroup=defaultCheckoutGroup

INSERT_UPDATE PromotionGroup;Identifier[unique=true];
;$promoGrp;

# Base Store
INSERT_UPDATE BaseStore;uid[unique=true];catalogs(id);currencies(isocode);net;taxGroup(code);storelocatorDistanceUnit(code);defaultCurrency(isocode);languages(isocode);defaultLanguage(isocode);deliveryCountries(isocode);submitOrderProcessCode;pickupInStoreMode(code);customerAllowedToIgnoreSuggestions;paymentProvider;checkoutFlowGroup;
;$storeUid;$productCatalog,$classificationCatalog;$currencies;$net;jp-taxes;km;$defaultCurrency;$languages;$defaultLanguage;$deliveryCountries;$orderProcessCode;$pickupInStoreMode;$customerAllowedToIgnoreSuggestions;$paymentProvider;$checkoutFlowGroup;

INSERT_UPDATE BaseStore2DeliveryModeRel;source(uid)[unique=true];target(code)[unique=true]
