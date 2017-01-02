<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<div class="span-24">
		<cms:pageSlot position="TopContent" var="feature" element="div" class="span-24 top-content-slot cms_disp-img_slot">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
		<div>
			<a href="${request.contextPath}" class="button positive right"><spring:theme code="checkout.orderConfirmation.continueShopping" /></a>
		</div>
		<div class="orderHead">
			<ycommerce:testId code="orderConfirmation_yourOrderResults_text">
				<div><spring:theme code="checkout.orderConfirmation.thankYouForOrder" /></div>
				<div><spring:theme code="checkout.orderConfirmation.copySentTo" arguments="${email}"/></div>
			</ycommerce:testId>
			<div><spring:theme code="text.account.order.orderNumber" text="Order number is {0}" arguments="${orderData.code}"/></div>
			<div><spring:theme code="text.account.order.orderPlaced" text="Placed on {0}" arguments="${orderData.created}"/></div>
			<c:if test="${not empty orderData.statusDisplay}">
				<spring:theme code="text.account.order.status.display.${orderData.statusDisplay}" var="orderStatus"/>
				<div><spring:theme code="text.account.order.orderStatus" text="The order is {0}" arguments="${orderStatus}"/></div>
			</c:if>
		</div>
		<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
			<div class="span-24 delivery_stages-guest last">
				<user:guestRegister actionNameKey="guest.register.submit"/>
			</div>
		</sec:authorize>
		<div class="orderBoxes clearfix">
			<order:deliveryAddressItem order="${orderData}"/>
			<order:deliveryMethodItem order="${orderData}"/>
			<order:paymentMethodItem order="${orderData}"/>
		</div>
		<c:forEach items="${orderData.deliveryOrderGroups}" var="orderGroup">
			<order:orderDetailsItem order="${orderData}" orderGroup="${orderGroup}" />
		</c:forEach>		
		<c:forEach items="${orderData.pickupOrderGroups}" var="orderGroup">
			<order:orderPickupDetailsItem order="${orderData}" orderGroup="${orderGroup}" />
		</c:forEach>	
		<div class="span-16">
			<order:receivedPromotions order="${orderData}"/>
		</div>
		<div class="span-8 right last">
			<order:orderTotalsItem order="${orderData}" containerCSS="positive"/>
		</div>
	</div>
	<cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<div>
		<a href="${request.contextPath}" class="button positive right"><spring:theme code="checkout.orderConfirmation.continueShopping" /></a>
	</div>
</template:page>