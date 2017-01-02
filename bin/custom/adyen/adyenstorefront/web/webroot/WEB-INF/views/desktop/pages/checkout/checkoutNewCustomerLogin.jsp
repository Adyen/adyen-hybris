<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<c:url value="/login/checkout/register" var="registerAndCheckoutActionUrl" />
<user:register actionNameKey="checkout.login.registerAndCheckout" action="${registerAndCheckoutActionUrl}"/>