<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>

<div class="orderBoxes clearfix">
	<order:deliveryAddressItem order="${orderData}"/>
	<order:deliveryMethodItem order="${orderData}"/>
	<div class="orderBox billing">
		<order:billingAddressItem order="${orderData}"/>
	</div>
	<c:if test="${not empty orderData.paymentInfo}">
		<div class="orderBox payment">
			<order:paymentDetailsItem order="${orderData}"/>
		</div>
	</c:if>
</div>