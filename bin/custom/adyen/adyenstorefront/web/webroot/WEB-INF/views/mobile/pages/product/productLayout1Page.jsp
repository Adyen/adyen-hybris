<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/mobile/storepickup"%>

<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<div id="globalMessages">
			<common:globalMessages />
		</div>

		<cms:pageSlot position="Section1" var="feature" element="div" class="span-4 section1 cms_disp-img_slot">
			<cms:component component="${feature}" />
		</cms:pageSlot>

		<h2 class="productHeadline">
			<div class="productTitle">${product.name}</div>
		</h2>
		<div class="productRatingHolder">
			<product:productAverageReviewDetailPage product="${product}" />
		</div>
		<product:productImage product="${product}" format="product" />
		<cms:pageSlot position="VariantSelector" var="component" element="div">
			<cms:component component="${component}"/>
		</cms:pageSlot>
		
		<cms:pageSlot position="AddToCart" var="comp" element="div">
			<cms:component component="${comp}" />
		</cms:pageSlot>
		<c:if test="${not empty product.description}">
			<div class="item_container_holder continuous-text" id="productDescription">${product.summary}</div>
		</c:if>
		<h6 class="descriptionHeadline">
			<spring:theme code="text.headline.productinfo" text="Click to get more information about product overview, product review or delivery method" />
		</h6>
		<cms:pageSlot position="Section2" var="feature" element="div" class="span-4 section2 cms_disp-img_slot">
			<cms:component component="${feature}" />
		</cms:pageSlot>
		<div class="productAccordeon productInfos">
			<product:productOverview product="${product}" />
			<product:productReviews product="${product}" />
			<cms:pageSlot position="Tabs" var="tabs">
				<cms:component component="${tabs}" />
			</cms:pageSlot>
		</div>

		<cms:pageSlot position="Section3" var="feature" element="div" class="span-4 section3 cms_disp-img_slot">
			<cms:component component="${feature}" />
		</cms:pageSlot>

		<cms:pageSlot position="UpSelling" var="comp" element="div" class="productAccordeon">
			<cms:component component="${comp}" />
		</cms:pageSlot>

		<cms:pageSlot position="CrossSelling" var="comp" element="div" class="productAccordeon">
			<cms:component component="${comp}" />
		</cms:pageSlot>

		<cms:pageSlot position="Section4" var="feature" element="div" class="span-4 section4 cms_disp-img_slot">
			<cms:component component="${feature}" />
		</cms:pageSlot>

	</jsp:body>
</template:page>
<storepickup:findPickupStores product="${product}"/>
<c:forEach items="${productReferences}" var="productReference">
	<storepickup:findPickupStores product="${productReference.target}"/>
</c:forEach>
<nav:popupMenu />
