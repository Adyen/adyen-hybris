<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="deliveryAddress" required="true" type="de.hybris.platform.commercefacades.user.data.AddressData" %>
<%@ attribute name="cartData" required="false" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:url value="/checkout/multi/delivery-address/edit" var="changeDeliveryAddressUrl"/>
<c:set var="hasShippedItems" value="${cartData.deliveryItemsQuantity > 0}" />

<div class="existing_address">
	<c:if test="${not hasShippedItems}">
		<spring:theme code="checkout.pickup.no.delivery.required"/>
	</c:if>
	<c:if test="${hasShippedItems}">
		<ycommerce:testId code="addressBook_address_label">
			<ul>
				<li>${fn:escapeXml(deliveryAddress.title)}&nbsp;${fn:escapeXml(deliveryAddress.firstName)}&nbsp;${fn:escapeXml(deliveryAddress.lastName)}</li>
				<li>${fn:escapeXml(deliveryAddress.line1)}</li>
				<li>${fn:escapeXml(deliveryAddress.line2)}</li>
				<li>${fn:escapeXml(deliveryAddress.town)}</li>
				<li>${fn:escapeXml(deliveryAddress.region.name)}</li>
				<li>${fn:escapeXml(deliveryAddress.postalCode)}</li>
				<li>${fn:escapeXml(deliveryAddress.country.name)}</li>
			</ul>
		</ycommerce:testId>
		<ycommerce:testId code="selectedDeliveryAddressDetails_change_button">
			<a href="${changeDeliveryAddressUrl}/?editAddressCode=${deliveryAddress.id}" class="right"><spring:theme code="checkout.multi.deliveryAddress.edit"/></a>
		</ycommerce:testId>
	</c:if>
</div>