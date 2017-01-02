<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<cms:pageSlot position="Section1" var="feature">
		<cms:component component="${feature}" element="div" class="span-24 section1"/>
	</cms:pageSlot>
	<div class="span-24">
		<cms:pageSlot position="Section3" var="feature" element="div" class="span-4 section3 cms_disp-img_slot">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
		<div class="span-20 last">
			<cms:pageSlot position="Section2" var="feature" element="div" class="span-20 section2 cms_disp-img_slot">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
			<cms:pageSlot position="Section4" var="feature" element="div" class="span-20 last">
				<cms:component component="${feature}" element="div" class="span-6 section4 small_detail ${(elementPos%3 == 2) ? 'last' : ''}"/>
			</cms:pageSlot>
			<cms:pageSlot position="Section5" var="feature" element="div" class="span-20 section5 cms_disp-img_slot last">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
	</div>
</template:page>