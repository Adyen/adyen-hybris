<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>

<div class="results">
	<h1><spring:theme code="search.page.searchText" arguments="${searchPageData.freeTextSearch}"/></h1>
</div>
<nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />

<nav:pagination top="true"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>

<div class="productGrid">
	<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
		<div class="span-6 ${(status.index+1)%3 == 0 ? ' last' : ''}${(status.index)%3 == 0 ? ' first clear' : ''}">
			<product:productListerGridItem product="${product}"/>
		</div>
	</c:forEach>
</div>

<nav:pagination top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>


<storepickup:pickupStorePopup />