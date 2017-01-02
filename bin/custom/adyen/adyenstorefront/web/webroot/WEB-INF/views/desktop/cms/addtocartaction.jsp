<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="${url}" var="addToCartUrl"/>
<form:form method="post" id="addToCartForm" class="add_to_cart_form" action="${addToCartUrl}">
	<c:if test="${product.purchasable}">
		<input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="1">
	</c:if>
	<input type="hidden" name="productCodePost" value="${product.code}"/>

	<c:if test="${empty showAddToCart ? true : showAddToCart}">
		<c:set var="buttonType">button</c:set>

		<c:if test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
			<c:set var="buttonType">submit</c:set>
		</c:if>

		<c:choose>
			<c:when test="${fn:contains(buttonType, 'button')}">
				<button type="${buttonType}" class="addToCartButton outOfStock" disabled="disabled">
					<spring:theme code="product.variants.out.of.stock"/>
				</button>
			</c:when>

			<c:otherwise>
				<button id="addToCartButton" type="${buttonType}" class="addToCartButton" disabled="disabled">
					<spring:theme code="basket.add.to.basket"/>
				</button>
			</c:otherwise>
		</c:choose>
	</c:if>
</form:form>
