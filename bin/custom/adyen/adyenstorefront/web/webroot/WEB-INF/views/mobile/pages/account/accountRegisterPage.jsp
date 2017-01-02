<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="hideBreadcrumb" value="true" scope="request" />
<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<div class="item_container_holder">
			<div id="globalMessages" data-theme="b">
				<common:globalMessages />
			</div>
			<cms:pageSlot position="TopRegisterSlot" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
			<cms:pageSlot position="BottomContentSlot" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
