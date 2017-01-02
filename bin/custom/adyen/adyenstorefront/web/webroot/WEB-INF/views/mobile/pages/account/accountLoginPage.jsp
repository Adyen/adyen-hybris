<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<div class="item_container_holder">
			<div id="globalMessages" data-theme="b">
				<common:globalMessages/>
			</div>
			<cms:pageSlot position="TopContent" var="feature" element="div" id="top-disp-img" class="home-disp-img">
				<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot"/>
			</cms:pageSlot>
			<h6 class="descriptionHeadline">
				<spring:theme code="text.headline.register" text="Click here to register a new customer"/>
			</h6>
			<div class="registerNewCustomerLinkHolder" data-theme="e" data-content-theme="e">
				<c:url value="/register" var="registerUrl"/>
				<a href="${registerUrl}" data-role="button" data-theme="c">
					<spring:theme code="register.new.customer"/>
				</a>
			</div>
			<div class="fakeHR"></div>
			<div class="loginLinkHolder">
				<c:url value="/j_spring_security_check" var="loginAction"/>
				<user:login actionNameKey="login.login" action="${loginAction}"/>
			</div>
			<cms:pageSlot position="BottomContent" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
				<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot"/>
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
