#
# Import the CMS content for the site
#
$contentCatalog=__CONTENT_CATALOG_NAME__
$contentCV=catalogVersion(CatalogVersion.catalog(Catalog.id[default=$contentCatalog]),CatalogVersion.version[default=Staged])[default=$contentCatalog:Staged]

# Import config properties into impex macros
UPDATE GenericItem[processor=de.hybris.platform.commerceservices.impex.impl.ConfigPropertyImportProcessor];pk[unique=true]
$jarResourceCms=$config-jarResourceCmsValue

# Create Mobile PageTemplates
# These define the layout for pages
# "FrontendTemplateName" is used to define the JSP that should be used to render the page for pages with multiple possible layouts.
# "RestrictedPageTypes" is used to restrict templates to page types
INSERT_UPDATE PageTemplate;$contentCV[unique=true];uid[unique=true];name;frontendTemplateName;restrictedPageTypes(code);active[default=true]
;;MobileProductDetailsPageTemplate;Mobile Product Details Page Template;product/productLayout2Page;ProductPage
;;MobileProductListPageTemplate;Mobile Product List Page Template;category/productListPage;CategoryPage
;;MobileProductListDynamicPageTemplate;Mobile Product List Dynamic Page Template;category/productListDynamicPage;CategoryPage
;;MobileProductGridPageTemplate;Mobile Product Grid Page Template;category/productGridPage;CategoryPage
;;MobileProductGridDynamicPageTemplate;Mobile Product Grid Dynamic Page Template;category/productGridDynamicPage;CategoryPage
;;MobileSearchResultsListPageTemplate;Mobile Search Results List Page Template;search/searchListPage;ContentPage
;;MobileSearchResultsGridPageTemplate;Mobile Search Results Grid Page Template;search/searchGridPage;ContentPage
;;MobileSearchResultsEmptyPageTemplate;Mobile Search Results Empty Page Template;search/searchEmptyPage;ContentPage
;;MobileCategoryPageTemplate;Mobile Category Page Template;category/categoryPage;CategoryPage
;;MobileContentPage1Template;Mobile Content Page 1 Template;layout/contentLayout1Page;ContentPage
;;MobileLandingPageTemplate;Mobile Landing Page Template;layout/landingLayoutPage;CategoryPage,ContentPage
;;MobileCategoryGridPageTemplate;Mobile Category Grid Page Template;category/categoryGridPage;CategoryPage
;;MobileAccountPageTemplate;Mobile Account Page Template;account/accountLayoutPage;ContentPage
;;MobileRegisterPageTemplate;Mobile Register Page Template;account/accountRegisterPage;ContentPage
;;MobileCheckoutRegisterPageTemplate;Mobile Checkout Register Page Template;checkout/checkoutRegisterPage;ContentPage

# Templates without a frontendTemplateName
;;MobileCartPageTemplate;Mobile Cart Page Template;;ContentPage;false;
;;MobileLoginPageTemplate;Mobile Login Page Template;;ContentPage;false;
;;MobileCheckoutLoginPageTemplate;Mobile Checkout Login Page Template;;ContentPage;false;
;;MobileMultiStepCheckoutSummaryPageTemplate;Mobile Multi Step Checkout Summary Page Template;;ContentPage;false;
;;MobileOrderConfirmationPageTemplate;Mobile Order Confirmation Page Template;;ContentPage;false;
;;MobileStoreFinderPageTemplate;Mobile Store Finder Page Template;storeFinder/storeFinderSearchPage;ContentPage;false;
;;MobileErrorPageTemplate;Mobile Error Page Template;;ContentPage;false;

