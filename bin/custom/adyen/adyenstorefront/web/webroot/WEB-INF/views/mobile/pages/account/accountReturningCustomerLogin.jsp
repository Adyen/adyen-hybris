<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<h6 class="descriptionHeadline">
	<spring:theme code="text.headline.register" text="Click here to register a new customer"/>
</h6>

<div class="registerNewCustomerLinkHolder" data-theme="e" data-content-theme="e">
	<c:url value="/register" var="registerUrl"/>
	<a href="${registerUrl}" data-role="button" data-theme="c">
		<spring:theme code="register.new.customer"/>
	</a>
</div>
<div class="fakeHR"></div>
<div class="loginLinkHolder">
	<c:url value="/j_spring_security_check" var="loginAction"/>
	<user:login actionNameKey="login.login" action="${loginAction}"/>
</div>

