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
		<div class="accmob-navigationHolder">
			<div class="accmob-navigationContent">
				<div id="breadcrumb" class="accmobBackLink">
					<nav:breadcrumb breadcrumbs="${breadcrumbs}" />
				</div>
			</div>
		</div>
		<div id="globalMessages">
			<common:globalMessages />
		</div>
		<cms:pageSlot position="Section2" var="feature" element="div" id="top-disp-img" class="home-disp-img">
			<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot"/>
		</cms:pageSlot>
		<div class="searchTopHolder">
			<nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="true" />
		</div>
		<div>
			<ul data-role="listview" id="resultsList" data-inset="true" data-theme="e" data-dividertheme="b">
				<c:forEach items="${searchPageData.results}" var="product">
					<product:productListerItem product="${product}" />
				</c:forEach>
			</ul>
		</div>
		<cms:pageSlot position="Section4" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
			<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot"/>
		</cms:pageSlot>
	</jsp:body>
</template:page>
<nav:facetNavRefinementsJQueryTemplates pageData="${searchPageData}" />
<nav:resultsListJQueryTemplates />
<nav:popupMenu />
