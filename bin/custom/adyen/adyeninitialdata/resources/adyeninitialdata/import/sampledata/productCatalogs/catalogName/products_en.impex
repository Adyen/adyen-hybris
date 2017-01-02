# ImpEx for Importing Product Localisations
 
# Macros / Replacement Parameter definitions
$productCatalog=__PRODUCT_CATALOG_NAME__
$productCatalogName=__PRODUCT_CATALOG_FULL_NAME__

$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]

# Language
$lang=en

# Update allProducts with localisations
UPDATE Product;code[unique=true];$catalogVersion;name[lang=$lang];summary[lang=$lang];description[lang=$lang]
