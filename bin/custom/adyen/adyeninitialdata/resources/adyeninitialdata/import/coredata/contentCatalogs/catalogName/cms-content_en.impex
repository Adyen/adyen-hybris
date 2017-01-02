#
# Import CMS content for the site
#

$contentCatalog=__CONTENT_CATALOG_NAME__
$contentCV=catalogVersion(CatalogVersion.catalog(Catalog.id[default=$contentCatalog]),CatalogVersion.version[default=Staged])[default=$contentCatalog:Staged]

# Language
$lang=en

# Content Pages
UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];title[lang=$lang]
 ;;account;"My Account"
 ;;add-edit-address;"Add/Edit Address"
 ;;address-book;"Address Book"
 ;;cartPage;"Your Shopping Cart"
 ;;checkout-login;"Proceed to Checkout"
 ;;faq;"Frequently Asked Questions"
 ;;homepage;"Homepage"
 ;;login;"Login"
 ;;multiStepCheckoutSummaryPage;"Checkout"
 ;;notFound;"Not Found"
 ;;order;"Order Details"
 ;;orderConfirmationPage;"Order Confirmation"
 ;;orders;"Order History"
 ;;payment-details;"Payment Details"
 ;;profile;"Profile"
 ;;searchEmpty;"No Results"
 ;;storefinderPage;"StoreFinder"
 ;;termsAndConditions;"Terms and Conditions"
 ;;updatePassword;"Update Forgotten Password"
