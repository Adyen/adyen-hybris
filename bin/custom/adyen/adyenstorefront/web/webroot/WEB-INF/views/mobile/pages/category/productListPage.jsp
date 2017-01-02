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
		<div class="accmob-navigationHolder">
			<div class="accmob-navigationContent">
				<div id="breadcrumb" class="accmobBackLink">
					<nav:breadcrumb breadcrumbs="${breadcrumbs}"/>
				</div>
			</div>
		</div>
		<cms:pageSlot position="Section1" var="feature" element="div" class="home-disp-img">
			<cms:component component="${feature}" element="div" class="span-24 section1 exhibit"/>
		</cms:pageSlot>

		<cms:pageSlot position="Section2" var="feature" element="div" id="top-disp-img" class="home-disp-img">
			<cms:component component="${feature}" element="div" class="span-24 section2 exhibit"/>
		</cms:pageSlot>

		<c:if test="${not empty searchPageData.results}">
			<div class="sortingBar item_container_holder">
				<nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="false"/>
			</div>
		</c:if>
		<nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/>
		<div class="productResultsList">
			<cms:pageSlot position="Section3" var="feature">
				<cms:component component="${feature}" element="div" class="span-24 section3 exhibit"/>
			</cms:pageSlot>

			<c:if test="${not empty searchPageData.results}">
				<ul data-role="listview" data-inset="true" data-theme="e" data-content-theme="e" class="mainNavigation">
					<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
						<product:productListerItem product="${product}"/>
					</c:forEach>
				</ul>
			</c:if>
		</div>
		<nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/>

		<cms:pageSlot position="Section4" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
			<cms:component component="${feature}" element="div" class="span-24 section4 exhibit"/>
		</cms:pageSlot>
	</jsp:body>
</template:page>
<nav:facetNavRefinementsJQueryTemplates pageData="${searchPageData}"/>
<nav:popupMenu/>
