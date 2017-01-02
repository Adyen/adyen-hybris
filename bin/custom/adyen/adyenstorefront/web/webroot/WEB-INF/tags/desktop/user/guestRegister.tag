<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<div class="userGuest">
	<div class="headline"><spring:theme code="guest.register"/></div>
	<div class="required right"><spring:theme code="form.required"/></div>
	<div class="description"><spring:theme code="guest.register.description"/></div>
	
	<form:form method="post" commandName="guestRegisterForm">
		<form:hidden path="orderCode"/>
		<form:hidden path="uid"/>
		
			<formElement:formPasswordBox idKey="password" labelKey="guest.pwd" path="pwd" inputCSS="text password strength" mandatory="true"/>
			<formElement:formPasswordBox idKey="guest.checkPwd" labelKey="guest.checkPwd" path="checkPwd" inputCSS="text password" mandatory="true"/>
		

			<div class="form-actions clearfix">
			<ycommerce:testId code="guest_Register_button">
				<button type="submit" class="positive">
					<spring:theme code="${actionNameKey}"/>
				</button>
			</ycommerce:testId>
		</div>
	</form:form>
</div>

