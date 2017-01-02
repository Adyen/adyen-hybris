<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/mobile/cart"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<jsp:body>
		<div class="item_container_holder">
			<div id="globalMessages" data-theme="b">
				<common:globalMessages />
			</div>

			<cms:pageSlot position="TopContent" var="feature" element="div" id="top-disp-img" class="home-disp-img">
				<cms:component component="${feature}" element="div" class="span-24 cms_disp-img_slot"/>
			</cms:pageSlot>

			<h6 class="descriptionHeadline">
				<spring:theme code="text.headline.register" text="Click here to register a new customer" />
			</h6>
			<div class="registerNewCustomerLinkHolder" data-theme="c" data-content-theme="c">
				<c:url value="/register/checkout" var="registerCheckoutUrl" />
				<a href="${registerCheckoutUrl}" data-role="button" data-theme="c">
					<spring:theme code="register.new.customer" /> &raquo;
				</a>
			</div>
			<div class="loginAndCheckoutLinkHolder">
				<c:url value="/checkout/j_spring_security_check" var="loginAndCheckoutAction" />
				<user:login actionNameKey="checkout.login.loginAndCheckout" action="${loginAndCheckoutAction}" />
			</div>
            <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
				<div class="fakeHR">
					<c:url value="/login/checkout/guest" var="guestCheckoutUrl" />
					<user:guestCheckout actionNameKey="checkout.login.guestCheckout" action="${guestCheckoutUrl}"/>
				</div>
			</sec:authorize>

			<cms:pageSlot position="BottomContent" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
				<cms:component component="${feature}" element="div" class="span-24 cms_disp-img_slot"/>
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
