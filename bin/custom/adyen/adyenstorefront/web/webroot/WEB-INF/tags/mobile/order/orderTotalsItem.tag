<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="hideHeading" required="false" type="java.lang.Boolean" %>
<%@ attribute name="containerCSS" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>


<div class="orderTotalFullPusher" id="cartTotals" data-theme="b" data-role="content">
	<c:if test="${not hideHeading}">
		<h4 class="subItemHeader">
			<spring:theme code="text.account.order.orderTotals" text="Order Totals" />
		</h4>
	</c:if>

	<c:if test="${order.orderDiscounts.value gt 0}">
		<div class="ui-grid-a" data-theme="b">
			<div class="ui-block-a">
				<spring:theme code="text.account.order.savings" text="Savings:" />
			</div>
			<div class="ui-block-b">
				<format:price priceData="${order.orderDiscounts}" />
			</div>
		</div>
	</c:if>
	
	<div class="ui-grid-a" data-theme="b">
		<div class="ui-block-a">
			<spring:theme code="text.account.order.subtotal" text="Subtotal:" />
		</div>
		<div class="ui-block-b">
			<format:price priceData="${order.subTotal}" />
		</div>
	</div>
	
	<div class="ui-grid-a" data-theme="b">
		<div class="ui-block-a">
			<spring:theme code="text.account.order.delivery" text="Delivery:" />
		</div>
		<div class="ui-block-b">
			<format:price priceData="${order.deliveryCost}" displayFreeForZero="true" />
		</div>
	</div>
	
	<c:if test="${order.net}">
		<div class="ui-grid-a" data-theme="b">
			<div class="ui-block-a">
				<spring:theme code="text.account.order.netTax" text="Tax:" />
			</div>
			<div class="ui-block-b">
				<format:price priceData="${order.totalTax}" />
			</div>
		</div>
	</c:if>				
	
	<div class="ui-grid-a" data-theme="b">
		<div class="ui-block-a completePrice">
			<spring:theme code="text.account.order.total" text="Total:" />
		</div>
		<div class="ui-block-b completePrice">
			<c:choose>
				<c:when test="${order.net}">
					<format:price priceData="${order.totalPriceWithTax}" />
				</c:when>
				<c:otherwise>
					<format:price priceData="${order.totalPrice}" />
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	
	<c:if test="${not order.net}">
	<p>
		<spring:theme code="text.account.order.includesTax" text="Your order includes {0} tax" arguments="${order.totalTax.formattedValue}" argumentSeparator="###" />
	</p>
	</c:if>

</div>
