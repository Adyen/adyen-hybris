<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:url value="${urlLink}" var="encodedUrl" />

<div class="simple_disp-img simple-banner">
	<c:choose>
		<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
			<img title="${media.altText}" alt="${medias[2].altText}" src="${medias[2].url}">
		</c:when>
		<c:otherwise>
			<a href="${encodedUrl}"><img title="${medias[2].altText}" alt="${medias[2].altText}" src="${medias[2].url}"></a>
		</c:otherwise>
	</c:choose>
</div>