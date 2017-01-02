<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url value="${not empty page ? page.label : urlLink}" var="encodedUrl" />
<div class="disp-img simple-banner" data-theme="b">
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
				<theme:image code="img.iconArrowCategoryTile" alt="${media.altText}" />
			</div>
		</c:when>
		<c:otherwise>
			<a href="${encodedUrl}">
				<c:if test="${not empty headline}">
				<div class="title">
					<h2>${headline}</h2>
				</div>
				</c:if>
				<div class="thumb">
					<img title="${headline}" alt="${media.altText}" src="${media.url}">
				</div>
				<c:if test="${not empty content}">
				<div class="details">
					<p>${content}</p>
				</div>
				</c:if>
				</a>
		</c:otherwise>
	</c:choose>
</div>
