<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<cms:pageSlot position="TopContent" var="feature">
		<cms:component component="${feature}"  element="div" class="top-content-slot cms_disp-img_slot"  />
	</cms:pageSlot>
	<div id="storeFinder">
		<cms:pageSlot position="MiddleContent" var="feature">
			<cms:component component="${feature}"  element="div"/>
		</cms:pageSlot>
	</div>
</template:page>