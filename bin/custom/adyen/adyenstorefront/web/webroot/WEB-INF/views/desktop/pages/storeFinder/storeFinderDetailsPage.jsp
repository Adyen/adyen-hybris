<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/desktop/store" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<template:page pageTitle="${store.name} | ${siteName}">

		<div id="breadcrumb" class="breadcrumb">
			<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
		</div>
		<div id="globalMessages">
			<common:globalMessages/>
		</div>

		<div id="storeDetail">
			<div class="detailPane">
				<div class="headline"><spring:theme code="storeDetails.title" /></div>
				<ycommerce:testId code="storeFinder_storeDetails_label">
					<store:storeDetails store="${store}"/>
					<store:storeImage store="${store}" format="store"/>
				</ycommerce:testId>
			</div>
			<store:storeMap store="${store}"/>
		</div>

</template:page>