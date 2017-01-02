# ImpEx for Related Products

# Macros / Replacement Parameter definitions
$productCatalog=__PRODUCT_CATALOG_NAME__
$productCatalogName=__PRODUCT_CATALOG_FULL_NAME__

$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]

# Insert Product References
INSERT_UPDATE ProductReference;source(code,$catalogVersion)[unique=true];target(code,$catalogVersion)[unique=true];referenceType(code);active[default=true];preselected[default=false]
