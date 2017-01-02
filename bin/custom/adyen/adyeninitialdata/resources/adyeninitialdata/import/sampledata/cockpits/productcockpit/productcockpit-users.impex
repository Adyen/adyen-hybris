#
# Import Product Cockpit Users
#

$passwordEncoding=md5
$defaultPassword=1234
$setPassword=@password[translator=de.hybris.platform.impex.jalo.translators.ConvertPlaintextToEncodedUserPasswordTranslator][default='$passwordEncoding:$defaultPassword']


INSERT_UPDATE Employee;UID[unique=true];$setPassword;description;name;groups(uid);sessionLanguage(isocode);sessionCurrency(isocode)

UPDATE CatalogVersion;catalog(id)[unique=true];version[unique=true];writePrincipals(uid)[default=productmanagergroup];
