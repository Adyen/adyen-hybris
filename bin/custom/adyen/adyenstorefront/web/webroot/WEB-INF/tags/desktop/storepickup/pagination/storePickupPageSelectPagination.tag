<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pickupInStoreUrl" required="true" %>
<%@ attribute name="numberPagesShown" required="true" %>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="hasNextPage" value="${(searchPageData.pagination.currentPage + 1) lt searchPageData.pagination.numberOfPages}"/>
<c:if test="${hasNextPage}">
	<form action="${pickupInStoreUrl}" id="last_page_storepickup_form" method="get">
		<input type="hidden" name="page" id="last_page_value" value="${searchPageData.pagination.numberOfPages-1}"/>
		<button class="link" type="submit"><spring:theme code="pickup.pagination.last"/></button>
	</form>
	<form action="${pickupInStoreUrl}" id="next_results_storepickup_form" method="get">
		<input type="hidden" name="page" id="next_page_value" value="${searchPageData.pagination.currentPage+1}"/>
		<button class="link" type="submit"><spring:theme code="pickup.pagination.next"/></button>
	</form>
</c:if>
<c:set var="limit" value="${numberPagesShown}"/>
<c:set var="halfLimit"><fmt:formatNumber value="${limit/2}" maxFractionDigits="0"/></c:set>
<c:set var="beginPage">
	<c:choose>
		<%-- Limit is higher than number of pages --%>
		<c:when test="${limit gt searchPageData.pagination.numberOfPages}">1</c:when>
		<%-- Start shifting page numbers once currentPage reaches halfway point--%>
		<c:when test="${searchPageData.pagination.currentPage + halfLimit ge limit}">
			<c:choose>
				<c:when test="${searchPageData.pagination.currentPage + halfLimit lt searchPageData.pagination.numberOfPages}">
					${searchPageData.pagination.currentPage + 1 - halfLimit}
				</c:when>
				<c:otherwise>${searchPageData.pagination.numberOfPages + 1 - limit}</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>1</c:otherwise>
	</c:choose>
</c:set>
<c:set var="endPage">
	<c:choose>
		<c:when test="${limit gt searchPageData.pagination.numberOfPages}">
			${searchPageData.pagination.numberOfPages}
		</c:when>
		<c:when test="${hasNextPage}">
			${beginPage + limit - 1}
		</c:when>
		<c:otherwise>
			${searchPageData.pagination.numberOfPages}
		</c:otherwise>
	</c:choose>
</c:set>
<c:forEach begin="${beginPage}" end="${endPage}" var="page">
	<%-- Need to list numbers in reverse ordering because of float: right --%>
	<c:set var="pageNumber" value="${endPage - page + beginPage}"/>
	<c:choose>
		<c:when test="${searchPageData.pagination.currentPage+1 eq pageNumber}">
			<div><button class="link" type="button" disabled="disabled">${pageNumber}</button></div>
		</c:when>
		<c:otherwise>
			<form action="${pickupInStoreUrl}" id="page_${pageNumber}_storepickup_form" method="get">
				<input type="hidden" name="page" id="page_${pageNumber}" value="${pageNumber-1}"/>
				<button class="link" type="submit">${pageNumber}</button>
			</form>
		</c:otherwise>
	</c:choose>
</c:forEach>
<c:if test="${searchPageData.pagination.currentPage gt 0}">
	<form action="${pickupInStoreUrl}" id="back_results_storepickup_form" method="get">
		<input type="hidden" name="page" id="back_page_value" value="${searchPageData.pagination.currentPage-1}"/>
		<button class="link" type="submit"><spring:theme code="pickup.pagination.previous"/></button>
	</form>
	<form action="${pickupInStoreUrl}" id="first_page_storepickup_form" method="get">
		<input type="hidden" name="page" id="first_page_value" value="0"/>
		<button class="link" type="submit"><spring:theme code="pickup.pagination.first"/></button>
	</form>
</c:if>