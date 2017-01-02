# ImpEx for Importing Email Content

# Macros / Replacement Parameter definitions
$contentCatalog=__CONTENT_CATALOG_NAME__

$contentCV=catalogVersion(CatalogVersion.catalog(Catalog.id[default=$contentCatalog]),CatalogVersion.version[default=Staged])[default=$contentCatalog:Staged]

# CMS Image Components
INSERT_UPDATE CMSImageComponent;$contentCV[unique=true];uid[unique=true];name

# Content Slots
INSERT_UPDATE ContentSlot;$contentCV[unique=true];uid[unique=true];cmsComponents(uid,$contentCV)
