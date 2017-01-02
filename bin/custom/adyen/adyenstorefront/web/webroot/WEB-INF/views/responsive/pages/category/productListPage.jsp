<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>

<template:page pageTitle="${pageTitle}">

	<div class="row">
		<cms:pageSlot position="ProductLeftRefinements" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>

		<cms:pageSlot position="ProductListSlot" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>


	</div>

</template:page>
