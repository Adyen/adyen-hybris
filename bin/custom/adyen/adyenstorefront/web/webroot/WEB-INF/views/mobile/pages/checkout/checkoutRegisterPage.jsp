<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<div class="item_container_holder">
			<div id="globalMessages" data-theme="b">
				<common:globalMessages />
			</div>
			<cms:pageSlot position="TopContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
			<div data-role="content">
				<c:url value="/register/checkout/newcustomer" var="registerAndCheckoutAction" />
				<user:register actionNameKey="checkout.login.registerAndCheckout" action="${registerAndCheckoutAction}" />
			</div>
			<cms:pageSlot position="BottomContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
