<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<div data-content-theme="d" data-theme="e">
	<div class="registerNewCustomerLinkHolder registerNewCustomerLinkHolderBack" data-theme="e" data-content-theme="e">
		<c:choose>
			<c:when test="${not empty accErrorMsgs}">
	            <c:url value="${savedHeader}" var="loginUrl"/>
	        </c:when>
	        <c:otherwise>
				<c:url value="${header['referer']}" var="loginUrl"/>
	            <c:set var="savedHeader" scope="session" value="${header['referer']}"/>
	        </c:otherwise>
        </c:choose>
		<a href="${loginUrl}" data-role="link" data-theme="d">
			&laquo; <spring:theme code="register.back.login"/> </a>
	</div>
	<div class="fakeHR"></div>
	<h3 class="register-new-headline">
		<spring:theme code="register.new.customer"/>
	</h3>
	<p>
	<ul class="mContentList">
		<li><spring:theme code="register.description"/></li>
		<li><spring:theme code="form.required"/></li>
	</ul>
	<form:form method="post" id="registerForm" commandName="registerForm" action="${action}" data-ajax="false">
		<common:errors/>
		<ul id="registerFormList" class="mFormList" data-theme="a" data-content-theme="a">
			<li>
				<formElement:formSelectBox idKey="register.title" labelKey="register.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titles}" />
			</li>
			<li>
				<formElement:formInputBox idKey="register.firstName" labelKey="register.firstName" path="firstName" inputCSS="text" mandatory="true" />
			</li>
			<li>
				<formElement:formInputBox idKey="register.lastName" labelKey="register.lastName" path="lastName" inputCSS="text" mandatory="true" />
			</li>
			<li>
				<formElement:formInputBox idKey="register.email" labelKey="register.email" path="email" inputCSS="text" mandatory="true" />
			</li>
			<li>
				<formElement:formPasswordBox idKey="password" labelKey="register.pwd" path="pwd" inputCSS="text password strength" mandatory="true" />
			</li>
			<li>
				<formElement:formPasswordBox idKey="register.checkPwd" labelKey="register.checkPwd" path="checkPwd" inputCSS="text password" mandatory="true" />
			</li>
			<input type="hidden" id="recaptchaChallangeAnswered" value="${requestScope.recaptchaChallangeAnswered}"/>

			<li>
				<span style="display: block; clear: both;">
					<ycommerce:testId code="register_Register_button">
						<button data-theme="c" class="form">
							<spring:theme code='${actionNameKey}' />
						</button>
					</ycommerce:testId> 
				</span>
			</li>
		</ul>
	</form:form>
</div>
