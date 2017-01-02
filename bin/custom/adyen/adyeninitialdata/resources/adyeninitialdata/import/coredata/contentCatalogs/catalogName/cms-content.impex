#
# Import the CMS content for the site
#
$contentCatalog=__CONTENT_CATALOG_NAME__
$contentCV=catalogVersion(CatalogVersion.catalog(Catalog.id[default=$contentCatalog]),CatalogVersion.version[default=Staged])[default=$contentCatalog:Staged]

# Import config properties into impex macros
UPDATE GenericItem[processor=de.hybris.platform.commerceservices.impex.impl.ConfigPropertyImportProcessor];pk[unique=true]
$jarResourceCms=$config-jarResourceCmsValue

# Create PageTemplates
# These define the layout for pages
# "FrontendTemplateName" is used to define the JSP that should be used to render the page for pages with multiple possible layouts.
# "RestrictedPageTypes" is used to restrict templates to page types
INSERT_UPDATE PageTemplate;$contentCV[unique=true];uid[unique=true];name;frontendTemplateName;restrictedPageTypes(code);active[default=true]
;;ProductDetailsPageTemplate;Product Details Page Template;product/productLayout2Page;ProductPage
;;ProductListPageTemplate;Product List Page Template;category/productListPage;CategoryPage
;;ProductGridPageTemplate;Product Grid Page Template;category/productGridPage;CategoryPage
;;SearchResultsListPageTemplate;Search Results List Page Template;search/searchListPage;ContentPage
;;SearchResultsGridPageTemplate;Search Results Grid Page Template;search/searchGridPage;ContentPage
;;SearchResultsEmptyPageTemplate;Search Results Empty Page Template;search/searchEmptyPage;ContentPage
;;CategoryPageTemplate;Category Page Template;category/categoryPage;CategoryPage
;;LandingPage1Template;Landing Page 1 Template;layout/landingLayout1Page;CategoryPage,ContentPage
;;LandingPage2Template;Landing Page 2 Template;layout/landingLayout2Page;CategoryPage,ContentPage
;;LandingPage3Template;Landing Page 3 Template;layout/landingLayout3Page;CategoryPage,ContentPage
;;LandingPage4Template;Landing Page 4 Template;layout/landingLayout4Page;CategoryPage,ContentPage
;;LandingPage5Template;Landing Page 5 Template;layout/landingLayout5Page;CategoryPage,ContentPage
;;LandingPage6Template;Landing Page 6 Template;layout/landingLayout6Page;CategoryPage,ContentPage
;;ContentPage1Template;Content Page 1 Template;layout/contentLayout1Page;ContentPage
;;AccountPageTemplate;Account Page Template;account/accountLayoutPage;ContentPage;false;
;;StoreFinderPageTemplate;Store Finder Page Template;storeFinder/storeFinderSearchPage;ContentPage;false;

# Templates without a frontendTemplateName
;;CartPageTemplate;Cart Page Template;;ContentPage;false;
;;LoginPageTemplate;Login Page Template;;ContentPage;false;
;;CheckoutLoginPageTemplate;Checkout Login Page Template;;ContentPage;false;
;;MultiStepCheckoutSummaryPageTemplate;Multi Step Checkout Summary Page Template;;ContentPage;false;
;;OrderConfirmationPageTemplate;Order Confirmation Page Template;;ContentPage;false;
;;ErrorPageTemplate;Error Page Template;;ContentPage;false;

