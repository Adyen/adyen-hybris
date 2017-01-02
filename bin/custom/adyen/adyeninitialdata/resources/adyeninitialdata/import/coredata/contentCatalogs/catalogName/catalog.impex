#
# Import the Content Catalog
#
$contentCatalog=__CONTENT_CATALOG_NAME__
$languages=ja,en,de,zh

# Content catalog for CMS contents
INSERT_UPDATE ContentCatalog;id[unique=true]
;$contentCatalog

# Catalog versions for content catalogs
INSERT_UPDATE CatalogVersion;catalog(id)[unique=true];version[unique=true];active;languages(isoCode)
;$contentCatalog;Staged;false;$languages
;$contentCatalog;Online;true;$languages
