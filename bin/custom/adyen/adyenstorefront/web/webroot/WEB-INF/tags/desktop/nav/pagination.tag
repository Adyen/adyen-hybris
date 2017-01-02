<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="searchUrl" required="true" %>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ attribute name="top" required="true" type="java.lang.Boolean" %>
<%@ attribute name="supportShowAll" required="true" type="java.lang.Boolean" %>
<%@ attribute name="supportShowPaged" required="true" type="java.lang.Boolean" %>
<%@ attribute name="msgKey" required="false" %>
<%@ attribute name="numberPagesShown" required="false" type="java.lang.Integer" %>

<%@ taglib prefix="pagination" tagdir="/WEB-INF/tags/desktop/nav/pagination" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.page'}"/>

<c:if test="${searchPageData.pagination.totalNumberOfResults == 0 && top }">
	<div class="paginationBar top clearfix">
		<ycommerce:testId code="searchResults_productsFound_label">
			<div class="totalResults"><spring:theme code="${themeMsgKey}.totalResults" arguments="${searchPageData.pagination.totalNumberOfResults}"/></div>
		</ycommerce:testId>
	</div>
</c:if>

<c:if test="${searchPageData.pagination.totalNumberOfResults > 0}">


<div class="paginationBar ${(top)?"top":"bottom"} clearfix">
	<ycommerce:testId code="searchResults_productsFound_label">
		<div class="totalResults"><spring:theme code="${themeMsgKey}.totalResults" arguments="${searchPageData.pagination.totalNumberOfResults}"/></div>
	</ycommerce:testId>
	<c:if test="${not empty searchPageData.sorts}">
		<form id="sort_form${top ? '1' : '2'}" name="sort_form${top ? '1' : '2'}" method="get" action="#" class="sortForm">
			<label for="sortOptions${top ? '1' : '2'}"><spring:theme code="${themeMsgKey}.sortTitle"/></label>
			<select id="sortOptions${top ? '1' : '2'}" name="sort" class="sortOptions">
				<c:forEach items="${searchPageData.sorts}" var="sort">
					<option value="${sort.code}" ${sort.selected ? 'selected="selected"' : ''}>
						<c:choose>
							<c:when test="${not empty sort.name}">
								${sort.name}
							</c:when>
							<c:otherwise>
								<spring:theme code="${themeMsgKey}.sort.${sort.code}"/>
							</c:otherwise>
						</c:choose>
					</option>
				</c:forEach>
			</select>
			<c:catch var="errorException">
				<spring:eval expression="searchPageData.currentQuery.query" var="dummyVar"/><%-- This will throw an exception is it is not supported --%>
				<input type="hidden" name="q" value="${searchPageData.currentQuery.query.value}"/>
			</c:catch>

			<c:if test="${supportShowAll}">
				<ycommerce:testId code="searchResults_showAll_link">
					<input type="hidden" name="show" value="Page"/>
				</ycommerce:testId>
			</c:if>
			<c:if test="${supportShowPaged}">
				<ycommerce:testId code="searchResults_showPage_link">
					<input type="hidden" name="show" value="All"/>
				</ycommerce:testId>
			</c:if>
		</form>
	</c:if>
	
		
	<div class="right">
			<c:if test="${supportShowPaged}">
				<spring:url value="${searchUrl}" var="showPageUrl" htmlEscape="true">
					<spring:param name="show" value="Page" />
				</spring:url>
				<ycommerce:testId code="searchResults_showPage_link">
					<a class="showPagination" href="${showPageUrl}"><spring:theme code="${themeMsgKey}.showPageResults" /></a>
				</ycommerce:testId>
			</c:if>

		<c:if test="${(searchPageData.pagination.numberOfPages > 1)}">

			<pagination:pageSelectionPagination searchUrl="${searchUrl}" searchPageData="${searchPageData}" numberPagesShown="${numberPagesShown}" themeMsgKey="${themeMsgKey}"/>
		</c:if>
	
		<c:if test="${supportShowAll}">
			<spring:url value="${searchUrl}" var="showAllUrl" htmlEscape="true">
				<spring:param name="show" value="All" />
			</spring:url>
			<ycommerce:testId code="searchResults_showAll_link">
				<a class="showAll" href="${showAllUrl}"><spring:theme code="${themeMsgKey}.showAllResults" /></a>
			</ycommerce:testId>
		</c:if>
	</div>
</div>



</c:if>