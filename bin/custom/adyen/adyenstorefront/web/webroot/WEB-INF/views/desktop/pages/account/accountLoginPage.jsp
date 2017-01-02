<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<div class="span-24">
		<cms:pageSlot position="LeftContentSlot" var="feature" element="div" class="span-12">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
		<cms:pageSlot position="RightContentSlot" var="feature" element="div" class="span-12 last">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</div>
</template:page>