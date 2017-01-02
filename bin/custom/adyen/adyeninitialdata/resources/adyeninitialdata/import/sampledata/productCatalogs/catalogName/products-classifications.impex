# ImpEx for Importing Product Classifications

#% impex.setLocale(Locale.ENGLISH);

# Macros / Replacement Parameter definitions
$productCatalog=__PRODUCT_CATALOG_NAME__
$productCatalogName=__PRODUCT_CATALOG_FULL_NAME__
$classificationCatalog=__CLASSIFICATION_CATALOG_NAME__

$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]
$clAttrModifiers=system='$classificationCatalog',version='1.0',translator=de.hybris.platform.catalog.jalo.classification.impex.ClassificationAttributeTranslator

# Insert Product Classifications

# replace <string> and <int> with actual values
$feature1=@<string>, <int>[$clAttrModifiers];  
$feature2=@<string>, <int>[$clAttrModifiers];  
$feature3=@<string>, <int>[$clAttrModifiers];  
$feature4=@<string>, <int>[$clAttrModifiers];  
$feature6=@<string>, <int>[$clAttrModifiers];  
$feature7=@<string>, <int>[$clAttrModifiers];  
$feature8=@<string>, <int>[$clAttrModifiers];  
INSERT_UPDATE Product;code[unique=true];$feature1;$feature2;$feature3;$feature4;$feature6;$feature7;$feature8;$catalogVersion;
