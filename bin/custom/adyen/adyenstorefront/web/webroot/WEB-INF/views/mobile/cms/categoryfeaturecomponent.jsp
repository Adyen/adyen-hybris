<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<li data-theme="d">
	<c:url value="${url}" var="urlValue"/>
	<a href="${urlValue}">
		<img title="${not empty component.title ? component.title : component.category.name}"
			alt="${not empty component.title ? component.title : component.category.name}"
			src="${not empty component.media.url ? component.media.url : component.category.thumbnail.url}"/>
		<h3 style="white-space:normal">
			${not empty component.title ? component.title : component.category.name}
		</h3>
		<h3 style="white-space:normal">
			${not empty component.description ? component.description : component.category.description}
		</h3>
	</a>
</li>
