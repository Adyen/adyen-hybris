<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="metaDescription" required="false"%>
<%@ attribute name="metaKeywords" required="false"%>
<%@ attribute name="pageCss" required="false" fragment="true"%>
<%@ attribute name="pageScripts" required="false" fragment="true"%>
<%@ attribute name="pageScriptsBeforeJspBody" required="false" fragment="true"%>
<%@ attribute name="pageScriptsAfterJspBody" required="false" fragment="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="analytics" tagdir="/WEB-INF/tags/shared/analytics"%>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/shared/debug"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="htmlmeta" uri="http://hybris.com/tld/htmlmeta" %>
<%@ taglib prefix="addonScripts" tagdir="/WEB-INF/tags/mobile/common/header" %>

<!DOCTYPE html>
<html lang="${currentLanguage.isocode}">
	<head>
		<meta charset="utf-8"/>
		<title>
			${not empty pageTitle ? pageTitle : not empty cmsPage.title ? cmsPage.title : 'Accelerator Title'}
		</title>
		
		<%-- Meta Content --%>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		
		<%-- Additional meta tags --%>
		<htmlmeta:meta items="${metatags}"/>

		<%-- Favourite Icon --%>
		<spring:theme code="img.favIcon" text="/" var="favIconPath"/>
        <link rel="shortcut icon" type="image/x-icon" media="all" href="${originalContextPath}${favIconPath}" />

		<%-- CSS Files Are Loaded First as they can be downloaded in parallel --%>
		<template:styleSheets/>

		<%-- Inject any additional CSS required by the page --%>
		<jsp:invoke fragment="pageCss"/>

		<analytics:analytics/>
	</head>

	<body class="language-${currentLanguage.isocode} - ${pageBodyCssClasses} - ${cmsPageRequestContextData.liveEdit ? ' yCmsLiveEdit' : ''}">

		<%-- Load JavaScript required by the site --%>
		<template:javaScript/>

		<%-- Inject any additional JavaScript required by the page included before page body --%>
		<jsp:invoke fragment="pageScriptsBeforeJspBody"/>

		<%-- Inject the page body here --%>
		<jsp:doBody/>

		<%-- Inject any additional JavaScript required by the page included after page body --%>
		<jsp:invoke fragment="pageScripts"/>

		<addonScripts:addonScripts/>
	</body>

	<jsp:invoke fragment="pageScriptsAfterJspBody"/>
	<debug:debugFooter/>
</html>
