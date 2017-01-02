<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<template:page pageTitle="${pageTitle}">

	<c:url value="/" var="homePageUrl" />
	
	<div class="search-empty">
		<div class="headline">
			<spring:theme code="search.no.results" text="0 items found for keyword <strong>${searchPageData.freeTextSearch}</strong>" arguments="${searchPageData.freeTextSearch}"/> 
		</div>
		
			<a class="btn btn-default  js-shopping-button" href="${homePageUrl}">
				<spring:theme code="general.continue.shopping" text="Continue Shopping"/>
			</a>
	</div>
	
</template:page>