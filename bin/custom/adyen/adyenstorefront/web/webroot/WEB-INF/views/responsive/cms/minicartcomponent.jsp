<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<c:url value="/cart/miniCart/${totalDisplay}" var="refreshMiniCartUrl"/>
<c:url value="/cart/rollover/${component.uid}" var="rolloverPopupUrl"/>
<c:url value="/cart" var="cartUrl"/>

<a 	href="${cartUrl}" 
	class="mini-cart-link js-mini-cart-link" 
	data-mini-cart-url="${rolloverPopupUrl}" 
	data-mini-cart-refresh-url="${refreshMiniCartUrl}" 
	data-mini-cart-name="<spring:theme code="text.cart"/>" 
	data-mini-cart-empty-name="<spring:theme code="popup.cart.empty"/>"

	>

	<div class="mini-cart-icon">
		<span class="glyphicon glyphicon-shopping-cart "></span>
	</div> 
	<ycommerce:testId code="miniCart_items_label">
		<div class="mini-cart-count js-mini-cart-count">${totalItems}</div>
		<div class="mini-cart-price js-mini-cart-price">
			<c:if test="${totalDisplay == 'TOTAL'}">
				<format:price priceData="${totalPrice}" />
			</c:if>
			<c:if test="${totalDisplay == 'SUBTOTAL'}">
				<format:price priceData="${subTotal}" />
			</c:if>
			<c:if test="${totalDisplay == 'TOTAL_WITHOUT_DELIVERY'}">
				<format:price priceData="${totalNoDelivery}" />
			</c:if>
		</div>
	</ycommerce:testId>
	
</a>
<div class="mini-cart-container js-mini-cart-container"></div>