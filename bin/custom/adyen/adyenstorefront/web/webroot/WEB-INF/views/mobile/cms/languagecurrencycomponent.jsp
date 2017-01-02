<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>

<div id="currencyLanguageSelector">
	<div id="currency" class="top-nav-bar" data-theme="f" data-role="header">
	<c:if test="${fn:length(currencies) > 1 and fn:length(languages) > 1}">
		<h6 class="descriptionHeadline">Click here to change the language</h6>
		<a href="#" id="top-nav-bar-settings" data-role="button" role="button" data-theme="f" data-iconpos="notext" data-icon="globe" title="Language and Currency">
			<spring:theme code="text.header.languageandcurrency" text="Language and Currency"/>
		</a>
	</c:if>
	</div>

	<div class="top-nav-bar-layer accmob-currencyLanguageSelector header-popup menu-container" style="display:none">
		<template:currencylanguage currencies="${currencies}" currentCurrency="${currentCurrency}" languages="${languages}" currentLanguage="${currentLanguage}"/>
	</div>
</div>
