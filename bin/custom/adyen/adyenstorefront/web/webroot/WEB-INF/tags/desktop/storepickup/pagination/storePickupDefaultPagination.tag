<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="pickupInStoreUrl" required="true" %>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>

<span class="pickup_store_search-paging-info">
	<spring:theme code="search.page.currentPage" arguments="${searchPageData.pagination.currentPage + 1},${searchPageData.pagination.numberOfPages}"/>
</span>
<c:if test="${(searchPageData.pagination.currentPage + 1) lt searchPageData.pagination.numberOfPages}">
	<form action="${pickupInStoreUrl}" id="next_results_storepickup_form" method="get">
		<input type="hidden" name="page" id="next_page_value" value="${searchPageData.pagination.currentPage+1}"/>
		<button class="neutral paginate_next_pickupstores_button" type="submit"><spring:theme code="pickup.pagination.next"/></button>
	</form>
</c:if>
<c:if test="${searchPageData.pagination.currentPage gt 0}">
	<form action="${pickupInStoreUrl}" id="back_results_storepickup_form" method="get">
		<input type="hidden" name="page" id="back_page_value" value="${searchPageData.pagination.currentPage-1}"/>
		<button class="neutral paginate_back_pickupstores_button" type="submit"><spring:theme code="pickup.pagination.previous"/></button>
	</form>
</c:if>