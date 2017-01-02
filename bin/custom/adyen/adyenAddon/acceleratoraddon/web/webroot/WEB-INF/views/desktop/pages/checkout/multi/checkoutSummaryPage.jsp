<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/desktop/checkout" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/desktop/checkout/multi" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:url value="/checkout/multi/placeOrder" var="placeOrderUrl" />
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl" />


<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">



	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>



	<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" currentStep="4" stepName="confirmOrder"/>
	<div class="span-14 append-1">
		<multi-checkout:summaryFlow deliveryAddress="${cartData.deliveryAddress}" deliveryMode="${deliveryMode}" paymentInfo="${paymentInfo}" requestSecurityCode="${requestSecurityCode}"  cartData="${cartData}"/>
		<cart:cartPromotions cartData="${cartData}"/>

		<form:form action="${placeOrderUrl}" id="placeOrderForm1" commandName="placeOrderForm">
			<!--  HPP attributes Start -->
			<c:if test="${cartData.paymentInfo.useHPP}">
				<input type="hidden" id="brandCode" name="brandCode" value="${cartData.paymentInfo.adyenPaymentBrand}"/>
				<input type="hidden" id="hppURL" name="hppURL" value="${cartData.paymentInfo.hppURL}"/>
			</c:if>
			<!--  HPP attributes End -->
			<c:if test="${requestSecurityCode}">
				<form:input type="hidden" class="securityCodeClass" path="securityCode"/>
				<button class="positive right pad_right place-order placeOrderWithSecurityCode">
					<spring:theme code="checkout.summary.placeOrder"/>
				</button>
			</c:if>
			
			<c:if test="${not requestSecurityCode}">
				<button type="submit" class="positive right place-order">
					<spring:theme code="checkout.summary.placeOrder"/>
				</button>
			</c:if>
			<div class="terms">
				<form:checkbox id="Terms1" path="termsCheck" />
				<label for="Terms1"><spring:theme code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${getTermsAndConditionsUrl}" /></label>
			</div>
		</form:form>
	</div>
	
	<!--  HPP Form Start -->
	<c:if test="${cartData.paymentInfo.useHPP}">
		<form id="hppForm" method="post" action="${cartData.paymentInfo.hppURL}" class="create_update_payment_form">
			<c:forEach items="${hppFormData.hppDataMap}" var="hppElement" varStatus="status">
				<c:choose>
				   <c:when test="${hppElement.key == 'brandCode'}">
					    <input type="hidden" id="hppForm_${hppElement.key}" name="${hppElement.key}" value="${cartData.paymentInfo.adyenPaymentBrand}"/>
				   </c:when>
				   <c:when test="${hppElement.key == 'issuerId'}">
					    <input type="hidden" id="hppForm_${hppElement.key}" name="${hppElement.key}" value="${cartData.paymentInfo.issuerId}"/>
				   </c:when>
				   <c:otherwise>  
				      <input type="hidden" id="hppForm_${hppElement.key}" name="${hppElement.key}" value="${hppElement.value}"/>
				   </c:otherwise>
				 </c:choose>
		    </c:forEach>
		</form>
	</c:if>
	<!--  HPP Form End -->
	<multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="true"/>

	<cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

</template:page>