# Add Velocity templates that are in the CMS Cockpit. These give a better layout for editing pages
# The FileLoaderValueTranslator loads a File into a String property. The templates could also be inserted in-line in this file.
UPDATE PageTemplate;$contentCV[unique=true];uid[unique=true];velocityTemplate[translator=de.hybris.platform.commerceservices.impex.impl.FileLoaderValueTranslator]
;;ProductDetailsPageTemplate;$jarResourceCms/structure-view/structure_productDetails2PageTemplate.vm
;;ProductListPageTemplate   ;$jarResourceCms/structure-view/structure_productListPageTemplate.vm
;;ProductGridPageTemplate   ;$jarResourceCms/structure-view/structure_productGridPageTemplate.vm
;;CategoryPageTemplate      ;$jarResourceCms/structure-view/structure_categoryPageTemplate.vm
;;LandingPage1Template      ;$jarResourceCms/structure-view/structure_landingPage1Template.vm
;;LandingPage2Template      ;$jarResourceCms/structure-view/structure_landingPage2Template.vm
;;LandingPage3Template      ;$jarResourceCms/structure-view/structure_landingPage3Template.vm
;;LandingPage4Template      ;$jarResourceCms/structure-view/structure_landingPage4Template.vm
;;LandingPage5Template      ;$jarResourceCms/structure-view/structure_landingPage5Template.vm
;;LandingPage6Template      ;$jarResourceCms/structure-view/structure_landingPage6Template.vm
;;ContentPage1Template      ;$jarResourceCms/structure-view/structure_contentPage1Template.vm
;;SearchResultsListPageTemplate ;$jarResourceCms/structure-view/structure_searchResultsPageTemplate.vm
;;SearchResultsGridPageTemplate ;$jarResourceCms/structure-view/structure_searchResultsPageTemplate.vm
;;CartPageTemplate 		    ;$jarResourceCms/structure-view/structure_cartPageTemplate.vm
;;AccountPageTemplate 		;$jarResourceCms/structure-view/structure_accountPageTemplate.vm
;;StoreFinderPageTemplate	;$jarResourceCms/structure-view/structure_storefinderSearchTemplate.vm
;;ErrorPageTemplate			;$jarResourceCms/structure-view/structure_errorPageTemplate.vm
;;SearchResultsEmptyPageTemplate;$jarResourceCms/structure-view/structure_errorPageTemplate.vm
;;MultiStepCheckoutSummaryPageTemplate   ;$jarResourceCms/structure-view/structure_multiStepCheckoutSummaryPageTemplate.vm
;;OrderConfirmationPageTemplate;$jarResourceCms/structure-view/structure_orderConfirmationPageTemplate.vm
;;LoginPageTemplate         ;$jarResourceCms/structure-view/structure_loginPageTemplate.vm
;;CheckoutLoginPageTemplate ;$jarResourceCms/structure-view/structure_checkoutLoginPageTemplate.vm

# Create ContentSlotNames
# Each PageTemplate has a number of ContentSlotNames, with a list of valid components for the slot.
# There are a standard set of slots and a number of specific slots for each template.
# Standard slots are SiteLogo, HeaderLinks, MiniCart and NavigationBar (that all appear in the Header), and the Footer. 

# Error Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='ErrorPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;MiddleContent;;CMSParagraphComponent
;BottomContent;;;wide
;SideContent;;;narrow
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Product Details Page Template
# The CrossSelling and UpSelling slots are designed for related products, cross-sells and up-sells.
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='ProductDetailsPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;Section1;;;wide
;Section2;;;wide
;Section3;;;wide
;Section4;;;wide
;VariantSelector;;ProductVariantSelectorComponent;narrow
;AddToCart;;ProductAddToCartComponent;narrow
;CrossSelling;;ProductReferencesComponent;narrow
;UpSelling;;ProductReferencesComponent;narrow
;Footer;;;footer
;Tabs;;CMSTabParagraphContainer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Product List / Grid are two layouts for Category Browsing pages

# Product List Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='ProductListPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;ProductLeftRefinements;;;narrow
;ProductListSlot;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Product Grid Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='ProductGridPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;ProductLeftRefinements;;;narrow
;ProductGridSlot;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Search Result List / Grid are two layouts for Search Result pages
# Search Empty is displayed when no results are found

# Search Results List Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='SearchResultsListPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;ProductLeftRefinements;;;narrow
;SearchResultListSlot;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Search Results Grid Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='SearchResultsGridPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;ProductLeftRefinements;;;narrow
;SearchResultGridSlot;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Search Results Empty Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='SearchResultsEmptyPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;MiddleContent;;CMSParagraphComponent
;BottomContent;;;wide
;SideContent;;;narrow
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Category and Landing Pages are various layouts for a Category Landing page
# Landing Pages are also good layouts for Homepages or general Content Pages

