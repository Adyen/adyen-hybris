<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData"%>
<%@ attribute name="top" required="true" type="java.lang.Boolean"%>
<%@ attribute name="msgKey" required="false"%>
<%@ attribute name="showSearchTerm" required="false"%>
<%@ attribute name="showSuggestions" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>

<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.mobile.page'}" />
<h6 class="descriptionHeadline"><spring:theme code="text.headline.sortandrefine" text="Sort and refine"/></h6>
<div class="results">
	<c:if test="${showSearchTerm || searchPageData.freeTextSearch}">
		<h2 class="searchHeadline">
			<spring:theme code="search.page.searchText" arguments="${searchPageData.freeTextSearch}" />
			<span class="resultsItems">
				(<spring:theme code="${themeMsgKey}.totalResults" arguments="${pageData.pagination.totalNumberOfResults}" />)
			</span>
		</h2>
	</c:if>
</div>
<c:if test="${showSuggestions || searchPageData.freeTextSearch}">
    <nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />
</c:if>
<div class="ui-grid-a resultsRefinements">
	<div class="ui-block-a">
		<nav:sorting top="true" pageData="${pageData}" />
	</div>
	<div class="ui-block-b">
		<nav:facetNavAppliedFilters pageData="${searchPageData}" />
	</div>
</div>
