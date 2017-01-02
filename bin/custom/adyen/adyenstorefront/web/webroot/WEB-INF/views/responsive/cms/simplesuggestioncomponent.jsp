<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>

<c:choose>
	<c:when test="${not empty suggestions and component.maximumNumberProducts > 0}">
		<div class="carousel-component">
			<div class="headline">${component.title}</div>
			<div class="carousel js-owl-carousel js-owl-lazy-reference js-owl-carousel-reference">
				<c:forEach end="${component.maximumNumberProducts}" items="${suggestions}" var="suggestion">
					<c:url value="${suggestion.url}/quickView" var="productQuickViewUrl"/>
					<div class="item">
						<a href="${productQuickViewUrl}" class="js-reference-item">
							<div class="thumb">
								<product:productPrimaryReferenceImage product="${suggestion}" format="product"/>
							</div>
							<c:if test="${component.displayProductTitles}">
								<div class="item-name">${suggestion.name}</div>
							</c:if>
							<c:if test="${component.displayProductPrices}">
								<div class="item-price"><format:fromPrice priceData="${suggestion.price}"/></div>
							</c:if>
						</a>
					</div>
				</c:forEach>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<component:emptyComponent/>
	</c:otherwise>
</c:choose>
