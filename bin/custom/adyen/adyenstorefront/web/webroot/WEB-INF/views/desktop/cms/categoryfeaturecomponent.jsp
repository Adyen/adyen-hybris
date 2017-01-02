<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:url value="${url}" var="componentLinkUrl"/>
<c:set value="${not empty component.title ? component.title : component.category.name}" var="componentTitle"/>
<c:set value="${not empty component.description ? component.description : component.category.description}" var="componentDescription"/>

<div class="prod_cat">
	<div class="title">
		<h2><a href="${componentLinkUrl}">${componentTitle}</a></h2>
	</div>
	<div class="thumb">
		<a href="${componentLinkUrl}"><img title="${componentTitle}" alt="${componentTitle}" src="${not empty component.media.url ? component.media.url : component.category.thumbnail.url}"></a>
	</div>
	<div class="details">
		<a href="${componentLinkUrl}">${componentDescription}</a>
	</div>
	<div class="action">
		<theme:image code="img.iconArrowCategoryTile" alt="${componentTitle}"/>
	</div>
</div>