# Category Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='CategoryPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;Section1;;;wide
;Section2;;;wide
;Section3;;;narrow
;Section4;;;narrow
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Landing Page Templates
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='LandingPage1Template'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;Section1;;;wide
;Section2A;;;wide
;Section2B;;;narrow
;Section2C;;;narrow
;Section3;;;wide
;Section4;;;narrow
;Section5;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='LandingPage2Template'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;Section1;;;wide
;Section2A;;;narrow
;Section2B;;;narrow
;Section2C;;;wide
;Section3;;;wide
;Section4;;;narrow
;Section5;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='LandingPage3Template'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;Section1;;;wide
;Section2A;;;wide
;Section2B;;;narrow
;Section2C;;;narrow
;Section3;;;wide
;Section4;;;narrow
;Section5;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='LandingPage4Template'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;Section1;;;wide
;Section2A;;;narrow
;Section2B;;;narrow
;Section2C;;;wide
;Section3;;;wide
;Section4;;;narrow
;Section5;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='LandingPage5Template'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;Section1;;;wide
;Section2;;;wide
;Section3;;;narrow
;Section4;;;narrow
;Section5;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='LandingPage6Template'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;Section1;;;wide
;Section2;;;wide
;Section3;;;narrow
;Section4;;;narrow
;Section5;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Simple Content Page Templates
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='ContentPage1Template'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;Section1;;;wide
;Section2A;;;narrow
;Section2B;;;wide
;Section3;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Cart Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='CartPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;TopContent;;;wide
;EmptyCartMiddleContent;;CMSParagraphComponent
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;CenterLeftContentSlot;;;narrow
;CenterRightContentSlot;;;narrow
;BottomContentSlot;;;wide
;PlaceholderContentSlot;;;

# Login Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='LoginPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;LeftContentSlot;;;wide
;RightContentSlot;;;wide
;PlaceholderContentSlot;;;

# Checkout Login Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='CheckoutLoginPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;TopContent;;;wide
;BottomContent;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;LeftContentSlot;;;wide
;RightContentSlot;;;wide
;CenterContentSlot;;;wide
;PlaceholderContentSlot;;;

# Multi Step Checkout Summary Page Templates
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='MultiStepCheckoutSummaryPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;SideContent;;;narrow
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Order Confirmation Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='OrderConfirmationPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;SideContent;;;narrow
;TopContent;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Account Page Template
# Template used for all of the Account pages
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='AccountPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;TopContent;;;wide
;SideContent;;;narrow
;BodyContent;;;wide
;BottomContent;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Store Finder/Locator Page Template
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='StoreFinderPageTemplate'];validComponentTypes(code);compTypeGroup(code)
;SiteLogo;;;logo
;HeaderLinks;;;headerlinks
;SearchBox;;;searchbox
;MiniCart;;;minicart
;NavigationBar;;;navigation
;TopContent;;;wide
;SideContent;;;narrow
;MiddleContentSlot;;;wide
;Footer;;;footer
;TopHeaderSlot;;;wide
;BottomHeaderSlot;;;wide
;PlaceholderContentSlot;;;

# Create Content Slots
INSERT_UPDATE ContentSlot;$contentCV[unique=true];uid[unique=true];name;active
;;SiteLogoSlot;Default Site Logo Slot;true
;;HomepageNavLinkSlot;Default Homepage Link;true
;;MiniCartSlot;Mini Cart;true
;;NavigationBarSlot;Navigation Bar;true
;;TabsSlot;Tabs;true
;;TopContentSlot;Top Content;true
;;SideContentSlot;Side Content;true
;;BottomContentSlot;Bottom Content;true
;;FeatureContentSlot;Feature Content;true
;;FooterSlot;Footer;true
;;HeaderLinksSlot;Header links;true
;;SearchBoxSlot;Search Box;true
;;VariantSelectorSlot;Variant Selector;true
;;AddToCartSlot;Add To Cart;true
;;UpSellingSlot;Up Selling;true
;;CrossSellingSlot;Cross Selling;true
;;TopHeaderSlot;Top Header;true
;;BottomHeaderSlot;Bottom Header;true
;;ProductLeftRefinements;Refinements;true
;;ProductGridSlot;Product List;true
;;ProductListSlot;Product Grid;true
;;SearchResultsListSlot;Search Result List;true
;;SearchResultsGridSlot;Search Result Grid;true
;;MiddleContentSlot;Middle Content Slot;true
;;LeftContentSlot;Left Content Slot;true
;;RightContentSlot;Right Content Slot;true
;;CenterContentSlot;Center Content Slot;true
;;CenterLeftContentSlot;Center Left Content Slot;true
;;CenterRightContentSlot;Center Right Content Slot;true
;;EmptyCartMiddleContent;Empty CartMiddle Content Slot;true
;;PlaceholderContentSlot;Placeholder for Addon tag files;true

