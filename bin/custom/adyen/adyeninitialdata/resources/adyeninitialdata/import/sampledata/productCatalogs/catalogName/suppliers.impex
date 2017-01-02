# ImpEx for Importing Suppliers

# Macros / Replacement Parameter definitions
$productCatalog=__PRODUCT_CATALOG_NAME__
$productCatalogName=__PRODUCT_CATALOG_FULL_NAME__

$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]
$superCategories=superCategories(code, $catalogVersion)

# Create Supplier Categories
INSERT_UPDATE Category;code[unique=true];$superCategories;allowedPrincipals(uid)[default='customergroup'];$catalogVersion
