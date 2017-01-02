<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %>
<%@ taglib prefix="category" tagdir="/WEB-INF/tags/mobile/category" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<template:page pageTitle="${pageTitle}">
	<jsp:body>
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
			<cms:component component="${feature}" element="div" class="section2 exhibit"/>
		</cms:pageSlot>

		<h6 class="descriptionHeadline">
			<spring:theme code="text.headline.refinements" text="Choose the relevance or add refinements"/>
		</h6>

		<div class="productResultsList">
			<cms:pageSlot position="Section3" var="feature">
				<cms:component component="${feature}" element="div" class="span-24 section3 exhibit"/>
			</cms:pageSlot>

			<c:if test="${empty searchPageData.results}">
				<ul id="categoryResultsList" data-role="listview" data-inset="true" data-theme="e" data-content-theme="e" class="mainNavigation">
					<category:categoryList pageData="${searchPageData}"/>
				</ul>
			</c:if>
			<c:if test="${not empty searchPageData.results}">
				<div class="sortingBar item_container_holder">
					<nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="false"/>
				</div>
				<ul id="resultsList" data-role="listview" data-inset="true" data-theme="e" data-content-theme="e" class="mainNavigation">
					<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
						<product:productListerItem product="${product}"/>
					</c:forEach>
				</ul>
			</c:if>
		</div>
		<cms:pageSlot position="Section4" var="feature">
			<cms:component component="${feature}" element="div" class="span-4 section4 exhibit last"/>
		</cms:pageSlot>
	</jsp:body>
</template:page>
<nav:facetNavRefinementsJQueryTemplates pageData="${searchPageData}"/>
<nav:resultsListJQueryTemplates/>
<nav:popupMenu/>
