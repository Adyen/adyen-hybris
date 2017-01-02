<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<div id="top-disp-img" class="home-disp-img">
		<h6 class="descriptionHeadline"><spring:theme code="text.headline.homebanner" text="Top Banner"/></h6>
		<cms:pageSlot position="Section1" var="feature" element="div" id="categoryImage-Disp">
			<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot"/>
		</cms:pageSlot>
	</div>

	<cms:pageSlot position="Section2" var="feature" element="div" class="home-disp-img">
		<cms:component component="${feature}" element="div" class="span-24 section2 cms_disp-img_slot"/>
	</cms:pageSlot>

	<cms:pageSlot position="Section3" var="feature" element="div" class="home-disp-img">
		<cms:component component="${feature}" element="div" class="span-24 section3 cms_disp-img_slot"/>
	</cms:pageSlot>

	<div class="mainNavigation" data-role="content" data-theme="e" data-content-theme="e">
		<h6 class="descriptionHeadline"><spring:theme code="text.headline.productcategories" text="Product categories"/></h6>
		<cms:pageSlot position="NavigationBar" var="feature" element="ul" data-role="listview" data-inset="true" data-theme="e">
			<cms:component component="${feature}" />
		</cms:pageSlot>
	</div>

	<cms:pageSlot position="Section4" var="feature" element="div" class="home-disp-img">
		<cms:component component="${feature}" element="div" class="span-24 section4 cms_disp-img_slot"/>
	</cms:pageSlot>

	<div id="bottom-disp-img" class="home-disp-img">
		<h6 class="descriptionHeadline"><spring:theme code="text.headline.bottombanner" text="Bottom Banner"/></h6>
		<cms:pageSlot position="Section5" var="feature">
			<cms:component component="${feature}" element="div" class="span-24 section5 cms_disp-img_slot"/>
		</cms:pageSlot>
	</div>
</template:page>
