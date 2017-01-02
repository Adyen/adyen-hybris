# ImpEx for Importing Categories

# Macros / Replacement Parameter definitions
$productCatalog=__PRODUCT_CATALOG_NAME__
$productCatalogName=__PRODUCT_CATALOG_FULL_NAME__

$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]
$supercategories=source(code, $catalogVersion)[unique=true]
$categories=target(code, $catalogVersion)[unique=true]

# Insert Categories
INSERT_UPDATE Category;code[unique=true];allowedPrincipals(uid)[default='customergroup'];$catalogVersion

# Insert Category Structure
INSERT_UPDATE CategoryCategoryRelation;$categories;$supercategories
