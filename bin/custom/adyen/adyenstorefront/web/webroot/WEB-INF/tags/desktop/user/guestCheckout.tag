<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="userGuest">
	<div class="headline"><spring:theme code="guest.checkout" arguments="${siteName}"/></div>
	<div class="required right"><spring:theme code="form.required"/></div>
	<div class="description"><spring:theme code="guest.description"/></div>
	
	<form:form action="${action}" method="post" commandName="guestForm">

		<formElement:formInputBox idKey="guest.email" labelKey="guest.email" path="email" inputCSS="text" mandatory="true"/>

		<div class="form-actions clearfix">
			<ycommerce:testId code="guest_Checkout_button">
				<button type="submit" class="positive"><spring:theme code="${actionNameKey}"/></button>
			</ycommerce:testId>
		</div>
	</form:form>
	
</div>