# Add Velocity templates that are in the CMS Cockpit. These give a better layout for editing pages
# The FileLoaderValueTranslator loads a File into a String property. The templates could also be inserted in-line in this file.
UPDATE PageTemplate;$contentCV[unique=true];uid[unique=true];velocityTemplate[translator=de.hybris.platform.commerceservices.impex.impl.FileLoaderValueTranslator]
;;MobileProductDetailsPageTemplate;$jarResourceCms/structure-view/mobile/structure_productDetails2PageTemplate.vm
;;MobileProductListPageTemplate   ;$jarResourceCms/structure-view/mobile/structure_productListPageTemplate.vm
;;MobileProductListDynamicPageTemplate   ;$jarResourceCms/structure-view/mobile/structure_productListDynamicPageTemplate.vm
;;MobileProductGridPageTemplate   ;$jarResourceCms/structure-view/mobile/structure_productGridPageTemplate.vm
;;MobileProductGridDynamicPageTemplate   ;$jarResourceCms/structure-view/mobile/structure_productGridDynamicPageTemplate.vm
;;MobileCategoryPageTemplate      ;$jarResourceCms/structure-view/mobile/structure_categoryPageTemplate.vm
;;MobileLandingPageTemplate      ;$jarResourceCms/structure-view/mobile/structure_landingPageTemplate.vm
;;MobileContentPage1Template      ;$jarResourceCms/structure-view/mobile/structure_contentPage1Template.vm
;;MobileSearchResultsListPageTemplate ;$jarResourceCms/structure-view/mobile/structure_searchResultsListPageTemplate.vm
;;MobileSearchResultsGridPageTemplate ;$jarResourceCms/structure-view/mobile/structure_searchResultsGridPageTemplate.vm
;;MobileCartPageTemplate 		    ;$jarResourceCms/structure-view/mobile/structure_cartPageTemplate.vm
;;MobileAccountPageTemplate 		;$jarResourceCms/structure-view/mobile/structure_accountPageTemplate.vm
;;MobileStoreFinderPageTemplate	;$jarResourceCms/structure-view/mobile/structure_storefinderSearchTemplate.vm
;;MobileErrorPageTemplate			;$jarResourceCms/structure-view/mobile/structure_errorPageTemplate.vm
;;MobileSearchResultsEmptyPageTemplate;$jarResourceCms/structure-view/mobile/structure_errorPageTemplate.vm
;;MobileMultiStepCheckoutSummaryPageTemplate   ;$jarResourceCms/structure-view/mobile/structure_multiStepCheckoutSummaryPageTemplate.vm
;;MobileOrderConfirmationPageTemplate;$jarResourceCms/structure-view/mobile/structure_orderConfirmationPageTemplate.vm
;;MobileLoginPageTemplate         ;$jarResourceCms/structure-view/mobile/structure_loginPageTemplate.vm
;;MobileCheckoutLoginPageTemplate         ;$jarResourceCms/structure-view/mobile/structure_checkoutLoginPageTemplate.vm
;;MobileRegisterPageTemplate      ;$jarResourceCms/structure-view/mobile/structure_registerPageTemplate.vm
;;MobileCheckoutRegisterPageTemplate      ;$jarResourceCms/structure-view/mobile/structure_checkoutRegisterPageTemplate.vm
;;MobileCategoryGridPageTemplate   ;$jarResourceCms/structure-view/mobile/structure_productGridDynamicPageTemplate.vm

# Create ContentSlotNames
# Each PageTemplate has a number of ContentSlotNames, with a list of valid components for the slot.
# There are a standard set of slots and a number of specific slots for each template.

# Error Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileErrorPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopContent;;;mobile
;MiddleContent;;CMSParagraphComponent
;BottomContent;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Product Details Page Template
# The CrossSelling and UpSelling slots are designed for related products, cross-sells and up-sells.
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileProductDetailsPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;Section1;;;mobile
;Section2;;;mobile
;Section3;;;mobile
;Section4;;;mobile
;CrossSelling;;ProductReferencesComponent;mobile
;UpSelling;;ProductReferencesComponent;mobile
;AddToCart;;ProductAddToCartComponent;mobile
;VariantSelector;;ProductVariantSelectorComponent;mobile
;Footer;;;footer
;Tabs;;CMSTabParagraphContainer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Product List / Grid are two layouts for Category Browsing pages
# Each layout can be paginated or loaded dynamically

# Product List Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileProductListPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;ProductListSlot;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Product List Dynamic Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileProductListDynamicPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;Section1;;;mobile
;Section2;;;mobile
;Section3;;;mobile
;Section4;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Product Grid Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileProductGridPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;ProductGridSlot;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Category Grid Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileCategoryGridPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;Section1;;;mobile
;Section2;;;mobile
;Section3;;;mobile
;Section4;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Product Grid Dynamic Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileProductGridDynamicPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;Section1;;;mobile
;Section2;;;mobile
;Section3;;;mobile
;Section4;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Search Result List / Grid are two layouts for Search Result pages
# Search Empty is displayed when no results are found

