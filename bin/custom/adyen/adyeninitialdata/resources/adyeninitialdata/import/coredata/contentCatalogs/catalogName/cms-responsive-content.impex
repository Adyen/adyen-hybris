#
# Import the CMS content for the site
#
$contentCatalog=__CONTENT_CATALOG_NAME__
$contentCV=catalogVersion(CatalogVersion.catalog(Catalog.id[default=$contentCatalog]),CatalogVersion.version[default=Staged])[default=$contentCatalog:Staged]

# Import config properties into impex macros
UPDATE GenericItem[processor=de.hybris.platform.commerceservices.impex.impl.ConfigPropertyImportProcessor];pk[unique=true]
$jarResourceCms=$config-jarResourceCmsValue

################################################################################################################################################
########## These templates are simply to display the un-implemented responsive pages that inherit from the Account page 	####################
########## Ideally all sub account pages would use the accountPageTemplate, but, as these pages are not implemented with  	####################
########## components, new templates directing to the appropriate jsps must be defined. Do not use these as templates.		####################
################################################################################################################################################

# Preview Image for use in the CMS Cockpit for special ContentPages
INSERT_UPDATE Media;$contentCV[unique=true];code[unique=true];mime;realfilename;@media[translator=de.hybris.platform.impex.jalo.media.MediaDataTranslator][forceWrite=true]
;;ContentPageModel__function_preview;text/gif;ContentPageModel__function_preview.gif;$jarResourceCms/preview-images/ContentPageModel__function_preview.gif

# Create PageTemplates
# These define the layout for pages
# "FrontendTemplateName" is used to define the JSP that should be used to render the page for pages with multiple possible layouts.
# "RestrictedPageTypes" is used to restrict templates to page types
INSERT_UPDATE PageTemplate;$contentCV[unique=true];uid[unique=true];name;frontendTemplateName;restrictedPageTypes(code);active[default=true]
;;AccountProfilePageTemplate;Profile Page Template;account/accountProfilePage;ContentPage
;;AccountUpdatePasswordPageTemplate;Update Password Page Template;account/accountChangePasswordPage;ContentPage
;;AccountAddressBookPageTemplate;Address Book Page Template;account/accountAddressBookPage;ContentPage
;;AccountEditAddressPageTemplate;Edit Address Page Template;account/accountEditAddressPage;ContentPage
;;AccountPaymentDetailsPageTemplate;Payment Details Page Template;account/accountPaymentInfoPage;ContentPage
;;AccountOrderDetailsPageTemplate;Order Details Page Template;account/accountOrderDetailPage;ContentPage
;;AccountOrderHistoryPageTemplate;Order History Page Template;account/accountOrderHistoryPage;ContentPage
;;AccountUpdateEmailPageTemplate;Update Email Page Template;account/accountProfileEmailEditPage;ContentPage
;;AccountUpdateProfilePageTemplate;Update Profile Page Template;account/accountProfileEditPage;ContentPage


####################  START Content Slot Names ####################

# Profile Page Template
# Template used for Profile page
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='AccountProfilePageTemplate'];validComponentTypes(code);compTypeGroup(code)
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

# Update Password Page Template
# Template used for Update Password page
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='AccountAddressBookPageTemplate'];validComponentTypes(code);compTypeGroup(code)
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

# Address Book Page Template
# Template used for Address Book page
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='AccountUpdatePasswordPageTemplate'];validComponentTypes(code);compTypeGroup(code)
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

# Edit Address Page Template
# Template used for Edit Address page
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='AccountEditAddressPageTemplate'];validComponentTypes(code);compTypeGroup(code)
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

# Payment Info Page Template
# Template used for Payment Info page
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='AccountPaymentDetailsPageTemplate'];validComponentTypes(code);compTypeGroup(code)
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

# Order Detail Page Template
# Template used for Order Detail page
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='AccountOrderDetailsPageTemplate'];validComponentTypes(code);compTypeGroup(code)
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

# Order History Page Template
# Template used for Order History page
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='AccountOrderHistoryPageTemplate'];validComponentTypes(code);compTypeGroup(code)
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

# Order History Page Template
# Template used for Order History page
INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='AccountUpdateEmailPageTemplate'];validComponentTypes(code);compTypeGroup(code)
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
#################### END Content Slot Names				####################

