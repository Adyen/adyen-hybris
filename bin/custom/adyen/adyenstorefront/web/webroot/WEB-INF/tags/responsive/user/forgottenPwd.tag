<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<div class="forgotten-password">
	<div class="description"><spring:theme code="forgottenPwd.description"/></div>
	<form:form method="post" commandName="forgottenPwdForm">
		<formElement:formInputBox idKey="forgottenPwd.email" labelKey="forgottenPwd.email" path="email" mandatory="true"/>
		<button class="btn btn-primary btn-block" type="submit"><spring:theme code="forgottenPwd.submit"/></button>
	</form:form>
</div>