# Search Results List Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileSearchResultsListPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;SearchResultsListSlot;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Search Results Grid Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileSearchResultsGridPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;SearchResultsGridSlot;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Search Results Empty Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileSearchResultsEmptyPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopContent;;;mobile
;MiddleContent;;CMSParagraphComponent
;BottomContent;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Category and Landing Pages are various layouts for a Category Landing page
# Landing Pages are also good layouts for Homepages or general Content Pages

# Category Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileCategoryPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;Section1;;;mobile
;Section2;;;mobile
;Section3;;;mobile
;Section4;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Landing Page Templates
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileLandingPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;Section1;;;mobile
;Section2;;;mobile
;Section3;;;mobile
;Section4;;;mobile
;Section5;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Simple Content Page Templates
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileContentPage1Template'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;Section1;;;mobile
;Section2A;;;mobile
;Section2B;;;mobile
;Section3;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Cart Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileCartPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopContent;;;mobile
;EmptyCartMiddleContent;;CMSParagraphComponent
;EmptyCartBottomContent;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;TopContentSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Login Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileLoginPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopContentSlot;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Checkout Login Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileCheckoutLoginPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopContentSlot;;;mobile
;BottomContentSlot;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Register Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileRegisterPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopRegisterSlot;;;mobile
;BottomContentSlot;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Checkout Register Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileCheckoutRegisterPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopRegisterSlot;;;mobile
;BottomContentSlot;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile


# Multi Step Checkout Summary Page Templates
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileMultiStepCheckoutSummaryPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopContent;;;mobile
;BottomContent;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Order Confirmation Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileOrderConfirmationPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopContent;;;mobile
;BottomContent;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Account Page Template
# Template used for all of the Account pages
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileAccountPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopContent;;;mobile
;BottomContent;;;mobile
;BodyContent;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Store Finder/Locator Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MobileStoreFinderPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;SearchBox;;;searchbox
;NavigationBar;;;navigation
;NavigationMenuBar;;;navigation
;TopContent;;;mobile
;BottomContent;;;mobile
;Footer;;;footer
;NavContent;;FooterComponent;navigation
;TopHeaderSlot;;;mobile
;NavigationSlot;;;mobile
;BottomHeaderSlot;;;mobile
;MiddleContentSlot;;;mobile
;PlaceholderContentSlot;;;mobile

# Create ContentSlots
INSERT_UPDATE ContentSlot;$contentCV[unique=true];uid[unique=true];name;active
;;MobileSiteLogoSlot;Mobile Default Site Logo Slot;true
;;MobileSearchBoxSlot;Mobile Search Box Slot;true
;;MobileHomepageNavLinkSlot;Mobile Default Homepage Link;true
;;MobileNavigationBarSlot;Mobile Navigation Bar;true
;;MobileNavigationMenuBarSlot;Mobile Navigation Menu Bar;true
;;MobileTabsSlot;Mobile Tabs;true
;;MobileTopContentSlot;Mobile Top Content;true
;;MobileBottomContentSlot;Mobile Bottom Content;true
;;MobileSideContentSlot;Mobile Side Content;true
;;MobileFeatureContentSlot;Mobile Feature Content;true
;;MobileFooterSlot;Mobile Footer;true
;;MobileUpSellingSlot;Mobile UpSelling Merchandising Area;true
;;MobileAddToCartSlot;Mobile Add To Cart;true
;;MobileVariantSelectorSlot;Mobile Variant Selector;true
;;MobileCrossSellingSlot;Mobile Cross Selling Merchandising Area;true
;;MobileNavContentSlot;Mobile Nav Content;true
;;MobileSuggestionSlot;Mobile Suggestions;true
;;MobileTopHeaderSlot;Mobile Top Header;true
;;MobileNavigationSlot;Mobile Navigation;true
;;MobileBottomHeaderSlot;Mobile Bottom Header;true
;;MobileProductListSlot;Mobile Product List;true
;;MobileProductGridSlot;Mobile Product Grid;true
;;MobileSearchResultsListSlot;Mobile Search Results List;true
;;MobileSearchResultsGridSlot;Mobile Search Results Grid;true
;;MobileMiddleContentSlot;Mobile Middle Content;true
;;MobileEmptyCartMiddleContent;Mobile Empty Cart Middle Content;true
;;MobileEmptyCartBottomContent;Mobile Empty Cart Bottom Content;true
;;MobileTopRegisterSlot;Mobile Top Register;true
;;MobilePlaceholderContentSlot;Mobile Placeholder for Addon tag files;true

