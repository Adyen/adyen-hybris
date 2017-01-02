<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/mobile/cart"%>
<%@ taglib prefix="checkout-cart" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout/multi"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout/multi"%>
<spring:url value="/checkout/multi/summary/placeOrder" var="placeOrderUrl" />
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl" />

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<jsp:attribute name="pageScripts">
		<script type="text/javascript">
			var getTermsAndConditionsUrl = "${getTermsAndConditionsUrl}";
		</script>
	</jsp:attribute>
	<jsp:body>
		<common:globalMessages/>
		<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}" />
		<cms:pageSlot position="TopContent" var="feature" element="div">
			<cms:component component="${feature}" />
		</cms:pageSlot>
		<div data-theme="d" data-role="content">
			<div data-theme="b">
				<checkout:summaryFlow deliveryMode="${deliveryMode}" paymentInfo="${paymentInfo}" requestSecurityCode="${requestSecurityCode}"
						cartData="${cartData}" />
			</div>
		</div>
		<div data-theme="d">
			<form:form action="${placeOrderUrl}" id="placeOrderForm1" commandName="placeOrderForm">
				<common:errors />
				<form:input type="hidden" name="securityCode" class="securityCodeClass" maxlength="5" path="securityCode" />
				<h6 class="descriptionHeadline">
					<spring:theme code="text.headline.terms" text="Agree with the terms and conditions" />
				</h6>
				<span class="termsCheck">
					<form:checkbox id="Terms1" name="Terms1" path="termsCheck" data-theme="d" />
					<label for="Terms1"><spring:theme code="checkout.summary.placeOrder.readTermsAndConditions" /></label>
				</span>
				<ul data-theme="c" data-content-theme="c" class="checkoutSummarySubmit">
					<li>
						<button type="submit" data-icon="arrow-r" data-theme="b" data-iconpos="right" class="positive right pad_right place-order placeOrderWithSecurityCode">
							<spring:theme code="checkout.summary.placeOrder" />
						</button>
					</li>
				</ul>
				<div class="checkoutOverviewItems">
					<div data-theme="b">
						<checkout-cart:summaryCartItems cartData="${cartData}" summary="true" />
						<checkout-cart:cartItemGroupsForPickUp cartData="${cartData}" showAllItems="${showAllItems}" summary="true" />
					</div>
					<div data-theme="b">
						<cart:cartPromotions cartData="${cartData}" />
					</div>
					<div data-theme="b">
						<cart:cartTotals cartData="${cartData}" />
					</div>
				</div>
				<span class="termsCheck">
					<form:checkbox id="Terms2" name="Terms2" path="termsCheck" data-theme="d" />
					<label for="Terms2"><spring:theme code="checkout.summary.placeOrder.readTermsAndConditions" /></label>
				</span>
				<ul data-theme="c" data-content-theme="c" class="checkoutSummarySubmit">
					<li>
						<button type="submit" data-icon="arrow-r" data-theme="b" data-iconpos="right" class="positive right pad_right place-order placeOrderWithSecurityCode show_processing_message">
							<spring:theme code="checkout.summary.placeOrder" />
						</button>
					</li>
				</ul>
			</form:form>

			<cms:pageSlot position="BottomContent" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
				<cms:component component="${feature}" element="div" class="span-24 cms_disp-img_slot"/>
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
