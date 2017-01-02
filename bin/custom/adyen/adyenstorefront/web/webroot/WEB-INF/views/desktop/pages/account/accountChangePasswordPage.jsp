<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:url var="profileUrl" value="/my-account/profile" />
<div class="span-24">
	<div class="span-20 last">
		<div class="accountContentPane clearfix">
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
	</div>
</div>	