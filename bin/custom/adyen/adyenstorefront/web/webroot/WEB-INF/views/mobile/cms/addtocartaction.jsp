<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:if test="${showAddToCart}">
	<c:url value="/cart/add" var="addToCartUrl"/>
	<form:form id="addToCartForm" class="add_to_cart_form" action="${addToCartUrl}" method="post">
		<input type="hidden" name="productCodePost" value="${product.code}"/>
		<c:if test="${product.purchasable}">
			<input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="1">
		</c:if>
		<div id='addToBasket'>
			<c:set var="buttonType">button</c:set>
			<c:choose>
				<c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock'}">
					<c:set var="buttonType">submit</c:set>
					<spring:theme code="text.addToCart" var="addToCartText"/>
					<button type="${buttonType}"
							data-rel="dialog"
							data-transition="pop"
							data-theme="b"
							class="positive large <c:if test="${fn:contains(buttonType, 'button')}">out-of-stock</c:if>">
						<spring:theme code="text.addToCart" var="addToCartText"/>
						<spring:theme code="basket.add.to.basket"/>
					</button>
				</c:when>
				<c:otherwise>
					<spring:theme code="text.addToCart" var="addToCartText"/>
					<button type="${buttonType}" data-rel="dialog" data-transition="pop" data-theme="b"
							class="positive large" disabled='true'>
						<spring:theme code="product.variants.out.of.stock"/>
					</button>
				</c:otherwise>
			</c:choose>
		</div>
	</form:form>
</c:if>