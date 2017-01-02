<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="deliveryMode" required="true" type="de.hybris.platform.commercefacades.order.data.DeliveryModeData" %>
<%@ attribute name="cartData" required="false" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="hasShippedItems" value="${cartData.deliveryItemsQuantity > 0}" />

<div class="summaryDeliveryMode clearfix" >
	<ycommerce:testId code="checkout_deliveryModeData_text">
		<c:if test="${cartData.deliveryItemsQuantity > 0}">
			<div class="column append-1">
				<strong><spring:theme code="checkout.summary.deliveryMode.header" htmlEscape="false"/></strong>
				<ul>
					<li>${deliveryMode.name} (${deliveryMode.code})</li>
					<li class="deliverymode-description" title="${deliveryMode.description} - ${deliveryMode.deliveryCost.formattedValue}">${deliveryMode.description} - ${deliveryMode.deliveryCost.formattedValue}</li>
				</ul>
			</div>
		</c:if>
	</ycommerce:testId>
		
	<c:if test="${cartData.pickupItemsQuantity > 0}">
		<div class="column">
			<strong>&nbsp;</strong>
			<ul>
				<li><spring:theme code="checkout.pickup.items.to.pickup" arguments="${cartData.pickupItemsQuantity}"/></li>
				<li><spring:theme code="checkout.pickup.store.destinations" arguments="${fn:length(cartData.pickupOrderGroups)}"/></li>
			</ul>
		</div>
	</c:if>

	<c:if test="${cartData.deliveryItemsQuantity > 0}">
		<ycommerce:testId code="checkout_changeDeliveryMode_element">
			<a href="<c:url value="/checkout/multi/delivery-method/choose"/>" class="button positive editButton"><spring:theme code="checkout.summary.edit"/></a>
		</ycommerce:testId>
	</c:if>
</div>