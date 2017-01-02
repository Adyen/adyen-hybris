<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<template:page pageTitle="${pageTitle}">
	

	<c:if test="${not empty message}">
		<spring:theme code="${message}"/>
	</c:if>
	
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	
	<div id="globalMessages">
		<common:globalMessages/>
	</div>

	<cms:pageSlot position="Section1" var="comp" element="div" class="span-24 section1 cms_disp-img_slot">
		<cms:component component="${comp}"/>
	</cms:pageSlot>

	<product:productDetailsPanel product="${product}" galleryImages="${galleryImages}"/>
		
	<cms:pageSlot position="CrossSelling" var="comp" element="div" class="span-24">
		<cms:component component="${comp}"/>
	</cms:pageSlot>

	<cms:pageSlot position="Section3" var="feature" element="div" class="span-20 section3 cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

	<cms:pageSlot position="UpSelling" var="comp" element="div" class="span-24">
		<cms:component component="${comp}"/>
	</cms:pageSlot>
	
	<product:productPageTabs />

	<cms:pageSlot position="Section4" var="feature" element="div" class="span-24 section4 cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	

</template:page>
