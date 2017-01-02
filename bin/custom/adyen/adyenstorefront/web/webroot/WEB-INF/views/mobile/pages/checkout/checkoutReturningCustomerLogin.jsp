<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<h6 class="descriptionHeadline">
	<spring:theme code="text.headline.register" text="Click here to register a new customer" />
</h6>
<div class="registerNewCustomerLinkHolder" data-theme="c" data-content-theme="c">
	<c:url value="/register/checkout" var="registerCheckoutUrl" />
	<a href="${registerCheckoutUrl}" data-role="button" data-theme="c">
		<spring:theme code="register.new.customer" /> &raquo;
	</a>
</div>
<div class="loginAndCheckoutLinkHolder">
	<c:url value="/checkout/j_spring_security_check" var="loginAndCheckoutAction" />
	<user:login actionNameKey="checkout.login.loginAndCheckout" action="${loginAndCheckoutAction}" />
</div>

