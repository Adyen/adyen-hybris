<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<template:page pageTitle="${pageTitle}">
	<div class="item_container_holder_error">
		<h2>
			<spring:theme code="system.error.page.not.found" />
		</h2>
		<div id="globalMessages">
			<common:globalMessages />
		</div>

		<cms:pageSlot position="TopContent" var="feature" element="div">
			<cms:component component="${feature}" />
		</cms:pageSlot>

		<div>
			<c:if test="${not empty message}">
				<c:out value="${message}" />
			</c:if>
		</div>

		<cms:pageSlot position="MiddleContent" var="comp" element="div">
			<cms:component component="${comp}"/>
		</cms:pageSlot>

		<cms:pageSlot position="BottomContent" var="feature" element="div">
			<cms:component component="${feature}" />
		</cms:pageSlot>
	</div>
</template:page>
