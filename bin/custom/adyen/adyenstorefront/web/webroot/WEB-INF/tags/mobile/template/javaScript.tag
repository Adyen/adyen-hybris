<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="g" uri="http://granule.com/tags/accelerator" %>
<%@ taglib prefix="compressible" tagdir="/WEB-INF/tags/mobile/template/compressible" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/mobile/template/cms" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>

<c:url value="/" var="siteRootUrl"/>

<template:javaScriptVariables />

<c:choose>
	<c:when test="${granuleEnabled}">
		<g:compress urlpattern="${encodingAttributes}">
			<compressible:js/>
		</g:compress>
	</c:when>
	<c:otherwise>
		<compressible:js/>
	</c:otherwise>
</c:choose>

<cms:previewJS cmsPageRequestContextData="${cmsPageRequestContextData}" />