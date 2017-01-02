<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<cms:pageSlot position="Section1" var="feature">
		<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot"/>
	</cms:pageSlot>
	<div class="span-24 section2">
		<cms:pageSlot position="Section2A" var="feature" element="div" class="span-4 zone_a cms_disp-img_slot">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
		<cms:pageSlot position="Section2B" var="feature" element="div" class="span-20 zone_b last">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</div>
	<cms:pageSlot position="Section3" var="feature" element="div" class="span-24 section3 cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
</template:page>