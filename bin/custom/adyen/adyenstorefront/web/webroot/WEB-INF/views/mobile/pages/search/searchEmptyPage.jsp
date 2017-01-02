<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<template:page pageTitle="${pageTitle}">
	<c:if test="${not empty message}">
		<spring:theme code="${message}" />
	</c:if>
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<div class="span-20 right last" data-theme="d">
		<div class="item_container_holder" data-theme="d">
			<div class="item_container no-search-result">
				<cms:pageSlot position="MiddleContent" var="comp" element="div">
					<cms:component component="${comp}" />
				</cms:pageSlot>
				<nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />
				<c:url value="/" var="homeUrl" />
				<a href="${homeUrl}" data-role="button" data-theme="c">
					<spring:theme code="text.link.home.label" text="Home" />
				</a>
			</div>
		</div>
	</div>
</template:page>
