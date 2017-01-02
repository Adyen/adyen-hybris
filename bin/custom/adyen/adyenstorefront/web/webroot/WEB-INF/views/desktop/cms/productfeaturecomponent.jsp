<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<c:url value="${product.url}" var="productUrl"/>
<div class="prod_cat">
	<div class="title">
		<h2><a href="${productUrl}">${not empty component.title ? component.title : product.name}</a></h2>
	</div>
	<div class="thumb">
		<c:if test="${not empty component.media.url}">
			<a href="${productUrl}">
				<img title="${not empty component.title ? component.title : product.name}" alt="${not empty component.title ? component.title : product.name}" src="${component.media.url}">
			</a>
		</c:if>
		<c:if test="${empty component.media.url}">
			<a href="${productUrl}">
				<product:productPrimaryImage product="${product}" format="product"/>
			</a>
		</c:if>
	</div>
	<div class="details">
		<a href="${productUrl}">${not empty component.description ? component.description : product.summary}</a>
	</div>
	<div class="price">
		<format:fromPrice priceData="${product.price}" />
	</div>
	<div class="action">
		<theme:image code="img.iconArrowCategoryTile" alt="${product.name}"/>
	</div>
</div>
