<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>


<c:url value="/cart/miniCart/${totalDisplay}" var="refreshMiniCartUrl"/>
<c:url value="/cart/rollover/${component.uid}" var="rolloverPopupUrl"/>
<c:url value="/cart" var="cartUrl"/>

<a href="${cartUrl}" class="minicart">
	${component.title}
	<ycommerce:testId code="miniCart_items_label">
		<span class="count">${totalItems}</span>	
		<span class="price">
			<c:if test="${totalDisplay == 'TOTAL'}">
				<format:price priceData="${totalPrice}"/>
			</c:if>
			<c:if test="${totalDisplay == 'SUBTOTAL'}">
				<format:price priceData="${subTotal}"/>
			</c:if>
			<c:if test="${totalDisplay == 'TOTAL_WITHOUT_DELIVERY'}">
				<format:price priceData="${totalNoDelivery}"/>
			</c:if>
		</span>
	</ycommerce:testId>
</a>
<div id="miniCartLayer" class="miniCartPopup" data-refreshMiniCartUrl="${refreshMiniCartUrl}/?"  data-rolloverPopupUrl="${rolloverPopupUrl}" ></div>

