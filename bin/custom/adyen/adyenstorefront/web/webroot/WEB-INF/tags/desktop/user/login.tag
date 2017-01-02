<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<div class="userLogin">
	<div class="headline"><spring:theme code="login.title"/></div>
	<div class="required right"><spring:theme code="form.required"/></div>
	<div class="description"><spring:theme code="login.description"/></div>

	<form:form action="${action}" method="post" commandName="loginForm">
		<c:if test="${not empty message}">
			<span class="errors">
				<spring:theme code="${message}"/>
			</span>
		</c:if>
		<c:if test="${loginError}">
			<div class="form_field_error">
		</c:if>
		<div class="form_field-elements">
			<formElement:formInputBox idKey="j_username" labelKey="login.email" path="j_username" inputCSS="text" mandatory="true"/>
			<formElement:formPasswordBox idKey="j_password" labelKey="login.password" path="j_password" inputCSS="text password" mandatory="true"/>
			<div class="form_field_error-message">
				<a href="javascript:void(0)" data-url="<c:url value='/login/pw/request'/>" class="password-forgotten"><spring:theme code="login.link.forgottenPwd"/></a>
			</div>
		</div>
		<c:if test="${loginError}">
			</div>
		</c:if>
		<c:if test="${expressCheckoutAllowed}">
				<div class="expressCheckoutLogin">
					<div class="headline"><spring:theme text="Express Checkout" code="text.expresscheckout.header"/></div>

					<div class="description"><spring:theme text="Benefit from a faster checkout by:" code="text.expresscheckout.title"/></div>

					<ul>
						<li><spring:theme text="setting a default Delivery Address in your account" code="text.expresscheckout.line1"/></li>
						<li><spring:theme text="setting a default Payment Details in your account" code="text.expresscheckout.line2"/></li>
						<li><spring:theme text="a default shipping method is used" code="text.expresscheckout.line3"/></li>
					</ul>

					<div class="expressCheckoutCheckbox clearfix">
						<label for="expressCheckoutCheckbox"><input id="expressCheckoutCheckbox" name="expressCheckoutEnabled"  type="checkbox" class="form left doExpressCheckout"/>
							<spring:theme text="I would like to Express checkout" code="cart.expresscheckout.checkbox"/></label>
					</div>
				</div>
		</c:if>

		<div class="form-actions clearfix">
			<ycommerce:testId code="login_Login_button">
				<button type="submit" class="positive"><spring:theme code="${actionNameKey}"/></button>
			</ycommerce:testId>
		</div>
	</form:form>
	

</div>
