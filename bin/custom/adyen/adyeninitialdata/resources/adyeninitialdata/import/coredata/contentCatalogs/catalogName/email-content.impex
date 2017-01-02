#
# Import the CMS content for the site emails
#
$contentCatalog=__CONTENT_CATALOG_NAME__
$contentCV=catalogVersion(CatalogVersion.catalog(Catalog.id[default=$contentCatalog]),CatalogVersion.version[default=Staged])[default=$contentCatalog:Staged]
$wideContent=CMSImageComponent,BannerComponent

# Import config properties into impex macros
UPDATE GenericItem[processor=de.hybris.platform.commerceservices.impex.impl.ConfigPropertyImportProcessor];pk[unique=true]
$jarResourceCms=$config-jarResourceCmsValue

# Email page Template
INSERT_UPDATE EmailPageTemplate;$contentCV[unique=true];uid[unique=true];name;active;frontendTemplateName;subject(code);htmlTemplate(code);restrictedPageTypes(code)

# Templates for CMS Cockpit Page Edit
UPDATE EmailPageTemplate;$contentCV[unique=true];uid[unique=true];velocityTemplate[translator=de.hybris.platform.commerceservices.impex.impl.FileLoaderValueTranslator]

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='CustomerRegistrationEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='ForgottenPasswordEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='OrderConfirmationEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='OrderCancelledEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='OrderRefundEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='DeliverySentEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='ReadyForPickupEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='OrderCollectionReminderEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='OrderMoveToCsEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='OrderPartiallyCanceledEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='OrderPartiallyRefundedEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

INSERT_UPDATE ContentSlotName;name[unique=true];template(uid,$contentCV)[unique=true][default='NotPickedUpConsignmentCanceledEmailTemplate'];validComponentTypes(code)
;SiteLogo;;;logo
;TopContent;;$wideContent;
;BottomContent;;$wideContent;

# Create Content Slots
INSERT_UPDATE ContentSlot;$contentCV[unique=true];uid[unique=true];name;active
;;EmailTopSlot;Default Email Top Slot;true
;;EmailBottomSlot;Default Email Bottom Slot;true
;;EmailSiteLogoSlot;Default Email Site Slot;true

# Bind Content Slots to Email Page Templates
INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='CustomerRegistrationEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-CustomerRegistrationEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-CustomerRegistrationEmail;TopContent;;EmailTopSlot;true
;;BottomContent-CustomerRegistrationEmail;BottomContent;;EmailBottomSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='ForgottenPasswordEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-ForgottenPasswordEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-ForgottenPasswordEmail;TopContent;;EmailTopSlot;true
;;BottomContent-ForgottenPasswordEmail;BottomContent;;EmailBottomSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='OrderConfirmationEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-OrderConfirmationEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-OrderConfirmationEmail;TopContent;;EmailTopSlot;true
;;BottomContent-OrderConfirmationEmail;BottomContent;;EmailBottomSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='OrderCancelledEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-OrderCancelledEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-OrderCancelledEmail;TopContent;;EmailTopSlot;true
;;BottomContent-OrderCancelledEmail;BottomContent;;EmailBottomSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='OrderRefundEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-OrderRefundEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-OrderRefundEmail;TopContent;;EmailTopSlot;true
;;BottomContent-OrderRefundEmail;BottomContent;;EmailBottomSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='DeliverySentEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-DeliverySentEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-DeliverySentEmail;TopContent;;EmailTopSlot;true
;;BottomContent-DeliverySentEmail;BottomContent;;EmailBottomSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='ReadyForPickupEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-ReadyForPickupEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-ReadyForPickupEmail;TopContent;;EmailTopSlot;true
;;BottomContent-ReadyForPickupEmail;BottomContent;;EmailBottomSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='OrderPartiallyCanceledEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-OrderPartiallyCanceledEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-OrderPartiallyCanceledEmail;TopContent;;EmailTopSlot;true
;;BottomContent-OrderPartiallyCanceledEmail;BottomContent;;EmailBottomSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='OrderPartiallyRefundedEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-OrderPartiallyRefundedEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-OrderPartiallyRefundedEmail;TopContent;;EmailTopSlot;true
;;BottomContent-OrderPartiallyRefundedEmail;BottomContent;;EmailBottomSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='NotPickedUpConsignmentCanceledEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-NotPickedUpConsignmentCanceledEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-NotPickedUpConsignmentCanceledEmail;TopContent;;EmailTopSlot;true
;;BottomContent-NotPickedUpConsignmentCanceledEmail;BottomContent;;EmailBottomSlot;true

INSERT_UPDATE ContentSlotForTemplate;$contentCV[unique=true];uid[unique=true];position[unique=true];pageTemplate(uid,$contentCV)[unique=true][default='OrderCollectionReminderEmailTemplate'];contentSlot(uid,$contentCV)[unique=true];allowOverwrite
;;SiteLogo-OrderCollectionReminderEmail;SiteLogo;;EmailSiteLogoSlot;true
;;TopContent-OrderCollectionReminderEmail;TopContent;;EmailTopSlot;true
;;BottomContent-OrderCollectionReminderEmail;BottomContent;;EmailBottomSlot;true

# Email Pages
INSERT_UPDATE EmailPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);defaultPage;approvalStatus(code)[default='approved']

# CMS components velocity templates
INSERT_UPDATE RendererTemplate;code[unique=true];contextClass;rendererType(code)[default='velocity']

# Email velocity templates
INSERT_UPDATE RendererTemplate;code[unique=true];contextClass;rendererType(code)[default='velocity']

# Preview Image for use in the CMS Cockpit
INSERT_UPDATE Media;$contentCV[unique=true];code[unique=true];mime;realfilename;@media[translator=de.hybris.platform.impex.jalo.media.MediaDataTranslator][forceWrite=true]
;;EmailPageModel_preview;text/gif;EmailPageModel_preview.gif;$jarResourceCms/preview-images/EmailPageModel_preview.gif

UPDATE EmailPage;$contentCV[unique=true];uid[unique=true];previewImage(code, $contentCV)
