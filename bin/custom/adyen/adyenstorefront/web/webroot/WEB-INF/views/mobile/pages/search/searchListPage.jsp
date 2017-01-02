<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<div id="globalMessages">
			<common:globalMessages />
		</div>
		<cms:pageSlot position="SearchResultsListSlot" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</jsp:body>
</template:page>
<nav:facetNavRefinementsJQueryTemplates pageData="${searchPageData}" />
<nav:resultsListJQueryTemplates />
<nav:popupMenu />
