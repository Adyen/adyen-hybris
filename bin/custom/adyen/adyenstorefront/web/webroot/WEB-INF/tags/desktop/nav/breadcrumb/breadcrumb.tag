<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="breadcrumbs" required="true" type="java.util.List" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/" var="homeUrl"/>
<ul class="clearfix">
	<li>
		<a href="${homeUrl}"><spring:theme code="breadcrumb.home"/></a>
	</li>

	<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
	<li class="separator">&gt;</li>
		<li <c:if test="${not empty breadcrumb.linkClass}">class="${breadcrumb.linkClass}"</c:if>>

			<c:choose>
				<c:when test="${breadcrumb.url eq '#'}">
					<a href="#" onclick="return false;" <c:if test="${status.last}">class="last"</c:if>>${breadcrumb.name}</a>
				</c:when>

				<c:otherwise>
					<c:url value="${breadcrumb.url}" var="breadcrumbUrl"/>
					<a href="${breadcrumbUrl}" <c:if test="${status.last}">class="last"</c:if>>${breadcrumb.name}</a>
				</c:otherwise>

			</c:choose>
		</li>

	</c:forEach>
</ul>
