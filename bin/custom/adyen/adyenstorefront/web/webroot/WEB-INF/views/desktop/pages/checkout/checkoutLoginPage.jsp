<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<div class="span-24">
		<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
			<c:set var="spanStyling" value="span-8"/>
		</sec:authorize>
		<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
			<c:set var="spanStyling" value="span-12"/>
			<c:set var="spanStylingLast" value=" last"/>
		</sec:authorize>
		<cms:pageSlot position="LeftContentSlot" var="feature" element="div" class="${spanStyling}">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
		<cms:pageSlot position="CenterContentSlot" var="feature" element="div" class="${spanStyling}${spanStylingLast}">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
		<cms:pageSlot position="RightContentSlot" var="feature" element="div" class="${spanStyling} last">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</div>
</template:page>