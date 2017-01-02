<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:theme code="updatePwd.title" var="title" />
<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages />
	</div>

	<cms:pageSlot position="Section1" var="feature" element="div">
		<cms:component component="${feature}" />
	</cms:pageSlot>

	<user:updatePwd />

	<cms:pageSlot position="Section5" var="feature" element="div">
		<cms:component component="${feature}" />
	</cms:pageSlot>
</template:page>
