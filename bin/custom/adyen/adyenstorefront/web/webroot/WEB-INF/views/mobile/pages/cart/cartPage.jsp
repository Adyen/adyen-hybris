<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/mobile/cart"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/mobile/storepickup"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<spring:theme text="Your Shopping Cart" var="title" code="cart.page.title" />
<c:url value="/cart/checkout" var="checkoutUrl" />
<c:url value="${continueUrl}" var="continueShoppingUrl" />
<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<common:globalMessages/>
		<cart:cartValidation/>
		<cart:cartPickupValidation/>
		<c:if test="${not empty message}">
			<br />
			<span class="errors"><spring:theme code="${message}" /></span>
		</c:if>
		<cms:pageSlot position="TopContent" var="feature" element="div">
			<cms:component component="${feature}" />
		</cms:pageSlot>
		<c:if test="${not empty cartData.entries}">
			<br />
			<cart:cartItems cartData="${cartData}" />
			<br />
			<cart:cartPromotions cartData="${cartData}" />
			<cart:cartTotals cartData="${cartData}" />

			<sec:authorize access="isFullyAuthenticated()">
				<cart:cartExpressCheckoutEnabled/>
			</sec:authorize>

			<div class="ui-grid-a">
				<div class="ui-block-a">
					<a href="${continueShoppingUrl}" data-theme="d" data-role="button" data-icon="arrow-l">
						<spring:theme text="Continue Shopping" code="cart.page.shop" />
					</a>
				</div>
				<div class="ui-block-b">
					<a href="${checkoutUrl}" id="checkoutButton" data-role="button" data-theme="b" data-icon="arrow-r" data-iconpos="right" class>
						<spring:theme code="checkout.checkout" />
					</a>
				</div>
			</div>
		</c:if>
		<c:if test="${empty cartData.entries}">
			<div class="accmob-navigationHolder">
				<div class="accmob-navigationContent">
					<div id="breadcrumb" class="accmobBackLink">
						<nav:breadcrumb breadcrumbs="${breadcrumbs}" />
					</div>
				</div>
			</div>
			<cms:pageSlot position="MiddleContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</c:if>
		<c:if test="${not empty cartData.entries}">
			<cart:cartPotentialPromotions cartData="${cartData}" />
			<cms:pageSlot position="Suggestions" var="feature" element="div" class="productAccordeon">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</c:if>


		<cms:pageSlot position="BottomContent" var="feature" element="div">
			<cms:component component="${feature}" />
		</cms:pageSlot>
	</jsp:body>
</template:page>
<c:forEach items="${cartData.entries}" var="entry">
	<storepickup:findPickupStores product="${entry.product}" cartPage="${true}" entryNumber="${entry.entryNumber}" qty="${entry.quantity}"/>
</c:forEach>
<nav:popupMenu />
