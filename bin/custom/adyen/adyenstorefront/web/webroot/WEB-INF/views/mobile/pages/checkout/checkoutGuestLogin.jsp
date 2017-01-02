<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user" %>

<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
	<div class="fakeHR">
		<c:url value="/login/checkout/guest" var="guestCheckoutUrl" />
		<user:guestCheckout actionNameKey="checkout.login.guestCheckout" action="${guestCheckoutUrl}"/>
	</div>
</sec:authorize>