# Bind Content Slots to Page Templates
INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='ProductDetailsPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-ProductDetails;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-ProductDetails;HomepageNavLink;;HomepageNavLinkSlot;true
;;MiniCart-ProductDetails;MiniCart;;MiniCartSlot;true
;;NavigationBar-ProductDetails;NavigationBar;;NavigationBarSlot;true
;;Tabs-ProductDetails;Tabs;;TabsSlot;true
;;Footer-ProductDetails;Footer;;FooterSlot;true
;;HeaderLinks-ProductDetails;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-ProductDetails;SearchBox;;SearchBoxSlot;true
;;VariantSelector-ProductDetails;VariantSelector;;VariantSelectorSlot;true
;;AddToCart-ProductDetails;AddToCart;;AddToCartSlot;true
;;UpSelling-ProductDetails;UpSelling;;UpSellingSlot;true
;;CrossSelling-ProductDetails;CrossSelling;;CrossSellingSlot;true
;;TopHeaderSlot-ProductDetails;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-ProductDetails;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-ProductDetails;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='SearchResultsListPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-SearchResultsList;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-SearchResultsList;HomepageNavLink;;HomepageNavLinkSlot;true
;;MiniCart-SearchResultsList;MiniCart;;MiniCartSlot;true
;;NavigationBar-SearchResultsList;NavigationBar;;NavigationBarSlot;true
;;Footer-SearchResultsList;Footer;;FooterSlot;true
;;HeaderLinks-SearchResultsList;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-SearchResultsList;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-SearchResultsList;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-SearchResultsList;BottomHeaderSlot;;BottomHeaderSlot;true
;;ProductLeftRefinements-SearchResultsList;ProductLeftRefinements;;ProductLeftRefinements;true
;;SearchResultsListSlot-SearchResultsList;SearchResultsListSlot;;SearchResultsListSlot;true
;;PlaceholderContentSlot-SearchResultsList;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='SearchResultsGridPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-SearchResultsGrid;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-SearchResultsGrid;HomepageNavLink;;HomepageNavLinkSlot;true
;;MiniCart-SearchResultsGrid;MiniCart;;MiniCartSlot;true
;;NavigationBar-SearchResultsGrid;NavigationBar;;NavigationBarSlot;true
;;Footer-SearchResultsGrid;Footer;;FooterSlot;true
;;HeaderLinks-SearchResultsGrid;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-SearchResultsGrid;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-SearchResultsGrid;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-SearchResultsGrid;BottomHeaderSlot;;BottomHeaderSlot;true
;;ProductLeftRefinements-SearchResultsGrid;ProductLeftRefinements;;ProductLeftRefinements;true
;;SearchResultsGridSlot-SearchResultsGrid;SearchResultsGridSlot;;SearchResultsGridSlot;true
;;PlaceholderContentSlot-SearchResultsGrid;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='SearchResultsEmptyPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-SearchResultsEmpty;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-SearchResultsEmpty;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-SearchResultsEmpty;NavigationBar;;NavigationBarSlot;true
;;MiniCart-SearchResultsEmpty;MiniCart;;MiniCartSlot;true
;;Footer-SearchResultsEmpty;Footer;;FooterSlot;true
;;HeaderLinks-SearchResultsEmpty;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-SearchResultsEmpty;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-SearchResultsEmpty;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-SearchResultsEmpty;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-SearchResultsEmpty;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='CategoryPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-CategoryPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-CategoryPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;MiniCart-CategoryPage;MiniCart;;MiniCartSlot;true
;;NavigationBar-CategoryPage;NavigationBar;;NavigationBarSlot;true
;;Footer-CategoryPage;Footer;;FooterSlot;true
;;HeaderLinks-CategoryPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-CategoryPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-CategoryPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-CategoryPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-CategoryPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='LandingPage1Template'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-LandingPage1;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-LandingPage1;HomepageNavLink;;HomepageNavLinkSlot;true
;;MiniCart-LandingPage1;MiniCart;;MiniCartSlot;true
;;NavigationBar-LandingPage1;NavigationBar;;NavigationBarSlot;true
;;Footer-LandingPage1;Footer;;FooterSlot;true
;;HeaderLinks-LandingPage1;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-LandingPage1;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-LandingPage1;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-LandingPage1;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-LandingPage1;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='LandingPage2Template'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-LandingPage2;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-LandingPage2;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-LandingPage2;NavigationBar;;NavigationBarSlot;true
;;MiniCart-LandingPage2;MiniCart;;MiniCartSlot;true
;;Footer-LandingPage2;Footer;;FooterSlot;true
;;HeaderLinks-LandingPage2;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-LandingPage2;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-LandingPage2;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-LandingPage2;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-LandingPage2;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='LandingPage3Template'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-LandingPage3;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-LandingPage3;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-LandingPage3;NavigationBar;;NavigationBarSlot;true
;;MiniCart-LandingPage3;MiniCart;;MiniCartSlot;true
;;Footer-LandingPage3;Footer;;FooterSlot;true
;;HeaderLinks-LandingPage3;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-LandingPage3;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-LandingPage3;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-LandingPage3;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-LandingPage3;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='LandingPage4Template'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-LandingPage4;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-LandingPage4;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-LandingPage4;NavigationBar;;NavigationBarSlot;true
;;MiniCart-LandingPage4;MiniCart;;MiniCartSlot;true
;;Footer-LandingPage4;Footer;;FooterSlot;true
;;HeaderLinks-LandingPage4;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-LandingPage4;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-LandingPage4;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-LandingPage4;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-LandingPage4;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='LandingPage5Template'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-LandingPage5;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-LandingPage5;HomepageNavLink;;HomepageNavLinkSlot;true 
;;NavigationBar-LandingPage5;NavigationBar;;NavigationBarSlot;true
;;MiniCart-LandingPage5;MiniCart;;MiniCartSlot;true
;;Footer-LandingPage5;Footer;;FooterSlot;true
;;HeaderLinks-LandingPage5;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-LandingPage5;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-LandingPage5;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-LandingPage5;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-LandingPage5;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='LandingPage6Template'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-LandingPage6;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-LandingPage6;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-LandingPage6;NavigationBar;;NavigationBarSlot;true
;;MiniCart-LandingPage6;MiniCart;;MiniCartSlot;true
;;Footer-LandingPage6;Footer;;FooterSlot;true
;;HeaderLinks-LandingPage6;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-LandingPage6;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-LandingPage6;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-LandingPage6;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-LandingPage6;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='ContentPage1Template'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-ContentPage1;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-ContentPage1;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-ContentPage1;NavigationBar;;NavigationBarSlot;true
;;MiniCart-ContentPage1;MiniCart;;MiniCartSlot;true
;;Footer-ContentPage1;Footer;;FooterSlot;true
;;HeaderLinks-ContentPage1;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-ContentPage1;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-ContentPage1;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-ContentPage1;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-ContentPage1;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='ProductGridPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-ProductGridPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-ProductGridPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;MiniCart-ProductGridPage;MiniCart;;MiniCartSlot;true
;;NavigationBar-ProductGridPage;NavigationBar;;NavigationBarSlot;true
;;Footer-ProductGridPage;Footer;;FooterSlot;true
;;HeaderLinks-ProductGridPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-ProductGridPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-ProductGridPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-ProductGridPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;ProductLeftRefinemnts-ProductGridPage;ProductLeftRefinements;;ProductLeftRefinements;true
;;ProductGridSlot-ProductGridPage;ProductGridSlot;;ProductGridSlot;true
;;PlaceholderContentSlot-ProductGridPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='ProductListPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-ProductListPage;SiteLogo;ProductListPageTemplate;SiteLogoSlot;true
;;HomepageLink-ProductListPage;HomepageNavLink;ProductListPageTemplate;HomepageNavLinkSlot;true
;;MiniCart-ProductListPage;MiniCart;ProductListPageTemplate;MiniCartSlot;true
;;NavigationBar-ProductListPage;NavigationBar;ProductListPageTemplate;NavigationBarSlot;true
;;Footer-ProductListPage;Footer;ProductListPageTemplate;FooterSlot;true
;;HeaderLinks-ProductListPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-ProductListPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-ProductListPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-ProductListPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;ProductLeftRefinemnts-ProductListPage;ProductLeftRefinements;;ProductLeftRefinements;true
;;ProductListSlot-ProductListPage;ProductListSlot;;ProductListSlot;true
;;PlaceholderContentSlot-ProductListPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='CartPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-CartPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-CartPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-CartPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-CartPage;MiniCart;;MiniCartSlot;true
;;Footer-CartPage;Footer;;FooterSlot;true
;;HeaderLinks-CartPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-CartPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-CartPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-CartPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;BottomContentSlot-CartPage;BottomContentSlot;;BottomContentSlot;true
;;CenterRightContentSlot-CartPage;CenterRightContentSlot;;CenterRightContentSlot;true
;;CenterLeftContentSlot-CartPage;CenterLeftContentSlot;;CenterLeftContentSlot;true
;;EmptyCartMiddleContent-CartPage;EmptyCartMiddleContent;;EmptyCartMiddleContent;true
;;PlaceholderContentSlot-CartPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='LoginPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-LoginPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-LoginPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-LoginPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-LoginPage;MiniCart;;MiniCartSlot;true
;;Footer-LoginPage;Footer;;FooterSlot;true
;;HeaderLinks-LoginPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-LoginPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-LoginPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-LoginPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;LeftContentSlot-LoginPage;LeftContentSlot;;LeftContentSlot;true
;;RightContentSlot-LoginPage;RightContentSlot;;RightContentSlot;true
;;PlaceholderContentSlot-LoginPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='CheckoutLoginPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-CheckoutLoginPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-CheckoutLoginPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-CheckoutLoginPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-CheckoutLoginPage;MiniCart;;MiniCartSlot;true
;;Footer-CheckoutLoginPage;Footer;;FooterSlot;true
;;HeaderLinks-CheckoutLoginPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-CheckoutLoginPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-CheckoutLoginPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-CheckoutLoginPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;LeftContentSlot-CheckoutLoginPage;LeftContentSlot;;LeftContentSlot;true
;;RightContentSlot-CheckoutLoginPage;RightContentSlot;;RightContentSlot;true
;;CenterContentSlot-CheckoutLoginPage;CenterContentSlot;;CenterContentSlot;true
;;PlaceholderContentSlot-CheckoutLoginPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

