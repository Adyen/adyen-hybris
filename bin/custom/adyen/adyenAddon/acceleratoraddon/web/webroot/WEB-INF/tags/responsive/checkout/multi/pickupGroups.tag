<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/responsive/checkout/multi" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:set var="firstPickupItem" value="true"></c:set>

<c:if test="${empty cartData.pickupOrderGroups}">
	<hr>
</c:if>

<c:forEach items="${cartData.pickupOrderGroups}" var="groupData" varStatus="status">
	<hr>
	<div class="checkout-shipping-items">
		<div class="checkout-shipping-items-header">
			<spring:theme code="checkout.multi.pickup.items" arguments="${status.index + 1},${fn:length(groupData.entries)}" 
						  text="Pick Up # ${status.index + 1} - ${fn:length(groupData.entries)} Item(s)">
			</spring:theme>
		</div>
		<p>
			<strong>${fn:escapeXml(groupData.deliveryPointOfService.name)}</strong>
			<br>
			<c:if test="${ not empty groupData.deliveryPointOfService.address.line1 }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.line1)},&nbsp;
			</c:if>
			<c:if test="${ not empty groupData.deliveryPointOfService.address.line2 }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.line2)},&nbsp;
			</c:if>
			<c:if test="${not empty groupData.deliveryPointOfService.address.town }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.town)},&nbsp;
			</c:if>
			<c:if test="${ not empty groupData.deliveryPointOfService.address.region.name }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.region.name)},&nbsp;
			</c:if>
			<c:if test="${ not empty groupData.deliveryPointOfService.address.postalCode }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.postalCode)},&nbsp;
			</c:if>
			<c:if test="${ not empty groupData.deliveryPointOfService.address.country.name }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.country.name)}
			</c:if>
		</p>
		<ul>
			<c:forEach items="${groupData.entries}" var="entry">
				<c:url value="${entry.product.url}" var="productUrl"/>
				<li class="details">
					<span class="name">${entry.product.name}</span> 
					<span class="qty"><spring:theme code="basket.page.qty"/>&nbsp;${entry.quantity}</span>
				</li>
			</c:forEach>
			<c:set var="firstPickupItem" value="true"></c:set>
		</ul>
	</div>
</c:forEach>

<c:if test="${not empty cartData.pickupOrderGroups}">
	<hr>
</c:if>