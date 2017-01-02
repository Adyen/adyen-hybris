#
# Create Warehouses for Store
#
#% impex.setLocale( Locale.GERMAN );

# Macros / Replacement Parameter definitions
$storeUid=__STORE_UID__

INSERT_UPDATE BaseStore2WarehouseRel;source(uid)[unique=true];target(code)[unique=true]

INSERT_UPDATE PointOfService;name[unique=true];displayName;warehouses(code);address(&addrID);latitude;longitude;geocodeTimestamp[dateformat=dd.MM.yyyy];type(code)[default=WAREHOUSE]

INSERT_UPDATE Address;&addrID;streetnumber[unique=true];streetname;town;country(isocode);postalcode[unique=true];phone1;owner(PointOfService.name)[unique=true]

INSERT_UPDATE BaseStore;uid[unique=true];defaultDeliveryOrigin(name)