# Bind Content Slots to Multi Step Checkout Summary Page Templates
INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='MultiStepCheckoutSummaryPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-MultiStepCheckoutSummaryPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-MultiStepCheckoutSummaryPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-MultiStepCheckoutSummaryPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-MultiStepCheckoutSummaryPage;MiniCart;;MiniCartSlot;true
;;Footer-MultiStepCheckoutSummaryPage;Footer;;FooterSlot;true
;;HeaderLinks-MultiStepCheckoutSummaryPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-MultiStepCheckoutSummaryPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-MultiStepCheckoutSummaryPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-MultiStepCheckoutSummaryPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-MultiStepCheckoutSummaryPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='OrderConfirmationPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-OrderConfirmationPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-OrderConfirmationPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-OrderConfirmationPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-OrderConfirmationPage;MiniCart;;MiniCartSlot;true
;;Footer-OrderConfirmationPage;Footer;;FooterSlot;true
;;HeaderLinks-OrderConfirmationPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-OrderConfirmationPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-OrderConfirmationPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-OrderConfirmationPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-OrderConfirmationPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='AccountPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-AccountPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-AccountPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-AccountPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-AccountPage;MiniCart;;MiniCartSlot;true
;;Footer-AccountPage;Footer;;FooterSlot;true
;;HeaderLinks-AccountPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-AccountPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-AccountPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-AccountPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-AccountPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='StoreFinderPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-StoreFinderPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-StoreFinderPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-StoreFinderPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-StoreFinderPage;MiniCart;;MiniCartSlot;true
;;Footer-StoreFinderPage;Footer;;FooterSlot;true
;;HeaderLinks-StoreFinderPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-StoreFinderPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-StoreFinderPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-StoreFinderPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;MiddleContentSlot-StoreFinderPage;MiddleContentSlot;;MiddleContentSlot;true
;;PlaceholderContentSlot-StoreFinderPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='ErrorPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-ErrorPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-ErrorPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-ErrorPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-ErrorPage;MiniCart;;MiniCartSlot;true
;;Footer-ErrorPage;Footer;;FooterSlot;true
;;HeaderLinks-ErrorPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-ErrorPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-ErrorPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-ErrorPage;BottomHeaderSlot;;BottomHeaderSlot;true
;;PlaceholderContentSlot-ErrorPage;PlaceholderContentSlot;;PlaceholderContentSlot;true