# Bind Content Slots to Page Templates

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position;pageTemplate(uid,$contentCV)[default='MobileProductDetailsPageTemplate'];contentSlot(uid,$contentCV);allowOverwrite
;;MobileSiteLogo-ProductDetails;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-ProductDetails;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-ProductDetails;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-ProductDetails;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-ProductDetails;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileTabs-ProductDetails;Tabs;;MobileTabsSlot;true
;;MobileFooter-ProductDetails;Footer;;MobileFooterSlot;true
;;MobileUpSelling-ProductDetails;UpSelling;;MobileUpSellingSlot;true
;;MobileAddToCart-ProductDetails;AddToCart;;MobileAddToCartSlot;true
;;MobileVariantSelector-ProductDetails;VariantSelector;;MobileVariantSelectorSlot;true
;;MobileCrossSelling-ProductDetails;CrossSelling;;MobileCrossSellingSlot;true
;;MobileNavContent-ProductDetails;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-ProductDetails;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-ProductDetails;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-ProductDetails;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-ProductDetails;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position;pageTemplate(uid,$contentCV)[default='MobileSearchResultsListPageTemplate'];contentSlot(uid,$contentCV);allowOverwrite
;;MobileSiteLogo-SearchResultsList;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-SearchResultsList;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-SearchResultsList;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-SearchResultsList;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-SearchResultsList;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-SearchResultsList;Footer;;MobileFooterSlot;true
;;MobileNavContent-SearchResultsList;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-SearchResultsList;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-SearchResultsList;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-SearchResultsList;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobileSearchResultsListSlot-SearchResultsList;SearchResultsListSlot;;MobileSearchResultsListSlot;true
;;MobilePlaceholderContentSlot-SearchResultsList;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true


INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position;pageTemplate(uid,$contentCV)[default='MobileSearchResultsGridPageTemplate'];contentSlot(uid,$contentCV);allowOverwrite
;;MobileSiteLogo-SearchResultsGrid;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-SearchResultsGrid;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-SearchResultsGrid;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-SearchResultsGrid;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-SearchResultsGrid;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-SearchResultsGrid;Footer;;MobileFooterSlot;true
;;MobileNavContent-SearchResultsGrid;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-SeachResultsGrid;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-SeachResultsGrid;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-SeachResultsGrid;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobileSearchResultsGridSlot-SearchResultsGrid;SearchResultsGridSlot;;MobileSearchResultsGridSlot;true
;;MobilePlaceholderContentSlot-SearchResultsGrid;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position;pageTemplate(uid,$contentCV)[default='MobileSearchResultsEmptyPageTemplate'];contentSlot(uid,$contentCV);allowOverwrite
;;MobileSiteLogo-SearchResultsEmpty;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-SearchResultsEmpty;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-SearchResultsEmpty;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-SearchResultsEmpty;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-SearchResultsEmpty;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-SearchResultsEmpty;Footer;;MobileFooterSlot;true
;;MobileNavContent-SearchResultsEmpty;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-SearchResultsEmpty;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-SearchResultsEmpty;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-SearchResultsEmpty;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-SearchResultsEmpty;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position;pageTemplate(uid,$contentCV)[default='MobileCategoryPageTemplate'];contentSlot(uid,$contentCV);allowOverwrite
;;MobileSiteLogo-CategoryPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-CategoryPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-CategoryPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-CategoryPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-CategoryPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-CategoryPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-CategoryPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-CategoryPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-CategoryPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-CategoryPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-CategoryPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileLandingPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-LandingPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-LandingPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-LandingPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-LandingPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-LandingPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-LandingPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-LandingPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-LandingPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-LandingPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-LandingPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-LandingPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileContentPage1Template'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-ContentPage1;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-ContentPage1;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-ContentPage1;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-ContentPage1;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-ContentPage1;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-ContentPage1;Footer;;MobileFooterSlot;true
;;MobileNavContent-ContentPage1;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-ContentPage1;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-ContentPage1;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-ContentPage1;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-ContentPage1;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileProductGridPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-ProductGridPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-ProductGridPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-ProductGridPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-ProductGridPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-ProductGridPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-ProductGridPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-ProductGridPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-ProductGridPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-ProductGridPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-ProductGridPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobileProductGridSlot-ProductGridPage;ProductGridSlot;;MobileProductGridSlot;true
;;MobilePlaceholderContentSlot-ProductGridPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileCategoryGridPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-CategoryGridPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-CategoryGridPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-CategoryGridPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-CategoryGridPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-CategoryGridPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-CategoryGridPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-CategoryGridPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-CategoryGridPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-CategoryGridPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-CategoryGridPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-CategoryGridPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileProductGridDynamicPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-ProductGridDynamicPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-ProductGridDynamicPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-ProductGridDynamicPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-ProductGridDynamicPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-ProductGridDynamicPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-ProductGridDynamicPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-ProductGridDynamicPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-ProductGridDynamicPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-ProductGridDynamicPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-ProductGridDynamicPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-ProductGridDynamicPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileProductListPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-ProductListPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-ProductListPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-ProductListPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-ProductListPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-ProductListPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-ProductListPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-ProductListPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-ProductListPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-ProductListPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-ProductListPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobileProductListSlot-ProductListPage;ProductListSlot;;MobileProductListSlot;true
;;MobilePlaceholderContentSlot-ProductListPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileProductListDynamicPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-ProductListDynamicPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-ProductListDynamicPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-ProductListDynamicPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-ProductListDynamicPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-ProductListDynamicPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-ProductListDynamicPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-ProductListDynamicPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-ProductListDynamicPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-ProductListDynamicPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-ProductListDynamicPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-ProductListDynamicPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileCartPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-CartPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-CartPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-CartPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-CartPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-CartPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-CartPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-CartPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-CartPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-CartPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-CartPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobileTopContentSlot-CartPage;TopContentSlot;;MobileTopContentSlot;true
;;MobileEmptyCartMiddleContent-CartPage;EmptyCartMiddleContent;;MobileEmptyCartMiddleContent;true
;;MobileEmptyCartBottomContent-CartPage;EmptyCartBottomContent;;MobileEmptyCartBottomContent;true
;;MobilePlaceholderContentSlot-CartPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileLoginPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-LoginPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-LoginPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-LoginPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-LoginPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-LoginPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-LoginPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-LoginPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-LoginPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-LoginPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-LoginPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobileTopContentSlot-LoginPage;TopContentSlot;;MobileTopContentSlot;true
;;MobilePlaceholderContentSlot-LoginPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileCheckoutLoginPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-CheckoutLoginPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-CheckoutLoginPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-CheckoutLoginPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-CheckoutLoginPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-CheckoutLoginPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-CheckoutLoginPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-CheckoutLoginPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-CheckoutLoginPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-CheckoutLoginPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-CheckoutLoginPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobileTopContentSlot-CheckoutLoginPage;TopContentSlot;;MobileTopContentSlot;true
;;MobileBottomContentSlot-CheckoutLoginPage;BottomContentSlot;;MobileBottomContentSlot;true
;;MobilePlaceholderContentSlot-CheckoutLoginPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true


INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileRegisterPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-RegisterPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-RegisterPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-RegisterPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-RegisterPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-RegisterPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-RegisterPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-RegisterPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-RegisterPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-RegisterPage;TopHeaderSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-RegisterPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobileTopRegisterSlot-RegisterPage;TopRegisterSlot;;MobileTopRegisterSlot;true
;;MobileBottomContentSlot-RegisterPage;BottomContentSlot;;MobileBottomContentSlot;true
;;MobilePlaceholderContentSlot-RegisterPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true


INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileCheckoutRegisterPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-CheckoutRegisterPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-CheckoutRegisterPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-CheckoutRegisterPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-CheckoutRegisterPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-CheckoutRegisterPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-CheckoutRegisterPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-CheckoutRegisterPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-CheckoutRegisterPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-CheckoutRegisterPage;TopHeaderSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-CheckoutRegisterPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobileTopRegisterSlot-CheckoutRegisterPage;TopRegisterSlot;;MobileTopRegisterSlot;true
;;MobileBottomContentSlot-CheckoutRegisterPage;BottomContentSlot;;MobileBottomContentSlot;true
;;MobilePlaceholderContentSlot-CheckoutRegisterPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true


