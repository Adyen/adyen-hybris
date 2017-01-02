<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="summary" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div data-theme="b" data-role="content">
	<div data-theme="b">
		<div>
			<span class="cart_id">
				<spring:theme code="basket.page.cartId"/>
				<span class="cart-id-nr">${cartData.code}</span>
			</span>
			<c:if test="${cartData.deliveryItemsQuantity gt 0}" >
				<div class="checkoutOverviewItemsHeadline">
					<spring:theme code="basket.page.title.yourDeliveryItems"/>
				</div>
				<c:if test="${ycommerce:checkIfPickupEnabledForStore() && cartData.pickupItemsQuantity gt 0 && !summary}">
					<span class="checkout-overview-pickup-info">
						<spring:theme code="basket.page.title.yourDeliveryItems.pickup"/>
					</span>
				</c:if>
			</c:if>
		</div>
		<checkout:cartItemsForShipping cartData="${cartData}" />
	</div>
</div>
