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

			<cms:pageSlot position="TopContentSlot" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>

			<cms:pageSlot position="BottomContentSlot" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
