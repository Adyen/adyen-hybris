<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<template:page pageTitle="${pageTitle}">

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	
	<cms:pageSlot position="Section1" var="feature">
		<cms:component component="${feature}" element="div" class="section1"/>
	</cms:pageSlot>

	<div class="span-24">
		<cms:pageSlot position="Section2A" var="feature" element="div" class="span-6 zoneA">
			<cms:component component="${feature}" />
		</cms:pageSlot>

		<cms:pageSlot position="Section2B" var="feature" element="div" class="span-6 zoneB ">
			<cms:component component="${feature}" />
		</cms:pageSlot>

		<cms:pageSlot position="Section2C" var="feature" element="div" class="span-12 last zoneC ">
			<cms:component component="${feature}" element="div"/>
		</cms:pageSlot>
	</div>

	<cms:pageSlot position="Section3" var="feature" element="div" class="span-24">
		<cms:component component="${feature}" />
	</cms:pageSlot>

	<cms:pageSlot position="Section4" var="feature" element="div" class="span-24 section4">
		<cms:component component="${feature}" element="div" class="span-6  ${(elementPos%4 == 3) ? 'last' : ''}"/>
	</cms:pageSlot>

	<cms:pageSlot position="Section5" var="feature" element="div" class="span-24 section5 cms_disp-img_slot">
		<cms:component component="${feature}" />
	</cms:pageSlot>

</template:page>