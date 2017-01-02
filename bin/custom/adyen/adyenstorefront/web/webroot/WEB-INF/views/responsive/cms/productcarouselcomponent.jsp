<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>

<c:choose>
	<c:when test="${not empty productData}">
		<div class="carousel-component">
			<div class="headline">${title}</div>

			<c:choose>
				<c:when test="${component.popup}">
					<div class="carousel js-owl-carousel js-owl-lazy-reference js-owl-carousel-reference">
						<div id="quickViewTitle" class="quickView-header" style="display:none">
							<div class="headline">		
								<span class="headline-text"><spring:theme code="popup.quick.view.select"/></span>
							</div>
						</div>
						<c:forEach items="${productData}" var="product">

							<c:url value="${product.url}/quickView" var="productQuickViewUrl"/>
							<div class="item">
								<a href="${productQuickViewUrl}" class="js-reference-item">
									<div class="thumb">
										<product:productPrimaryReferenceImage product="${product}" format="product"/>
									</div>
									<div class="item-name">${product.name}</div>
									<div class="item-price"><format:fromPrice priceData="${product.price}"/></div>
								</a>
							</div>
						</c:forEach>
					</div>
				</c:when>
				<c:otherwise>
					<div class="carousel js-owl-carousel js-owl-default">
						<c:forEach items="${productData}" var="product">

							<c:url value="${product.url}" var="productUrl"/>

							<div class="item">
								<a href="${productUrl}">
									<div class="thumb">
										<product:productPrimaryImage product="${product}" format="product"/>
									</div>
									<div class="item-name">${product.name}</div>
									<div class="item-price"><format:fromPrice priceData="${product.price}"/></div>
								</a>
							</div>
						</c:forEach>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</c:when>

	<c:otherwise>
		<component:emptyComponent/>
	</c:otherwise>
</c:choose>