# Bind Content Slots to Multi Step Checkout Summary Page Templates
INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileMultiStepCheckoutSummaryPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-MultiStepCheckoutSummaryPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-MultiStepCheckoutSummaryPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-MultiStepCheckoutSummaryPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-MultiStepCheckoutSummaryPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-MultiStepCheckoutSummaryPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-MultiStepCheckoutSummaryPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-MultiStepCheckoutSummaryPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-MultiStepCheckoutSummaryPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-MultiStepCheckoutSummaryPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-MultiStepCheckoutSummaryPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-MultiStepCheckoutSummaryPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true


INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileOrderConfirmationPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-OrderConfirmationPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-OrderConfirmationPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-OrderConfirmationPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-OrderConfirmationPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-OrderConfirmationPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-OrderConfirmationPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-OrderConfirmationPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-OrderConfirmationPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-OrderConfirmationPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-OrderConfirmationPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-OrderConfirmationPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileAccountPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-AccountPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-AccountPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-AccountPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-AccountPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-AccountPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-AccountPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-AccountPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-AccountPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-AccountPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-AccountPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-AccountPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileStoreFinderPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-StoreFinderPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-StoreFinderPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-StoreFinderPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-StoreFinderPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-StoreFinderPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-StoreFinderPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-StoreFinderPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-StoreFinderPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-StoreFinderPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-StoreFinderPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobileMiddleContentSlot-StoreFinderPage;MiddleContentSlot;;MobileMiddleContentSlot;true
;;MobilePlaceholderContentSlot-StoreFinderPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true


INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MobileErrorPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;MobileSiteLogo-ErrorPage;SiteLogo;;MobileSiteLogoSlot;true
;;MobileSearchBox-ErrorPage;SearchBox;;MobileSearchBoxSlot;true
;;MobileHomepageLink-ErrorPage;HomepageNavLink;;MobileHomepageNavLinkSlot;true
;;MobileNavigationBar-ErrorPage;NavigationBar;;MobileNavigationBarSlot;true
;;MobileNavigationMenuBar-ErrorPage;NavigationMenuBar;;MobileNavigationMenuBarSlot;true
;;MobileFooter-ErrorPage;Footer;;MobileFooterSlot;true
;;MobileNavContent-ErrorPage;NavContent;;MobileNavContentSlot;true
;;MobileTopHeaderSlot-ErrorPage;TopHeaderSlot;;MobileTopHeaderSlot;true
;;MobileNavigationSlot-ErrorPage;NavigationSlot;;MobileNavigationSlot;true
;;MobileBottomHeaderSlot-ErrorPage;BottomHeaderSlot;;MobileBottomHeaderSlot;true
;;MobilePlaceholderContentSlot-ErrorPage;PlaceholderContentSlot;;MobilePlaceholderContentSlot;true

# All Content Pages have a UiExperienceRestriction
INSERT_UPDATE CMSUiExperienceRestriction;$contentCV[unique=true];uid[unique=true];name;uiExperience(code)
;;MobileExperienceRestriction;Restriction for Mobile Experience;Mobile


# Create Content Pages

# Site-wide Homepage
INSERT_UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);label;defaultPage[default='false'];approvalStatus(code)[default='approved'];restrictions(uid,$contentCV)[default='MobileExperienceRestriction'];onlyOneRestrictionMustApply[default='false']
;;mobile-homepage;Mobile Homepage;MobileLandingPageTemplate;homepage;

# Preview Image for use in the CMS Cockpit for special ContentPages
INSERT_UPDATE Media;$contentCV[unique=true];code[unique=true];mime;realfilename;@media[translator=de.hybris.platform.impex.jalo.media.MediaDataTranslator][forceWrite=true]
;;MobileContentPageModel__function_preview;text/gif;ContentPageModel__function_preview.gif;$jarResourceCms/preview-images/ContentPageModel__function_preview.gif

