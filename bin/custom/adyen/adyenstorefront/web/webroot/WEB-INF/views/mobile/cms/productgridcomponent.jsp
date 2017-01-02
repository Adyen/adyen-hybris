<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>

<c:if test="${not empty searchPageData.results}">
	<div class="sortingBar item_container_holder">
		<nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="false"/>
	</div>
</c:if>
<nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/>
<div class="span-24 productResultsGrid">
	<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
		<c:choose>
			<c:when test="${status.first}">
				<div class="ui-grid-a">
					<div class='ui-block-a left'>
						<product:productListerGridItem product="${product}"/>
					</div>
					<c:if test="${status.last}">
				</div>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${(status.count % 2) == 0}">
						<div class='ui-block-b right'>
							<product:productListerGridItem product="${product}"/>
						</div>
						</div>
					</c:when>
					<c:otherwise>
						<div class="ui-grid-a">
							<div class='ui-block-a left'>
								<product:productListerGridItem product="${product}"/>
							</div>
						<c:if test="${status.last}">
						</div></c:if>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</div>
<nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/>

