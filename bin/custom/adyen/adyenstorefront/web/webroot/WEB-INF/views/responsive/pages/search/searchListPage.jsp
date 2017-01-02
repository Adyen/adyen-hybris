<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>

<template:page pageTitle="${pageTitle}">

	<div class="row">
		<cms:pageSlot position="ProductLeftRefinements" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>

		<cms:pageSlot position="SearchResultsListSlot" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</div>

	<storepickup:pickupStorePopup />

</template:page>
