<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>

<div class="col-md-9 col-lg-10">
	<div class="results">
		<h1><spring:theme code="search.page.searchText" arguments="${searchPageData.freeTextSearch}"/></h1>
	</div>
	<nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />

	<nav:pagination top="true"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>

	<ul class="product-listing product-grid">
		<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
			<product:productListerGridItem product="${product}"/>
		</c:forEach>
	</ul>

	<div id="addToCartTitle" style="display:none">
		<div class="add-to-cart-header">
			<div class="headline">
				<span class="headline-text"><spring:theme code="basket.added.to.basket"/></span>
			</div>
		</div>
	</div>

	<nav:pagination top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>
</div>
