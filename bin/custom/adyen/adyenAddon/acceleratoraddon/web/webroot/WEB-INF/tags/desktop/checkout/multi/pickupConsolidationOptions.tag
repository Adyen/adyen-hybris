<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="pickupConsolidationOptions" required="true" type="java.util.List" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/desktop/checkout/multi" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/desktop/store" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<c:if test="${not empty pickupConsolidationOptions}">
	<c:url value="/checkout/multi/select-delivery-location" var="consolidatePickupUrl"/>
	
	<div class="simplifyPickupPanel clearfix">
		<form:form id="simplifyDeliverylocationForm" action="${consolidatePickupUrl}" method="POST">
		<div class="headline">	<spring:theme code="checkout.pickup.items.available.at.one.location" /></div>
		<div class="description"><spring:theme code="checkout.pickup.items.at.one.location" /></div>
		<div class="content clearfix">
			<c:forEach items="${pickupConsolidationOptions}" var="option" varStatus="status">
				<div class="selectDeliverylocationItem column">
					<label class="selectDeliverylocationItemLabel" for="selectDeliverylocationItemId${status.count}"><input id="selectDeliverylocationItemId${status.count}" class="selectDeliverylocationItemOption" type="radio" name="posName" value="${option.name}" <c:if test="${status.first}">checked="checked"</c:if>/>
					
						<span class="pickupAdressList">
							<c:if test="${not empty userLocation}"><span class="pickupAdressItem pickupOptionDistance">${option.formattedDistance}</span></c:if>
							<span class="pickupAdressItem strong">${fn:escapeXml(option.name)}</span>
							<span class="pickupAdressItem">${fn:escapeXml(option.address.line1)}</span>
							<span class="pickupAdressItem">${fn:escapeXml(option.address.line2)}</span>
							<span class="pickupAdressItem">${fn:escapeXml(option.address.town)}</span>
							<span class="pickupAdressItem">${fn:escapeXml(option.address.postalCode)}</span>
							<span class="pickupAdressItem">${fn:escapeXml(option.address.country.name)}</span>
						</span>
					</label>
					
				</div>
			</c:forEach>
			</div>
			<div class="footline">
				<div class="footlineText"><spring:theme code="checkout.pickup.items.simplify.pickup.location" /></div>
				<button id="chooseDeliveryLocation_simplify_button" class="positive right pad_right selectDeliverylocationItemButton">
					<spring:theme code="checkout.pickup.simplifyPickup" />
				</button>
			</div>
		</form:form>	
	</div>
</c:if>
