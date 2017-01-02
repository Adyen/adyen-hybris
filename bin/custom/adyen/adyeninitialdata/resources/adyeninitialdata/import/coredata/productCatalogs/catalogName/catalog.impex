# 
# Import the Product Catalog and Classification Catalog
#

$productCatalog=__PRODUCT_CATALOG_NAME__
$classificationCatalog=__CLASSIFICATION_CATALOG_NAME__
$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]
$languages=ja,en,de,zh

# Product catalog
INSERT_UPDATE Catalog;id[unique=true]
;$productCatalog

# Classification catalog
INSERT_UPDATE ClassificationSystem;id[unique=true]
;$classificationCatalog

# Product versions for product catalogs
INSERT_UPDATE CatalogVersion;catalog(id)[unique=true];version[unique=true];active;languages(isoCode);readPrincipals(uid)
;$productCatalog;Staged;false;$languages;employeegroup
;$productCatalog;Online;true;$languages;employeegroup

# Insert Classifications System Version
INSERT_UPDATE ClassificationSystemVersion;catalog(id)[unique=true];version[unique=true];active;inclPacking[virtual=true,default=true];inclDuty[virtual=true,default=true];inclFreight[virtual=true,default=true];inclAssurance[virtual=true,default=true]
;$classificationCatalog;1.0;true

# Create default tax row for all products in product catalog
INSERT_UPDATE TaxRow;$catalogVersion;tax(code)[unique=true];pg(code)[unique=true];ug(code)[unique=true]
;;jp-vat-full;jp-vat-full;jp-taxes
