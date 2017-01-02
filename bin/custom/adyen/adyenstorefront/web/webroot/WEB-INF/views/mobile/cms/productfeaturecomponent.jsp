<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<c:url value="${product.url}" var="productUrl" />

<div class="ui-block-a left product-feature-component">
	<a href="${productUrl}" class="ui-link">
		<c:if test="${not empty component.media.url}">
			<div class="prod_image_main">
				<img title="${not empty component.title ? component.title : product.name}" alt="${not empty component.title ? component.title : product.name}" src="${component.media.url}" />
			</div>
		</c:if>
		<c:if test="${empty component.media.url}">
			<product:productPrimaryImage product="${product}" format="thumbnail" zoomable="false" /> 
		</c:if>
		<span class="mlist-price" id="productPrice"><format:fromPrice priceData="${product.price}" /></span>
		<div class="productTitle">
			${not empty component.title ? component.title : product.name}
		</div>
		<!--
		<div class="details">
			<a href="${productUrl}">${not empty component.description ? component.description : product.summary}</a>
		</div>
		-->
	</a>
</div>