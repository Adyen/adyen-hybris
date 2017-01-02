<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
	<c:url value="/login/checkout/guest" var="guestCheckoutUrl" />
	<user:guestCheckout actionNameKey="checkout.login.guestCheckout" action="${guestCheckoutUrl}"/>
</sec:authorize>