# Create Content Pages

# Site-wide Homepage
INSERT_UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);label;defaultPage[default='true'];approvalStatus(code)[default='approved'];homepage[default='true']
;;homepage;Homepage;LandingPage2Template;homepage

# Preview Image for use in the CMS Cockpit for special ContentPages
INSERT_UPDATE Media;$contentCV[unique=true];code[unique=true];mime;realfilename;@media[translator=de.hybris.platform.impex.jalo.media.MediaDataTranslator][forceWrite=true]
;;ContentPageModel__function_preview;text/gif;ContentPageModel__function_preview.gif;$jarResourceCms/preview-images/ContentPageModel__function_preview.gif

# Functional Content Pages
INSERT_UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);label;defaultPage[default='true'];approvalStatus(code)[default='approved'];homepage[default='false'];previewImage(code, $contentCV)[default='ContentPageModel__function_preview']
;;multiStepCheckoutSummaryPage;Multi Checkout Summary Page;MultiStepCheckoutSummaryPageTemplate;multiStepCheckoutSummary
;;orderConfirmationPage;Order Confirmation Page;OrderConfirmationPageTemplate;orderConfirmation
;;cartPage;Cart Page;CartPageTemplate;cart
;;search;Search Results Page;SearchResultsGridPageTemplate;search
;;account;Account Page;AccountPageTemplate;account
;;profile;Profile Page;AccountPageTemplate;profile
;;address-book;Address Book Page;AccountPageTemplate;address-book
;;add-edit-address;Add Edit Address Page;AccountPageTemplate;add-edit-address
;;payment-details;Payment Details Page;AccountPageTemplate;payment-details
;;order;Order Details Page;AccountPageTemplate;order
;;orders;Order History Page;AccountPageTemplate;orders
;;storefinderPage;StoreFinder Page;StoreFinderPageTemplate;storefinder
;;checkout-login;Checkout-Login Page;CheckoutLoginPageTemplate;checkout-login
;;login;Login Page;LoginPageTemplate;login
;;notFound;Not Found Page;ErrorPageTemplate;notFound
;;searchEmpty;Search Results Empty Page;SearchResultsEmptyPageTemplate;searchEmpty
;;updatePassword;Update Forgotten Password Page;AccountPageTemplate;updatePassword
;;update-profile;Update Profile Page;AccountPageTemplate;update-profile
;;update-email;Update Email Page;AccountPageTemplate;update-email

# Simple Content Pages
INSERT_UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);label;defaultPage[default='true'];approvalStatus(code)[default='approved'];homepage[default='false']
;;faq;Frequently Asked Questions FAQ Page;ContentPage1Template;/faq

INSERT_UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);label;defaultPage[default='true'];approvalStatus(code)[default='approved'];homepage[default='false']
;;orderExpired;Order Expired Page;ContentPage1Template;/orderExpired

INSERT_UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);label;defaultPage[default='true'];approvalStatus(code)[default='approved'];homepage[default='false']
;;termsAndConditions;Terms and Conditions Page;ContentPage1Template;/termsAndConditions

# Product Details Page
INSERT_UPDATE ProductPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);defaultPage;approvalStatus(code)[default='approved']
;;productDetails;Product Details;ProductDetailsPageTemplate;true;

# Category Pages
INSERT_UPDATE CategoryPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);defaultPage;approvalStatus(code)[default='approved']
;;productList;Product List;ProductListPageTemplate;true;
;;productGrid;Product Grid;ProductGridPageTemplate;false;
;;category;Default Category Page;CategoryPageTemplate;false;
