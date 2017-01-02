<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:url var="profileUrl" value="/my-account/profile" />
<template:page pageTitle="${pageTitle}">

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav selected="update-password" />
	
	<div class="column accountContentPane clearfix">
		<div class="headline"><spring:theme code="text.account.profile.updatePasswordForm" text="Update Password"/></div>
		<div class="required right"><spring:theme code="form.required" text="Fields marked * are required"/></div>
		<div class="description"><spring:theme code="text.account.profile.updatePassword" text="Please use this form to update your account password"/></div>
	
		<form:form action="update-password" method="post" commandName="updatePasswordForm" autocomplete="off">
			
				<formElement:formPasswordBox idKey="profile.currentPassword" labelKey="profile.currentPassword" path="currentPassword" inputCSS="text password" mandatory="true"/>
				<formElement:formPasswordBox idKey="profile-newPassword" labelKey="profile.newPassword" path="newPassword" inputCSS="text password strength" mandatory="true"/>
				<formElement:formPasswordBox idKey="profile.checkNewPassword" labelKey="profile.checkNewPassword" path="checkNewPassword" inputCSS="text password" mandatory="true"/>
			
			<div class="form-actions">
				<button type="button" class="negative" onclick="window.location='${profileUrl}'"><spring:theme code="text.account.profile.cancel" text="Cancel"/></button>
				<ycommerce:testId code="profilePage_SaveUpdatePasswordButton">
					<button class="positive" type="submit"><spring:theme code="text.account.profile.updatePasswordForm" text="Update Password"/></button>
				</ycommerce:testId>
			</div>
		</form:form>
	
	</div>
	

</template:page>

