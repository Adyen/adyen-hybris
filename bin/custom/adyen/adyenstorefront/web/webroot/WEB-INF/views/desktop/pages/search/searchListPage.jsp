<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<div class="span-6 facetNavigation">
		<cms:pageSlot position="ProductLeftRefinements" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</div>
	<div class="span-18 last">
		<cms:pageSlot position="SearchResultsListSlot" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</div>
	<storepickup:pickupStorePopup />
</template:page>