####################  START ContentSlotsForTemplates	####################

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='AccountProfilePageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-AccountProfilePage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-AccountProfilePage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-AccountProfilePage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-AccountProfilePage;MiniCart;;MiniCartSlot;true
;;Footer-AccountProfilePage;Footer;;FooterSlot;true
;;HeaderLinks-AccountProfilePage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-AccountProfilePage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-AccountProfilePage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-AccountProfilePage;BottomHeaderSlot;;BottomHeaderSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='AccountUpdatePasswordPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-AccountUpdatePassword;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-AccountUpdatePassword;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-AccountUpdatePassword;NavigationBar;;NavigationBarSlot;true
;;MiniCart-AccountUpdatePassword;MiniCart;;MiniCartSlot;true
;;Footer-AccountUpdatePassword;Footer;;FooterSlot;true
;;HeaderLinks-AccountUpdatePassword;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-AccountUpdatePassword;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-AccountUpdatePassword;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-AccountUpdatePassword;BottomHeaderSlot;;BottomHeaderSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='AccountAddressBookPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-AccountAddressBookPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-AccountAddressBookPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-AccountAddressBookPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-AccountAddressBookPage;MiniCart;;MiniCartSlot;true
;;Footer-AccountAddressBookPage;Footer;;FooterSlot;true
;;HeaderLinks-AccountAddressBookPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-AccountAddressBookPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-AccountAddressBookPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-AccountAddressBookPage;BottomHeaderSlot;;BottomHeaderSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='AccountEditAddressPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-AccountEditAddressPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-AccountEditAddressPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-AccountEditAddressPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-AccountEditAddressPage;MiniCart;;MiniCartSlot;true
;;Footer-AccountEditAddressPage;Footer;;FooterSlot;true
;;HeaderLinks-AccountEditAddressPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-AccountEditAddressPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-AccountEditAddressPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-AccountEditAddressPage;BottomHeaderSlot;;BottomHeaderSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='AccountPaymentDetailsPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-AccountPaymentDetailsPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-AccountPaymentDetailsPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-AccountPaymentDetailsPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-AccountPaymentDetailsPage;MiniCart;;MiniCartSlot;true
;;Footer-AccountPaymentDetailsPage;Footer;;FooterSlot;true
;;HeaderLinks-AccountPaymentDetailsPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-AccountPaymentDetailsPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-AccountPaymentDetailsPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-AccountPaymentDetailsPage;BottomHeaderSlot;;BottomHeaderSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='AccountOrderDetailsPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-AccountOrderDetailsPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-AccountOrderDetailsPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-AccountOrderDetailsPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-AccountOrderDetailsPage;MiniCart;;MiniCartSlot;true
;;Footer-AccountOrderDetailsPage;Footer;;FooterSlot;true
;;HeaderLinks-AccountOrderDetailsPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-AccountOrderDetailsPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-AccountOrderDetailsPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-AccountOrderDetailsPage;BottomHeaderSlot;;BottomHeaderSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='AccountOrderHistoryPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-AccountOrderHistoryPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-AccountOrderHistoryPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-AccountOrderHistoryPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-AccountOrderHistoryPage;MiniCart;;MiniCartSlot;true
;;Footer-AccountOrderHistoryPage;Footer;;FooterSlot;true
;;HeaderLinks-AccountOrderHistoryPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-AccountOrderHistoryPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-AccountOrderHistoryPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-AccountOrderHistoryPage;BottomHeaderSlot;;BottomHeaderSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='AccountUpdateEmailPageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-AccountUpdateEmailPage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-AccountUpdateEmailPage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-AccountUpdateEmailPage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-AccountUpdateEmailPage;MiniCart;;MiniCartSlot;true
;;Footer-AccountUpdateEmailPage;Footer;;FooterSlot;true
;;HeaderLinks-AccountUpdateEmailPage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-AccountUpdateEmailPage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-AccountUpdateEmailPage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-AccountUpdateEmailPage;BottomHeaderSlot;;BottomHeaderSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='AccountUpdateProfilePageTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-AccountUpdateProfilePage;SiteLogo;;SiteLogoSlot;true
;;HomepageLink-AccountUpdateProfilePage;HomepageNavLink;;HomepageNavLinkSlot;true
;;NavigationBar-AccountUpdateProfilePage;NavigationBar;;NavigationBarSlot;true
;;MiniCart-AccountUpdateProfilePage;MiniCart;;MiniCartSlot;true
;;Footer-AccountUpdateProfilePage;Footer;;FooterSlot;true
;;HeaderLinks-AccountUpdateProfilePage;HeaderLinks;;HeaderLinksSlot;true
;;SearchBox-AccountUpdateProfilePage;SearchBox;;SearchBoxSlot;true
;;TopHeaderSlot-AccountUpdateProfilePage;TopHeaderSlot;;TopHeaderSlot;true
;;BottomHeaderSlot-AccountUpdateProfilePage;BottomHeaderSlot;;BottomHeaderSlot;true


####################  END ContentSlotsForTemplates	####################

## Update static pages with new temporary templates
# Functional Content Pages
INSERT_UPDATE ContentPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);label;defaultPage[default='true'];approvalStatus(code)[default='approved'];homepage[default='false'];previewImage(code, $contentCV)[default='ContentPageModel__function_preview']
;;profile;Profile Page;AccountProfilePageTemplate;profile
;;updatePassword;Update Forgotten Password Page;AccountUpdatePasswordPageTemplate;updatePassword
;;address-book;Address Book Page;AccountAddressBookPageTemplate;address-book
;;add-edit-address;Add Edit Address Page;AccountEditAddressPageTemplate;add-edit-address
;;payment-details;Payment Details Page;AccountPaymentDetailsPageTemplate;payment-details
;;order;Order Details Page;AccountOrderDetailsPageTemplate;order
;;orders;Order History Page;AccountOrderHistoryPageTemplate;orders
;;update-email;Update Email Page;AccountUpdateEmailPageTemplate;update-email
;;update-profile;Update Profile Page;AccountUpdateProfilePageTemplate;update-profile

################################################################################################################################################
########## END OF RESPONSIVE TEMPORARY STATIC TEMPLATE DEFINITIONS 															####################
################################################################################################################################################
