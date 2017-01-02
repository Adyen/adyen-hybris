#
# Import the Solr configuration for the store
#
$productCatalog=__PRODUCT_CATALOG_NAME__
$catalogVersions=catalogVersions(catalog(id),version);
$serverConfigName=
$indexConfigName=
$searchConfigName=
$facetSearchConfigName=
$facetSearchConfigDescription=
$searchIndexNamePrefix=
$solrIndexedType=
$indexBaseSite=
$indexLanguages=ja,en,de,zh
$indexCurrencies=JPY,USD


#
# Setup the Solr server, indexer, and search configs
#

# Create the solr server configuration
INSERT_UPDATE SolrServerConfig;name[unique=true];mode(code);embeddedMaster

# Create the solr indexer configuration
INSERT_UPDATE SolrIndexConfig;name[unique=true];batchSize;numberOfThreads;indexMode(code);

# Create the faceted search configuration
INSERT_UPDATE SolrSearchConfig;description[unique=true];pageSize

#
# Setup the indexed types, their properties, and the update queries
#

# Declare the indexed type Product
INSERT_UPDATE SolrIndexedType;identifier[unique=true];type(code);variant;sorts(&sortRefID)

INSERT_UPDATE SolrFacetSearchConfig;name[unique=true];description;indexNamePrefix;languages(isocode);currencies(isocode);solrServerConfig(name);solrSearchConfig(description);solrIndexConfig(name);solrIndexedTypes(identifier);enabledLanguageFallbackMechanism;$catalogVersions

UPDATE BaseSite;uid[unique=true];solrFacetSearchConfiguration(name)

# Define price range set
INSERT_UPDATE SolrValueRangeSet;name[unique=true];qualifier;type;solrValueRanges(&rangeValueRefID)

# Define price ranges
INSERT_UPDATE SolrValueRange;&rangeValueRefID;solrValueRangeSet(name)[unique=true];name[unique=true];from;to

# Non-facet properties
INSERT_UPDATE SolrIndexedProperty;solrIndexedType(identifier)[unique=true];name[unique=true];type(code);sortableType(code);currency[default=false];localized[default=false];multiValue[default=false];useForSpellchecking[default=false];useForAutocomplete[default=false];fieldValueProvider;valueProviderParameter

# Category fields
INSERT_UPDATE SolrIndexedProperty;solrIndexedType(identifier)[unique=true];name[unique=true];type(code);localized[default=false];multiValue[default=true];categoryField[default=true];useForSpellchecking[default=false];useForAutocomplete[default=false];fieldValueProvider

# Category facets
INSERT_UPDATE SolrIndexedProperty;solrIndexedType(identifier)[unique=true];name[unique=true];type(code);multiValue[default=true];facet[default=true];facetType(code);facetSort(code);priority;visible;categoryField[default=true];fieldValueProvider;facetDisplayNameProvider;topValuesProvider

# Other facet properties
INSERT_UPDATE SolrIndexedProperty;solrIndexedType(identifier)[unique=true];name[unique=true];type(code);sortableType(code);currency[default=false];localized[default=false];multiValue[default=false];facet[default=true];facetType(code);facetSort(code);priority;visible;useForSpellchecking[default=false];useForAutocomplete[default=false];fieldValueProvider;facetDisplayNameProvider;customFacetSortProvider;topValuesProvider;rangeSets(name)

# Create the queries that will be used to extract data for Solr
INSERT_UPDATE SolrIndexerQuery;solrIndexedType(identifier)[unique=true];identifier[unique=true];type(code);injectCurrentDate[default=true];injectCurrentTime[default=true];injectLastIndexTime[default=true];query;user(uid)

# Define the available sorts
INSERT_UPDATE SolrSort;&sortRefID;indexedType(identifier)[unique=true];code[unique=true];useBoost

# Define the sort fields
INSERT_UPDATE SolrSortField;sort(indexedType(identifier),code)[unique=true];fieldName[unique=true];ascending[unique=true]
