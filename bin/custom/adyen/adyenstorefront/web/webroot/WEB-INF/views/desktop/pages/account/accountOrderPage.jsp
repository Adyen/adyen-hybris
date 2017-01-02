<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<template:page pageTitle="${pageTitle}">

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav selected="orders" />

	
	<div class="column accountContentPane clearfix">
		<div class="headline">Order Details</div>
		<div class="span-19">
			<div class="span-7">
				<spring:theme code="text.account.order.orderNumber" arguments="${orderData.code}"/><br />
				<spring:theme code="text.account.order.orderPlaced" arguments="${orderData.created}"/><br />
				<c:if test="${not empty orderData.statusDisplay}">
					<spring:theme code="text.account.order.status.display.${orderData.statusDisplay}" var="orderStatus"/>
					<spring:theme code="text.account.order.orderStatus" arguments="${orderStatus}"/><br />
				</c:if>	
			</div>	
	
			<div class="span-5">&nbsp;
				<order:receivedPromotions order="${orderData}"/>
			</div>
			<div class="span-6 last order-totals">
				<order:orderTotalsItem order="${orderData}"/>
			</div>
		</div>
		
		
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
		
		<c:if test="${not empty orderData.unconsignedEntries}" >
			<order:orderUnconsignedEntries order="${orderData}"/>				
		</c:if>
	
	
	
	
	
	
	
	
	
	
	
		<c:set var="headingWasShown" value="false"/>
		<c:forEach items="${orderData.consignments}" var="consignment">
			<c:if test="${consignment.status.code eq 'WAITING' or consignment.status.code eq 'PICKPACK' or consignment.status.code eq 'READY'}">
					<c:if test="${not headingWasShown}">
					<c:set var="headingWasShown" value="true"/>
					<h2><spring:theme code="text.account.order.title.inProgressItems"/></h2>
				</c:if>
				<div class="productItemListHolder fulfilment-states-${consignment.status.code}">
					<order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}" inProgress="true"/>
				</div>
			</c:if>
		</c:forEach>	
		
		<c:forEach items="${orderData.consignments}" var="consignment">
			<c:if test="${consignment.status.code ne 'WAITING' and consignment.status.code ne 'PICKPACK' and consignment.status.code ne 'READY'}">
				<div class="productItemListHolder fulfilment-states-${consignment.status.code}">
					<order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}" />
				</div>
			</c:if>
		</c:forEach>	
		
		
		
		
		
	</div>
	
	
</template:page>

