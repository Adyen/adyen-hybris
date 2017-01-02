<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="accountOrderDetailOrderTotals clearfix">
	<div class="span-7">
		<spring:theme code="text.account.order.orderNumber" arguments="${orderData.code}"/>
		<spring:theme code="text.account.order.orderPlaced" arguments="${orderData.created}"/>
		<c:if test="${not empty orderData.statusDisplay}">
			<spring:theme code="text.account.order.status.display.${orderData.statusDisplay}" var="orderStatus"/>
			<spring:theme code="text.account.order.orderStatus" arguments="${orderStatus}"/>
		</c:if>
	</div>
	<div class="span-7">
		<order:receivedPromotions order="${orderData}"/>
	</div>
	<div class="span-6 last order-totals">
		<order:orderTotalsItem order="${orderData}"/>
	</div>
</div>