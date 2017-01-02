<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>

<c:choose>
	<c:when test="${not empty productData}">
		<div class="scroller vertical">
			<div class="title_holder">
				<h2>${title}</h2>
			</div>
			<ul id="carousel" class="jcarousel-skin">
				<c:forEach items="${productData}" var="product">
					<c:url value="${product.url}/quickView" var="productQuickViewUrl" />
					<li>
						<a href="${productQuickViewUrl}">
							<span><product:productPrimaryImage product="${product}" format="thumbnail" zoomable="false" /></span>
							<h3>${product.name}</h3>
							<p><format:fromPrice priceData="${product.price}" /></p>
						</a>
					</li>
				</c:forEach>
			</ul>
		</div>
	</c:when>

	<c:otherwise>
		<component:emptyComponent/>
	</c:otherwise>
</c:choose>
