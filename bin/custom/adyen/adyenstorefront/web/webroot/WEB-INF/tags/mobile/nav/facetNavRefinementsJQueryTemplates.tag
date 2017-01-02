<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/mobile/common/footer"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<c:url value="/store-finder" var="searchUserLocationUrl"/>
<c:url value="/store-finder/position" var="autoUserLocationUrl"/>
<c:set value="5" var="initialLimit" scope="session"/>

<script type="text/javascript">
	var searchUserLocationUrl = '${searchUserLocationUrl}';
	var autoUserLocationUrl = '${autoUserLocationUrl}';
	var userLocation = "${userLocation.searchTerm}";
	var longitude = "${userLocation.point.longitude}";
	var latitude = "${userLocation.point.latitude}";
	var showStoreLimit = Number("${initialLimit}");
</script>

<template:mobilePage pageId="facetRefinements-page" dataSearchQuery="${pageData.currentQuery.query.value}">
	<jsp:attribute name="header">
		<h3 data-role="heading">
			<spring:theme code="search.nav.refinements"/>
		</h3>
		<a href="#" data-role="button" id="applyFilter" data-icon="check" data-theme="c" class="ui-btn-right"><spring:theme code="search.nav.done.button"/></a>
	</jsp:attribute>
	<jsp:attribute name="footer">
		<footer:simpleFooter/>
	</jsp:attribute>
	<jsp:body>
		<nav:facetNavRefinements pageData="${pageData}"/>
	</jsp:body>
</template:mobilePage>
<script id="refinementFacetPageTemplate" type="text/x-jquery-tmpl">
	<div id="{{= $data.name}}-page"
		class="{{if $data.multiSelect}}multiSelectFacetPage{{else}}facetPage{{/if}} refinementFacetPageHeader"
		data-role="page"
		data-theme="d"
		data-url="<c:url value="/"/>">
		<div data-role="header" data-position="fixed" class="ui-bar" data-position="inline">
			<a href="#" class="backToFacets" data-role="button" data-rel="back" data-icon="arrow-l"
				data-transition="slideIn" data-theme="a">
				<spring:theme code="search.nav.refine.button"/>
			</a>
			<h3><spring:theme code="search.nav.refinements"/></h3>
			<a href="#" data-role="button" id="applyFilter" rel="external" data-icon="check" data-theme="c">
				<spring:theme code="search.nav.done.button"/>
			</a>
		</div>
		{{tmpl($data) "#refinementFacetContentTemplate"}}
	</div>
</script>
<script id="refinementFacetContentTemplate" type="text/x-jquery-tmpl">
	{{if $data.code === "availableInStores"}}
		<div class="accmob-storeSearch accmob-storeSearch-filter">
			<div class="item_container_holder accmob-storeSearch-filter-headline">
			{{if userLocation}}
				{{if userLocation.length}}
					<h3><spring:theme code="storeFinder.stores.nearto" arguments="{{= userLocation }}"/><h3>
				{{else}}
					<h3><spring:theme code="storeFinder.stores.nearby"/></h3>
				{{/if}}
				<a href="#" id="changeLocationLink"><spring:theme code="search.nav.changeLocation"/></a>
				<div data-role="content" data-theme="d" class="ui-li-has-count-checkbox">
					<fieldset data-role="controlgroup" class="facetValueList" data-facet="{{= $data.name}}">
						{{each(index, store) $data.values}}
							{{if index < showStoreLimit}}
								<input type="checkbox" data-theme="d" data-query="{{= $data.code}}:{{= code}}" id="f{{= code.replace(/\W/g,"")}}" {{if selected}} checked="true" {{/if}} />
								<label for="f{{= code.replace(/\W/g,"")}}">{{= name}} {{if count>0}}<span class="ui-li-count ui-btn-up-c ui-btn-corner-all">{{= count}}</span>{{/if}}</label>
							{{/if}}
						{{/each}}
					</fieldset>
				</div>
			{{else}}
				<h3><spring:theme code="storeFinder.stores.nearby"/></h3>
				<p><spring:theme code="text.storefinder.mobile.page.description"/></p>
				<form id="mobileSearchLocationUrl" name="mobileSearchLocationForm" method="GET" action="${searchUserLocationUrl}">
					<div class="accmob-storeFinderFieldHolder">
						<label for="storelocator-query" class="skip"><spring:theme code="storeFinder.stores.nearby"/></label>
						<input id="storelocator-query" name="q" class="storeSearchBox" data-type="search" placeholder="<spring:theme code='storelocator.postcode.city.search'/>">
						<div class="accmob-storeSearch-trigger">
							<button class="form search" id="user_location_query_button" data-role="button" data-theme="d">
								<span class="search-icon"> <spring:theme code="storeFinder.search" /> </span>
							</button>
						</div>
					</div>
				</form>
				<div class="line-text clearfix"><span><spring:theme code="storeFinder.line.text"/></span></div>
				<form:form id="mobileAutoLocationForm" name="mobileAutoLocationForm" method="POST" action="${autoUserLocationUrl}">
					<input type="hidden" id="latitude" name="latitude"/>
					<input type="hidden" id="longitude" name="longitude"/>
					<a href="#" id="findStoresNearMeButton" class="form search" data-theme="d" data-role="button" data-icon="custom ui-icon-custom-storesearch">
						<span class="search-icon"><spring:theme code="storeFinder.findStoresNearMe"/></span>
					</a>
				</form:form>
			{{/if}}
		</div>
	{{else $data.multiSelect}}
		<div data-role="content" data-theme="d" class="ui-li-has-count-checkbox">
			<fieldset data-role="controlgroup" class="facetValueList" data-facet="{{= $data.name}}">
				{{each $data.values}}
					<input type="checkbox" data-theme="d" data-query="{{= $data.code}}:{{= code}}" id="f{{= code.replace(/\W/g,"")}}" {{if selected}} checked="true" {{/if}} />
					<label for="f{{= code.replace(/\W/g,"")}}">{{= name}} {{if count>0}}<span class="ui-li-count ui-btn-up-c ui-btn-corner-all">{{= count}}</span>{{/if}}</label>
				{{/each}}
			</fieldset>
		</div>
	{{else}}
		<ul data-role="listview">
			{{each $data.values}}
				<li data-theme="d" data-query="{{= $data.code}}:{{= code}}" id="{{= name}}">
					<a href="<%= request.getContextPath() %>{{= query.url}}">
						{{= name}}
						{{if count>0}}<span class="ui-li-count ui-btn-up-c ui-btn-corner-all">{{= count}}</span>{{/if}}
					</a>
				</li>
			{{/each}}
		</ul>
	{{/if}}
</script>
