<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<div class="span-24">
		<div class="span-20 last">
			<div data-role="collapsible-set" data-theme="d">
				<ul data-role="listview" data-inset="true" data-split-theme="b" data-theme="d">
					<cms:pageSlot position="Section1" var="feature"><cms:component component="${feature}" /></cms:pageSlot>
					<cms:pageSlot position="Section2" var="feature"><cms:component component="${feature}" /></cms:pageSlot>
					<cms:pageSlot position="Section3" var="feature"><cms:component component="${feature}" /></cms:pageSlot>
					<cms:pageSlot position="Section4" var="feature"><cms:component component="${feature}" /></cms:pageSlot>
				</ul>
			</div>
		</div>
	</div>
</template:page>
<nav:popupMenu/>
