<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/desktop/checkout/multi" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/desktop/store" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<script type="text/javascript">
		var showText = '<spring:theme code="checkout.orderDetails.show"/>';
		var hideText = '<spring:theme code="checkout.orderDetails.hide"/>';

</script>

<div id="checkoutContentPanel" class="clearfix">
	<div class="headline"><spring:theme code="checkout.pickup.pickup.in.store.title" /></div>
	<div class="description"><spring:theme code="checkout.pickup.confirm.and.continue" /></div>
	
	
	<c:forEach items="${cartData.pickupOrderGroups}" var="groupData" varStatus="status">
	<div class="headline clear">
		<spring:theme code="checkout.pickup.items.to.pickup" arguments="${groupData.quantity}" />
		<span class="right">${groupData.deliveryPointOfService.formattedDistance}</span>
	</div>
	<div class="column contentPanelLeft">
		<ul class="pickupAdressList">
			<li class="strong">${fn:escapeXml(groupData.deliveryPointOfService.name)}</li>
			<li>${fn:escapeXml(groupData.deliveryPointOfService.address.line1)}</li>
			<li>${fn:escapeXml(groupData.deliveryPointOfService.address.line2)}</li>
			<li>${fn:escapeXml(groupData.deliveryPointOfService.address.town)}</li>
			<li>${fn:escapeXml(groupData.deliveryPointOfService.address.postalCode)}</li>
			<li>${fn:escapeXml(groupData.deliveryPointOfService.address.country.name)}</li>
		</ul>
		<store:openingSchedule openingSchedule="${groupData.deliveryPointOfService.openingHours}" />
	</div>
	<div class="column contentPanelRight last">
		<multi-checkout:pickupCartItems cartData="${cartData}" groupData="${groupData}" index="${status.index}" showHead="false" />
	</div>
	</c:forEach>
</div>
