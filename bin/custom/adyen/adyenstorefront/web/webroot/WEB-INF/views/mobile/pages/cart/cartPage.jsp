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
<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<common:globalMessages/>
		<cart:cartValidation/>
		<cart:cartPickupValidation/>
		<c:if test="${not empty message}">
			<br />
			<span class="errors"><spring:theme code="${message}" /></span>
		</c:if>

		<c:if test="${not empty cartData.entries}">
			<c:url value="/cart/checkout" var="checkoutUrl" scope="session" />
			<c:url value="${continueUrl}" var="continueShoppingUrl" scope="session" />

			<cms:pageSlot position="TopContentSlot" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</c:if>


		<c:if test="${empty cartData.entries}">
			<cms:pageSlot position="EmptyCartTopContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>

			<cms:pageSlot position="EmptyCartMiddleContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
			
			<cms:pageSlot position="EmptyCartBottomContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</c:if>

	</jsp:body>
</template:page>
<c:forEach items="${cartData.entries}" var="entry">
	<storepickup:findPickupStores product="${entry.product}" cartPage="${true}" entryNumber="${entry.entryNumber}" qty="${entry.quantity}"/>
</c:forEach>
<nav:popupMenu />
