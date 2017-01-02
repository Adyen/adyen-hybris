<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
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

		<cms:pageSlot position="Section1" var="feature" element="div" id="top-disp-img" class="home-disp-img">
			<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot"/>
		</cms:pageSlot>

		<div class="span-4">
			<nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="true" />
			<nav:facetNavRefinements pageData="${searchPageData}" />
		</div>
		<div class="span-20 last">
			<div class="span-20">
				<nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}" />
			</div>
			<div class="span-24 productResultsGrid">
				<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
					<c:choose>
						<c:when test="${status.first}">
							<div class="ui-grid-a">
								<div class='ui-block-a left'>
									<product:productListerGridItem product="${product}" />
								</div>
						</c:when>
						<c:otherwise>
							<c:if test="${(status.count % 2) == 0}">
								<div class='ui-block-b right'>
									<product:productListerGridItem product="${product}" />
								</div>
							</div>
							</c:if>
							<c:if test="${(status.count % 2) == 1}">
								<div class="ui-grid-a">
									<div class='ui-block-a left'>
										<product:productListerGridItem product="${product}" />
									</div>
								<c:if test="${status.last}">
								</div>
								</c:if>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</div>
			<div class="span-20 last">
				<nav:pagination searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}" />
			</div>

			<cms:pageSlot position="Section2" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
				<cms:component component="${feature}" element="div" class="span-24 section2 cms_disp-img_slot"/>
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
<nav:facetNavRefinementsJQueryTemplates pageData="${searchPageData}" />
<nav:resultsListJQueryTemplates />
<nav:popupMenu />
