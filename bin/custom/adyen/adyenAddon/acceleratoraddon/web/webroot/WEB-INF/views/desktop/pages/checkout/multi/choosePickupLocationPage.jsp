<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/desktop/checkout/multi" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<c:url value="/checkout/multi/choose-delivery-location" var="continueToDeliveryLocationUrl"/>
<c:url value="/checkout/multi/add-payment-method" var="continueToPaymentMethodUrl"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
		<cart:cartValidation/>
	</div>
	


	<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" currentStep="2" stepName="deliveryMethod"/>
	<div class="span-14 append-1">
	
		
		<multi-checkout:pickupConsolidationOptions cartData="${cartData}" pickupConsolidationOptions="${pickupConsolidationOptions}"/>		
		<multi-checkout:pickupGroups cartData="${cartData}"/>
		
		<form:form id="selectDeliverylocationForm" action="${continueToPaymentMethodUrl}" method="get">	
			<button id="chooseDeliveryLocation_continue_button" class="positive continue right pad_right">
				<spring:theme code="checkout.pickup.continue.button" text="Continue"/>
			</button>
		</form:form>	
	</div>
	<multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="false" showPickupDeliveryEntries="true" showTax="false"/>
	
	
	<cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

</template:page>
