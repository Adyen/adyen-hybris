# ImpEx for Importing Prices

# Macros / Replacement Parameter definitions
$productCatalog=__PRODUCT_CATALOG_NAME__

$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default='$productCatalog:Staged']
$prices=Europe1prices[translator=de.hybris.platform.europe1.jalo.impex.Europe1PricesTranslator]

# Set product approval status to Approved only for those products that have prices.
$approved=approvalstatus(code)[default='approved']

UPDATE Product;code[unique=true];$prices;$approved;$catalogVersion;Europe1PriceFactory_PTG(code)
