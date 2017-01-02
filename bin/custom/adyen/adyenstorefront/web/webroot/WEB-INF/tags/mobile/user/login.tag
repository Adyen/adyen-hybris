<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/mobile/cart"%>

<div class="loginTag" data-theme="e" data-content-theme="c">
	<h3>
		<spring:theme code="login.title"/>
	</h3>
	<ul class="mContentList">
		<li class="continuous-text">
			<spring:theme code="login.description"/>
		</li>
		<li class="continuous-text">
			<spring:theme code="login.required.message"/>
		</li>
	</ul>
	<form:form action="${action}" method="post" commandName="loginForm" data-ajax="false">
		<common:errors/>
		<c:if test="${not empty accErrorMsgs}">
				<span class="form_field_error">
		</c:if>
		<ul class="mFormList" data-theme="a" data-content-theme="a">
			<li>
				<formElement:formInputBox idKey="j_username" labelKey="login.email" path="j_username" inputCSS="text" mandatory="true"/>
			</li>
			<li>
				<formElement:formPasswordBox idKey="j_password" labelKey="login.password" path="j_password" inputCSS="text password" mandatory="true"/>
			</li>

			<cart:cartExpressCheckoutEnabled/>

			<li>
				<h6 class="descriptionHeadline"><spring:theme code="text.headline.login" text="Click here to login"/></h6>
				<ycommerce:testId code="login_button">
					<button type="submit" class="form" data-role="button" data-theme="b">
						<spring:theme code="${actionNameKey}"/>
					</button>
				</ycommerce:testId>
			</li>
			<li>
				<ycommerce:testId code="forgotten_password">
					<a href="#forgotPassword" data-url="<c:url value="/login/pw/request"/>" class="password-forgotten">
						<spring:theme code="login.link.forgottenPwd"/>
					</a>
				</ycommerce:testId>
				<div class="fakeHR"></div>
			</li>
		</ul>
	</form:form>
</div>
