<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="deliveryMethod" required="true" type="de.hybris.platform.commercefacades.order.data.DeliveryModeData" %>
<%@ attribute name="cartData" required="false" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:url value="${currentStepUrl}" var="changeDeliveryMethodUrl"/>

<div class="delivery_method">
	<c:if test="${cartData.deliveryItemsQuantity > 0}">
		<ul class="delivery_method-list">
			<li>${deliveryMethod.name}</li>
			<li>${deliveryMethod.description}</li>
			<li>${deliveryMethod.deliveryCost.formattedValue}</li>
		</ul>
		<ycommerce:testId code="selectedDeliveryMethodDetails_change_button">
			<a href="${changeDeliveryMethodUrl}" class="right"><spring:theme code="checkout.multi.deliveryMethod.edit"/></a>
		</ycommerce:testId>
	</c:if>
	
	<c:if test="${cartData.pickupItemsQuantity > 0}">
			<ul class="delivery_method-list delivery_method-list-pickup">
				<li><spring:theme code="checkout.pickup.items.to.pickup" arguments="${cartData.pickupItemsQuantity}"/></li>
				<li><spring:theme code="checkout.pickup.store.destinations" arguments="${fn:length(cartData.pickupOrderGroups)}"/></li>
			</ul>
	</c:if>
	
</div>
