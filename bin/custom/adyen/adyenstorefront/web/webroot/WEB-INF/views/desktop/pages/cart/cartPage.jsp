<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<spring:theme text="Your Shopping Cart" var="title" code="cart.page.title"/>
<template:page pageTitle="${pageTitle}">
	<spring:theme code="basket.add.to.cart" var="basketAddToCart"/>
	<spring:theme code="cart.page.checkout" var="checkoutText"/>
	<common:globalMessages/>
	<cart:cartValidation/>
	<cart:cartPickupValidation/>
	<c:url value="/cart/checkout" var="checkoutUrl" scope="session"/>
	<c:url value="${continueUrl}" var="continueShoppingUrl" scope="session"/>
	<cms:pageSlot position="TopContent" var="feature" element="div" class="span-24">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<c:if test="${not empty cartData.entries}">
		<div class="clearfix">
			<div class="span-16">
				<cms:pageSlot position="CenterLeftContentSlot" var="feature">
					<cms:component component="${feature}"/>
				</cms:pageSlot>
			</div>
			<div class="span-8 last">
				<cms:pageSlot position="CenterRightContentSlot" var="feature">
					<cms:component component="${feature}"/>
				</cms:pageSlot>
			</div>
		</div>
		<cms:pageSlot position="BottomContentSlot" var="feature" element="div" class="span-24">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</c:if>
	<c:if test="${empty cartData.entries}">
		<div class="span-24">
			<div class="span-24 wide-content-slot cms_disp-img_slot">
				<cms:pageSlot position="EmptyCartMiddleContent" var="feature" element="div">
					<cms:component component="${feature}"/>
				</cms:pageSlot>
			</div>
		</div>
	</c:if>
</template:page>
