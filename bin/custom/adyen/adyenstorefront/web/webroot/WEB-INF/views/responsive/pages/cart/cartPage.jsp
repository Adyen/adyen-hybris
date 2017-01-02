<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
	
<template:page pageTitle="${pageTitle}">

	<cart:cartValidation/>
	<cart:cartPickupValidation/>

	<div class="cart-top-bar">
		<div class="container">
			<div class="text-right">
				<a href="" class="help js-cart-help" data-help="<spring:theme code="text.help" />"><spring:theme code="text.help" text="Help" />
					<span class="glyphicon glyphicon-info-sign"></span>
				</a>
				<div class="help-popup-content-holder js-help-popup-content">
					<div class="help-popup-content">
						<strong>${cartData.code }</strong>
						<spring:theme code="basket.page.cartHelpContent" text="Need Help? Contact us or call Customer Service at 1-###-###-####. If you are calling regarding your shopping cart, please reference the Shopping Cart ID above." />
					</div>
				</div>
			</div>
		</div>

	</div>

	<div class="container">
	   <c:if test="${not empty cartData.entries}">
			   <cms:pageSlot position="CenterLeftContentSlot" var="feature">
				   <cms:component component="${feature}"/>
			   </cms:pageSlot>
		</c:if>

		<cms:pageSlot position="TopContent" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot> 
		
		 <c:if test="${not empty cartData.entries}">
			<cms:pageSlot position="CenterRightContentSlot" var="feature">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
			<cms:pageSlot position="BottomContentSlot" var="feature">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</c:if>
				
				
		<c:if test="${empty cartData.entries}">
			<cms:pageSlot position="EmptyCartMiddleContent" var="feature" element="div">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</c:if>
	</div>
</template:page>