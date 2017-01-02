<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="item_container_holder">
	<div class="title_holder">
		<h2><spring:theme code="guest.register"/></h2>
	</div>

	<div class="item_container">
		<p><spring:theme code="guest.register.description"/></p>
		<p class="required"><spring:theme code="form.required"/></p>
		<form:form method="post" commandName="guestRegisterForm">
			<form:hidden path="orderCode"/>
			<form:hidden path="uid"/>
			<div class="form_field-elements">
				<formElement:formPasswordBox idKey="password" labelKey="guest.pwd" path="pwd" inputCSS="text password strength" mandatory="true"/>
				<formElement:formPasswordBox idKey="guest.checkPwd" labelKey="guest.checkPwd" path="checkPwd" inputCSS="text password" mandatory="true"/>
			</div>

			<div class="form-field-button">
				<ycommerce:testId code="guest_Register_button">
					<button type="submit" class="form" data-role="button" data-theme="c">
						<spring:theme code="${actionNameKey}"/>
					</button>
				</ycommerce:testId>
			</div>
		</form:form>
	</div>
</div>