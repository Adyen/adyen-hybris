<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<template:page pageTitle="${pageTitle}">

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	
	<cms:pageSlot position="TopContent" var="feature" element="div" class="span-24 cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

	<div class="span-24">
		<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
			<c:set var="spanStyling" value="span-8"/>
		</sec:authorize>
		<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
			<c:set var="spanStyling" value="span-12"/>
			<c:set var="spanStylingLast" value=" last"/>
		</sec:authorize>

		<div class="${spanStyling}">
			<c:url value="/login/checkout/register" var="registerAndCheckoutActionUrl" />
			<user:register actionNameKey="checkout.login.registerAndCheckout" action="${registerAndCheckoutActionUrl}"/>
		</div>

		<div class="${spanStyling}${spanStylingLast}">
			<c:url value="/checkout/j_spring_security_check" var="loginAndCheckoutActionUrl" />
			<user:login actionNameKey="checkout.login.loginAndCheckout" action="${loginAndCheckoutActionUrl}"/>
		</div>

		<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
			<div class="${spanStyling} last">
				<c:url value="/login/checkout/guest" var="guestCheckoutUrl" />
				<user:guestCheckout actionNameKey="checkout.login.guestCheckout" action="${guestCheckoutUrl}"/>
			</div>
		</sec:authorize>
	</div>
	
	<cms:pageSlot position="BottomContent" var="feature" element="div" class="span-24 cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

</template:page>