# Functional Content Pages
INSERT_UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);label;defaultPage[default='false'];approvalStatus(code)[default='approved'];homepage[default='false'];previewImage(code, $contentCV)[default='MobileContentPageModel__function_preview'];restrictions(uid,$contentCV)[default='MobileExperienceRestriction'];onlyOneRestrictionMustApply[default='false']
;;mobile-multiStepCheckoutSummaryPage;Mobile Multi Checkout Summary Page;MobileMultiStepCheckoutSummaryPageTemplate;multiStepCheckoutSummary
;;mobile-orderConfirmationPage;Mobile Order Confirmation Page;MobileOrderConfirmationPageTemplate;orderConfirmation
;;mobile-cartPage;Mobile Cart Page;MobileCartPageTemplate;cart
;;mobile-search;Mobile Search Results Page;MobileSearchResultsListPageTemplate;search
;;mobile-account;Mobile Account Page;MobileAccountPageTemplate;account
;;mobile-profile;Mobile Profile Page;MobileAccountPageTemplate;profile
;;mobile-update-profile;Mobile Update Profile Page;MobileAccountPageTemplate;update-profile
;;mobile-update-email;Mobile Update Email Page;MobileAccountPageTemplate;update-email
;;mobile-address-book;Mobile Address Book Page;MobileAccountPageTemplate;address-book
;;mobile-add-edit-address;Mobile Add Edit Address Page;MobileAccountPageTemplate;add-edit-address
;;mobile-payment-details;Mobile Payment Details Page;MobileAccountPageTemplate;payment-details
;;mobile-order;Mobile Order Details Page;MobileAccountPageTemplate;order
;;mobile-orders;Mobile Order History Page;MobileAccountPageTemplate;orders
;;mobile-storefinderPage;Mobile StoreFinder Page;MobileStoreFinderPageTemplate;storefinder
;;mobile-checkout-login;Mobile Checkout-Login Page;MobileCheckoutLoginPageTemplate;checkout-login
;;mobile-login;Mobile Login Page;MobileLoginPageTemplate;login
;;mobile-register;Mobile Register Page;MobileRegisterPageTemplate;register
;;mobile-checkout-register;Mobile Checkout-Register Page;MobileCheckoutRegisterPageTemplate;checkout-register
;;mobile-notFound;Mobile Not Found Page;MobileErrorPageTemplate;notFound
;;mobile-searchEmpty;Mobile Search Results Empty Page;MobileSearchResultsEmptyPageTemplate;searchEmpty
;;mobile-forgottenPassword;Mobile Forgotten Password Page;MobileLoginPageTemplate;forgottenPassword
;;mobile-updatePassword;Mobile Update Forgotten Password Page;MobileAccountPageTemplate;updatePassword

# Simple Content Pages
INSERT_UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);label;defaultPage[default='false'];approvalStatus(code)[default='approved'];homepage[default='false'];restrictions(uid,$contentCV)[default='MobileExperienceRestriction'];onlyOneRestrictionMustApply[default='false']
;;mobile-faq;Mobile Frequently Asked Questions FAQ Page;MobileContentPage1Template;/faq

INSERT_UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);label;defaultPage[default='false'];approvalStatus(code)[default='approved'];homepage[default='false'];restrictions(uid,$contentCV)[default='MobileExperienceRestriction'];onlyOneRestrictionMustApply[default='false']
;;mobile-termsAndConditions;Mobile Terms and Conditions Page;MobileContentPage1Template;/termsAndConditions

# Product Details Page
INSERT_UPDATE ProductPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);defaultPage[default='false'];approvalStatus(code)[default='approved'];restrictions(uid,$contentCV)[default='MobileExperienceRestriction'];onlyOneRestrictionMustApply[default='false']
;;mobile-productDetails;Mobile Product Details;MobileProductDetailsPageTemplate;

# Category Pages
INSERT_UPDATE CategoryPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);defaultPage[default='false'];approvalStatus(code)[default='approved'];restrictions(uid,$contentCV);onlyOneRestrictionMustApply[default='false']
;;mobile-productList;Mobile Product List;MobileProductListPageTemplate;false;approved;MobileExperienceRestriction
;;mobile-productListDynamic;Mobile Product List Dynamic;MobileProductListDynamicPageTemplate;false;approved;
;;mobile-productGrid;Mobile Product Grid;MobileProductGridPageTemplate;false;approved;
;;mobile-productGridDynamic;MobileProduct Grid Dynamic;MobileProductGridDynamicPageTemplate;false;approved;
;;mobile-category;Mobile Default Category Page;MobileCategoryPageTemplate;false;approved;
