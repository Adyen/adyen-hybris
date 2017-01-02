<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<cms:pageSlot position="Section1" var="feature">
		<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot"/>
	</cms:pageSlot>
	<div class="span-24">
		<div class="span-6 facetNavigation">
			<nav:categoryNav pageData="${searchPageData}"/>
			<cms:pageSlot position="Section4" var="feature">
				<cms:component component="${feature}" element="div" class="section4 small_detail"/>
			</cms:pageSlot>
		</div>
		<div class="span-18 last">
			<cms:pageSlot position="Section2" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
			<cms:pageSlot position="Section3" var="feature" element="div" class="span-18 last">
				<cms:component component="${feature}" element="div" class="span-6 section3 cms_disp-img_slot ${(elementPos%3 == 2) ? 'last' : ''}"/>
			</cms:pageSlot>
		</div>
	</div>
</template:page>