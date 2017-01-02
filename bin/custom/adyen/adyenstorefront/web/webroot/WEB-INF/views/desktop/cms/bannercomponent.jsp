<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url value="${not empty page ? page.label : urlLink}" var="encodedUrl" />
<div class="disp-img simple-banner">
	<c:choose>
		<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
			<div class="title">
				<h2>${headline}</h2>
			</div>
			<div class="thumb">
				<img title="${headline}" alt="${media.altText}" src="${media.url}">
			</div>
			<div class="details">
				<p>${content}</p>
			</div>
			<div class="action">
				<theme:image code="img.iconArrowCategoryTile" alt="${media.altText}"/>
			</div>
		</c:when>
		<c:otherwise>
			<a href="${encodedUrl}">
				<span class="title">
					<strong>${headline}</strong>
				</span>
				<span class="thumb">
					<img title="${headline}" alt="${media.altText}" src="${media.url}">
				</span>
				<span class="details">
					${content}
				</span>
				<span class="action">
					<theme:image code="img.iconArrowCategoryTile" alt="${media.altText}"/>
				</span>
			</a>
		</c:otherwise>
	</c:choose>
</div>