<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="searchUrl" required="true"%>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData"%>
<%@ attribute name="msgKey" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>

<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.mobile.page'}"/>
<div class="pagination">
	<div class="ui-grid-a">
		<div class="ui-block-a">
			<nav:paginationPageCounter searchPageData="${searchPageData}" msgKey="${msgKey}"/>
		</div>
		<div data-position="inline" class="ui-block-b">
			<div data-role="controlgroup" data-type="horizontal">
				<c:set var="hasPreviousPage" value="${searchPageData.pagination.currentPage > 0}"/>
				<c:if test="${hasPreviousPage}">
					<spring:url value="${searchUrl}" var="previousPageUrl">
						<spring:param name="page" value="${searchPageData.pagination.currentPage - 1}"/>
					</spring:url>
					<ycommerce:testId code="searchResults_previousPage_link">
						<a href="${previousPageUrl}" data-role="button">
							<spring:theme code="${themeMsgKey}.linkPreviousPage"/>
						</a>
					</ycommerce:testId>
				</c:if>
				<c:if test="${not hasPreviousPage}">
					<button data-role="button" disabled>
						<spring:theme code="${themeMsgKey}.linkPreviousPage"/>
					</button>
				</c:if>
				<c:set var="hasNextPage"
					   value="${(searchPageData.pagination.currentPage + 1) < searchPageData.pagination.numberOfPages}"/>
				<c:if test="${hasNextPage}">
					<spring:url value="${searchUrl}" var="nextPageUrl">
						<spring:param name="page" value="${searchPageData.pagination.currentPage + 1}"/>
					</spring:url>
					<ycommerce:testId code="searchResults_nextPage_link">
						<a href="${nextPageUrl}" data-role="button">
							<spring:theme code="${themeMsgKey}.linkNextPage"/>
						</a>
					</ycommerce:testId>
				</c:if>
				<c:if test="${not hasNextPage}">
					<button data-role="button" disabled>
						<spring:theme code="${themeMsgKey}.linkNextPage"/>
					</button>
				</c:if>
			</div>
		</div>
	</div>
</div>
