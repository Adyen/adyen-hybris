<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData"%>
<%@ attribute name="top" required="true" type="java.lang.Boolean"%>
<%@ attribute name="msgKey" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:if test="${not empty pageData.sorts}">
	<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.page'}" />
	<form id="sort_form${top ? '1' : '2'}" name="sort_form${top ? '1' : '2'}" method="get" action="#" class="sort_form">
		<select id="sortOptions${top ? '1' : '2'}" name="sort" class="sortOptions">
			<c:forEach items="${pageData.sorts}" var="sort">
				<option value="${sort.code}" ${sort.selected ? 'selected="selected"' : ''}>
					<c:choose>
						<c:when test="${not empty sort.name}">
							${sort.name}
						</c:when>
						<c:otherwise>
							<spring:theme code="${themeMsgKey}.sort.${sort.code}" />
						</c:otherwise>
					</c:choose>
				</option>
			</c:forEach>
		</select>
		<c:catch var="errorException">
			<spring:eval expression="pageData.currentQuery.query" var="dummyVar" />
			<%-- This will throw an exception is it is not supported --%>
			<input type="hidden" name="q" value="${pageData.currentQuery.query.value}" />
		</c:catch>
	</form>
</c:if>
