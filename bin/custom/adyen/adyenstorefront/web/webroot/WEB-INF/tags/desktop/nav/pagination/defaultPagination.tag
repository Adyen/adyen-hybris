<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="searchUrl" required="true" %>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ attribute name="themeMsgKey" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<ul class="pagination">
	<li>
		<c:set var="hasPreviousPage" value="${searchPageData.pagination.currentPage > 0}"/>
		<c:if test="${hasPreviousPage}">
			<spring:url value="${searchUrl}" var="previousPageUrl" htmlEscape="true">
				<spring:param name="page" value="${searchPageData.pagination.currentPage - 1}"/>
			</spring:url>
			<ycommerce:testId code="searchResults_previousPage_link">
				<a href="${previousPageUrl}" rel="prev"><spring:theme code="${themeMsgKey}.linkPreviousPage"/></a>
			</ycommerce:testId>
		</c:if>
		<c:if test="${not hasPreviousPage}">
			<a href="#" class="hidden" onclick="return false">
				<spring:theme code="${themeMsgKey}.linkPreviousPage"/>
			</a>
		</c:if>
	</li>
	<li><p><spring:theme code="${themeMsgKey}.currentPage" arguments="${searchPageData.pagination.currentPage + 1},${searchPageData.pagination.numberOfPages}"/></p></li>
	<li>
		<c:set var="hasNextPage" value="${(searchPageData.pagination.currentPage + 1) < searchPageData.pagination.numberOfPages}"/>
		<c:if test="${hasNextPage}">
			<spring:url value="${searchUrl}" var="nextPageUrl" htmlEscape="true">
				<spring:param name="page" value="${searchPageData.pagination.currentPage + 1}"/>
			</spring:url>
			<ycommerce:testId code="searchResults_nextPage_link">
				<a href="${nextPageUrl}" rel="next">
					<spring:theme code="${themeMsgKey}.linkNextPage"/>
				</a>
			</ycommerce:testId>
		</c:if>
		<c:if test="${not hasNextPage}">
			<a href="#" class="hidden" onclick="return false">
				<spring:theme code="${themeMsgKey}.linkNextPage"/>
			</a>
		</c:if>
	</li>
</ul>