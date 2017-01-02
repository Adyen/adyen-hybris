<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ attribute name="msgKey" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.mobile.page'}"/>
<c:set value="${(searchPageData.pagination.currentPage * searchPageData.pagination.pageSize) + 1}" var="currentPageStart"/>
<c:set value="${(searchPageData.pagination.currentPage + 1) * searchPageData.pagination.pageSize}" var="currentPageEnd"/>

<c:if test="${currentPageEnd > searchPageData.pagination.totalNumberOfResults}">
    <c:set value="${searchPageData.pagination.totalNumberOfResults}" var="currentPageEnd"/>
</c:if>

<ycommerce:testId code="searchResults_productsFound_label">
    <p class="paginationItems">
        <spring:theme code="${themeMsgKey}.currentPage" arguments="${currentPageStart},${currentPageEnd},${searchPageData.pagination.totalNumberOfResults}"/>
    </p>
</ycommerce:testId>
