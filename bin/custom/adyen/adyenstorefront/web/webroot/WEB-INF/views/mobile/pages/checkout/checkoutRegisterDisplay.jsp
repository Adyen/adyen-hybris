<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user"%>

<div data-role="content">
	<c:url value="/register/checkout/newcustomer" var="registerAndCheckoutAction" />
	<user:register actionNameKey="checkout.login.registerAndCheckout" action="${registerAndCheckoutAction}" />
</div>

