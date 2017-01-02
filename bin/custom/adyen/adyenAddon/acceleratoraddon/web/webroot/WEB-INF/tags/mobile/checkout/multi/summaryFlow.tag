<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="deliveryMode" required="true" type="de.hybris.platform.commercefacades.order.data.DeliveryModeData"%>
<%@ attribute name="paymentInfo" required="true" type="de.hybris.platform.commercefacades.order.data.CCPaymentInfoData"%>
<%@ attribute name="requestSecurityCode" required="true" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout/multi"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<c:set var="hasShippedItems" value="${cartData.deliveryItemsQuantity > 0}" />

<div class="checkout_summary_flow">
	<div data-theme="d">
		<h2>
			<spring:theme code="checkout.summary.reviewYourOrder"/>
		</h2>
		<h3 class="infotext">
			<spring:theme code="checkout.summary.reviewYourOrderMessage"/>
		</h3>
	</div>
	<c:if test="${hasShippedItems}">
		<checkout:summaryFlowDeliveryAddress deliveryAddress="${cartData.deliveryAddress}" />
	</c:if>
	<checkout:summaryFlowDeliveryMode cartData="${cartData}" />
	<checkout:summaryFlowPayment paymentInfo="${paymentInfo}" requestSecurityCode="${requestSecurityCode}" />
</div>
