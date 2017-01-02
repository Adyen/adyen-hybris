# ImpEx for Importing Products

# Macros / Replacement Parameter definitions
$productCatalog=__PRODUCT_CATALOG_NAME__
$productCatalogName=__PRODUCT_CATALOG_FULL_NAME__

$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]
$supercategories=supercategories(code, $catalogVersion)
$baseProduct=baseProduct(code,$catalogVersion)
$approved=approvalstatus(code)[default='check']

# Insert Products
INSERT_UPDATE Product;code[unique=true];$supercategories;manufacturerName;manufacturerAID;unit(code);ean;variantType(code);$catalogVersion;$approved
