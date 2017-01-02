# ImpEx for Importing Products Stock Levels and Warehouses

# Macros / Replacement Parameter definitions
$productCatalog=__PRODUCT_CATALOG_NAME__
$productCatalogName=__PRODUCT_CATALOG_FULL_NAME__
$vendor=__VENDOR_NAME__

$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]


INSERT_UPDATE Vendor;code[unique=true]
#;$vendor

INSERT_UPDATE Warehouse;code[unique=true];vendor(code);default[default=true]

INSERT_UPDATE StockLevel;available;warehouse(code)[unique=true];inStockStatus(code);maxPreOrder;maxStockLevelHistoryCount;overSelling;preOrder;productCode[unique=true];reserved

UPDATE Product;code[unique=true];$catalogVersion;stockLevels(productCode,warehouse(code));vendors(code)
