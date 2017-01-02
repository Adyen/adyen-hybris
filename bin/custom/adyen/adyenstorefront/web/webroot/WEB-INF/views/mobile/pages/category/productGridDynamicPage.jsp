<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>

<template:page pageTitle="${pageTitle}">

	<jsp:body>
		<div id="globalMessages">
			<common:globalMessages/>
		</div>
		<cms:pageSlot position="Section1" var="feature" element="div" class="home-disp-img">
			<cms:component component="${feature}" element="div" class="span-24 section1 exhibit"/>
		</cms:pageSlot>
		<cms:pageSlot position="Section2" var="feature" element="div" id="top-disp-img" class="home-disp-img">
			<cms:component component="${feature}" element="div" class="span-24 cms_disp-img_slot"/>
		</cms:pageSlot>
		<c:if test="${not empty searchPageData.results}">
			<div class="sortingBar item_container_holder">
				<nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="false" showSuggestions="false"/>
			</div>
		</c:if>
		<div id="resultsGrid" class="span-24 productResultsGrid">
			<cms:pageSlot position="Section3" var="feature">
				<cms:component component="${feature}" element="div" class="span-24 section3 exhibit"/>
			</cms:pageSlot>
			<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
				<c:set var="component" value="${gridComponent}" scope="request"/>
				<c:choose>
				<c:when test="${status.first}">
					<div class="ui-grid-a">
						<div class='ui-block-a left'>
							<product:productListerGridItem product="${product}"/>
						</div>
					<c:if test="${status.last}">
					</div>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:choose>
					<c:when test="${(status.count % 2) == 0}">
						<div class='ui-block-b right'>
							<product:productListerGridItem product="${product}"/>
						</div>
					</div>
					</c:when>
					<c:otherwise>
						<div class="ui-grid-a">
							<div class='ui-block-a left'>
								<product:productListerGridItem product="${product}"/>
							</div>
						<c:if test="${status.last}">
						</div>
						</c:if>
					</c:otherwise>
					</c:choose>
				</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
		<cms:pageSlot position="Section4" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
			<cms:component component="${feature}" element="div" class="span-24 section4 exhibit"/>
		</cms:pageSlot>
	</jsp:body>
</template:page>
<nav:facetNavRefinementsJQueryTemplates pageData="${searchPageData}"/>
<nav:resultsGridJQueryTemplates/>
<nav:popupMenu/>
