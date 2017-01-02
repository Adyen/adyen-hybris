<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="hideHeaderLinks" required="false" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/mobile/cart" %>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/mobile/common/header"  %>


<h6 class="descriptionHeadline">
	<spring:theme code="text.headline.navigationbar" text="Browse through the navigation bar"/>
</h6>

<div id="top-nav-bar" class="top-nav-bar" data-theme="f" data-role="header">
	<a href="#" id="top-nav-bar-menu" data-role="button" role="button" data-iconpos="notext" data-theme="f" data-icon="home" title="<spring:theme code="text.button.menu"/>">
		<spring:theme code="text.button.menu"/>
	</a>
	<a href="<c:url value="/store-finder"/>" id="top-nav-bar-home" class="ui-btn-left" data-role="button" role="button" data-iconpos="notext" data-theme="f" data-icon="custom-stores-w" title="Store Finder">
		<spring:theme code="text.header.storefinder" text="Store Finder"/>
	</a>

	<cart:miniCart/>

    <c:if test="${empty hideHeaderLinks}">
		<h6 class="descriptionHeadline"><spring:theme code="text.headline.myaccount" text="Click here to login or get to your Account"/></h6>
		<a href="#" id="top-nav-bar-account" class="ui-btn-right" data-role="button" role="button" data-theme="f" data-iconpos="notext" data-icon="user" title="Login and Account">
			<spring:theme code="text.header.loginandaccount" text="Login and Account"/>
		</a>
    </c:if>
	<c:if test="${fn:length(currencies) > 1 and fn:length(languages) > 1}">
		<h6 class="descriptionHeadline">Click here to change the language</h6>
		<a href="#" id="top-nav-bar-settings" class="ui-btn-right" data-role="button" role="button" data-theme="f" data-iconpos="notext" data-icon="globe" title="Language and Currency">
			<spring:theme code="text.header.languageandcurrency" text="Language and Currency"/>
		</a>
	</c:if>
</div>
<h6 class="descriptionHeadline"><spring:theme code="text.headline.search" text="Here you can search for products"/></h6>

<div id="header" data-role="header" data-theme="d">
	<div class="siteLogo">
		<cms:pageSlot position="SiteLogo" var="logo" limit="1">
			<cms:component component="${logo}"/>
		</cms:pageSlot>
	</div>

	<cms:pageSlot position="SearchBox" var="component">
		<cms:component component="${component}"/>
	</cms:pageSlot>
</div>
<%-- the following elements are hidden and should show up by clicking on the corresponding tob-nav-bar button --%>
<div id="menuContainer" class="top-nav-bar-layer accmob-topMenu header-popup menu-container" style="display:none" data-theme="f">
	<h6 class="descriptionHeadline"><spring:theme code="text.headline.categories" text="Click here the menu button to get to the categories"/></h6>
	<ul>
		<li class="La  auto ui-btn ui-btn-up-f ui-btn-icon-right ui-li-has-arrow ui-li">
			<div class="ui-btn-inner ui-li">
				<div class="ui-btn-text">
					<a title="Home" href="<c:url value="/"/>" class="ui-link-inherit"><spring:theme code="menu.button.home" text="Home"/>
					</a>
				</div>
				<span class="ui-icon ui-icon-arrow-r ui-icon-shadow">&nbsp;</span>
			</div>
		</li>
	</ul>
	<cms:pageSlot position="NavigationMenuBar" var="component" element="ul" data-role="listview" data-inset="true" class="menulist" data-theme="f">
		<cms:component component="${component}"/>
	</cms:pageSlot>
</div>
<div id="currencyLanguageSelector" class="top-nav-bar-layer accmob-currencyLanguageSelector header-popup menu-container" style="display:none">
	<template:currencylanguage currencies="${currencies}" currentCurrency="${currentCurrency}" languages="${languages}" currentLanguage="${currentLanguage}"/>
</div>
<c:if test="${empty hideHeaderLinks}">
	<div id="userSettings" class="top-nav-bar-layer user-settings header-popup menu-container" style="display:none; right:0;">
		<ul data-role="listview" data-inset="true" data-theme="f">
			<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
				<li>
					<ycommerce:testId code="header_Login_link">
						<a href="<c:url value='/login'/>"><spring:theme code="header.mobile.link.login"/></a>
					</ycommerce:testId>
				</li>
			</sec:authorize>
			<li>
				<spring:url value="/my-account" var="encodedUrl"/>
				<a href="${encodedUrl}">
					<spring:theme code="text.account.account" text="Account"/>
				</a>
			</li>
			<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
				<li>
					<ycommerce:testId code="header_signOut">
						<a href="<c:url value='/logout'/>"><spring:theme code="text.logout"/></a>
					</ycommerce:testId>
				</li>
			</sec:authorize>
		</ul>
	</div>
</c:if>
