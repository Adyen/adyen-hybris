<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/mobile/nav/breadcrumb" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<c:if test="${not passwordRequestSent}">
		<user:forgottenPwd/>
	</c:if>
</template:page>
