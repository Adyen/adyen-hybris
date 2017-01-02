<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showPotentialPromotions" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showAllItems" type="java.lang.Boolean" %>
<%@ attribute name="summary" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>



	<c:if test="${summary and cartData.pickupItemsQuantity gt 0}">
		<div class="checkoutOverviewItemsHeadline">
			<spring:theme code="basket.page.title.yourPickUpItems" text="Your Pick Up Items"/>
		</div>
	</c:if>
	<c:forEach items="${cartData.pickupOrderGroups}" var="groupData" varStatus="status">
		<ul class="mFormList itemsList productItemListDetailsHolder listview">
			<li class="checkoutOverview-PickUp-Items-Location">
				<c:choose>
					<c:when test="${summary}"><h4><spring:theme code="basket.page.title.pickupFrom"/></h4></c:when>
					<c:otherwise><h3><spring:theme code="checkout.pickup.items.to.pickup" arguments="${groupData.quantity}" /></h3></c:otherwise>
				</c:choose>
				<span class="pickup_store_results-entry pickup_store_results-name">${fn:escapeXml(groupData.deliveryPointOfService.name)}</span>
				<span class="pickup_store_results-entry pickup_store_results-line1">${fn:escapeXml(groupData.deliveryPointOfService.address.line1)}</span>
				<span class="pickup_store_results-entry pickup_store_results-line2">${fn:escapeXml(groupData.deliveryPointOfService.address.line2)}</span>
				<span class="pickup_store_results-entry pickup_store_results-town">${fn:escapeXml(groupData.deliveryPointOfService.address.town)}</span>
				<span class="pickup_store_results-entry pickup_store_results-zip">${fn:escapeXml(groupData.deliveryPointOfService.address.postalCode)}</span>
				<span class="pickup_store_results-entry pickup_store_results-country">${fn:escapeXml(groupData.deliveryPointOfService.address.country.name)}</span>
				<c:if test="${not summary}">
					<span class="pickup_store_results-entry pickup_store_results-total">
						<span class="pickup_store_results-total-label"><spring:theme code="checkout.pickup.estimated.total"/></span>
						<format:price priceData="${groupData.totalPriceWithTax}" displayFreeForZero="true"/>
					</span>
				</c:if>
			</li>

			<checkout:cartItemsForPickUp cartData="${cartData}" groupData="${groupData}"/>

			<li class="checkoutOverview-PickUp-Items-Toggle">
				<a href="#" data-role="button" class="toggleItemsButton" data-toggle-text="<spring:theme code="mobile.checkout.items.hide" />">
					<spring:theme code="mobile.checkout.items.show" />
				</a>
			</li>
		</ul>
	</c:forEach>
