<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/desktop/store" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<template:page pageTitle="${pageTitle}">

		
		<div id="breadcrumb" class="breadcrumb">
			<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
		</div>
		<div id="globalMessages">
			<common:globalMessages/>
		</div>
		
		<cms:pageSlot position="TopContent" var="feature">
			<cms:component component="${feature}"  element="div" class="top-content-slot cms_disp-img_slot"  />
		</cms:pageSlot>

		<div id="storeFinder">
			<store:storeSearch errorNoResults="${errorNoResults}"/>
			<store:storesMap storeSearchPageData="${searchPageData}"/>
			<store:storeListForm searchPageData="${searchPageData}" locationQuery="${locationQuery}"  numberPagesShown="${numberPagesShown}" geoPoint="${geoPoint}"/>
		</div>

</template:page>
