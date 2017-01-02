<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>

<nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="true" />
	<nav:facetNavRefinements pageData="${searchPageData}" />
</div>
<div class="span-20 last">
	<div class="span-20">
		<nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}" />
	</div>
	<div class="span-24 productResultsGrid">
		<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
			<c:choose>
				<c:when test="${status.first}">
					<div class="ui-grid-a">
						<div class='ui-block-a left'>
							<product:productListerGridItem product="${product}" />
						</div>
				</c:when>
				<c:otherwise>
					<c:if test="${(status.count % 2) == 0}">
						<div class='ui-block-b right'>
							<product:productListerGridItem product="${product}" />
						</div>
					</div>
					</c:if>
					<c:if test="${(status.count % 2) == 1}">
						<div class="ui-grid-a">
							<div class='ui-block-a left'>
								<product:productListerGridItem product="${product}" />
							</div>
						<c:if test="${status.last}">
						</div>
						</c:if>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</div>
	<div class="span-20 last">
		<nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}" />
	</div>

