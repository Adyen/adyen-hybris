<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>


<spring:theme code="updatePwd.title" var="title"/>
<template:page pageTitle="${pageTitle}">
	<div class="global-alerts">
		<div class="alert alert-danger alert-dismissable">
			<spring:theme code="text.page.message.underconstruction" text="Information: Page Under Construction - Not Completely Functional"/>
		</div>
	</div>
	
</template:page>
