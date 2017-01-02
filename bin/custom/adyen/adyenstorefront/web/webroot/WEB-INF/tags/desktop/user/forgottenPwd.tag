<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<div class="forgottenPwd clearfix">
		<div class="headline"><spring:theme code="forgottenPwd.title"/></div>
		<div class="required right"><spring:theme code="form.required"/></div>
		<div class="description"><spring:theme code="forgottenPwd.description"/></div>
		<form:form method="post" commandName="forgottenPwdForm">
			<formElement:formInputBox idKey="forgottenPwd.email" labelKey="forgottenPwd.email" path="email" inputCSS="text" mandatory="true"/>
			<button class="positive" type="submit"><spring:theme code="forgottenPwd.submit"/></button>
		</form:form>
</div>
