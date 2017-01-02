# ImpEx for Importing CMS Content
# Macros / Replacement Parameter definitions
$contentCatalog=__CONTENT_CATALOG_NAME__
$contentCV=catalogVersion(CatalogVersion.catalog(Catalog.id[default=$contentCatalog]),CatalogVersion.version[default=Staged])[default=$contentCatalog:Staged]

INSERT_UPDATE SearchBoxComponent;$contentCV[unique=true];uid[unique=true];name;&componentRef

INSERT_UPDATE ContentSlot;$contentCV[unique=true];uid[unique=true];cmsComponents(&componentRef)

INSERT_UPDATE FooterComponent;$contentCV[unique=true];uid[unique=true];wrapAfter;showLanguageCurrency