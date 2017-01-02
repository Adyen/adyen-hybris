<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>


<c:if test="${not empty searchPageData.results}">
	<div class="sortingBar item_container_holder">
		<nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="false"/>
	</div>
</c:if>
<nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/>
<div class="productResultsList">
	<c:if test="${not empty searchPageData.results}">
		<ul data-role="listview" data-inset="true" data-theme="e" data-content-theme="e" class="mainNavigation">
			<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
				<product:productListerItem product="${product}"/>
			</c:forEach>
		</ul>
	</c:if>
</div>
<nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/>
