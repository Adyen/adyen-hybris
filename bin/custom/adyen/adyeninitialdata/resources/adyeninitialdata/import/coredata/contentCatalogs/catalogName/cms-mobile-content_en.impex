#
# Import CMS content for the site
#

$contentCatalog=__CONTENT_CATALOG_NAME__
$contentCV=catalogVersion(CatalogVersion.catalog(Catalog.id[default=$contentCatalog]),CatalogVersion.version[default=Staged])[default=$contentCatalog:Staged]

# Language
$lang=en

# Content Pages
UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];title[lang=$lang]
 ;;mobile-account;"My Account"
 ;;mobile-add-edit-address;"Add/Edit Address"
 ;;mobile-address-book;"Address Book"
 ;;mobile-cartPage;"Your Shopping Cart"
 ;;mobile-checkout-login;"Proceed to Checkout"
 ;;mobile-checkout-register;"Registration"
 ;;mobile-faq;"Frequently Asked Questions"
 ;;mobile-homepage;"Homepage"
 ;;mobile-login;"Login"
 ;;mobile-multiStepCheckoutSummaryPage;"Checkout"
 ;;mobile-notFound;"Not Found"
 ;;mobile-order;"Order Details"
 ;;mobile-orderConfirmationPage;"Order Confirmation"
 ;;mobile-orders;"Order History"
 ;;mobile-payment-details;"Payment Details"
 ;;mobile-profile;"Profile"
 ;;mobile-register;"Register"
 ;;mobile-searchEmpty;"No Results"
 ;;mobile-storefinderPage;"StoreFinder"
 ;;mobile-termsAndConditions;"Terms and Conditions"
 ;;mobile-updatePassword;"Update Forgotten Password"
