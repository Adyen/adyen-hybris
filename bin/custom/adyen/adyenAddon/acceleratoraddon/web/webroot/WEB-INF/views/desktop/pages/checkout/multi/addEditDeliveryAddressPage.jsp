<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/desktop/checkout/multi" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/desktop/address" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>

	<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" currentStep="1" stepName="deliveryAddress"/>
	<div class="span-14 append-1">
		<div id="checkoutContentPanel" class="clearfix">
			
					<div class="headline"><spring:theme code="checkout.multi.addressDetails" text="Address Details"/></div>
					<div class="required right"><spring:theme code="form.required" text="Fields marked * are required"/></div>
					<div class="description"><spring:theme code="checkout.multi.addEditform" text="Please use this form to add/edit an address."/></div>
		
					<address:addressFormSelector supportedCountries="${countries}"
												 regions="${regions}"
												 cancelUrl="/checkout/multi/add-delivery-address"
												 country="${country}"/>
												 
												 
												 
	 		<c:if test="${not empty deliveryAddresses}">
	 			<div id="savedAddressListHolder" class="clear" >
	 				<div id="savedAddressList" class="summaryOverlay clearfix">
	 					<div class="headline">Addressbook</div>
	 					<div class="addressList">
	 					<c:forEach items="${deliveryAddresses}" var="deliveryAddress" varStatus="status">
	 						<div class="addressEntry">
	 							<form action="${request.contextPath}/checkout/multi/select-delivery-address" method="GET">
	 								<input type="hidden" name="selectedAddressCode" value="${deliveryAddress.id}"/>
	 								<ul>
	 									<li>${fn:escapeXml(deliveryAddress.title)}&nbsp; ${fn:escapeXml(deliveryAddress.firstName)}&nbsp; ${fn:escapeXml(deliveryAddress.lastName)}</li>
	 									<li>${fn:escapeXml(deliveryAddress.line1)}</li>
	 									<li>${fn:escapeXml(deliveryAddress.line2)}</li>
	 									<li>${fn:escapeXml(deliveryAddress.town)}&nbsp; ${fn:escapeXml(deliveryAddress.postalCode)}</li>
	 									<li>${fn:escapeXml(deliveryAddress.country.name)}<c:if test="${not empty deliveryAddress.region.name}">&nbsp; ${fn:escapeXml(deliveryAddress.region.name)}</c:if></li>
	 								</ul>


	 								<button type="submit" class="positive left" tabindex="${status.count + 21}">Use this Delivery Address</button>
	 							</form>
	 							<form:form action="${request.contextPath}/checkout/multi/remove-address" method="POST">
	 								<input type="hidden" name="addressCode" value="${deliveryAddress.id}"/>
	 								<button type="submit" class="negative remove-payment-item left" tabindex="${status.count + 22}">Remove</button>
	 							</form:form>
	 						</div>
	 					</c:forEach>
	 					</div>
	 				</div>
	 			</div>
	 		</c:if>
				
        </div>
	
		<address:suggestedAddresses selectedAddressUrl="/checkout/multi/select-suggested-address"/>
		
		
		
		
		
		
		
		
		
		
		
	</div>
	<multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="false"/>
	<cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

</template:page>
