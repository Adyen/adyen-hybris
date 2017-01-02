<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:theme text="Your Shopping Cart" var="title" code="cart.page.title"/>
<c:url value="/cart/checkout" var="checkoutUrl"/>

<template:page pageTitle="${pageTitle}">


	<spring:theme code="basket.add.to.cart" var="basketAddToCart"/>
	<spring:theme code="cart.page.checkout" var="checkoutText"/>
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<common:globalMessages/>
	<cart:cartRestoration/>
	<cart:cartValidation/>
	<cart:cartPickupValidation/>
	
	<cms:pageSlot position="TopContent" var="feature" element="div" class="span-24">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

			<c:if test="${not empty cartData.entries}">
				<spring:url value="${continueUrl}" var="continueShoppingUrl" htmlEscape="true"/>

					<button id="checkoutButtonTop" class="doCheckoutBut positive right" type="button" data-checkout-url="${checkoutUrl}"><spring:theme code="checkout.checkout" /></button>
					<cart:cartItems cartData="${cartData}"/>
	
						<div class="clearfix">
							<div class="span-16">
								<cart:cartPromotions cartData="${cartData}"/>
								&nbsp;
								<cart:cartPotentialExpressCheckoutInfoBox/>
							</div>
							<div class="span-8 last">
								<cart:cartTotals cartData="${cartData}" showTaxEstimate="${taxEstimationEnabled}"/>
							</div>
						</div>
				
					<a class="button" href="${continueShoppingUrl}"><spring:theme text="Continue Shopping" code="cart.page.continue"/></a>
					<button id="checkoutButtonBottom" class="doCheckoutBut positive right" type="button" data-checkout-url="${checkoutUrl}"><spring:theme code="checkout.checkout" /></button>
			</c:if>
			
			
			<c:if test="${empty cartData.entries}">
				<div class="span-24">
					<div class="span-24 wide-content-slot cms_disp-img_slot">
						<cms:pageSlot position="MiddleContent" var="feature" element="div">
							<cms:component component="${feature}"/>
						</cms:pageSlot>

						<cms:pageSlot position="BottomContent" var="feature" element="div">
							<cms:component component="${feature}"/>
						</cms:pageSlot>
					</div>
				</div>
			</c:if>


			<c:if test="${not empty cartData.entries}">
				<cart:cartPotentialPromotions cartData="${cartData}"/>
			</c:if>
	
		
		<c:if test="${showCheckoutStrategies && not empty cartData.entries}" >
			<div class="span-24">
				<div class="right">
					<input type="hidden" name="flow" id="flow"/>
					<input type="hidden" name="pci" id="pci"/>
					<select id="selectAltCheckoutFlow" class="doFlowSelectedChange">
						<option value="multistep"><spring:theme code="checkout.checkout.flow.select"/></option>
						<option value="multistep"><spring:theme code="checkout.checkout.multi"/></option>
						<option value="multistep-pci"><spring:theme code="checkout.checkout.multi.pci"/></option>
					</select>
					<select id="selectPciOption" style="margin-left: 10px; display: none;">
						<option value=""><spring:theme code="checkout.checkout.multi.pci.select"/></option>
						<c:if test="${!isOmsEnabled}">
							<option value="default"><spring:theme code="checkout.checkout.multi.pci-ws"/></option>
							<option value="hop"><spring:theme code="checkout.checkout.multi.pci-hop"/></option>
						</c:if>
						<option value="sop"><spring:theme code="checkout.checkout.multi.pci-sop" text="PCI-SOP" /></option>
					</select>
				</div>
			</div>
		</c:if>

		<c:if test="${not empty cartData.entries}" >
			<cms:pageSlot position="Suggestions" var="feature" element="div" class="span-24">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</c:if>

	<cms:pageSlot position="BottomContent" var="feature" element="div" class="span-24">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	
	

</template:page>
