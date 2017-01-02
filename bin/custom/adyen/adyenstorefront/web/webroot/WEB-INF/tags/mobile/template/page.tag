<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/mobile/common/header"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/mobile/common/footer"%>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="pageCss" required="false" fragment="true"%>
<%@ attribute name="pageScripts" required="false" fragment="true"%>
<%@ attribute name="pageScriptsBeforeJspBody" required="false" fragment="true"%>
<%@ attribute name="pageScriptsAfterJspBody" required="false" fragment="true"%>
<%@ attribute name="hideHeader" required="false"%>
<%@ attribute name="hideFooter" required="false"%>
<%@ attribute name="hideHeaderLinks" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<template:master pageTitle="${pageTitle}">

	<jsp:attribute name="pageCss">
		<jsp:invoke fragment="pageCss"/>
	</jsp:attribute>

	<jsp:attribute name="pageScriptsBeforeJspBody">
		<jsp:invoke fragment="pageScriptsBeforeJspBody"/>
	</jsp:attribute>

	<jsp:attribute name="pageScripts">
		<jsp:invoke fragment="pageScripts"/>
	</jsp:attribute>

	<jsp:attribute name="pageScriptsAfterJspBody">
		<jsp:invoke fragment="pageScriptsAfterJspBody"/>
	</jsp:attribute>

	<jsp:body>
		<div id="body" data-role="page" data-url="<c:url value="/"/>" data-title="${pageTitle}">
			<spring:theme code="text.skipToContent" var="skipToContent"/>
			<a href="#skip-to-content" class="skiptocontent" tabindex="0">${skipToContent}</a>
			<c:if test="${empty hideHeader}">
				<header:header hideHeaderLinks="${hideHeaderLinks}"/>
			</c:if>
			<div data-role="content" data-theme="d" data-content-theme="d" id="skip-to-content">
				<jsp:doBody/>
			</div>
			<c:if test="${empty hideFooter}">
				<footer:footer/>
			</c:if>
		</div>
		<nav:connectMenu/>
	</jsp:body>
</template:master>
