<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/mobile/cart" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout/multi" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<c:url value="/checkout/multi/add-payment-method" var="choosePaymentMethodUrl"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<jsp:body>
		<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" currentStep="2" stepName="deliveryMethod"/>
		<div class="item_container_holder" data-theme="b" data-role="content">
			<div data-theme="b">
				<h2>
					<spring:theme code="checkout.multi.pickupInStore"/>
				</h2>
				<h3 class="infotext">
					<spring:theme code="checkout.multi.pickupInStore.confirm.and.continue"/>
				</h3>
			</div>
		</div>
		<div class="checkoutOverviewItems checkoutOverview-PickUp-Items">
			<checkout:cartItemGroupsForPickUp cartData="${cartData}"/>
		</div>
		<div>
			<checkout:pickupConsolidationOptions cartData="${cartData}" pickupConsolidationOptions="${pickupConsolidationOptions}"/>
		</div>
		<div>
			<a data-role="button" data-theme="b" href="${choosePaymentMethodUrl}" data-icon="arrow-r"
			   data-iconpos="right">
				<spring:theme code="mobile.checkout.continue.button"/>
			</a>
		</div>
	</jsp:body>
</template:page>
