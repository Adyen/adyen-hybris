<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:url value="${urlLink}" var="encodedUrl"/>

<div class="simple_disp-img simple-banner">
	<c:choose>
		<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
			<img title="${media.altText}" alt="${media.altText}" src="${media.url}">
		</c:when>
		<c:otherwise>
			<a href="${encodedUrl}"><img title="${media.altText}" alt="${media.altText}" src="${media.url}"></a>
		</c:otherwise>
	</c:choose>
</div>
