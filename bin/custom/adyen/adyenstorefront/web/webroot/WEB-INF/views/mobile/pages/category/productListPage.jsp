<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>

<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<div id="globalMessages">
			<common:globalMessages/>
		</div>
		<cms:pageSlot position="ProductListSlot" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>

	</jsp:body>
</template:page>
<nav:facetNavRefinementsJQueryTemplates pageData="${searchPageData}"/>
<nav:popupMenu/>
